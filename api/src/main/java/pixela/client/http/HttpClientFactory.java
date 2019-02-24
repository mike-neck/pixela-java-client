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
import pixela.client.PixelaClientConfig;

public interface HttpClientFactory {

  @NotNull
  HttpClient newClient(@NotNull final PixelaClientConfig pixelaClientConfig);

  static IllegalStateException notFoundImplementation() {
    return new IllegalStateException(
        "An implementation class for " + HttpClientFactory.class + " not found.");
  }
}
