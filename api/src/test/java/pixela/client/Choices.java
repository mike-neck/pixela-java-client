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

import java.util.List;
import java.util.Random;

class Choices {
  private final int max;
  private final List<Choice> choices;

  Choices(final int max, final List<Choice> choices) {
    this.max = max;
    this.choices = choices;
  }

  String random(final Random random) {
    final int index = random.nextInt(max);
    final Choice choice =
        choices
            .stream()
            .filter(ch -> ch.minInclusive <= index && index < ch.maxExclusive)
            .findAny()
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "index = " + index + ", max = " + max + "choice = " + choices.toString()));
    final int i = index - choice.minInclusive;
    return new String(new char[] {(char) (choice.range.min + i)});
  }
}
