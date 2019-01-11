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
package pixela.client.api.graph;

import java.net.URI;
import java.time.LocalDate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.ApiException;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;

public class SimpleGraph implements Graph, PostPixel.PixelDate {

  @NotNull private final HttpClient httpClient;

  @NotNull private final Pixela pixela;

  @NotNull private final GraphId graphId;

  SimpleGraph(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final GraphId graphId) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graphId = graphId;
  }

  @Contract("_, _, _ -> new")
  @NotNull
  public static SimpleGraph of(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final GraphId graphId) {
    return new SimpleGraph(httpClient, pixela, graphId);
  }

  @NotNull
  @Override
  public DeleteGraph delete() {
    return new DeleteGraph(httpClient, pixela, graphId);
  }

  @NotNull
  @Override
  public URI viewUri() {
    final URI uri = httpClient.baseUri();
    final URI usersUri = pixela.usersUri(uri);
    final String string = usersUri.toASCIIString() + subPath() + ".html";
    return URI.create(string);
  }

  @NotNull
  @Override
  public PostPixel.PixelDate postPixel() {
    return this;
  }

  @NotNull
  @Override
  public String subPath() {
    return Graph.PATH + graphId.path();
  }

  @Override
  public String toString() {
    return "Graph[" + string() + "]";
  }

  @NotNull
  String string() {
    return "graphId=" + graphId;
  }

  @NotNull
  @Override
  public PostPixel.PixelQuantity date(@NotNull final LocalDate date) {
    return new PostPixel.PixelQuantity() {
      @NotNull
      @Override
      public PostPixel.OptionData quantity(final int quantity) throws ApiException {
        return new IntPostPixel(httpClient, pixela, SimpleGraph.this, date, quantity);
      }

      @NotNull
      @Override
      public PostPixel.OptionData quantity(final double quantity) throws ApiException {
        return new FloatPostPixel(httpClient, pixela, SimpleGraph.this, date, quantity);
      }
    };
  }
}
