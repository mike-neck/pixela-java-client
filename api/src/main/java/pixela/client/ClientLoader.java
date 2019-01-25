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
package pixela.client;

import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.HttpClient;
import pixela.client.http.HttpClientFactory;

class ClientLoader {

  @NotNull private final PixelaClientConfig config;

  private ClientLoader(@NotNull final PixelaClientConfig config) {
    this.config = config;
  }

  @SuppressWarnings("SameParameterValue")
  static ClientLoader create(@NotNull final PixelaClientConfig config) {
    return new ClientLoader(config);
  }

  @NotNull
  HttpClient load() {
    final ServiceLoader<HttpClientFactory> loader = ServiceLoader.load(HttpClientFactory.class);
    final Spliterator<HttpClientFactory> spliterator = loader.spliterator();
    final HttpClientFactory httpClientFactory =
        StreamSupport.stream(spliterator, false)
            .findFirst()
            .orElseThrow(HttpClientFactory::notFoundImplementation);
    return httpClientFactory.newClient(config);
  }
}
