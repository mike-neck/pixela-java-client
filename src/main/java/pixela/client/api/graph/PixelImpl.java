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
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

class PixelImpl implements pixela.client.Pixel {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;
  @NotNull private final LocalDate date;
  @NotNull private final PixelDetail raw;

  PixelImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date,
      @NotNull final PixelDetail raw) {
    this.raw = raw;
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
    return raw.quantity();
  }

  @NotNull
  @Override
  public Optional<String> optionalData() {
    return raw.optionalData();
  }

  @NotNull
  @Override
  public <T> Mono<T> as(@NotNull final Class<T> type) {
    return Mono.justOrEmpty(raw.optionalData()).flatMap(json -> httpClient.decodeJson(json, type));
  }

  @Override
  public UpdatePixel.Quantity update() {
    return quantity -> new UpdatePixelImpl(httpClient, pixela, graph, date, quantity);
  }

  @Override
  public DeletePixel delete() {
    return DeletePixel.of(httpClient, pixela, graph, date);
  }

  @Override
  public String toString() {
    return "Pixel["
        + graph
        + ",date="
        + date.format(Graph.PIXEL_DATE_FORMAT)
        + ",quantity="
        + raw.quantity()
        + (raw.optionalData().map(data -> ",optionalData=" + data).orElse(""))
        + ']';
  }
}
