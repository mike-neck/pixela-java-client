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

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface Pixel {

  @NotNull
  String quantity();

  @NotNull
  Optional<String> optionalData();

  @NotNull
  <T> Mono<T> as(@NotNull final T type);

  // TODO Update Pixel via Pixel object
  // TODO Increment Pixel via Pixel object
  // TODO Decrement Pixel via Pixel object
}
