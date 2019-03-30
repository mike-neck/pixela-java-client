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
package pixela.client.api.user;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.YesNo;
import pixela.client.http.HttpClient;
import pixela.client.http.Post;
import reactor.core.publisher.Mono;

public class CreateUser implements Post<Void>, Api<Pixela> {

  private static final String ENDPOINT = "/v1/users";

  @NotNull private final HttpClient httpClient;

  @NotNull private final UserToken token;
  @NotNull private final String username;
  @NotNull private final YesNo agreeTermsOfService;
  @NotNull private final YesNo notMinor;

  CreateUser(
      @NotNull final HttpClient httpClient,
      @NotNull final UserToken token,
      @NotNull final String username,
      @NotNull final YesNo agreeTermsOfService,
      @NotNull final YesNo notMinor) {
    this.httpClient = httpClient;
    this.token = token;
    this.username = username;
    this.agreeTermsOfService = agreeTermsOfService;
    this.notMinor = notMinor;
  }

  @NotNull
  public String getToken() {
    return token.tokenValue();
  }

  @NotNull
  public String getUsername() {
    return username;
  }

  @NotNull
  public String getAgreeTermsOfService() {
    return agreeTermsOfService.asString();
  }

  @NotNull
  public String getNotMinor() {
    return notMinor.asString();
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("CreateUser{");
    sb.append("token='").append(token).append('\'');
    sb.append(", username='").append(username).append('\'');
    sb.append(", agreeTermsOfService=").append(agreeTermsOfService);
    sb.append(", notMinor=").append(notMinor);
    sb.append('}');
    return sb.toString();
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    return baseUrl.resolve(ENDPOINT);
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.empty();
  }

  @NotNull
  @Override
  public WithBody withBody() {
    return WithBody.TRUE;
  }

  @NotNull
  @Override
  public Class<Void> responseType() {
    return Void.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "POST "
        + ENDPOINT
        + '\n'
        + "  token:"
        + token.tokenValue()
        + '\n'
        + "  username:"
        + username
        + '\n'
        + "  agreeTermsOfService:"
        + agreeTermsOfService
        + '\n'
        + "  notMinor:"
        + notMinor;
  }

  @NotNull
  @Override
  public Mono<Pixela> call() {
    final Mono<Void> response = httpClient.post(this);
    return response.thenReturn(
        PixelaImpl.of(httpClient, token, pixela.client.Username.of(username)));
  }

  public interface Builder {
    CreateUser.Token createUser();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Contract(pure = true)
  @NotNull
  public static CreateUser.Token builder(@NotNull final HttpClient httpClient) {
    Objects.requireNonNull(httpClient);
    return token -> {
      Objects.requireNonNull(token);
      final UserToken userToken = UserToken.validated(token);
      return username -> {
        Objects.requireNonNull(username);
        return agreeTermsOfService -> {
          Objects.requireNonNull(agreeTermsOfService);
          return notMinor -> {
            Objects.requireNonNull(notMinor);
            return new CreateUser(httpClient, userToken, username, agreeTermsOfService, notMinor);
          };
        };
      };
    };
  }

  public interface Token {
    @NotNull
    Username withToken(@NotNull final String token);
  }

  public interface Username {
    @NotNull
    AgreeTermsOfService username(@NotNull final String username);
  }

  public interface AgreeTermsOfService {

    @NotNull
    default NotMinor agreeTermsOfService() {
      return agreeTermsOfService(YesNo.YES);
    }

    @NotNull
    default NotMinor doNotAgreeTermsOfService() {
      return agreeTermsOfService(YesNo.NO);
    }

    @NotNull
    NotMinor agreeTermsOfService(@NotNull final YesNo agreeTermsOfService);
  }

  public interface NotMinor {

    @NotNull
    default CreateUser notMinor() {
      return notMinor(YesNo.YES);
    }

    @NotNull
    default CreateUser minor() {
      return notMinor(YesNo.NO);
    }

    @NotNull
    CreateUser notMinor(@NotNull final YesNo notMinor);
  }
}
