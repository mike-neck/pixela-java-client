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
package pixela.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.json.JsonDecoder;
import reactor.core.publisher.Mono;

public interface JsonDec {

  @NotNull
  @Contract(value = " -> new", pure = true)
  static JsonDecoder decoder() {
    return new JsonDecoder() {
      @Override
      public @NotNull <T> Mono<T> decode(@NotNull final String json, @NotNull final Class<T> type) {
        return Mono.just(readJson(json, type));
      }
    };
  }

  static <T> T readJson(final String json, final Class<T> type) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, type);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
