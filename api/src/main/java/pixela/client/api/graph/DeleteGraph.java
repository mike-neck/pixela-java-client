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
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import pixela.client.*;
import pixela.client.http.Delete;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

public class DeleteGraph implements Delete<Void>, Api<Pixela> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final GraphId graphId;

  DeleteGraph(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final GraphId graphId) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graphId = graphId;
  }

  @NotNull
  @Override
  public Mono<Pixela> call() {
    final Mono<Void> response = httpClient.delete(this);
    return response.then(Mono.just(pixela));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String uri = pixela.usersUri(baseUrl).toASCIIString() + Graph.PATH + graphId.path();
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
    return "DELETE " + pixela.usersUri() + Graph.PATH + graphId.path();
  }

  @NotNull
  @Override
  public String toString() {
    return errorRequest();
  }
}
