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
package pixela.client.impl;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JsonCodecTest.Params.class)
class JsonCodecTest {

  @Test
  void successToEncode(final ExecutorService service) {
    final JsonCodecImpl codec = new JsonCodecImpl(service, JsonCodecFactoryImpl.objectMapper);
    final Value value = new Value("value");
    final Mono<String> mono = codec.encodeObject(value);
    StepVerifier.create(mono).expectNext("{\"key\":\"value\"}").verifyComplete();
  }

  @Test
  void failToEncode(final ExecutorService service) {
    final JsonCodecImpl codec = new JsonCodecImpl(service, JsonCodecFactoryImpl.objectMapper);
    final Object obj = new Object();
    final Mono<String> mono = codec.encodeObject(obj);
    StepVerifier.create(mono).expectError(IOException.class).verify();
  }

  @Test
  void successToDecode(final ExecutorService service) {
    final JsonCodecImpl codec = new JsonCodecImpl(service, JsonCodecFactoryImpl.objectMapper);
    final Mono<Value> mono = codec.decode("{\"key\":\"value\"}", Value.class);
    StepVerifier.create(mono).expectNext(new Value("value")).verifyComplete();
  }

  @Test
  void failToDecode(final ExecutorService service) {
      final JsonCodecImpl codec = new JsonCodecImpl(service, JsonCodecFactoryImpl.objectMapper);
      final Mono<String> mono = codec.decode("foo-bar", String.class);
      StepVerifier.create(mono).expectError(IOException.class).verify();
  }

  @Test
  void successToEncodeSync(final ExecutorService service) {
    final JsonCodecImpl codec = new JsonCodecImpl(service, JsonCodecFactoryImpl.objectMapper);
    final String json = codec.encodeSync(new Value("value"));
    assertThatJson(json).node("key").isString().isEqualTo("value");
  }

  @Test
  void successToDecodeSync(final ExecutorService service) {
    final JsonCodecImpl codec = new JsonCodecImpl(service, JsonCodecFactoryImpl.objectMapper);
    final Value value = codec.decodeSync("{\"key\":\"value\"}", Value.class);
    assertThat(value).isEqualTo(new Value("value"));
  }

  @SuppressWarnings("WeakerAccess")
  public static class Value {
    public String key;

    public Value() {}

    Value(@NotNull final String key) {
      this.key = key;
    }

    @Override
    public boolean equals(final Object object) {
      if (this == object) return true;
      if (!(object instanceof Value)) return false;

      final Value value = (Value) object;
      if (key == null) {
        return value.key == null;
      }
      return key.equals(value.key);
    }

    @Override
    public int hashCode() {
      return key.hashCode();
    }
  }

  static class Params implements ParameterResolver, AfterEachCallback {

    private final ExtensionContext.Namespace KEY = ExtensionContext.Namespace.create(Params.class);

    @Override
    public boolean supportsParameter(
        @NotNull final ParameterContext parameterContext,
        @NotNull final ExtensionContext extensionContext)
        throws ParameterResolutionException {
      final Class<?> type = parameterContext.getParameter().getType();
      return ExecutorService.class.equals(type);
    }

    @NotNull
    @Override
    public Object resolveParameter(
        @NotNull final ParameterContext parameterContext,
        @NotNull final ExtensionContext extensionContext)
        throws ParameterResolutionException {
      final ExtensionContext.Store store = extensionContext.getStore(KEY);
      final Class<?> type = parameterContext.getParameter().getType();
      return store.getOrComputeIfAbsent(type, this::generate);
    }

    @NotNull
    Object generate(@NotNull final Class<?> type) {
      if (ExecutorService.class.equals(type)) {
        return Executors.newSingleThreadExecutor();
      } else {
        throw new ParameterResolutionException("cannot provide :" + type.getCanonicalName());
      }
    }

    @Override
    public void afterEach(@NotNull final ExtensionContext context) {
      final ExtensionContext.Store store = context.getStore(KEY);
      final ExecutorService executorService =
          store.remove(ExecutorService.class, ExecutorService.class);
      executorService.shutdown();
    }
  }
}
