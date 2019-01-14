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
import pixela.client.http.Get;
import reactor.core.publisher.Mono;

class JdkGetRequestBuilder implements RequestBuilder<Get<?>> {

  @NotNull private final URI baseUri;

  private JdkGetRequestBuilder(@NotNull final URI baseUri) {
    this.baseUri = baseUri;
  }

  static JdkGetRequestBuilder of(@NotNull final URI baseUri) {
    return new JdkGetRequestBuilder(baseUri);
  }

  @Override
  @NotNull
  public Mono<HttpRequest> apply(@NotNull final Get<?> get) {
    final URI uri = get.apiEndpoint(baseUri);
    final HttpRequest.Builder builder = HttpRequest.newBuilder(uri).GET();
    final HttpRequest request = UserTokenHeader.of(get).configure(builder).build();
    return Mono.just(request);
  }
}
