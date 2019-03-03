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
import pixela.client.BasicResponse;
import pixela.client.http.json.JsonDecoder;
import reactor.core.publisher.Mono;

class VoidResponseReader<T> implements HttpResponseReader<T> {

  @NotNull private final Request<T> request;
  @NotNull private final JsonDecoder decoder;

  VoidResponseReader(@NotNull final Request<T> request, @NotNull final JsonDecoder decoder) {
    this.request = request;
    this.decoder = decoder;
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull Class<T> responseType() {
    return (Class<T>) Void.class;
  }

  @Override
  public boolean matchCondition(@NotNull final HttpResponse response) {
    return Void.class.equals(request.responseType());
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull Mono<T> read(@NotNull final HttpResponse response) {
    final String body = response.body();
    final Mono<BasicResponse> mono = decoder.decode(body, BasicResponse.class);
    return mono.flatMap(res -> (Mono<T>) res.emptyOrError());
  }
}
