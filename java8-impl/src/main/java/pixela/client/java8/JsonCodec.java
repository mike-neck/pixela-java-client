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
package pixela.client.java8;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import pixela.client.ApiException;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;

public interface JsonCodec {

  @NotNull
  <T> Mono<T> decode(@NotNull final String json, @NotNull final Class<? extends T> type);

  @NotNull
  default Mono<String> encode(@NotNull final Request<?> request) {
    if (request.hasBody()) {
      return encodeObject(request);
    } else {
      return Mono.empty();
    }
  }

  @NotNull
  Mono<String> encodeObject(@NotNull final Object object);

  @NotNull
  static JsonCodec ofDefaultJackson() {
    return new JsonCodec() {

      private final ObjectMapper objectMapper =
          new ObjectMapper()
              .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
              .setSerializationInclusion(JsonInclude.Include.NON_NULL);

      @Override
      @NotNull
      public Mono<String> encodeObject(@NotNull final Object object) {
        return Mono.create(
            sink -> {
              try {
                final String json = objectMapper.writeValueAsString(object);
                sink.success(json);
              } catch (final IOException e) {
                final ApiException exception =
                    ApiException.of(
                        String.format(
                            "While encoding json, error occurred. [%s]", object.toString()));
                exception.addSuppressed(e);
                sink.error(exception);
              }
            });
      }

      @Override
      public @NotNull <T> Mono<T> decode(
          @NotNull final String json, @NotNull final Class<? extends T> type) {
        return Mono.create(
            sink -> {
              try {
                final T object = objectMapper.readValue(json, type);
                sink.success(object);
              } catch (final IOException e) {
                final ApiException apiException =
                    ApiException.of("While decoding json, error occurred.");
                apiException.addSuppressed(e);
                sink.error(apiException);
              }
            });
      }
    };
  }
}
