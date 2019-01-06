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
package pixela.client;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class UserToken {

  public static final String X_USER_TOKEN = "X-USER-TOKEN";

  @NotNull private final String value;

  private UserToken(@NotNull final String value) {
    this.value = value;
  }

  @NotNull
  public static UserToken of(@NotNull final String value) {
    Objects.requireNonNull(value);
    return new UserToken(value);
  }

  @NotNull
  public String tokenName() {
    return X_USER_TOKEN;
  }

  @NotNull
  public String tokenValue() {
    return value;
  }

  @Override
  public String toString() {
    return "[" + X_USER_TOKEN + ":" + value + "]";
  }
}
