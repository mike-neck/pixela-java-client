/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pixela.client;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import integration.PixelaUser;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.*;

public class MockPixelaServer implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(MockPixelaServer.class);

  private static final int PORT_NUMBER = 8_000;

  private static final SupportedType[] SUPPORTED_TYPES =
      new SupportedType[] {
        SupportedType.of(PixelaClientConfig.class),
        SupportedType.of(PixelaClient.class),
        SupportedType.typeWithMethodAnnotation(Pixela.class, PixelaUser.class)
      };

  @Override
  public void afterEach(@NotNull final ExtensionContext context) throws Exception {
    final ExtensionContext.Store store = context.getStore(NAMESPACE);
    final AutoCloseables empty = AutoCloseables.empty();
    empty
        .add(
            () -> {
              final WireMockServer wireMockServer = store.remove(NAMESPACE, WireMockServer.class);
              if (wireMockServer == null) {
                return;
              }
              wireMockServer.stop();
            })
        .add(
            () -> {
              final PixelaClient pixelaClient =
                  store.remove(PixelaClient.class, PixelaClient.class);
              pixelaClient.close();
            })
        .add(() -> store.remove(PixelaClientConfig.class))
        .add(() -> store.remove(Pixela.class))
        .close();
  }

  @Override
  public void beforeEach(@NotNull final ExtensionContext context) {
    final ExtensionContext.Store store = context.getStore(NAMESPACE);
    final WireMockServer wireMockServer =
        store.getOrComputeIfAbsent(
            NAMESPACE, ns -> new WireMockServer(options().port(PORT_NUMBER)), WireMockServer.class);
    wireMockServer.start();
    configureFor(MockPixelaServer.PORT_NUMBER);
  }

  @Override
  public boolean supportsParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Class<?> type = parameterContext.getParameter().getType();
    final Optional<Method> testMethod = extensionContext.getTestMethod();
    final List<Annotation> annotations =
        testMethod
            .map(Method::getDeclaredAnnotations)
            .stream()
            .flatMap(Arrays::stream)
            .collect(Collectors.toUnmodifiableList());

    return Arrays.stream(SUPPORTED_TYPES)
        .anyMatch(supportedType -> supportedType.matches(type, annotations));
  }

  @Override
  public Object resolveParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Class<?> type = parameterContext.getParameter().getType();
    final ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);

    final PixelaClientConfig pixelaClientConfig =
        store.getOrComputeIfAbsent(
            PixelaClientConfig.class, klass -> pixelaClientConfig(), PixelaClientConfig.class);

    final PixelaClient pixelaClient =
        store.getOrComputeIfAbsent(
            PixelaClient.class, klass -> pixelaClient(pixelaClientConfig), PixelaClient.class);

    extensionContext
        .getTestMethod()
        .stream()
        .map(Method::getDeclaredAnnotations)
        .flatMap(Arrays::stream)
        .filter(PixelaUser.class::isInstance)
        .findFirst()
        .map(PixelaUser.class::cast)
        .ifPresent(
            pixelaUser ->
                store.getOrComputeIfAbsent(
                    Pixela.class,
                    klass ->
                        pixelaClient.username(pixelaUser.username()).token(pixelaUser.userToken()),
                    Pixela.class));

    return store.get(type);
  }

  @NotNull
  private PixelaClientConfig pixelaClientConfig() {
    return PixelaClientConfig.builder().serviceUri("http://localhost:" + PORT_NUMBER).build();
  }

  @NotNull
  private PixelaClient pixelaClient(@NotNull final PixelaClientConfig pixelaClientConfig) {
    return Pixela.withDefaultJavaClient(pixelaClientConfig);
  }
}
