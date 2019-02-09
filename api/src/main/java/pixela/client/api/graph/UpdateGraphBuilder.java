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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Graph;
import pixela.client.GraphSelfSufficient;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;

public class UpdateGraphBuilder {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;

  private UpdateGraphBuilder(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
  }

  @NotNull
  @Contract("_, _, _ -> new")
  static UpdateGraphBuilder of(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph) {
    return new UpdateGraphBuilder(httpClient, pixela, graph);
  }

  @NotNull
  public UpdateGraph.Unit name(@NotNull final String name) {
    return UpdateGraphImpl.withName(httpClient, pixela, graph, name);
  }

  @NotNull
  public UpdateGraph.GraphColor unit(@NotNull final String unit) {
    return UpdateGraphImpl.withUnit(httpClient, pixela, graph, unit);
  }

  @FunctionalInterface
  public interface Color {
    @NotNull
    UpdateGraph.Timezone color(@NotNull final Graph.Color color);

    @NotNull
    default UpdateGraph.Timezone shibafu() {
      return color(Graph.Color.SHIBAFU);
    }

    @NotNull
    default UpdateGraph.Timezone momiji() {
      return color(Graph.Color.MOMIJI);
    }

    @NotNull
    default UpdateGraph.Timezone sora() {
      return color(Graph.Color.SORA);
    }

    @NotNull
    default UpdateGraph.Timezone ichou() {
      return color(Graph.Color.ICHOU);
    }

    @NotNull
    default UpdateGraph.Timezone ajisai() {
      return color(Graph.Color.AJISAI);
    }

    @NotNull
    default UpdateGraph.Timezone kuro() {
      return color(Graph.Color.KURO);
    }
  }

  @NotNull
  public Color color() {
    return color -> UpdateGraphImpl.withColor(httpClient, pixela, graph, color);
  }

  @NotNull
  public UpdateGraph.PurgeCacheUrls timezone(@NotNull final String timezone) {
    return timezone(ZoneId.of(timezone));
  }

  @NotNull
  public UpdateGraph.PurgeCacheUrls timezone(@NotNull final ZoneId timezone) {
    return UpdateGraphImpl.withTimezone(httpClient, pixela, graph, timezone);
  }

  @NotNull
  public UpdateGraph.SelfSufficient purgeCacheURLs(@NotNull final String... purgeCacheURLs) {
    final List<URI> uris =
        Arrays.stream(purgeCacheURLs).map(URI::create).collect(Collectors.toList());
    return purgeCacheURLs(uris);
  }

  @NotNull
  public UpdateGraph.SelfSufficient purgeCacheURLs(@NotNull final Iterable<String> purgeCacheURLs) {
    final List<URI> uris =
        StreamSupport.stream(purgeCacheURLs.spliterator(), false)
            .map(URI::create)
            .collect(Collectors.toList());
    return purgeCacheURLs(uris);
  }

  @SuppressWarnings("WeakerAccess")
  @NotNull
  public UpdateGraph.SelfSufficient purgeCacheURLs(@NotNull final List<URI> purgeCacheURLs) {
    return UpdateGraphImpl.withPurgeCacheURLs(httpClient, pixela, graph, purgeCacheURLs);
  }

  @FunctionalInterface
  public interface SelfSufficient {
    @NotNull
    UpdateGraph selfSufficient(@NotNull final GraphSelfSufficient selfSufficient);

    @NotNull
    default UpdateGraph increment() {
      return selfSufficient(GraphSelfSufficient.INCREMENT);
    }

    @NotNull
    default UpdateGraph decrement() {
      return selfSufficient(GraphSelfSufficient.DECREMENT);
    }

    @NotNull
    default UpdateGraph none() {
      return selfSufficient(GraphSelfSufficient.NONE);
    }
  }

  public SelfSufficient selfSufficient() {
    return selfSufficient ->
        UpdateGraphImpl.withSelfSufficient(httpClient, pixela, graph, selfSufficient);
  }
}
