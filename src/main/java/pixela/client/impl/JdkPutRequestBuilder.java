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

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.Put;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;

class JdkPutRequestBuilder implements RequestBuilder<Put<?>> {

  @NotNull private final JsonEncoder encoder;
  @NotNull private final RequestConfigurer configurer;

  JdkPutRequestBuilder(
      @NotNull final JsonEncoder encoder, @NotNull final RequestConfigurer configurer) {
    this.encoder = encoder;
    this.configurer = configurer;
  }

  @NotNull
  static JdkPutRequestBuilder of(@NotNull final URI uri, @NotNull final JsonEncoder encoder) {
    final RequestConfigurer configurer = new PutRequestConfigurer(uri);
    return new JdkPutRequestBuilder(encoder, configurer);
  }

  @NotNull
  @Override
  public Mono<HttpRequest> apply(@NotNull final Put<?> request) {
    return encoder
        .encode(request)
        .map(payload -> HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
        .switchIfEmpty(Mono.just(HttpRequest.BodyPublishers.noBody()))
        .map(pub -> configurer.configureRequest(request, pub));
  }

  static class PutRequestConfigurer implements RequestConfigurer {

    private final URI baseUri;

    PutRequestConfigurer(final URI baseUri) {
      this.baseUri = baseUri;
    }

    @NotNull
    @Override
    public HttpRequest configureRequest(
        @NotNull final Request<?> request, @NotNull final HttpRequest.BodyPublisher bodyPublisher) {
      final URI endpoint = request.apiEndpoint(baseUri);
      final HttpRequest.Builder builder = HttpRequest.newBuilder(endpoint).PUT(bodyPublisher);
      return Stream.of(UserTokenHeader.of(request), ContentTypeHeader.of(request))
          .reduce(builder, (b, h) -> h.configure(b), (l, r) -> l)
          .build();
    }
  }
}
