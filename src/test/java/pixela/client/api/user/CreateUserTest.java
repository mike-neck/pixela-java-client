/*
 * Copyright 2018 Shinya Mochida
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pixela.client.Pixela;
import pixela.client.YesNo;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreateUserTest {

  private HttpClient httpClient;

  @BeforeEach
  void setup() {
    this.httpClient = mock(HttpClient.class);
  }

  @Test
  void callTest_errorThenError() {
    final CreateUser createUser =
        new CreateUser(httpClient, "token", "username", YesNo.YES, YesNo.YES);
    when(httpClient.post(createUser)).thenReturn(() -> Mono.error(new IOException("IOException")));
    final Mono<Pixela> pixelaMono = createUser.call();
    StepVerifier.create(pixelaMono).expectErrorMessage("IOException").verify();
  }

  @SuppressWarnings("NullableProblems")
  @Test
  void callTest_successThenPixela() {
    final CreateUser createUser =
        new CreateUser(httpClient, "token", "username", YesNo.YES, YesNo.YES);
    when(httpClient.post(createUser)).thenReturn(Mono::empty);
    final Mono<Pixela> pixelaMono = createUser.call();
    StepVerifier.create(pixelaMono)
        .expectNextMatches(pixela -> pixela instanceof NewPixela)
        .verifyComplete();
  }
}
