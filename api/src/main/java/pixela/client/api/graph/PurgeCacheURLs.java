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
package pixela.client.api.graph;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
interface PurgeCacheURLs {

  default boolean toBeUpdated() {
    return false;
  }

  @Nullable
  List<URI> values();

  @Nullable
  default List<String> asStringList() {
    final List<URI> values = values();
    if (values == null) {
      return null;
    }
    return values.stream().map(URI::toASCIIString).collect(Collectors.toList());
  }

  @NotNull PurgeCacheURLs NOT_UPDATE = () -> null;

  @NotNull
  static PurgeCacheURLs remove() {
    return update(Collections.emptyList());
  }

  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static PurgeCacheURLs update(@NotNull final List<URI> purgeCacheURIs) {
    return new PurgeCacheURLs() {

      @NotNull
      @Override
      public List<URI> values() {
        return purgeCacheURIs;
      }

      @Override
      public boolean toBeUpdated() {
        return true;
      }

      @Override
      public String toString() {
        return purgeCacheURIs.toString();
      }
    };
  }
}
