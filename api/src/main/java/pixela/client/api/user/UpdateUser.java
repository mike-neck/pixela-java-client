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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.http.HttpClient;
import pixela.client.http.Put;
import reactor.core.publisher.Mono;

public class UpdateUser implements Put<Void>, Api<Pixela> {

  @NotNull private final HttpClient httpClient;

  @NotNull private final Pixela pixela;

  @NotNull private final UserToken newToken;

  private UpdateUser(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final UserToken newToken) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.newToken = newToken;
  }

  @NotNull
  @Contract("_, _, _ -> new")
  static UpdateUser of(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final UserToken newToken) {
    return new UpdateUser(httpClient, pixela, newToken);
  }

  @FunctionalInterface
  public interface WithToken {
    @NotNull
    default UpdateUser newToken(@NotNull final String newToken) {
      return newToken(UserToken.of(newToken));
    }

    @NotNull
    UpdateUser newToken(@NotNull final UserToken newToken);
  }

  @NotNull
  public String getNewToken() {
    return newToken.tokenValue();
  }

  @NotNull
  @Override
  public Mono<Pixela> call() {
    final Mono<Void> response = httpClient.put(this);
    return response.then(Mono.fromSupplier(() -> pixela.updateToken(newToken)));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    return pixela.usersUri(baseUrl);
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
    return "PUT " + pixela.usersUri() + '\n' + " newToken: " + newToken.tokenValue();
  }
}
