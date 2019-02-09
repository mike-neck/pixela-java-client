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
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.Graph;
import pixela.client.GraphSelfSufficient;
import pixela.client.http.Put;

public interface UpdateGraph extends Put<Void>, Api<Graph> {

  @NotNull
  @Override
  default WithBody withBody() {
    return WithBody.TRUE;
  }

  @NotNull
  @Override
  default Class<? extends Void> responseType() {
    return Void.class;
  }

  interface Unit extends GraphColor {

    @NotNull
    GraphColor unit(@NotNull final String unit);
  }

  interface GraphColor extends Timezone {
    @NotNull
    Timezone color(@NotNull final Graph.Color color);

    @NotNull
    default Timezone shibafu() {
      return color(Graph.Color.SHIBAFU);
    }

    @NotNull
    default Timezone momiji() {
      return color(Graph.Color.MOMIJI);
    }

    @NotNull
    default Timezone sora() {
      return color(Graph.Color.SORA);
    }

    @NotNull
    default Timezone ichou() {
      return color(Graph.Color.ICHOU);
    }

    @NotNull
    default Timezone ajisai() {
      return color(Graph.Color.AJISAI);
    }

    @NotNull
    default Timezone kuro() {
      return color(Graph.Color.KURO);
    }
  }

  interface Timezone extends PurgeCacheUrls {

    @NotNull
    PurgeCacheUrls timezone(@NotNull final ZoneId timezone);

    @NotNull
    default PurgeCacheUrls timezone(@NotNull final String timezone) {
      return timezone(ZoneId.of(timezone));
    }
  }

  interface PurgeCacheUrls extends SelfSufficient {
    @NotNull
    SelfSufficient purgeCacheURLs(@NotNull final List<URI> purgeCacheURLs);

    @NotNull
    default SelfSufficient purgeCacheURLs(@NotNull final Iterable<String> purgeCacheURLs) {
      final List<URI> uris =
          StreamSupport.stream(purgeCacheURLs.spliterator(), false)
              .map(URI::create)
              .collect(Collectors.toList());
      return purgeCacheURLs(uris);
    }

    @NotNull
    default SelfSufficient purgeCacheURLs(@NotNull final String... purgeCacheURLs) {
      final List<URI> uris =
          Arrays.stream(purgeCacheURLs).map(URI::create).collect(Collectors.toList());
      return purgeCacheURLs(uris);
    }
  }

  interface SelfSufficient extends UpdateGraph {

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
}
