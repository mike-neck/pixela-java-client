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

import java.net.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import pixela.client.ApiException;
import pixela.client.BasicResponse;
import pixela.client.http.Request;
import pixela.client.http.json.JsonDecoder;
import reactor.core.publisher.Mono;

class JdkHttpResponse {

  @NotNull private final HttpResponse<String> response;
  @NotNull private final JsonDecoder decoder;

  private JdkHttpResponse(
      @NotNull final HttpResponse<String> response, @NotNull final JsonDecoder decoder) {
    this.response = response;
    this.decoder = decoder;
  }

  static JdkHttpResponse create(
      @NotNull final HttpResponse<String> response, @NotNull final JsonDecoder decoder) {
    return new JdkHttpResponse(response, decoder);
  }

  @SuppressWarnings("unchecked")
  <T> Mono<T> readObject(final Request<T> request) {
    final String json = response.body();
    final int statusCode = response.statusCode();
    final Class<T> responseType = request.responseType();
    if (statusCode / 100 != 2) {
      final Mono<BasicResponse> response = decoder.decode(json, BasicResponse.class);
      return response
          .map(BasicResponse::getMessage)
          .flatMap(message -> Mono.error(ApiException.of(message)));
    } else if (responseType.equals(Void.class)) {
      final Mono<BasicResponse> response = decoder.decode(json, BasicResponse.class);
      return response.flatMap(res -> (Mono<T>) res.emptyOrError());
    } else if (responseType.equals(String.class)) {
      return Mono.just((T) json);
    } else {
      return decoder.decode(json, responseType);
    }
  }

  @TestOnly
  int statusCode() {
    return response.statusCode();
  }
}
