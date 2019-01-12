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

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.api.graph.CreateGraph;
import pixela.client.api.graph.GetPixel;
import pixela.client.api.graph.PostPixel;
import pixela.client.api.user.DeleteUser;
import reactor.core.Disposable;
import reactor.core.Disposables;
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
              .call()
              .log("user-creation");

      final Mono<Graph> graphCreation =
          pixela
              .map(
                  px ->
                      px.createGraph()
                          .id("test-graph")
                          .name("テストグラフ")
                          .unit("kilo")
                          .floating()
                          .ichou()
                          .timezone("Asia/Tokyo"))
              .log("graph-creation")
              .flatMap(CreateGraph::call)
              .log("new-graph")
              .cache()
              .doOnError(
                  e -> {
                    throw new RuntimeException(e);
                  });

      final GraphId testGraph = GraphId.of("test-graph");

      final Mono<URI> viewUri = graphCreation.map(Graph::viewUri).log("view-graph");

      final LocalDate date10 = LocalDate.of(2019, 1, 10);

      final Mono<Graph> postPixelViaGraph =
          viewUri
              .then(graphCreation)
              .map(graph -> graph.postPixel().date(date10).quantity(10.25))
              .log("post-pixel-1")
              .flatMap(PostPixel::call)
              .log("post-pixel-via-graph");

      final Mono<Pixel> getPixelFromPixela = postPixelViaGraph
              .then(pixela)
              .map(px -> px.graph(testGraph))
              .map(graph -> graph.getPixel(date10))
              .flatMap(GetPixel::call)
              .log("get-pixel-via-graph-id-from-Pixela");

      final LocalDate date = LocalDate.of(2019, 1, 9);

      final Mono<Graph> postPixelViaPixela =
          getPixelFromPixela
              .then(pixela)
              .flatMap(
                  pix ->
                      pix.postPixel(testGraph)
                          .date(date)
                          .quantity(11.10)
                          .optionData(Map.of("test", 20))
                          .log("post-pixel-2"))
              .flatMap(PostPixel::call)
              .log("post-pixel-via-pixela");

      final Mono<Pixel> getPixel =
          postPixelViaPixela
              .then(graphCreation)
              .map(graph -> graph.getPixel(date))
              .flatMap(GetPixel::call)
              .log("get-pixel");

      final Mono<Void> mono =
          getPixel
              .then(pixela)
              .map(Pixela::deleteUser)
              .log("user-deletion")
              .flatMap(DeleteUser::call);

      final CountDownLatch latch = new CountDownLatch(1);

      final Disposable disposable = mono.doOnTerminate(latch::countDown).subscribe();

      latch.await();

      Disposables.composite(disposable).dispose();
    }
  }
}
