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

import org.jetbrains.annotations.NotNull;
import pixela.client.ApiException;
import reactor.core.publisher.Mono;

class BasicResponse {
  @NotNull private String message = "";
  private boolean isSuccess;

  @NotNull
  String getMessage() {
    return message;
  }

  public void setMessage(@NotNull final String message) {
    this.message = message;
  }

  boolean isIsSuccess() {
    return isSuccess;
  }

  public void setIsSuccess(final boolean success) {
    isSuccess = success;
  }

  Mono<Void> emptyOrError() {
    if (isSuccess) {
      return Mono.empty();
    } else {
      return Mono.error(ApiException.of(message));
    }
  }
}
