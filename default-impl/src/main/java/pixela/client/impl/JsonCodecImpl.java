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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.json.JsonCodec;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class JsonCodecImpl implements JsonCodec {
  private @NotNull ExecutorService executorService;
  private @NotNull ObjectMapper objectMapper;

  JsonCodecImpl(
      @NotNull final ExecutorService executorService, @NotNull final ObjectMapper objectMapper) {
    this.executorService = executorService;
    this.objectMapper = objectMapper;
  }

  @SuppressWarnings("BlockingMethodInNonBlockingContext")
  @NotNull
  @Override
  public <T> Mono<T> decode(@NotNull final String json, @NotNull final Class<? extends T> type) {
    final Supplier<Mono<T>> decodeJson =
        () -> {
          try {
            final T object = objectMapper.readValue(json, type);
            return Mono.just(object);
          } catch (final IOException e) {
            return Mono.error(e);
          }
        };
    return Mono.defer(decodeJson).subscribeOn(Schedulers.fromExecutor(executorService));
  }

  @NotNull
  @Override
  public Mono<String> encodeObject(@NotNull final Object object) {
    final Function<Object, Mono<String>> toJson =
        obj -> {
          try {
            final String json = objectMapper.writeValueAsString(obj);
            return Mono.just(json);
          } catch (final IOException e) {
            return Mono.error(e);
          }
        };
    return Mono.defer(() -> toJson.apply(object))
        .subscribeOn(Schedulers.fromExecutor(executorService));
  }
}
