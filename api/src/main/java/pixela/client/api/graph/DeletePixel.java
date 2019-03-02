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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.http.Delete;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

public class DeletePixel implements Delete<Void>, Api<Graph> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;
  @NotNull private final LocalDate date;

  private DeletePixel(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.date = date;
  }

  @Contract("_, _, _, _ -> new")
  @NotNull
  static DeletePixel of(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date) {
    return new DeletePixel(httpClient, pixela, graph, date);
  }

  @NotNull
  @Override
  public Mono<Graph> call() {
    final Mono<Void> response = httpClient.delete(this);
    return response.then(Mono.just(graph));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String users = pixela.usersUri(baseUrl).toASCIIString();
    final String uri = users + graph.subPath() + '/' + date.format(Graph.PIXEL_DATE_FORMAT);
    return URI.create(uri);
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.of(pixela.token());
  }

  @NotNull
  @Override
  public Class<Void> responseType() {
    return Void.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "DELETE "
        + pixela.usersUri()
        + graph.subPath()
        + '/'
        + date.format(Graph.PIXEL_DATE_FORMAT);
  }

  @Override
  public String toString() {
    return errorRequest();
  }
}
