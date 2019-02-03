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

import integration.PixelaUser;
import integration.ToJson;
import integration.ToJsonProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.*;
import pixela.client.api.webhook.WebhookImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class CreateWebhookTest {

  @BeforeEach
  void prepare(@NotNull final ToJson toJson) {
    stubFor(
        post("/v1/users/test-user/webhooks")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .withRequestBody(matchingJsonPath("$.type", equalTo("increment")))
            .withRequestBody(matchingJsonPath("$.graphID", equalTo("test-graph")))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        toJson.apply(Response.success("webhook-increment-hash", "Success.")))));

    stubFor(
        post("/v1/users/another-user/webhooks")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .withRequestBody(matchingJsonPath("$.type", equalTo("decrement")))
            .withRequestBody(matchingJsonPath("$.graphID", equalTo("test-graph")))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        toJson.apply(Response.success("webhook-decrement-hash", "Success.")))));

    stubFor(
        post("/v1/users/failure-user/webhooks")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .withRequestBody(matchingJsonPath("$.type", equalTo("increment")))
            .withRequestBody(matchingJsonPath("$.graphID", equalTo("test-graph")))
            .willReturn(
                notFound()
                    .withBody(toJson.apply(Response.failure("Graph not found.")))
                    .withHeader("Content-Type", "application/json")));
  }

  @PixelaUser(userToken = "test-token", username = "test-user")
  @Test
  void incrementSuccess(@NotNull final Pixela pixela) {
    final Mono<Webhook> response =
        pixela.graph(GraphId.of("test-graph")).createIncrementWebhook().call();
    StepVerifier.create(response)
        .consumeNextWith(
            webhook ->
                assertAll(
                    () -> assertThat(webhook.targetGraph().id()).isEqualTo("test-graph"),
                    () -> assertThat(webhook).isInstanceOf(WebhookImpl.class),
                    () ->
                        assertThat(((WebhookImpl) webhook).webhookHash())
                            .isEqualTo(WebhookHash.of("webhook-increment-hash"))))
        .verifyComplete();
  }

  @PixelaUser(userToken = "test-token", username = "another-user")
  @Test
  void decrementSuccess(@NotNull final Pixela pixela) {

    final Mono<Webhook> response =
        pixela.graph(GraphId.of("test-graph")).createDecrementWebhook().call();
    StepVerifier.create(response)
        .consumeNextWith(
            webhook ->
                assertAll(
                    () -> assertThat(webhook.targetGraph().id()).isEqualTo("test-graph"),
                    () ->
                        assertThat(((WebhookImpl) webhook).webhookHash())
                            .isEqualTo(WebhookHash.of("webhook-decrement-hash"))))
        .verifyComplete();
  }

  @PixelaUser(username = "failure-user", userToken = "test-token")
  @Test
  void failure(@NotNull final Pixela pixela) {
    final Mono<Webhook> response =
        pixela.graph(GraphId.of("test-graph")).createIncrementWebhook().call();
    StepVerifier.create(response)
        .expectErrorSatisfies(
            error ->
                assertAll(
                    () -> assertThat(error).isInstanceOf(ApiException.class),
                    () -> assertThat(error).hasMessageContaining("Graph not found."),
                    () ->
                        assertThat(error)
                            .hasMessageContaining("POST /v1/users/failure-user/webhooks")))
        .verify();
  }

  static class Response {
    private final String webhookHash;
    private final String message;
    private final boolean isSuccess;

    Response(final String webhookHash, final String message, final boolean isSuccess) {
      this.webhookHash = webhookHash;
      this.message = message;
      this.isSuccess = isSuccess;
    }

    @NotNull
    @Contract("_, _ -> new")
    static Response success(@NotNull final String webhookHash, @NotNull final String message) {
      return new Response(webhookHash, message, true);
    }

    @NotNull
    @Contract("_ -> new")
    static Response failure(@NotNull final String message) {
      return new Response(null, message, false);
    }

    public String getWebhookHash() {
      return webhookHash;
    }

    public String getMessage() {
      return message;
    }

    public boolean getIsSuccess() {
      return isSuccess;
    }
  }
}
