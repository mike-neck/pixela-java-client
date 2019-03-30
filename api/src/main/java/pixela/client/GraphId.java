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

import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GraphId {

  // language=regexp
  private static final String VALID = "^[a-z][a-z0-9-]{1,16}";

  private static final Pattern PATTERN = Pattern.compile(VALID);

  @NotNull private final String value;

  private GraphId(@NotNull final String value) {
    this.value = value;
  }

  @Contract("_ -> new")
  @NotNull
  public static GraphId of(@NotNull final String value) {
    return new GraphId(value);
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @NotNull
  public static GraphId validated(@NotNull final String value) {
    Objects.requireNonNull(value);
    if (PATTERN.matcher(value).matches()) {
      return of(value);
    }
    throw ApiException.of("invalid graph-id");
  }

  @NotNull
  public String value() {
    return value;
  }

  @NotNull
  public String path() {
    return '/' + value;
  }

  @NotNull
  @Override
  public String toString() {
    return "[graphID:" + value + "]";
  }

  @Contract(value = "null -> false", pure = true)
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (!(object instanceof GraphId)) return false;

    final GraphId graphId = (GraphId) object;

    return value.equals(graphId.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
