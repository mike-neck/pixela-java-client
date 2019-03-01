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
package pixela.client.impl;

import static org.mockito.Mockito.mock;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import pixela.client.UserToken;
import pixela.client.http.Put;
import pixela.client.http.Request;
import pixela.client.http.json.JsonEncoder;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JdkPutRequestBuilderTest {

  private static final ExecutorService executor = Executors.newSingleThreadExecutor();

  @AfterAll
  static void closeTest() {
    executor.shutdown();
  }

  private final JsonEncoder encoder = new JsonCodecFactoryImpl().create(executor);

  private static RequestConfigurer requestConfigurer(
      final Consumer<Flux<ByteBuffer>> stepVerifier) {
    return (request, bodyPublisher) -> {
      final Flux<ByteBuffer> flux = JdkFlowAdapter.flowPublisherToFlux(bodyPublisher);
      stepVerifier.accept(flux);
      return mock(HttpRequest.class);
    };
  }

  @Test
  void noBodyRequest() {
    final PutReq request = new PutReq("abc123", Request.WithBody.FALSE);
    final RequestConfigurer requestConfigurer =
        requestConfigurer(flux -> StepVerifier.create(flux).expectComplete().verify());

    final JdkPutRequestBuilder builder = new JdkPutRequestBuilder(encoder, requestConfigurer);

    final Mono<HttpRequest> mono = builder.apply(request);
    StepVerifier.create(mono).expectNextCount(1L).verifyComplete();
  }

  @Test
  void withBodyRequest() {
    final PutReq request = new PutReq("abc123", Request.WithBody.TRUE);
    final RequestConfigurer requestConfigurer =
        requestConfigurer(
            flux -> {
              final Mono<String> mono =
                  flux.buffer()
                      .next()
                      .map(JdkPostRequestBuilderTest::concatBuffer)
                      .map(buffer -> new String(buffer.array(), StandardCharsets.UTF_8));
              StepVerifier.create(mono).expectNext("{\"value\":\"value\"}").verifyComplete();
            });

    final JdkPutRequestBuilder builder = new JdkPutRequestBuilder(encoder, requestConfigurer);

    final Mono<HttpRequest> mono = builder.apply(request);
    StepVerifier.create(mono).expectNextCount(1L).verifyComplete();
  }

  static class PutReq implements Put<Void> {

    @NotNull private final String token;
    @NotNull private final WithBody withBody;

    PutReq(@NotNull final String token, @NotNull final WithBody withBody) {
      this.token = token;
      this.withBody = withBody;
    }

    public String getValue() {
      return "value";
    }

    @NotNull
    @Override
    public URI apiEndpoint(@NotNull final URI baseUrl) {
      return baseUrl.resolve("/api/test");
    }

    @NotNull
    @Override
    public Optional<UserToken> userToken() {
      return Optional.of(UserToken.of(token));
    }

    @NotNull
    @Override
    public WithBody withBody() {
      return withBody;
    }

    @NotNull
    @Override
    public Class<? extends Void> responseType() {
      return Void.class;
    }

    @NotNull
    @Override
    public String errorRequest() {
      return "request";
    }
  }
}
