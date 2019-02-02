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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Graph;
import pixela.client.Webhook;
import pixela.client.WebhookHash;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;

public class WebhookImpl implements Webhook {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Graph graph;
  @NotNull private final WebhookHash webhookHash;

  private WebhookImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Graph graph,
      @NotNull final WebhookHash webhookHash) {
    this.httpClient = httpClient;
    this.graph = graph;
    this.webhookHash = webhookHash;
  }

  @NotNull
  @Contract("_, _, _ -> new")
  static WebhookImpl of(
      @NotNull final HttpClient httpClient,
      @NotNull final Graph graph,
      @NotNull final WebhookHash webhookHash) {
    return new WebhookImpl(httpClient, graph, webhookHash);
  }

  @NotNull
  @Override
  public Graph targetGraph() {
    return graph;
  }

  @NotNull
  WebhookHash webhookHash() {
    return webhookHash;
  }

  @NotNull
  @Override
  public Mono<Webhook> invoke() {
    return Mono.error(UnsupportedOperationException::new);
  }
}
