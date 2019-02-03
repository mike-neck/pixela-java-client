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
import pixela.client.*;
import pixela.client.api.graph.SimpleGraph;
import pixela.client.http.HttpClient;

public class WebhookRaw {

  private String webhookHash;
  private String graphID;
  private String type;

  public WebhookRaw() {}

  private WebhookRaw(
      @NotNull final String webhookHash,
      @NotNull final String graphID,
      @NotNull final String type) {
    this.webhookHash = webhookHash;
    this.graphID = graphID;
    this.type = type;
  }

  @NotNull
  @Contract("_, _, _ -> new")
  public static WebhookRaw of(
      @NotNull final String webhookHash,
      @NotNull final String graphID,
      @NotNull final String type) {
    return new WebhookRaw(webhookHash, graphID, type);
  }

  Webhook toWebhook(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    final SimpleGraph graph = SimpleGraph.of(httpClient, pixela, GraphId.of(graphID));
    return WebhookImpl.of(httpClient, graph, WebhookHash.of(webhookHash), WebhookType.of(type));
  }

  public void setWebhookHash(final String webhookHash) {
    this.webhookHash = webhookHash;
  }

  public void setGraphID(final String graphID) {
    this.graphID = graphID;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("WebhookRaw{");
    sb.append("webhookHash='").append(webhookHash).append('\'');
    sb.append(", graphID='").append(graphID).append('\'');
    sb.append(", type='").append(type).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Contract(value = "null -> false", pure = true)
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (!(object instanceof WebhookRaw)) return false;

    final WebhookRaw that = (WebhookRaw) object;

    if (!webhookHash.equals(that.webhookHash)) return false;
    if (!graphID.equals(that.graphID)) return false;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    int result = webhookHash.hashCode();
    result = 31 * result + graphID.hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }
}
