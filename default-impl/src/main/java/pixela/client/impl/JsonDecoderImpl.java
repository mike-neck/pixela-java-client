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
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class JsonDecoderImpl implements JsonDecoder {

  @NotNull private final ObjectMapper objectMapper;
  @NotNull private final ExecutorService executorService;

  JsonDecoderImpl(
      @NotNull final ObjectMapper objectMapper, @NotNull final ExecutorService executorService) {
    this.objectMapper = objectMapper;
    this.executorService = executorService;
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
}
