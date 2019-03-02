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
import pixela.client.http.Response;
import pixela.client.http.json.JsonEncoder;
import reactor.core.publisher.Mono;

public class UpdatePixelImpl implements UpdatePixel, UpdatePixel.OptionalData, PixelDetail {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;
  @NotNull private final LocalDate date;
  @NotNull private final pixela.client.Quantity quantity;
  @Nullable private final String optionalData;

  private UpdatePixelImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date,
      @NotNull final pixela.client.Quantity quantity,
      @Nullable final String optionalData) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.date = date;
    this.quantity = quantity;
    this.optionalData = optionalData;
  }

  UpdatePixelImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date,
      @NotNull final pixela.client.Quantity quantity) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.date = date;
    this.quantity = quantity;
    this.optionalData = null;
  }

  @NotNull
  public String getQuantity() {
    return quantity.asString();
  }

  @Nullable
  public String getOptionalData() {
    return optionalData;
  }

  @NotNull
  @Override
  public Mono<pixela.client.Pixel> call() {
    final Response<Void> response = httpClient.put(this);
    return response
        .toPublisher()
        .<pixela.client.Pixel>then(
            Mono.defer(() -> Mono.just(new PixelImpl(httpClient, pixela, graph, date, this))))
        .cache();
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String uri =
        pixela.usersUri(baseUrl).toASCIIString()
            + graph.subPath()
            + '/'
            + date.format(Graph.PIXEL_DATE_FORMAT);
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
    return "PUT "
        + pixela.usersUri()
        + graph.subPath()
        + '/'
        + date.format(Graph.PIXEL_DATE_FORMAT)
        + '\n'
        + "  quantity: "
        + '\n'
        + quantity
        + "  optionalData: "
        + optionalData;
  }

  @NotNull
  @Override
  public PixelDetail increment() {
    return new UpdatePixelImpl(httpClient, pixela, graph, date, quantity.increment(), optionalData);
  }

  @NotNull
  @Override
  public PixelDetail decrement() {
    return new UpdatePixelImpl(httpClient, pixela, graph, date, quantity.decrement(), optionalData);
  }

  @NotNull
  @Override
  public String quantity() {
    return quantity.asString();
  }

  @Nullable
  @Override
  public String optionalDataString() {
    return optionalData;
  }

  @Override
  public String toString() {
    return errorRequest();
  }

  @NotNull
  @Override
  public UpdatePixel optionalDataString(@NotNull final String optionalData) {
    return new UpdatePixelImpl(httpClient, pixela, graph, date, quantity, optionalData);
  }

  @NotNull
  @Override
  public Mono<UpdatePixel> optionalData(@NotNull final Object object) {
    final JsonEncoder encoder = httpClient.encoder();
    return encoder
        .encodeObject(object)
        .map(opd -> new UpdatePixelImpl(httpClient, pixela, graph, date, quantity, opd));
  }
}
