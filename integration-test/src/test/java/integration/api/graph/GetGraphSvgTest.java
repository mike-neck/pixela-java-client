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
import static org.junit.jupiter.api.Assertions.assertAll;

import integration.NormalResponse;
import integration.PixelaUser;
import integration.ToJson;
import integration.ToJsonProvider;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.MockPixelaServer;
import pixela.client.Pixela;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class GetGraphSvgTest {

  private static final String svgBody =
      "<svg version=\"1.1\" baseProfile=\"full\" xmlns=\"http://www.w3"
          + ".org/2000/svg\">"
          + "<rect width=\"100%\" height=\"100%\" fill=\"red\"/>"
          + "</svg>";

  @BeforeEach
  void prepare(@NotNull final ToJson toJson) {
    stubFor(
        get(urlEqualTo("/v1/users/test-user/graphs/test-graph"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(svgBody)
                    .withHeader("Content-Type", "image/svg+xml")));

    stubFor(
        get(urlEqualTo("/v1/users/abc123/graphs/test-graph"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(toJson.apply(NormalResponse.failure("Not found")))));
    stubFor(
        get(urlEqualTo("/v1/users/with-query/graphs/test-graph?date=20190102&mode=short"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(svgBody)
                    .withHeader("Content-Type", "image/svg+xml")));
  }

  @PixelaUser(username = "test-user", userToken = "test-token")
  @Test
  void success(@NotNull final Pixela pixela) {
    final Mono<Tuple2<Graph, String>> mono =
        pixela.graph(GraphId.of("test-graph")).getGraphSvg().call();

    StepVerifier.create(mono)
        .consumeNextWith(
            tuple ->
                assertAll(
                    () -> assertThat(tuple.getT2()).isEqualTo(svgBody),
                    () -> assertThat(tuple.getT1().subPath()).isEqualTo("/graphs/test-graph")))
        .verifyComplete();
  }

  @PixelaUser(username = "abc123", userToken = "test-token")
  @Test
  void failure(@NotNull final Pixela pixela) {
    final Mono<Tuple2<Graph, String>> mono =
        pixela.graph(GraphId.of("test-graph")).getGraphSvg().call();
    StepVerifier.create(mono)
        .expectErrorSatisfies(
            error ->
                assertThat(error)
                    .hasMessageContaining("Not found")
                    .hasMessageContaining("GET /v1/users/abc123/graphs/test-graph"))
        .verify();
  }

  @PixelaUser(username = "with-query", userToken = "test-token")
  @Test
  void withQuery(@NotNull final Pixela pixela) {
    final Mono<Tuple2<Graph, String>> mono =
        pixela
            .graph(GraphId.of("test-graph"))
            .getGraphSvg()
            .date(LocalDate.of(2019, 1, 2))
            .shortMode()
            .call();
    StepVerifier.create(mono)
        .consumeNextWith(
            tuple ->
                assertAll(
                    () -> assertThat(tuple.getT2()).isEqualTo(svgBody),
                    () -> assertThat(tuple.getT1().subPath()).isEqualTo("/graphs/test-graph")))
        .verifyComplete();
  }
}
