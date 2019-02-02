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
import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.*;
import pixela.client.http.HttpClient;
import pixela.client.http.Post;
import pixela.client.http.Response;
import reactor.core.publisher.Mono;

public class CreateWebhook implements Post<CreateWebhookResult>, Api<Webhook> {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;
  @NotNull private final WebhookType webhookType;

  private CreateWebhook(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final WebhookType webhookType) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.webhookType = webhookType;
  }

  @NotNull
  @Contract("_, _, _, _ -> new")
  public static CreateWebhook of(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final WebhookType webhookType) {
    return new CreateWebhook(httpClient, pixela, graph, webhookType);
  }

  @SuppressWarnings("WeakerAccess")
  public String getGraphID() {
    return graph.id();
  }

  public String getType() {
    return webhookType.asString();
  }

  @NotNull
  @Override
  public Mono<Webhook> call() {
    final Response<CreateWebhookResult> response = httpClient.post(this);
    return response
        .toPublisher()
        .flatMap(result -> result.webhookHash(this))
        .map(WebhookHash::of)
        .map(hash -> WebhookImpl.of(httpClient, graph, hash));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String userUri = pixela.usersUri(baseUrl).toASCIIString();
    return URI.create(userUri + "/webhooks");
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
  public Class<? extends CreateWebhookResult> responseType() {
    return CreateWebhookResult.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "POST "
        + pixela.usersUri()
        + "/webhooks"
        + "\n"
        + "graphID: "
        + getGraphID()
        + "\n"
        + "type: "
        + getType();
  }
}
