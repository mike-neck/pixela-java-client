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
import java.time.ZoneId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class CreateGraphTest {

  private PixelaClient pixelaClient;

  @BeforeEach
  void prepare(@NotNull final PixelaClientConfig config, @NotNull final ToJson toJson) {
    pixelaClient = Pixela.withDefaultJavaClient(config);

    stubFor(
        post(urlPathMatching("/v1/users/test-user/graphs"))
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .withRequestBody(matchingJsonPath("$.id", matching("^[a-z][a-z0-9-]{1,16}")))
            .withRequestBody(matchingJsonPath("$.name"))
            .withRequestBody(matchingJsonPath("$.unit"))
            .withRequestBody(matchingJsonPath("$.type", matching("int|float")))
            .withRequestBody(
                matchingJsonPath("$.color", matching("shibafu|momiji|sora|ichou|ajisai|kuro")))
            .withRequestBody(matchingJsonPath("$.timezone", matching("[A-Z][A-Za-z0-9/\\-]+")))
            .willReturn(
                aResponse()
                    .withBody(toJson.apply(NormalResponse.success("Success.")))
                    .withHeader("Content-Type", "application/json")));
  }

  @AfterEach
  void finish() throws Exception {
    pixelaClient.close();
  }

  @PixelaUser(username = "test-user", userToken = "test-token")
  @Test
  void success(@NotNull final Pixela pixela) {
    final Mono<Graph> graphMono =
        pixela
            .createGraph()
            .id("test-graph")
            .name("test-graph-name")
            .unit("times")
            .integer()
            .ajisai()
            .timezone(ZoneId.of("Asia/Tokyo"))
            .call();

    StepVerifier.create(graphMono.log("create-graph"))
        .consumeNextWith(
            graph ->
                assertAll(
                    () -> assertThat(graph.subPath()).isEqualTo("/graphs/test-graph"),
                    () -> assertThat(graph.pixela()).isEqualTo(pixela),
                    () ->
                        assertThat(graph.viewUri().toASCIIString())
                            .isEqualTo(
                                "http://localhost:8000/v1/users/test-user/graphs/test-graph.html")))
        .verifyComplete();
  }
}
