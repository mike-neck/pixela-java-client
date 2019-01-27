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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.*;

public class MockPixelaServer implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(MockPixelaServer.class);

  public static final int PORT_NUMBER = 8_000;

  @Override
  public void afterEach(@NotNull final ExtensionContext context) {
    final ExtensionContext.Store store = context.getStore(NAMESPACE);
    final WireMockServer wireMockServer = store.get(NAMESPACE, WireMockServer.class);
    if (wireMockServer == null) {
      return;
    }
    wireMockServer.stop();
  }

  @Override
  public void beforeEach(@NotNull final ExtensionContext context) {
    final ExtensionContext.Store store = context.getStore(NAMESPACE);
    final WireMockServer wireMockServer =
        store.getOrComputeIfAbsent(
            NAMESPACE, ns -> new WireMockServer(options().port(PORT_NUMBER)), WireMockServer.class);
    wireMockServer.start();
  }

  @Override
  public boolean supportsParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Class<?> type = parameterContext.getParameter().getType();
    return PixelaClientConfig.class.equals(type);
  }

  @Override
  public Object resolveParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Class<?> type = parameterContext.getParameter().getType();
    if (!PixelaClientConfig.class.equals(type)) {
      throw new ParameterResolutionException("Only PixelaClientConfig is supported.");
    }
    return PixelaClientConfig.builder().serviceUri("http://localhost:" + PORT_NUMBER).build();
  }
}
