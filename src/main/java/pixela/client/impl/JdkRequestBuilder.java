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
import pixela.client.http.Post;
import reactor.core.publisher.Mono;

class JdkRequestBuilder {

  @NotNull private final URI baseUri;
  @NotNull private final JdkPostRequestBuilder postBuilder;
  @NotNull private final JdkDeleteRequestBuilder deleteBuilder;

  private JdkRequestBuilder(
      @NotNull final URI baseUri,
      @NotNull final JdkPostRequestBuilder postBuilder,
      @NotNull final JdkDeleteRequestBuilder deleteBuilder) {
    this.baseUri = baseUri;
    this.postBuilder = postBuilder;
    this.deleteBuilder = deleteBuilder;
  }

  static JdkRequestBuilder create(@NotNull final URI baseUri, @NotNull final JsonEncoder encoder) {
    return new JdkRequestBuilder(
        baseUri, JdkPostRequestBuilder.of(baseUri, encoder), JdkDeleteRequestBuilder.of(baseUri));
  }

  URI baseUri() {
    return baseUri;
  }

  @NotNull
  Mono<HttpRequest> post(@NotNull final Post<?> post) {
    return postBuilder.apply(post);
  }

  @NotNull
  Mono<HttpRequest> delete(@NotNull final Delete<?> delete) {
    return deleteBuilder.apply(delete);
  }
}
