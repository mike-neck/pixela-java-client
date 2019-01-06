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
package pixela.client.api.user;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.Username;
import pixela.client.api.graph.CreateGraph;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

class NewPixela implements Pixela {

  private static final String USERS_PATH = "/v1/users";

  @NotNull private final HttpClient httpClient;
  @NotNull private final UserToken userToken;
  @NotNull private final Username username;

  private NewPixela(
      @NotNull final HttpClient httpClient,
      @NotNull final UserToken userToken,
      @NotNull final Username username) {
    this.httpClient = httpClient;
    this.userToken = userToken;
    this.username = username;
  }

  static NewPixela of(
      final HttpClient httpClient, final UserToken userToken, final Username username) {
    return new NewPixela(httpClient, userToken, username);
  }

  @NotNull
  @Override
  public URI usersUri(@NotNull final URI baseUri) {
    final String string = baseUri.toASCIIString() + USERS_PATH + username.path();
    return URI.create(string);
  }

  @Override
  public String usersUri() {
    return USERS_PATH + username.path();
  }

  @NotNull
  @Override
  public UserToken token() {
    return userToken;
  }

  @Override
  public Mono<Pixela> usingClient(final HttpClient client) {
    return Mono.fromCallable(
        () -> {
          try (httpClient) {
            return of(client, userToken, username);
          }
        });
  }

  @Override
  public Mono<Void> persistAsFile(final Path file) {
    return Mono.<Void>fromRunnable(
            () -> {
              final Properties properties = new Properties(2);
              properties.setProperty(userToken.tokenName(), userToken.tokenValue());
              properties.setProperty(Username.USER_NAME_PROPERTY_KEY, username.value());
              try (final Writer writer =
                  Files.newBufferedWriter(
                      file, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
                properties.store(writer, "Generated by " + NewPixela.class.getCanonicalName());
              } catch (final IOException e) {
                throw new UncheckedIOException(e);
              }
            })
        .subscribeOn(Schedulers.elastic());
  }

  @Override
  public DeleteUser deleteUser() {
    return DeleteUser.of(httpClient, userToken, username);
  }

  @Override
  public CreateGraph.Id createGraph() {
    return CreateGraph.builder(httpClient, this);
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Pixela{");
    sb.append("userToken=").append(userToken);
    sb.append(", username=").append(username);
    sb.append('}');
    return sb.toString();
  }
}
