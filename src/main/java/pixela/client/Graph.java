/*
 * Copyright 2018 Shinya Mochida
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

import java.net.URI;
import java.util.Arrays;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.api.graph.DeleteGraph;

public interface Graph {

  DeleteGraph delete();

  URI viewUri();

  enum Type {
    INT("int"),
    FLOAT("float"),
    ;

    @NotNull private final String string;

    Type(@NotNull final String string) {
      this.string = string;
    }

    @Contract(pure = true)
    @NotNull
    public String value() {
      return string;
    }
  }

  enum Color {
    SHIBAFU("shibafu", false),
    GREEN("shibafu", true),
    MOMIJI("momiji", false),
    RED("momiji", true),
    SORA("sora", false),
    BLUE("sora", true),
    ICHOU("ichou", false),
    YELLOW("ichou", true),
    AJISAI("ajisai", false),
    PURPLE("ajisai", true),
    KURO("kuro", false),
    BLACK("kuro", true),
    ;

    @NotNull private final String string;
    private final boolean alias;

    Color(@NotNull final String string, final boolean alias) {
      this.string = string;
      this.alias = alias;
    }

    @Contract(pure = true)
    @NotNull
    public String value() {
      return string;
    }

    public boolean isNotAlias() {
      return !alias;
    }

    @NotNull
    public static Color fromString(@NotNull final String string) {
      return Arrays.stream(values())
          .filter(Color::isNotAlias)
          .filter(it -> it.string.equals(string))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("Color " + string + " is not found."));
    }
  }
}
