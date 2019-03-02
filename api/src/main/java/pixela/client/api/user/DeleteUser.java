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
package pixela.client.api.user;

import java.net.URI;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.UserToken;
import pixela.client.Username;
import pixela.client.http.Delete;
import pixela.client.http.HttpClient;
import pixela.client.http.Response;
import reactor.core.publisher.Mono;

public class DeleteUser implements Delete<Void>, Api<Void> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final UserToken userToken;
  @NotNull private final Username username;

  private DeleteUser(
      @NotNull final HttpClient httpClient,
      @NotNull final UserToken userToken,
      @NotNull final Username username) {
    this.httpClient = httpClient;
    this.userToken = userToken;
    this.username = username;
  }

  public static DeleteUser of(
      @NotNull final HttpClient httpClient,
      @NotNull final UserToken userToken,
      @NotNull final Username username) {
    return new DeleteUser(httpClient, userToken, username);
  }

  @NotNull
  @Override
  public Mono<Void> call() {
    final Response<Void> response = httpClient.delete(this);
    return response.toPublisher();
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    return baseUrl.resolve(endpoint());
  }

  @NotNull
  private String endpoint() {
    return "/v1/users" + username.path();
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.of(userToken);
  }

  @NotNull
  @Override
  public Class<Void> responseType() {
    return Void.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "DELETE " + endpoint();
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("DeleteUser{");
    sb.append("userToken=").append("*****");
    sb.append(", username=").append(username);
    sb.append('}');
    return sb.toString();
  }
}
