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
import pixela.client.ApiException;
import reactor.core.publisher.Mono;

public interface Response<T> {

  @NotNull
  Mono<T> toPublisher();

  @NotNull
  static Response<Void> of(@NotNull final String message, final boolean success) {
    return new ApiResponse(message, success);
  }

  @NotNull
  static ApiException error(@NotNull final String message) {
    return ApiException.of(message);
  }
}
