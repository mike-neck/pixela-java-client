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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.api.graph.*;
import pixela.client.api.webhook.CreateWebhook;
import pixela.client.http.HttpClient;

public interface Graph {

  @NotNull
  DateTimeFormatter PIXEL_DATE_FORMAT =
      new DateTimeFormatterBuilder()
          .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
          .appendValue(ChronoField.MONTH_OF_YEAR, 2)
          .appendValue(ChronoField.DAY_OF_MONTH, 2)
          .toFormatter();

  @NotNull String PATH = "/graphs";

  @NotNull
  GraphId id();

  @NotNull
  Pixela pixela();

  @NotNull
  UpdateGraphBuilder updateGraph();

  @NotNull
  DeleteGraph delete();

  @NotNull
  GetGraphSvg.NoOption getGraphSvg();

  @NotNull
  URI viewUri();

  @NotNull
  PostPixel.PixelDate postPixel();

  @NotNull
  GetPixel getPixel(@NotNull final LocalDate date);

  @NotNull
  String subPath();

  @NotNull
  IncrementPixel incrementPixel();

  @NotNull
  DecrementPixel decrementPixel();

  @NotNull
  CreateWebhook createIncrementWebhook();

  @NotNull
  static Graph simple(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final GraphId graphId) {
    return SimpleGraph.of(httpClient, pixela, graphId);
  }

  @NotNull
  CreateWebhook createDecrementWebhook();

  enum Type {
    INT("int"),
    FLOAT("float"),
    ;

    @NotNull private final String string;

    Type(@NotNull final String string) {
      this.string = string;
    }

    @NotNull
    @Contract(pure = true)
    public static Type fromString(@NotNull final String type) {
      return Arrays.stream(values())
          .filter(t -> t.string.equals(type.toLowerCase()))
          .findFirst()
          .orElseThrow(
              () ->
                  new NoSuchElementException("Type [" + type + "] is not found in this library."));
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
