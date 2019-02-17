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
package org.mikeneck.slack;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public enum Color {
  DIMGRAY("#000000"),
  ROYALBLUE("4169e1"),
  TEAL("#008080"),
  KHAKI("#f0e68c"),
  TOMATO("#ff6347"),
  DARKORCHID("#9932cc");

  final String rgb;

  Color(final String rgb) {
    this.rgb = rgb;
  }

  public static Iterator<Color> infiniteIterator() {
    final List<Color> list = list();
    final int size = list.size();
    final int start = new Random().nextInt(size);
    return new Iterator<>() {

      private final List<Color> colors = list();
      private int index = start;

      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Color next() {
        final int current = index;
        if (current == size - 1) {
          this.index = 0;
        } else {
          this.index = current + 1;
        }
        return colors.get(current);
      }
    };
  }

  static List<Color> list() {
    return List.of(values());
  }
}
