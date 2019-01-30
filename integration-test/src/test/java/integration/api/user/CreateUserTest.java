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
package integration.api.user;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import integration.NormalResponse;
import integration.ToJson;
import integration.ToJsonProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.MockPixelaServer;
import pixela.client.Pixela;
import pixela.client.PixelaClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class CreateUserTest {

  @BeforeEach
  void prepare(@NotNull final ToJson toJson) {
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

  @Test
  void success(@NotNull final PixelaClient pixelaClient) {
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
  void failure(@NotNull final PixelaClient pixelaClient) {
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
