/*
 * Copyright 2018 Shinya Mochida
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

import java.net.http.HttpRequest;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import pixela.client.UserToken;
import pixela.client.http.Request;

class UserTokenHeader implements Header<HttpRequest.Builder> {

  @NotNull private final Request<?> request;

  private UserTokenHeader(@NotNull final Request<?> request) {
    this.request = request;
  }

  static UserTokenHeader of(@NotNull final Request<?> request) {
    return new UserTokenHeader(request);
  }

  @NotNull
  @Override
  public HttpRequest.Builder configure(@NotNull final HttpRequest.Builder targetObject) {
    final Optional<UserToken> userToken = request.userToken();
    return userToken
        .map(token -> targetObject.header(token.tokenName(), token.tokenValue()))
        .orElse(targetObject);
  }
}
