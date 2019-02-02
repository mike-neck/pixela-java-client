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

import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pixela.client.ApiException;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;

public class CreateWebhookResult {

  @Nullable private String webhookHash;
  private String message;
  private boolean isSuccess;

  public CreateWebhookResult() {}

  private CreateWebhookResult(
      @Nullable final String webhookHash, @NotNull final String message, final boolean isSuccess) {
    this.webhookHash = webhookHash;
    this.message = message;
    this.isSuccess = isSuccess;
  }

  @NotNull
  @Contract("_, _ -> new")
  static CreateWebhookResult success(
      @NotNull final String webhookHash, @NotNull final String message) {
    return new CreateWebhookResult(webhookHash, message, true);
  }

  @Contract("_ -> new")
  @NotNull
  static CreateWebhookResult failure(@NotNull final String message) {
    return new CreateWebhookResult(null, message, false);
  }

  @NotNull
  Mono<String> webhookHash(@NotNull final Request<?> request) {
    if (isSuccess) {
      return Mono.justOrEmpty(Optional.ofNullable(webhookHash));
    } else {
      return Mono.error(ApiException.of(message).appendDebugInfo(request));
    }
  }

  @Nullable
  public String getWebhookHash() {
    return webhookHash;
  }

  public void setWebhookHash(@Nullable final String webhookHash) {
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
