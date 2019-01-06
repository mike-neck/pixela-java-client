/*
 * Copyright 2018 Shinya Mochida
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
import org.jetbrains.annotations.NotNull;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

interface JsonEncoder {

  @NotNull
  Mono<String> encode(@NotNull final Request<?> request);

  @NotNull
  static JsonEncoder forJackson(
      @NotNull final ExecutorService executorService, @NotNull final ObjectMapper objectMapper) {
    final Function<Request<?>, Mono<String>> toJson =
        request -> {
          try {
            if (request.hasBody()) {
              final String json = objectMapper.writeValueAsString(request);
              return Mono.just(json);
            } else {
              return Mono.empty();
            }
          } catch (final IOException e) {
            return Mono.error(e);
          }
        };
    return request ->
        Mono.defer(() -> toJson.apply(request))
            .subscribeOn(Schedulers.fromExecutor(executorService));
  }
}
