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

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pixela.client.*;
import pixela.client.http.HttpClient;
import pixela.client.http.json.JsonEncoder;
import reactor.core.publisher.Mono;

public class PostPixelImpl implements PostPixel, PostPixel.OptionData {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;
  @NotNull private final LocalDate date;
  @NotNull private final Quantity quantity;
  @Nullable private final String optionalData;

  PostPixelImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date,
      @NotNull final Quantity quantity) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.date = date;
    this.quantity = quantity;
    this.optionalData = null;
  }

  private PostPixelImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date,
      @NotNull final Quantity quantity,
      @NotNull final String optionalData) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.date = date;
    this.quantity = quantity;
    this.optionalData = optionalData;
  }

  @NotNull
  @Override
  public Mono<Pixel> call() {
    final Mono<Void> response = httpClient.post(this);
    return response.thenReturn(
        new PixelRaw(quantity, optionalData).toPixel(httpClient, pixela, graph, date));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String uri = pixela.usersUri(baseUrl).toASCIIString() + graph.subPath();
    return URI.create(uri);
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.of(pixela.token());
  }

  @NotNull
  @Override
  public WithBody withBody() {
    return WithBody.TRUE;
  }

  @NotNull
  @Override
  public Class<Void> responseType() {
    return Void.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "POST "
        + pixela.usersUri()
        + graph.subPath()
        + '\n'
        + "  date: "
        + date.format(Graph.PIXEL_DATE_FORMAT)
        + '\n'
        + "  quantity: "
        + quantity
        + '\n'
        + "  optionalData: "
        + (optionalData == null ? "[null]" : optionalData);
  }

  @NotNull
  @Override
  public Mono<PostPixel> optionData(@NotNull final Object pojo) {
    final JsonEncoder encoder = httpClient.encoder();
    return encoder.encodeObject(pojo).map(this::optionDataJson);
  }

  @NotNull
  @Override
  public PostPixel optionDataJson(@NotNull final String json) {
    return new PostPixelImpl(httpClient, pixela, graph, date, quantity, json);
  }

  @NotNull
  @Override
  public PostPixel noOptionData() {
    return this;
  }

  public String toString() {
    return errorRequest();
  }

  @NotNull
  @Override
  public String getDate() {
    return date.format(Graph.PIXEL_DATE_FORMAT);
  }

  @NotNull
  @Override
  public String getQuantity() {
    return quantity.asString();
  }

  @Nullable
  @Override
  public String getOptionalData() {
    return optionalData;
  }
}
