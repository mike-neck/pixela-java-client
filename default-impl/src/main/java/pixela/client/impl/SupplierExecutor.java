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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

interface SupplierExecutor {

  @NotNull
  <T> Mono<T> runSupplier(@NotNull final Supplier<? extends T> supplier);

  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static SupplierExecutor fromExecutorService(@NotNull final ExecutorService executorService) {
    return new SupplierExecutor() {
      @NotNull
      @Override
      public <T> Mono<T> runSupplier(@NotNull final Supplier<? extends T> supplier) {
        return Mono.defer(
            () -> Mono.fromFuture(CompletableFuture.supplyAsync(supplier, executorService)));
      }
    };
  }

  @Contract(value = " -> new", pure = true)
  @SuppressWarnings("Anonymous2MethodRef")
  @NotNull
  static SupplierExecutor noExecutor() {
    return new SupplierExecutor() {
      @NotNull
      @Override
      public <T> Mono<T> runSupplier(@NotNull final Supplier<? extends T> supplier) {
        return Mono.fromSupplier(supplier);
      }
    };
  }
}
