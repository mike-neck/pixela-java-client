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
package integration.api.webhook;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import integration.JsonResolver;
import integration.JsonResource;
import integration.PixelaUser;
import integration.ToJsonProvider;
import java.util.Collections;
import java.util.List;
import org.assertj.core.data.Index;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.*;
import pixela.client.api.webhook.WebhookImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class, JsonResolver.class})
class GetWebhooksTest {

  @JsonResource(name = "webhooks.json")
  @BeforeEach
  void prepare(@NotNull final String webhooksJson) {
    stubFor(
        get("/v1/users/test-user/webhooks")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(webhooksJson)));

    stubFor(
        get("/v1/users/empty-user/webhooks")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"webhooks\":[]}")));

    stubFor(
        get("/v1/users/fail-user/webhooks")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"User not found.\",\"isSuccess\":false}")));
  }

  @PixelaUser(username = "test-user", userToken = "test-token")
  @Test
  void found(@NotNull final Pixela pixela) {
    final Mono<List<Webhook>> webhooks = pixela.getWebhooks().call();
    StepVerifier.create(webhooks)
        .consumeNextWith(
            list ->
                assertThat(list)
                    .hasSize(3)
                    .satisfies(
                        webhook ->
                            assertAll(
                                () ->
                                    assertThat(((WebhookImpl) webhook).webhookHash())
                                        .isEqualTo(WebhookHash.of("hash-1i")),
                                () ->
                                    assertThat(webhook.targetGraph().id())
                                        .isEqualTo(GraphId.of("graph-1"))),
                        Index.atIndex(0))
                    .satisfies(
                        webhook ->
                            assertAll(
                                () ->
                                    assertThat(((WebhookImpl) webhook).webhookHash())
                                        .isEqualTo(WebhookHash.of("hash-1d")),
                                () ->
                                    assertThat(webhook.targetGraph().id())
                                        .isEqualTo(GraphId.of("graph-1"))),
                        Index.atIndex(1))
                    .satisfies(
                        webhook ->
                            assertAll(
                                () ->
                                    assertThat(((WebhookImpl) webhook).webhookHash())
                                        .isEqualTo(WebhookHash.of("hash-2i")),
                                () ->
                                    assertThat(webhook.targetGraph().id())
                                        .isEqualTo(GraphId.of("graph-2"))),
                        Index.atIndex(2)))
        .verifyComplete();
  }

  @PixelaUser(username = "empty-user", userToken = "test-token")
  @Test
  void emptyUser(@NotNull final Pixela pixela) {
    final Mono<List<Webhook>> listMono = pixela.getWebhooks().call();
    StepVerifier.create(listMono).expectNext(Collections.emptyList()).verifyComplete();
  }

  @PixelaUser(username = "fail-user", userToken = "test-token")
  @Test
  void fail(@NotNull final Pixela pixela) {
    final Mono<List<Webhook>> mono = pixela.getWebhooks().call();
    StepVerifier.create(mono)
        .expectErrorSatisfies(
            error ->
                assertThat(error)
                    .hasMessageContaining("User not found")
                    .hasMessageContaining("GET /v1/users/fail-user/webhooks"))
        .verify();
  }
}
