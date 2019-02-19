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
package org.mikeneck.example;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.Pixela;
import pixela.client.PixelaClient;
import pixela.client.api.graph.GetPixel;
import reactor.core.Disposable;

public class PixelaApplication {

  public static void main(@NotNull final String[] args) throws Exception {
    if (args.length != 3) {
      final String message =
          Arrays.stream(args)
              .collect(
                  Collectors.joining(
                      ",",
                      "Application requires 3 parameters. The 1st is username, the 2nd is user token, the 3rd is graph id. [",
                      "]"));
      System.out.println(message);
      throw new IllegalArgumentException(message);
    }

    final String username = args[0];
    final String userToken = args[1];
    final String graphName = args[2];

    try (final PixelaClient pixelaClient = Pixela.withDefaultJavaClient()) {
      final CountDownLatch latch = new CountDownLatch(1);

      final Pixela pixela = pixelaClient.username(username).token(userToken);
      final Graph graph = pixela.graph(GraphId.of(graphName));
      final Disposable disposable =
          graph
              .incrementPixel()
              .call()
              .map(g -> g.getPixel(LocalDate.now(ZoneId.of("Asia/Tokyo"))))
              .flatMap(GetPixel::call)
              .subscribe(
                  pixel ->
                      System.out.println(
                          "Pixel[date: "
                              + pixel.date().format(DateTimeFormatter.ISO_LOCAL_DATE)
                              + ", quantity:"
                              + pixel.quantity()
                              + "]"),
                  e -> {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                  },
                  latch::countDown);

      latch.await();
      disposable.dispose();
    }
  }
}
