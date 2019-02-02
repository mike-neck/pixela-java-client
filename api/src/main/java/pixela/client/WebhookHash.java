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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class WebhookHash {

  @NotNull private final String webhookHash;

  private WebhookHash(@NotNull final String webhookHash) {
    this.webhookHash = webhookHash;
  }

  @Contract("_ -> new")
  @NotNull
  public static WebhookHash of(@NotNull final String webhookHash) {
    return new WebhookHash(webhookHash);
  }

  public String subPath() {
    return "/webhooks" + webhookHash;
  }
}
