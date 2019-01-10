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
import java.util.concurrent.ExecutorService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

interface JsonCodec extends JsonDecoder, JsonEncoder {

  @NotNull
  static JsonCodec forJackson(
      @NotNull final ExecutorService executorService, @NotNull final ObjectMapper objectMapper) {
    final JsonDecoder decoder = JsonDecoder.forJackson(executorService, objectMapper);
    final JsonEncoder encoder = JsonEncoder.forJackson(executorService, objectMapper);
    return new JsonCodec() {
      @NotNull
      @Override
      public <T> Mono<T> decode(
          @NotNull final String json, @NotNull final Class<? extends T> type) {
        return decoder.decode(json, type);
      }

      @NotNull
      @Override
      public Mono<String> encodeObject(@NotNull final Object object) {
        return encoder.encodeObject(object);
      }
    };
  }
}
