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
package pixela.client.impl;

import java.util.Arrays;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

interface AutoCloseables extends AutoCloseable {

  @NotNull
  AutoCloseables add(@NotNull final AutoCloseable autoCloseable);

  static AutoCloseables of(final AutoCloseable... autoCloseables) {
    return Arrays.stream(autoCloseables).reduce(empty(), AutoCloseables::add, (l, r) -> l);
  }

  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  private static AutoCloseables next(
      @NotNull final AutoCloseable autoCloseable, @NotNull final AutoCloseables current) {
    return new AutoCloseables() {
      @NotNull
      @Override
      public AutoCloseables add(@NotNull final AutoCloseable autoCloseable) {
        return next(autoCloseable, this);
      }

      @SuppressWarnings("EmptyTryBlock")
      @Override
      public void close() throws Exception {
        try (current;
            autoCloseable) {}
      }
    };
  }

  @Contract(value = " -> new", pure = true)
  @NotNull
  private static AutoCloseables empty() {
    return new AutoCloseables() {
      @NotNull
      @Override
      public AutoCloseables add(@NotNull final AutoCloseable autoCloseable) {
        return next(autoCloseable, this);
      }

      @Override
      public void close() {}
    };
  }
}
