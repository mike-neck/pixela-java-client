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
import reactor.core.publisher.Mono;

class StringResponseReader<T> implements HttpResponseReader<T> {

  @NotNull private final Request<T> request;

  StringResponseReader(@NotNull final Request<T> request) {
    this.request = request;
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull Class<T> responseType() {
    return (Class<T>) String.class;
  }

  @Override
  public boolean matchCondition(@NotNull final HttpResponse response) {
    return String.class.equals(request.responseType());
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull Mono<T> read(@NotNull final HttpResponse response) {
    final String body = response.body();
    return Mono.just((T) body);
  }
}
