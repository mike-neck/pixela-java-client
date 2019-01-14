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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuantityTest {

  @Nested
  class IntQuantityTest {

    @Test
    void incrementReturnsNextValue() {
      final Quantity quantity = Quantity.integer(-1);
      assertThat(quantity.increment()).isEqualTo(Quantity.integer(0));
    }
  }

  @Nested
  class FloatingQuantityTest {

    @Test
    void incrementReturnsNextValue() {
      final Quantity quantity = Quantity.floating(0.375802);
      assertThat(quantity.increment().asString()).isEqualTo(Quantity.floating(0.385802).asString());
    }
  }

  @Nested
  class StringQuantityTest {

    @Test
    void incrementIntReturnsNextValue() {
      final Quantity quantity = Quantity.string("23");
      assertThat(quantity.increment()).isEqualTo(Quantity.integer(24));
    }

    @Test
    void incrementFloatReturnsNextValue() {
      final Quantity quantity = Quantity.string("240.091");
      assertThat(quantity.increment().asString()).isEqualTo("240.101");
    }
  }
}
