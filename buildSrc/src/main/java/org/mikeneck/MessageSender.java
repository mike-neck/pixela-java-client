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
package org.mikeneck;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

class MessageSender {

  private final Codec codec;

  private final URI destination;
  private final Object payload;

  MessageSender(final Codec codec, final URI destination, final Object payload) {
    this.codec = codec;
    this.destination = destination;
    this.payload = payload;
  }

  Optional<SlackException> sendMessage() {
    final String json = codec.encode(payload);
    final HttpClient httpClient = HttpClient.newHttpClient();
    final HttpRequest postRequest =
        HttpRequest.newBuilder(destination)
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .header("content-type", "application/json")
            .build();
    try {
      final HttpResponse<String> response =
          httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      if (response.statusCode() == 200) {
        return Optional.empty();
      }
      final SlackException throwable = new SlackException(response.body());
      return Optional.of(throwable);
    } catch (final IOException | InterruptedException e) {
      final SlackException throwable = new SlackException("failed to send message.", e);
      return Optional.of(throwable);
    }
  }
}
