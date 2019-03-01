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

import java.io.UncheckedIOException;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

final class Exceptions {

  private Exceptions() {}

  @NotNull
  @Contract(pure = true)
  static Function<Throwable, Void> asFunction(final Consumer<Throwable> consumer) {
    return (Throwable t) -> {
      if (t instanceof CompletionException) {
        final Throwable cause = t.getCause();
          if (cause instanceof UncheckedIOException) {
              final Throwable ioe = cause.getCause();
              consumer.accept(ioe);
          } else {
              consumer.accept(cause);
          }
      } else {
        consumer.accept(t);
      }
      return null;
    };
  }
}
