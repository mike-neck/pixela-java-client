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

import pixela.client.PixelaClientConfig;
import pixela.client.http.HttpClient;
import pixela.client.http.HttpClientFactory;

public class HttpClientFactoryImpl implements HttpClientFactory {

  @Override
  public HttpClient newClient(final PixelaClientConfig pixelaClientConfig) {
    return new HttpClientImpl(pixelaClientConfig);
  }
}
