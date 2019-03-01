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
package pixela.client.impl;

import static pixela.client.impl.Exceptions.asFunction;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.json.JsonCodec;
import reactor.core.publisher.Mono;

public class JsonCodecImpl implements JsonCodec {
  @NotNull private final ExecutorService executorService;
  @NotNull private final ObjectMapper objectMapper;

  JsonCodecImpl(
      @NotNull final ExecutorService executorService, @NotNull final ObjectMapper objectMapper) {
    this.executorService = executorService;
    this.objectMapper = objectMapper;
  }

  @NotNull
  @Override
  public <T> Mono<T> decode(@NotNull final String json, @NotNull final Class<? extends T> type) {
    final CompletableFuture<T> future =
        CompletableFuture.supplyAsync(() -> decodeSync(json, type), executorService);
    return Mono.create(
        sink -> future.thenAccept(sink::success).exceptionally(asFunction(sink::error)));
  }

  <T> T decodeSync(@NotNull final String json, @NotNull final Class<? extends T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  @Override
  public Mono<String> encodeObject(@NotNull final Object object) {
    final CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> encodeSync(object), executorService);
    return Mono.create(
        sink -> future.thenAccept(sink::success).exceptionally(asFunction(sink::error)));
  }

  String encodeSync(@NotNull final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
