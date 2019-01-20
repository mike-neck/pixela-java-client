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
import pixela.client.api.graph.*;
import pixela.client.api.user.DeleteUser;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

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

      // Create User
      final Mono<Pixela> pixela =
          Pixela.withDefaultJavaClient()
              .createUser()
              .withToken(token)
              .username(username)
              .agreeTermsOfService()
              .notMinor()
              .call()
              .log("user-creation");

      // Create Graph
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

      final Mono<Tuple2<Graph, URI>> viewUri =
          graphCreation.map(graph -> Tuples.of(graph, graph.viewUri())).log("view-graph");

      // Post Pixel
      final LocalDate date10 = LocalDate.of(2019, 1, 10);

      final Mono<Pixel> postPixelViaGraph =
          viewUri
              .map(Tuple2::getT1)
              .map(graph -> graph.postPixel().date(date10).quantity(10.25))
              .log("post-pixel-1")
              .flatMap(PostPixel::call)
              .log("post-pixel-via-graph")
              .cache();

      final Mono<Pixel> getPixelFromPixela =
          postPixelViaGraph
              .map(Pixel::pixela)
              .map(px -> px.graph(testGraph))
              .map(graph -> graph.getPixel(date10))
              .flatMap(GetPixel::call)
              .log("get-pixel-via-graph-id-from-Pixela");

      // Post Pixel
      final LocalDate date = LocalDate.of(2019, 1, 9);

      final Mono<Pixel> postPixelViaPixela =
          getPixelFromPixela
              .map(Pixel::pixela)
              .flatMap(
                  pix ->
                      pix.postPixel(testGraph)
                          .date(date)
                          .quantity(11.10)
                          .optionData(Map.of("test", 20))
                          .log("post-pixel-2"))
              .flatMap(PostPixel::call)
              .log("post-pixel-via-pixela");

      // Get Pixel
      final Mono<Pixel> getPixel =
          postPixelViaPixela
              .map(Pixel::graph)
              .map(graph -> graph.getPixel(date))
              .flatMap(GetPixel::call)
              .log("get-pixel");

      // Update Pixel
      final Mono<Pixel> updatePixel =
          getPixel
              .map(Pixel::update)
              .flatMap(update -> update.quantity(-4.57).optionalData(Map.of("test", "example")))
              .log("update-pixel-api")
              .flatMap(UpdatePixel::call)
              .log("update-pixel");

      // Increment Pixel
      final Mono<Graph> incrementPixel =
          updatePixel
              .map(Pixel::pixela)
              .map(px -> px.graph(testGraph))
              .map(Graph::incrementPixel)
              .flatMap(IncrementPixel::call)
              .log("increment-pixel");

      // Decrement Pixel
      final Mono<Graph> decrementPixel =
          incrementPixel
              .map(Graph::decrementPixel)
              .flatMap(DecrementPixel::call)
              .log("decrement-pixel");

      // Delete Pixel
      final Mono<Graph> deletePixel =
          decrementPixel
              .map(graph -> graph.getPixel(date))
              .flatMap(GetPixel::call)
              .map(Pixel::delete)
              .flatMap(DeletePixel::call)
              .log("delete-pixel");

      // Delete Graph
      final Mono<Pixela> deleteGraph =
          deletePixel.map(Graph::delete).flatMap(DeleteGraph::call).log("delete-graph");

      // Delete User
      final Mono<Void> mono =
          deleteGraph.map(Pixela::deleteUser).log("user-deletion").flatMap(DeleteUser::call);

      final CountDownLatch latch = new CountDownLatch(1);

      final Disposable disposable = mono.doOnTerminate(latch::countDown).subscribe();

      latch.await();

      Disposables.composite(disposable).dispose();
    }
  }
}
