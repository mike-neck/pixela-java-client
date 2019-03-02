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

  JsonDecoder decoder();

  @NotNull
  <T> Mono<T> runAsync(@NotNull final Supplier<? extends T> supplier);

  @NotNull
  URI baseUri();

  @NotNull
  <T> Response<T> get(@NotNull final Get<T> getRequest);

  @NotNull
  <T> Response<T> post(@NotNull final Post<T> postRequest);

  @NotNull
  <T> Response<T> put(@NotNull final Put<T> putRequest);

  @NotNull
  <T> Response<T> delete(@NotNull final Delete<T> deleteRequest);
}
