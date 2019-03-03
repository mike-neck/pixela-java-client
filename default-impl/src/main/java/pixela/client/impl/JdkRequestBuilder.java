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
import org.jetbrains.annotations.NotNull;
import pixela.client.http.Delete;
import pixela.client.http.Get;
import pixela.client.http.Post;
import pixela.client.http.Put;
import pixela.client.http.json.JsonEncoder;
import reactor.core.publisher.Mono;

class JdkRequestBuilder {

  @NotNull private final URI baseUri;
  @NotNull private final JsonEncoder encoder;
  @NotNull private final JdkGetRequestBuilder getBuilder;
  @NotNull private final JdkPostRequestBuilder postBuilder;
  @NotNull private final JdkPutRequestBuilder putBuilder;
  @NotNull private final JdkDeleteRequestBuilder deleteBuilder;

  private JdkRequestBuilder(
      @NotNull final URI baseUri,
      @NotNull final JsonEncoder encoder,
      @NotNull final JdkGetRequestBuilder getBuilder,
      @NotNull final JdkPostRequestBuilder postBuilder,
      @NotNull final JdkPutRequestBuilder putBuilder,
      @NotNull final JdkDeleteRequestBuilder deleteBuilder) {
    this.baseUri = baseUri;
    this.encoder = encoder;
    this.getBuilder = getBuilder;
    this.postBuilder = postBuilder;
    this.putBuilder = putBuilder;
    this.deleteBuilder = deleteBuilder;
  }

  static JdkRequestBuilder create(@NotNull final URI baseUri, @NotNull final JsonEncoder encoder) {
    return new JdkRequestBuilder(
        baseUri,
        encoder,
        JdkGetRequestBuilder.of(baseUri),
        JdkPostRequestBuilder.of(baseUri, encoder),
        JdkPutRequestBuilder.of(baseUri, encoder),
        JdkDeleteRequestBuilder.of(baseUri));
  }

  URI baseUri() {
    return baseUri;
  }

  @NotNull
  Mono<String> encodeJson(@NotNull final Object object) {
    return encoder.encodeObject(object);
  }

  @NotNull
  Mono<HttpRequest> get(@NotNull final Get<?> get) {
    return getBuilder.apply(get);
  }

  @NotNull
  Mono<HttpRequest> post(@NotNull final Post<?> post) {
    return postBuilder.apply(post);
  }

  @NotNull
  <T> Mono<HttpRequest> put(@NotNull final Put<T> put) {
    return putBuilder.apply(put);
  }

  @NotNull
  Mono<HttpRequest> delete(@NotNull final Delete<?> delete) {
    return deleteBuilder.apply(delete);
  }
}
