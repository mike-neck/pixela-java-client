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
package pixela.client.api.user;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.*;
import pixela.client.api.NormalResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class CreateUserTest {

  private PixelaClient pixelaClient;

  @BeforeEach
  void prepare(@NotNull final PixelaClientConfig config, @NotNull final ToJson toJson) {
    pixelaClient = Pixela.withDefaultJavaClient(config);

    configureFor(MockPixelaServer.PORT_NUMBER);
    stubFor(
        post("/v1/users")
            .withRequestBody(matchingJsonPath("$.token", matching("[ -~]{8,128}")))
            .withRequestBody(matchingJsonPath("$.username", matching("[a-z][a-z0-9\\-]{1,32}")))
            .withRequestBody(matchingJsonPath("$.agreeTermsOfService", matching("yes")))
            .withRequestBody(matchingJsonPath("$.notMinor", matching("yes")))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(toJson.apply(NormalResponse.success("success.")))
                    .withHeader("Content-Type", "application/json")));
    stubFor(
        post("/v1/users")
            .withRequestBody(matchingJsonPath("$.token", matching("[ -~]{8,128}")))
            .withRequestBody(matchingJsonPath("$.username", matching("[a-z][a-z0-9\\-]{1,32}")))
            .withRequestBody(matchingJsonPath("$.agreeTermsOfService", matching("yes")))
            .withRequestBody(matchingJsonPath("$.notMinor", matching("no")))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withBody(toJson.apply(NormalResponse.failure("failure.")))
                    .withHeader("Content-Type", "application/json")));
  }

  @AfterEach
  void finish() throws Exception {
    pixelaClient.close();
  }

  @Test
  void success() {
    final Mono<Pixela> response =
        pixelaClient
            .createUser()
            .withToken("user-token")
            .username("user-name")
            .agreeTermsOfService()
            .notMinor()
            .call();

    StepVerifier.create(response)
        .expectNextMatches(pixela -> pixela.token().tokenValue().equals("user-token"))
        .verifyComplete();
  }

  @Test
  void failure() {
    final Mono<Pixela> response =
        pixelaClient
            .createUser()
            .withToken("user-token")
            .username("user-name")
            .agreeTermsOfService()
            .minor()
            .call();

    StepVerifier.create(response)
        .expectErrorSatisfies(e -> assertThat(e).hasMessageContaining("failure."))
        .verify();
  }
}
