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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.json.JsonDecoder;
import reactor.core.publisher.Mono;

class ResponseReader<T> {

  @NotNull private final Function<HttpResponse, Mono<T>> defaultReader;

  @NotNull private final List<HttpResponseReader<T>> readers;

  private ResponseReader(
      @NotNull final Function<HttpResponse, Mono<T>> defaultReader,
      @NotNull final List<HttpResponseReader<T>> readers) {
    this.defaultReader = defaultReader;
    this.readers = readers;
  }

  @Contract("_, _ -> new")
  @NotNull
  static <T> ResponseReader<T> create(final Request<T> request, final JsonDecoder decoder) {
    return new ResponseReader<>(
        response -> decoder.decode(response.body(), request.responseType()),
        Arrays.asList(
            new ErrorResponseReader<>(request, decoder),
            new VoidResponseReader<>(request, decoder),
            new StringResponseReader<>(request)));
  }

  Mono<T> read(@NotNull final HttpResponse response) {
    return readers
        .stream()
        .map(reader -> reader.readResponse(response))
        .filter(Optional::isPresent)
        .findFirst()
        .map(Optional::get)
        .orElseGet(() -> defaultReader.apply(response));
  }
}
