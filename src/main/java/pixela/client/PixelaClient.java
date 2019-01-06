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

import org.jetbrains.annotations.NotNull;
import pixela.client.api.user.CreateUser;
import pixela.client.http.HttpClient;
import reactor.core.Disposable;

public class PixelaClient implements AutoCloseable, Disposable {

  @NotNull private final HttpClient httpClient;

  private PixelaClient(@NotNull final HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @NotNull
  static PixelaClient using(final HttpClient httpClient) {
    return new PixelaClient(httpClient);
  }

  @SuppressWarnings("WeakerAccess")
  @NotNull
  public CreateUser.Token createUser() {
    return CreateUser.builder(httpClient);
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
