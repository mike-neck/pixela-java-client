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
import pixela.client.http.json.JsonCodec;
import reactor.core.publisher.Mono;

public class JsonCodecImpl implements JsonCodec {
  @NotNull private final JsonDecoder decoder;
  @NotNull private final JsonEncoder encoder;

  JsonCodecImpl(
      @NotNull final ExecutorService executorService, @NotNull final ObjectMapper objectMapper) {
    this(
        JsonDecoder.forJackson(executorService, objectMapper),
        JsonEncoder.forJackson(executorService, objectMapper));
  }

  private JsonCodecImpl(@NotNull final JsonDecoder decoder, @NotNull final JsonEncoder encoder) {
    this.decoder = decoder;
    this.encoder = encoder;
  }

  @NotNull
  @Override
  public <T> Mono<T> decode(@NotNull final String json, @NotNull final Class<? extends T> type) {
    return decoder.decode(json, type);
  }

  @NotNull
  @Override
  public Mono<String> encodeObject(@NotNull final Object object) {
    return encoder.encodeObject(object);
  }
}
