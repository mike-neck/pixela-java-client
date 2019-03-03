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
package pixela.client.http;

import java.net.URI;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import pixela.client.ApiException;
import pixela.client.http.json.JsonDecoder;
import pixela.client.http.json.JsonEncoder;
import reactor.core.publisher.Mono;

public interface HttpClient extends AutoCloseable {

  /**
   * encode json.
   *
   * @param object - object to be encoded into json.
   * @return - {@link Mono} wrapping json string.
   * @deprecated use {@link JsonEncoder} from {@link HttpClient#encoder()} method.
   */
  @Deprecated
  @NotNull
  Mono<String> encodeJson(@NotNull Object object);

  @NotNull
  JsonEncoder encoder();

  /**
   * decoding json.
   *
   * @param json - json string.
   * @param type - the type of object.
   * @param <T> - the type of object.
   * @return {@link Mono} instance which may hold decoded object.
   * @deprecated use {@link JsonDecoder} from {@link HttpClient#decoder()} method.
   */
  @Deprecated
  @NotNull
  <T> Mono<T> decodeJson(@NotNull String json, @NotNull final Class<T> type);

  @NotNull
  JsonDecoder decoder();

  @NotNull
  default <T> Mono<T> readResponse(
      @NotNull final Request<T> request, @NotNull final Mono<HttpResponse> response) {
    return response
        .flatMap(res -> ResponseReader.create(request, decoder()).read(res))
        .onErrorMap(ApiException.class, e -> e.appendDebugInfo(request))
        .cache();
  }

  @NotNull
  <T> Mono<T> runAsync(@NotNull final Supplier<? extends T> supplier);

  @NotNull
  URI baseUri();

  @NotNull
  default <T> Mono<T> get(@NotNull final Get<T> request) {
    final Mono<HttpResponse> response = runGet(request);
    return readResponse(request, response);
  }

  @NotNull
  <T> Mono<HttpResponse> runGet(@NotNull final Get<T> getRequest);

  @NotNull
  default <T> Mono<T> post(@NotNull final Post<T> request) {
    final Mono<HttpResponse> response = runPost(request);
    return readResponse(request, response);
  }

  @NotNull
  <T> Mono<HttpResponse> runPost(@NotNull final Post<T> postRequest);

  @NotNull
  default <T> Mono<T> put(@NotNull final Put<T> request) {
    final Mono<HttpResponse> response = runPut(request);
    return readResponse(request, response);
  }

  @NotNull
  <T> Mono<HttpResponse> runPut(@NotNull final Put<T> putRequest);

  @NotNull
  default <T> Mono<T> delete(@NotNull final Delete<T> request) {
    final Mono<HttpResponse> response = runDelete(request);
    return readResponse(request, response);
  }

  @NotNull
  <T> Mono<HttpResponse> runDelete(@NotNull final Delete<T> deleteRequest);
}
