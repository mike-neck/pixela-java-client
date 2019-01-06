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

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.api.user.DeleteUser;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

class PixelaTest {

  @Disabled
  @Nested
  class NewUserTest {

    @Test
    void exampleUser() throws InterruptedException {
      final UUID uuid = UUID.randomUUID();

      final long least = uuid.getLeastSignificantBits();
      final String token = Long.toHexString(least);
      System.out.println(token);

      final long most = uuid.getMostSignificantBits();
      final String username = "a" + Long.toHexString(most);
      System.out.println(username);

      final Mono<Pixela> pixela =
          Pixela.withDefaultJavaClient()
              .createUser()
              .withToken(token)
              .username(username)
              .agreeTermsOfService()
              .notMinor()
              .call();

      final Mono<Void> mono =
          pixela
              .log("create_user API")
              .map(Pixela::deleteUser)
              .log("delete_user")
              .flatMap(DeleteUser::call);

      final CountDownLatch latch = new CountDownLatch(1);

      final Disposable disposable =
          mono.doOnTerminate(latch::countDown).subscribe(System.out::println);

      latch.await();
      disposable.dispose();
    }
  }
}
