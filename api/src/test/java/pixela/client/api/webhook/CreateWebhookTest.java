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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.*;
import pixela.client.api.graph.SimpleGraph;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreateWebhookTest {

  private HttpClient httpClient;
  private Pixela pixela;
  private Graph graph;

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
          .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @BeforeEach
  void setup() {
    this.httpClient = mock(HttpClient.class);
    this.pixela = mock(Pixela.class);
    this.graph = SimpleGraph.of(httpClient, pixela, GraphId.of("test-graph"));
  }

  @Test
  void endpoint() {
    final CreateWebhook createWebhook =
        CreateWebhook.of(httpClient, pixela, graph, WebhookType.DECREMENT);
    final URI baseUri = URI.create("https://example.com");
    when(pixela.usersUri(baseUri)).thenReturn(baseUri.resolve("/v1/users/test-user"));

    final URI endpoint = createWebhook.apiEndpoint(baseUri);

    assertThat(endpoint)
        .hasNoQuery()
        .hasHost("example.com")
        .hasScheme("https")
        .hasPath("/v1/users/test-user/webhooks");
  }

  @Test
  void userToken() {
    final CreateWebhook createWebhook =
        CreateWebhook.of(httpClient, pixela, graph, WebhookType.DECREMENT);
    when(pixela.token()).thenReturn(UserToken.of("test-token"));

    final Optional<UserToken> userToken = createWebhook.userToken();

    assertThat(userToken).hasValue(UserToken.of("test-token"));
  }

  @Test
  void jsonFields() throws IOException {
    final CreateWebhook createWebhook =
        CreateWebhook.of(httpClient, pixela, graph, WebhookType.INCREMENT);
    final String json = objectMapper.writeValueAsString(createWebhook);
    assertAll(
        () -> assertThatJson(json).node("graphID").isString().isEqualTo("test-graph"),
        () -> assertThatJson(json).node("type").isString().isEqualTo("increment"));
  }

  @Nested
  class CallTest {

    @Test
    void success() {
      final CreateWebhook createWebhook =
          CreateWebhook.of(httpClient, pixela, graph, WebhookType.DECREMENT);
      when(httpClient.post(createWebhook))
          .thenReturn(() -> Mono.just(CreateWebhookResult.success("test-hash", "Success.")));

      final Mono<Webhook> mono = createWebhook.call();

      StepVerifier.create(mono)
          .consumeNextWith(
              webhook ->
                  assertAll(
                      () -> assertThat(webhook.targetGraph()).isEqualTo(graph),
                      () ->
                          assertThat(((WebhookImpl) webhook).webhookHash())
                              .isEqualTo(WebhookHash.of("test-hash"))))
          .verifyComplete();
    }

    @Test
    void failure() {
      final CreateWebhook createWebhook =
          CreateWebhook.of(httpClient, pixela, graph, WebhookType.INCREMENT);
      when(httpClient.post(createWebhook))
          .thenReturn(() -> Mono.just(CreateWebhookResult.failure("No such graph id.")));
      when(pixela.usersUri()).thenReturn("/v1/users/test-user");

      final Mono<Webhook> mono = createWebhook.call();

      StepVerifier.create(mono)
          .expectErrorSatisfies(
              e ->
                  assertAll(
                      () -> assertThat(e).hasMessageContaining("POST /v1/users/test-user/webhooks"),
                      () -> assertThat(e).hasMessageContaining("No such graph id."),
                      () ->
                          assertThat(e)
                              .hasMessageContaining("type: increment")
                              .hasMessageContaining("graphID: test-graph")))
          .verify();
    }

    @Test
    void error() {
      final CreateWebhook createWebhook =
          CreateWebhook.of(httpClient, pixela, graph, WebhookType.INCREMENT);
      when(httpClient.post(createWebhook))
          .thenReturn(() -> Mono.error(ApiException.of("error message")));

      final Mono<Webhook> mono = createWebhook.call();

      StepVerifier.create(mono)
          .expectErrorSatisfies(error -> assertThat(error).hasMessage("error message"))
          .verify();
    }
  }
}
