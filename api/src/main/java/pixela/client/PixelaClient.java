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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import pixela.client.api.user.CreateUser;
import pixela.client.api.user.PixelaImpl;
import pixela.client.http.HttpClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public class PixelaClient implements AutoCloseable, Disposable {

  @NotNull private final HttpClient httpClient;

  private PixelaClient(@NotNull final HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @NotNull
  static PixelaClient using(final HttpClient httpClient) {
    return new PixelaClient(httpClient);
  }

  @NotNull
  public CreateUser.Token createUser() {
    return CreateUser.builder(httpClient);
  }

  @NotNull
  public PixelaImpl.PixelaToken username(@NotNull final String username) {
    return token -> PixelaImpl.of(httpClient, UserToken.of(token), Username.of(username));
  }

  @SuppressWarnings("WeakerAccess")
  @NotNull
  public Mono<Pixela> loadFromPropertiesFile() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final URL resource = classLoader.getResource(Pixela.PROPERTIES_FILE);
    if (resource == null) {
      return Mono.empty();
    }
    return httpClient.runAsync(
        () -> {
          try (final Reader reader =
              new InputStreamReader(
                  Objects.requireNonNull(
                      classLoader.getResourceAsStream(Pixela.PROPERTIES_FILE)))) {
            final Properties properties = new Properties();
            properties.load(reader);
            return PixelaImpl.fromProperties(httpClient, properties);
          } catch (final IOException e) {
            throw new UncheckedIOException(
                "there is not properties file[" + Pixela.PROPERTIES_FILE + "] in system resource.",
                e);
          }
        });
  }

  @Override
  public void close() throws Exception {
    httpClient.close();
  }

  @Override
  public void dispose() {
    try {
      this.close();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
