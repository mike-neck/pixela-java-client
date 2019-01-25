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

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import org.jetbrains.annotations.NotNull;
import pixela.client.PixelaClientConfig;
import reactor.core.publisher.Mono;

class JdkHttpClient {

  @NotNull private final HttpClient httpClient;
  @NotNull private final JsonDecoder decoder;

  private JdkHttpClient(@NotNull final HttpClient httpClient, @NotNull final JsonDecoder decoder) {
    this.httpClient = httpClient;
    this.decoder = decoder;
  }

  @NotNull
  static JdkHttpClient create(
      @NotNull final ExecutorService executorService,
      @NotNull final JsonDecoder decoder,
      @NotNull final PixelaClientConfig config) {
    final HttpClient httpClient =
        HttpClient.newBuilder()
            .executor(executorService)
            .connectTimeout(Duration.ofMillis(config.getTimeout()))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .version(HttpClient.Version.HTTP_2)
            .build();
    return create(httpClient, decoder);
  }

  @NotNull
  <T> Mono<T> decodeJson(@NotNull final String json, @NotNull final Class<T> type) {
    return decoder.decode(json, type);
  }

  @NotNull
  static JdkHttpClient create(
      @NotNull final HttpClient httpClient, @NotNull final JsonDecoder decoder) {
    return new JdkHttpClient(httpClient, decoder);
  }

  @NotNull
  Mono<JdkHttpResponse> sendRequest(@NotNull final HttpRequest request) {
    final Mono<HttpResponse<String>> response =
        Mono.fromFuture(
            httpClient.sendAsync(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
    return response.map(res -> JdkHttpResponse.create(res, decoder));
  }
}
