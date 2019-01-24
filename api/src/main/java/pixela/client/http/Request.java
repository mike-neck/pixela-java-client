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
package pixela.client.http;

import java.net.URI;
import java.util.Optional;
import java.util.OptionalInt;
import org.jetbrains.annotations.NotNull;
import pixela.client.UserToken;

public interface Request<T> {

  @NotNull
  URI apiEndpoint(@NotNull final URI baseUrl);

  @NotNull
  Optional<UserToken> userToken();

  @NotNull
  WithBody withBody();

  @NotNull
  default OptionalInt contentLength() {
    return withBody().optionalInt();
  }

  default boolean hasBody() {
    return withBody().asBoolean();
  }

  @NotNull
  Class<? extends T> responseType();

  @NotNull
  default Optional<String> contentType() {
    return Optional.empty();
  }

  @NotNull
  String errorRequest();

  enum WithBody {
    FALSE {
      @Override
      public OptionalInt optionalInt() {
        return OptionalInt.of(0);
      }

      @Override
      public boolean asBoolean() {
        return false;
      }
    },
    TRUE {
      @Override
      public OptionalInt optionalInt() {
        return OptionalInt.empty();
      }

      @Override
      public boolean asBoolean() {
        return true;
      }
    };

    public abstract OptionalInt optionalInt();

    public abstract boolean asBoolean();
  }
}
