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
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface HttpClient extends AutoCloseable {

  @NotNull
  Mono<String> encodeJson(@NotNull Object object);

  @NotNull
  <T> Mono<T> decodeJson(@NotNull String json, @NotNull final Class<T> type);

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
