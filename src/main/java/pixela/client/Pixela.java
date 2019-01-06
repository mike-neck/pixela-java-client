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
import java.nio.file.Path;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import pixela.client.api.graph.CreateGraph;
import pixela.client.api.user.DeleteUser;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

public interface Pixela {

  URI usersUri(final URI baseUri);

  String usersUri();

  UserToken token();

  Mono<Pixela> usingClient(HttpClient client);

  Mono<Void> persistAsFile(final Path file);

  DeleteUser deleteUser();

  CreateGraph.Id createGraph();

  PixelaClientConfig DEFAULT_CONFIG = PixelaClientConfig.builder().build();

  @NotNull
  static PixelaClient withDefaultJavaClient() {
    return withDefaultJavaClient(DEFAULT_CONFIG);
  }

  @NotNull
  static PixelaClient withDefaultJavaClient(@NotNull final PixelaClientConfig config) {
    Objects.requireNonNull(config);
    final ClientLoader clientLoader = ClientLoader.create(config);
    final HttpClient httpClient = clientLoader.load();
    return PixelaClient.using(httpClient);
  }

  @NotNull
  static PixelaClient usingHttpClient(@NotNull final HttpClient httpClient) {
    Objects.requireNonNull(httpClient);
    return PixelaClient.using(httpClient);
  }
}
