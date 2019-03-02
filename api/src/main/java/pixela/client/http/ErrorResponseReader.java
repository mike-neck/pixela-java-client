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
package pixela.client.http;

import org.jetbrains.annotations.NotNull;
import pixela.client.ApiException;
import pixela.client.BasicResponse;
import pixela.client.http.json.JsonDecoder;
import reactor.core.publisher.Mono;

class ErrorResponseReader<T> implements HttpResponseReader<T> {

  @NotNull private final Request<T> request;
  @NotNull private final JsonDecoder decoder;

  ErrorResponseReader(@NotNull final Request<T> request, @NotNull final JsonDecoder decoder) {
    this.request = request;
    this.decoder = decoder;
  }

  @Override
  @NotNull
  public Class<T> responseType() {
    return request.responseType();
  }

  @Override
  public boolean matchCondition(@NotNull final HttpResponse response) {
    return response.isErrorResponse();
  }

  @Override
  @NotNull
  public Mono<T> read(@NotNull final HttpResponse response) {
    final String json = response.body();
    final Mono<BasicResponse> mono = decoder.decode(json, BasicResponse.class);
    return mono.map(BasicResponse::getMessage)
        .flatMap(message -> Mono.error(ApiException.of(message)));
  }
}
