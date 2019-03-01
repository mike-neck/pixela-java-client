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
package pixela.client.http.json;

import org.jetbrains.annotations.NotNull;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface JsonEncoder {

  @NotNull
  default Mono<String> encode(@NotNull final Request<?> request) {
    if (request.hasBody()) {
      return encodeObject(request);
    } else {
      return Mono.empty();
    }
  }

  @NotNull
  Mono<String> encodeObject(@NotNull final Object object);
}
