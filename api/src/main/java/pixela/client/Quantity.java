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

import java.math.BigDecimal;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Quantity {

  @NotNull
  String asString();

  @NotNull
  Quantity increment();

  @NotNull
  Quantity decrement();

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

    @NotNull
    @Override
    public Quantity increment() {
      return toNumberBased().increment();
    }

    @NotNull
    @Override
    public Quantity decrement() {
      return toNumberBased().decrement();
    }

    @NotNull
    @Contract(" -> new")
    private Quantity toNumberBased() {
      if (quantity.contains(".")) {
        return new FloatQuantity(Double.parseDouble(quantity));
      }
      return new IntQuantity(Integer.parseInt(quantity));
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

    @NotNull
    @Override
    public Quantity increment() {
      return new IntQuantity(quantity + 1);
    }

    @NotNull
    @Override
    public Quantity decrement() {
      return new IntQuantity(quantity - 1);
    }

    @Override
    public String toString() {
      return asString();
    }

    @Override
    public boolean equals(final Object object) {
      if (this == object) return true;
      if (!(object instanceof IntQuantity)) return false;

      final IntQuantity that = (IntQuantity) object;

      return quantity == that.quantity;
    }

    @Override
    public int hashCode() {
      return quantity;
    }
  }

  class FloatQuantity implements Quantity {

    private static final BigDecimal diff = new BigDecimal("0.01");

    private final double quantity;

    FloatQuantity(final double quantity) {
      this.quantity = quantity;
    }

    @NotNull
    @Override
    public String asString() {
      return Double.toString(quantity);
    }

    @NotNull
    @Override
    public Quantity increment() {
      final BigDecimal current = BigDecimal.valueOf(quantity);
      final BigDecimal next = current.add(diff);
      return new FloatQuantity(next.doubleValue());
    }

    @NotNull
    @Override
    public Quantity decrement() {
      final BigDecimal current = BigDecimal.valueOf(quantity);
      final BigDecimal next = current.subtract(diff);
      return new FloatQuantity(next.doubleValue());
    }

    @Override
    public String toString() {
      return asString();
    }

    @Override
    public boolean equals(final Object object) {
      if (this == object) return true;
      if (!(object instanceof FloatQuantity)) return false;

      final FloatQuantity that = (FloatQuantity) object;

      return Double.compare(that.quantity, quantity) == 0;
    }

    @Override
    public int hashCode() {
      final long temp = Double.doubleToLongBits(quantity);
      return (int) (temp ^ (temp >>> 32));
    }
  }
}
