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
import pixela.client.*;
import pixela.client.http.Get;
import pixela.client.http.HttpClient;
import pixela.client.http.Response;
import reactor.core.publisher.Mono;

public class GetPixel implements Get<PixelRaw>, Api<Pixel> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;
  @NotNull private final LocalDate date;

  GetPixel(
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
  public Mono<Pixel> call() {
    final Response<PixelRaw> response = httpClient.get(this);
    return response.toPublisher().map(raw -> raw.toPixel(httpClient, pixela, graph, date)).cache();
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
  public Class<? extends PixelRaw> responseType() {
    return PixelRaw.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "GET " + graph.subPath() + '/' + date.format(Graph.PIXEL_DATE_FORMAT);
  }

  @Override
  public String toString() {
    return errorRequest();
  }
}
