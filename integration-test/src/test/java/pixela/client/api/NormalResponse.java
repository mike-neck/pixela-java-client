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
package pixela.client.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class NormalResponse {

  private final boolean isSuccess;
  @NotNull private final String message;

  private NormalResponse(final boolean isSuccess, @NotNull final String message) {
    this.isSuccess = isSuccess;
    this.message = message;
  }

  @Contract("_ -> new")
  @NotNull
  public static NormalResponse success(@NotNull final String message) {
    return new NormalResponse(true, message);
  }

  @Contract("_ -> new")
  @NotNull
  public static NormalResponse failure(@NotNull final String message) {
    return new NormalResponse(false, message);
  }

  public boolean getIsSuccess() {
    return isSuccess;
  }

  @NotNull
  public String getMessage() {
    return message;
  }
}
