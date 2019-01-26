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
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.*;
import pixela.client.http.Get;
import pixela.client.http.HttpClient;
import pixela.client.http.Response;
import reactor.core.publisher.Mono;

public class GetGraphDefinitions implements Api<List<Graph>>, Get<GraphDefinitions> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;

  private GetGraphDefinitions(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    this.httpClient = httpClient;
    this.pixela = pixela;
  }

  @Contract("_, _ -> new")
  @NotNull
  public static GetGraphDefinitions of(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    return new GetGraphDefinitions(httpClient, pixela);
  }

  @NotNull
  @Override
  public Mono<List<Graph>> call() {
    final Response<GraphDefinitions> response = httpClient.get(this);
    return response
        .toPublisher()
        .map(graphDefinitions -> graphDefinitions.asCollection(httpClient, pixela));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String uri = pixela.usersUri(baseUrl) + Graph.PATH;
    return URI.create(uri);
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.empty();
  }

  @NotNull
  @Override
  public Class<? extends GraphDefinitions> responseType() {
    return GraphDefinitions.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "GET " + pixela.usersUri() + Graph.PATH;
  }

  @Override
  public String toString() {
    return errorRequest();
  }
}
