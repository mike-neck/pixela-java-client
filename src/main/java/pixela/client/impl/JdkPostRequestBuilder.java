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
package pixela.client.impl;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.Post;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;

class JdkPostRequestBuilder {

  @NotNull private final JsonEncoder encoder;
  @NotNull private final RequestConfigurer configurer;

  private JdkPostRequestBuilder(@NotNull final URI baseUri, @NotNull final JsonEncoder encoder) {
    this(encoder, new ReqConfigurer(baseUri));
  }

  JdkPostRequestBuilder(
      @NotNull final JsonEncoder encoder, @NotNull final RequestConfigurer configurer) {
    this.configurer = configurer;
    this.encoder = encoder;
  }

  @NotNull
  static JdkPostRequestBuilder of(@NotNull final URI baseUri, @NotNull final JsonEncoder encoder) {
    return new JdkPostRequestBuilder(baseUri, encoder);
  }

  @NotNull
  Mono<HttpRequest> apply(@NotNull final Post<?> post) {
    return encoder
        .encode(post)
        .map(payload -> HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
        .switchIfEmpty(Mono.just(HttpRequest.BodyPublishers.noBody()))
        .map(pub -> configurer.configureRequest(post, pub));
  }

  static class ReqConfigurer implements RequestConfigurer {

    @NotNull final URI baseUri;

    ReqConfigurer(@NotNull final URI baseUri) {
      this.baseUri = baseUri;
    }

    @NotNull
    @Override
    public HttpRequest configureRequest(
        @NotNull final Request<?> request, @NotNull final HttpRequest.BodyPublisher bodyPublisher) {
      final URI endpoint = request.apiEndpoint(baseUri);
      final HttpRequest.Builder builder = HttpRequest.newBuilder(endpoint).POST(bodyPublisher);
      return Stream.of(UserTokenHeader.of(request), ContentTypeHeader.of(request))
          .reduce(builder, (b, h) -> h.configure(b), (l, r) -> l)
          .build();
    }
  }
}
