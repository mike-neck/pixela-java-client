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
package pixela.client.api.webhook;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.Webhook;
import pixela.client.http.Get;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

public class GetWebhooks implements Get<Webhooks>, Api<List<Webhook>> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;

  private GetWebhooks(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    this.httpClient = httpClient;
    this.pixela = pixela;
  }

  @NotNull
  @Contract("_, _ -> new")
  public static GetWebhooks of(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    return new GetWebhooks(httpClient, pixela);
  }

  @NotNull
  @Override
  public Mono<List<Webhook>> call() {
    final Mono<Webhooks> response = httpClient.get(this);
    return response.map(webhooks -> webhooks.toList(httpClient, pixela));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String usersUri = pixela.usersUri(baseUrl).toASCIIString();
    return URI.create(usersUri + "/webhooks");
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.of(pixela.token());
  }

  @NotNull
  @Override
  public Class<Webhooks> responseType() {
    return Webhooks.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "GET " + pixela.usersUri() + "/webhooks";
  }
}
