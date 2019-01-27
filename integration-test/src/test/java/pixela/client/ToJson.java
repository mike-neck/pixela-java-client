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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import org.jetbrains.annotations.NotNull;

public interface ToJson {

  @NotNull
  String apply(@NotNull final Object object);

  @NotNull
  static ToJson of(@NotNull final ObjectMapper objectMapper) {
    return object -> {
      try {
        return objectMapper.writeValueAsString(object);
      } catch (final JsonProcessingException e) {
        throw new UncheckedIOException(e);
      }
    };
  }
}
