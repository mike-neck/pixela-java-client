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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pixela.client.Pixela;
import pixela.client.Webhook;
import pixela.client.http.HttpClient;

public class Webhooks implements Iterable<WebhookRaw> {

  private static final List<WebhookRaw> EMPTY = Collections.emptyList();

  @NotNull private List<WebhookRaw> webhooks = EMPTY;

  public Webhooks() {}

  private Webhooks(@NotNull final List<WebhookRaw> webhooks) {
    this.webhooks = webhooks;
  }

  @NotNull
  @Contract("_ -> new")
  public static Webhooks create(@NotNull final List<WebhookRaw> webhooks) {
    return new Webhooks(webhooks);
  }

  @NotNull
  public static Webhooks of(@NotNull final WebhookRaw... webhooks) {
    return create(Arrays.asList(webhooks));
  }

  List<Webhook> toList(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    final List<Webhook> list =
        webhooks
            .stream()
            .map(raw -> raw.toWebhook(httpClient, pixela))
            .collect(Collectors.toList());
    return Collections.unmodifiableList(list);
  }

  public void setWebhooks(@Nullable final List<WebhookRaw> webhooks) {
    if (webhooks != null) {
      this.webhooks = webhooks;
    } else {
      this.webhooks = EMPTY;
    }
  }

  @NotNull
  @Override
  public Iterator<WebhookRaw> iterator() {
    return webhooks.iterator();
  }

  @Contract(value = "null -> false", pure = true)
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (!(object instanceof Webhooks)) return false;

    final Webhooks webhooks1 = (Webhooks) object;

    return webhooks.equals(webhooks1.webhooks);
  }

  @Override
  public int hashCode() {
    return webhooks.hashCode();
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Webhooks{");
    sb.append("webhooks=").append(webhooks);
    sb.append('}');
    return sb.toString();
  }
}
