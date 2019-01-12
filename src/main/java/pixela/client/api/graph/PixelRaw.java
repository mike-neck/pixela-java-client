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

import java.time.LocalDate;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

public class PixelRaw {

  @NotNull private String quantity = "";

  @Nullable private String optionalData;

  public PixelRaw() {}

  public PixelRaw(@NotNull final String quantity, @NotNull final String optionalData) {
    this.quantity = quantity;
    this.optionalData = optionalData;
  }

  @NotNull
  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(@NotNull final String quantity) {
    this.quantity = quantity;
  }

  @Nullable
  public String getOptionalData() {
    return optionalData;
  }

  public void setOptionalData(@Nullable final String optionalData) {
    this.optionalData = optionalData;
  }

  @NotNull
  pixela.client.Pixel toPixel(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date) {
    return new Pixel(httpClient, pixela, graph, date);
  }

  class Pixel implements pixela.client.Pixel {

    @NotNull private final HttpClient httpClient;
    @NotNull private final Pixela pixela;
    @NotNull private final Graph graph;
    @NotNull private final LocalDate date;

    Pixel(
        @NotNull final HttpClient httpClient,
        @NotNull final Pixela pixela,
        @NotNull final Graph graph,
        @NotNull final LocalDate date) {
      this.httpClient = httpClient;
      this.pixela = pixela;
      this.graph = graph;
      this.date = date;
    }

    @NotNull
    @Override
    public LocalDate date() {
      return date;
    }

    @NotNull
    @Override
    public String quantity() {
      return PixelRaw.this.quantity;
    }

    @NotNull
    @Override
    public Optional<String> optionalData() {
      return Optional.ofNullable(PixelRaw.this.optionalData);
    }

    @NotNull
    @Override
    public <T> Mono<T> as(@NotNull final Class<T> type) {
      return Mono.justOrEmpty(optionalData).flatMap(json -> httpClient.decodeJson(json, type));
    }

    @Override
    public String toString() {
      return "Pixel["
          + graph
          + ",date="
          + date.format(Graph.PIXEL_DATE_FORMAT)
          + ",quantity="
          + PixelRaw.this.quantity
          + (optionalData == null ? "" : ",optionalData=" + optionalData)
          + ']';
    }
  }
}
