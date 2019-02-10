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
import static org.junit.jupiter.api.Assertions.assertAll;

import integration.NormalResponse;
import integration.PixelaUser;
import integration.ToJson;
import integration.ToJsonProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pixela.client.ApiException;
import pixela.client.MockPixelaServer;
import pixela.client.Pixela;
import pixela.client.UserToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith({MockPixelaServer.class, ToJsonProvider.class})
class UpdateUserTest {

  @BeforeEach
  void prepare(@NotNull final ToJson toJson) {
    stubFor(
        put("/v1/users/test-user")
            .withHeader("X-USER-TOKEN", equalTo("test-token"))
            .withRequestBody(matchingJsonPath("$.newToken", equalTo("new-token")))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("content-type", "application/json")
                    .withBody(toJson.apply(NormalResponse.success("Success.")))));

    stubFor(
        put("/v1/users/another-user")
            .withHeader("X-USER-TOKEN", equalTo("another-token"))
            .withRequestBody(matchingJsonPath("$.newToken", equalTo("new-token")))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("content-type", "application/json")
                    .withBody(toJson.apply(NormalResponse.failure("User is not existing.")))));
  }

  @PixelaUser(username = "test-user", userToken = "test-token")
  @Test
  void success(@NotNull final Pixela pixela) {
    final Mono<Pixela> pixelaMono = pixela.updateUser().newToken("new-token").call();
    StepVerifier.create(pixelaMono)
        .assertNext(
            px ->
                assertAll(
                    () -> assertThat(px.token()).isEqualTo(UserToken.of("new-token")),
                    () -> assertThat(px.usersUri()).isEqualTo("/v1/users/test-user")))
        .verifyComplete();
  }

  @PixelaUser(username = "another-user", userToken = "another-token")
  @Test
  void failure(@NotNull final Pixela pixela) {
    final Mono<Pixela> pixelaMono = pixela.updateUser().newToken("new-token").call();
    StepVerifier.create(pixelaMono)
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .hasMessageContaining("PUT /v1/users/another-user")
                    .hasMessageContaining("User is not existing.")
                    .isInstanceOf(ApiException.class))
        .verify();
  }
}
