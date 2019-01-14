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

import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.http.Put;
import reactor.core.publisher.Mono;

public interface UpdatePixel extends Put<Void>, Api<pixela.client.Pixel> {

  interface Quantity {
    @NotNull
    OptionalData quantity(@NotNull final pixela.client.Quantity quantity);

    @NotNull
    default OptionalData quantity(final int quantity) {
      return quantity(pixela.client.Quantity.integer(quantity));
    }

    @NotNull
    default OptionalData quantity(final double quantity) {
      return quantity(pixela.client.Quantity.floating(quantity));
    }
  }

  interface OptionalData extends UpdatePixel {
    @NotNull
    UpdatePixel optionalDataString(@NotNull final String optionalData);

    @NotNull
    Mono<UpdatePixel> optionalData(@NotNull final Object object);
  }
}
