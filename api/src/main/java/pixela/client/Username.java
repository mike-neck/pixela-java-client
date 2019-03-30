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
import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Username {

  public static final String USER_NAME_PROPERTY_KEY = "pixela.username";

  private static final String VALID_NAME = "^[a-z][a-z0-9-]{1,32}$";
  private static final Pattern PATTERN = Pattern.compile(VALID_NAME);

  @NotNull private final String value;

  @Contract(pure = true)
  private Username(@NotNull final String value) {
    this.value = value;
  }

  @Contract("_ -> new")
  @NotNull
  public static Username of(@NotNull final String value) {
    return new Username(value);
  }

  @NotNull
  @Contract("!null -> new; null -> fail")
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static Username validated(@NotNull final String value) {
    Objects.requireNonNull(value);
    if (PATTERN.matcher(value).matches()) {
      return of(value);
    }
    throw ApiException.of("invalid username");
  }

  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return "[username: " + value + "]";
  }

  public String path() {
    return "/" + value;
  }
}
