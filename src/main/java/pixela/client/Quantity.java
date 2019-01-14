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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Quantity {

  @NotNull
  String asString();

  @Contract("_ -> new")
  @NotNull
  static Quantity string(@NotNull final String quantity) {
    return new StringQuantity(quantity);
  }

  @Contract("_ -> new")
  @NotNull
  static Quantity integer(final int quantity) {
    return new IntQuantity(quantity);
  }

  @Contract("_ -> new")
  @NotNull
  static Quantity floating(final double quantity) {
    return new FloatQuantity(quantity);
  }

  class StringQuantity implements Quantity {

    @NotNull private final String quantity;

    StringQuantity(@NotNull final String quantity) {
      this.quantity = Objects.requireNonNull(quantity);
    }

    @NotNull
    @Override
    public String asString() {
      return quantity;
    }
  }

  class IntQuantity implements Quantity {
    private final int quantity;

    IntQuantity(final int quantity) {
      this.quantity = quantity;
    }

    @NotNull
    @Override
    public String asString() {
      return Integer.toString(quantity);
    }

    @Override
    public String toString() {
      return asString();
    }
  }

  class FloatQuantity implements Quantity {
    private final double quantity;

    FloatQuantity(final double quantity) {
      this.quantity = quantity;
    }

    @NotNull
    @Override
    public String asString() {
      return Double.toString(quantity);
    }

    @Override
    public String toString() {
      return asString();
    }
  }
}
