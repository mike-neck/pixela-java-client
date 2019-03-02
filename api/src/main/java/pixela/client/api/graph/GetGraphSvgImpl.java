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
import org.jetbrains.annotations.NotNull;
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.api.QueryParams;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

class GetGraphSvgImpl implements GetGraphSvg.WithDateOption, GetGraphSvg.NoOption {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;

  @NotNull private final DateOption date;
  @NotNull private final ModeOption mode;

  GetGraphSvgImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final DateOption date,
      @NotNull final ModeOption mode) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.date = date;
    this.mode = mode;
  }

  @NotNull
  @Override
  public Mono<Tuple2<Graph, String>> call() {
    final Mono<String> response = httpClient.get(this);
    return response.map(svg -> Tuples.of(graph, svg));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String graphUri = pixela.usersUri(baseUrl).toASCIIString() + graph.subPath();
    final String query = QueryParams.concatAll(date, mode);
    final String uri = graphUri + (query.isEmpty() ? "" : "?" + query);
    return URI.create(uri);
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "GET "
        + pixela.usersUri()
        + graph.subPath()
        + "\n"
        + "date: "
        + date.asString().orElse("")
        + "\n"
        + "mode: "
        + mode.asString().orElse("");
  }

  @NotNull
  @Override
  public WithDateOption date(@NotNull final LocalDate date) {
    return new GetGraphSvgImpl(httpClient, pixela, graph, DateOption.of(date), mode);
  }

  @NotNull
  @Override
  public GetGraphSvg mode(@NotNull final ModeOption mode) {
    return new GetGraphSvgImpl(httpClient, pixela, graph, date, mode);
  }
}
