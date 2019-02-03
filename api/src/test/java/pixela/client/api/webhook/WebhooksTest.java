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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.assertj.core.data.Index;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.*;
import pixela.client.http.HttpClient;

@ExtendWith(JacksonProvider.class)
class WebhooksTest {

  private final String json =
      "{\"webhooks\":[{\"webhookHash\":\"hash-1\",\"graphID\":\"graph-1\",\"type\":\"increment\"},"
          + "{\"webhookHash\":\"hash-2\",\"graphID\":\"graph-2\",\"type\":\"decrement\"}]}";

  @Test
  void fromJson(@NotNull final ObjectMapper objectMapper) throws IOException {
    final Webhooks webhooks = objectMapper.readValue(json, Webhooks.class);
    assertThat(webhooks)
        .contains(
            WebhookRaw.of("hash-1", "graph-1", "increment"),
            WebhookRaw.of("hash-2", "graph-2", "decrement"));
  }

  @Test
  void transform(@NotNull final ObjectMapper objectMapper) throws IOException {
    final Webhooks webhooks = objectMapper.readValue(json, Webhooks.class);
    final List<Webhook> list = webhooks.toList(mock(HttpClient.class), mock(Pixela.class));
    assertThat(list)
        .satisfies(
            webhook ->
                assertThat(((WebhookImpl) webhook).webhookHash())
                    .isEqualTo(WebhookHash.of("hash-1")),
            Index.atIndex(0))
        .satisfies(
            webhook ->
                assertThat(((WebhookImpl) webhook).webhookType()).isEqualTo(WebhookType.INCREMENT),
            Index.atIndex(0))
        .satisfies(
            webhook ->
                assertThat(((WebhookImpl) webhook).webhookHash())
                    .isEqualTo(WebhookHash.of("hash-2")),
            Index.atIndex(1))
        .satisfies(
            webhook ->
                assertThat(((WebhookImpl) webhook).webhookType()).isEqualTo(WebhookType.DECREMENT),
            Index.atIndex(1));
  }
}
