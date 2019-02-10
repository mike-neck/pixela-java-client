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
package integration.api.graph;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import integration.NormalResponse;
import integration.PixelaUser;
import integration.ToJson;
import integration.ToJsonProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class UpdateGraphTest {

  @BeforeEach
  void prepare(@NotNull final ToJson toJson) {
    stubFor(
        put("/v1/users/test-user/graphs/test-graph")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .withRequestBody(matchingJsonPath("$.color", equalTo("ajisai")))
            .withRequestBody(matchingJsonPath("$.selfSufficient", equalTo("none")))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(toJson.apply(NormalResponse.success("Success.")))));
    stubFor(
        put("/v1/users/another-user/graphs/another-graph")
            .withHeader("X-USER-TOKEN", equalTo("another-token"))
            .withRequestBody(matchingJsonPath("$.name"))
            .withRequestBody(matchingJsonPath("$.unit"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("content-type", "application/json")
                    .withBody(toJson.apply(NormalResponse.failure("Graph not found.")))));

    stubFor(
        put("/v1/users/url-user/graphs/url-graph")
            .withHeader("X-USER-TOKEN", equalTo("url-token"))
            .withRequestBody(matchingJsonPath("$[?(@.purgeCacheURLs.size() == 0)]"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("content-type", "application/json")
                    .withBody(toJson.apply(NormalResponse.success("Success.")))));
  }

  @PixelaUser(username = "test-user", userToken = "test-token")
  @Test
  void success(@NotNull final Pixela pixela) {
    final Mono<Graph> graphMono =
        pixela.graph(GraphId.of("test-graph")).updateGraph().color().ajisai().none().call();

    StepVerifier.create(graphMono)
        .assertNext(graph -> assertThat(graph.id()).isEqualTo(GraphId.of("test-graph")))
        .verifyComplete();
  }

  @PixelaUser(username = "another-user", userToken = "another-token")
  @Test
  void failure(@NotNull final Pixela pixela) {
    final Mono<Graph> mono =
        pixela
            .graph(GraphId.of("another-graph"))
            .updateGraph()
            .name("new-name")
            .unit("times")
            .call();

    StepVerifier.create(mono)
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("Graph not found.")
                    .hasMessageContaining("name: new-name")
                    .hasMessageContaining("unit: times"))
        .verify();
  }

  @PixelaUser(username = "url-user", userToken = "url-token")
  @Test
  void purgeCacheURLs(@NotNull final Pixela pixela) {
    final Mono<Graph> graphMono =
        pixela.graph(GraphId.of("url-graph")).updateGraph().purgeCacheURLs().call();

    StepVerifier.create(graphMono)
        .assertNext(graph -> assertThat(graph.id()).isEqualTo(GraphId.of("url-graph")))
        .verifyComplete();
  }
}
