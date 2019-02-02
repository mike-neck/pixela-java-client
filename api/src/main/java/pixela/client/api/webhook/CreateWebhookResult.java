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
package pixela.client.api.webhook;

public class CreateWebhookResult {

  private String webhookHash;
  private String message;
  private boolean isSuccess;

  @SuppressWarnings("WeakerAccess")
  public String getWebhookHash() {
    return webhookHash;
  }

  public void setWebhookHash(final String webhookHash) {
    this.webhookHash = webhookHash;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public boolean getIsSuccess() {
    return isSuccess;
  }

  public void setIsSuccess(final boolean success) {
    isSuccess = success;
  }
}
