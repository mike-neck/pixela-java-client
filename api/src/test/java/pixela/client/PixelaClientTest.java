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
package pixela.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PixelaClientTest {

  @Test
  void loadTest() {
    final HttpClient httpClient = mock(HttpClient.class);
    when(httpClient.<Pixela>runAsync(any()))
        .then(
            (Answer<Mono<Pixela>>)
                invocation -> {
                  final Supplier<Pixela> supplier = invocation.getArgument(0);
                  return Mono.just(supplier.get());
                });

    final PixelaClient pixelaClient = PixelaClient.using(httpClient);
    final Mono<Pixela> pixelaMono = pixelaClient.loadFromPropertiesFile();

    StepVerifier.create(pixelaMono)
        .assertNext(
            pixela ->
                assertAll(
                    () -> assertThat(pixela.token()).isEqualTo(UserToken.of("TestToken")),
                    () -> assertThat(pixela.usersUri()).isEqualTo("/v1/users/TestUser")))
        .verifyComplete();
  }
}
