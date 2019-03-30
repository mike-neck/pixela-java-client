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

import java.util.Random;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

enum Klass {
  LOWER {
    @Override
    Range range() {
      return new Range(97, 26);
    }
  },
  NUMBER {
    @Override
    Range range() {
      return new Range(48, 10);
    }
  },
  HYPHEN {
    @Override
    Range range() {
      return new Range(45, 1);
    }
  },
  ;

  abstract Range range();

  @NotNull
  static String random(final int len, final Random random) {
    final StringBuilder stringBuilder = new StringBuilder();
    final String firstChar = Choice.choices(LOWER.range()).random(random);
    stringBuilder.append(firstChar);

    final int length = random.nextInt(len) + 1;
    final Choices choices = Choice.choices(LOWER.range(), NUMBER.range(), HYPHEN.range());
    Stream.generate(() -> choices.random(random)).limit(length).forEach(stringBuilder::append);
    return stringBuilder.toString();
  }
}
