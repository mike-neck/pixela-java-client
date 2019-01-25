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
package pixela.client.http;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

class ApiResponse implements Response<Void> {
  @NotNull private final String message;
  private final boolean success;

  ApiResponse(@NotNull final String message, final boolean success) {
    this.message = message;
    this.success = success;
  }

  @NotNull
  @Override
  public Mono<Void> toPublisher() {
    if (success) {
      return Mono.empty();
    } else {
      return Mono.error(() -> Response.error(message));
    }
  }
}
