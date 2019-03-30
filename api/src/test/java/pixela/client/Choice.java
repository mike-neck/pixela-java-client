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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Choice {
  final int minInclusive;
  final int maxExclusive;
  final Range range;

  private Choice(final int minInclusive, final int maxExclusive, final Range range) {
    this.minInclusive = minInclusive;
    this.maxExclusive = maxExclusive;
    this.range = range;
  }

  static Choices choices(final Range... ranges) {
    int max = 0;
    final List<Choice> choices = new ArrayList<>();
    for (final Range range : ranges) {
      choices.add(new Choice(max, max + range.size, range));
      max += range.size;
    }
    final List<Choice> list = Collections.unmodifiableList(choices);
    return new Choices(max, list);
  }

  @Override
  public String toString() {
    return "choice[min:" + minInclusive + ",max:" + maxExclusive + ",range:" + range + "]";
  }
}
