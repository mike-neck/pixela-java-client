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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pixela.client.Graph;
import pixela.client.GraphSelfSufficient;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.http.HttpClient;
import pixela.client.http.Response;
import reactor.core.publisher.Mono;

class UpdateGraphImpl implements UpdateGraph {

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;
  @NotNull private final Graph graph;

  @Nullable private final String name;
  @Nullable private final String unit;
  @Nullable private final Graph.Color color;
  @Nullable private final ZoneId timezone;
  @NotNull private final List<URI> purgeCacheURLs;
  @Nullable private final GraphSelfSufficient selfSufficient;

  UpdateGraphImpl(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @Nullable final String name,
      @Nullable final String unit,
      @Nullable final Graph.Color color,
      @Nullable final ZoneId timezone,
      @NotNull final List<URI> purgeCacheURLs,
      @Nullable final GraphSelfSufficient selfSufficient) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.graph = graph;
    this.name = name;
    this.unit = unit;
    this.color = color;
    this.timezone = timezone;
    this.purgeCacheURLs = purgeCacheURLs;
    this.selfSufficient = selfSufficient;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Nullable
  public String getUnit() {
    return unit;
  }

  @Nullable
  public String getColor() {
    if (color == null) {
      return null;
    }
    return color.value();
  }

  @Nullable
  public String getTimezone() {
    if (timezone == null) {
      return null;
    }
    return timezone.toString();
  }

  @NotNull
  public List<String> getPurgeCacheURLs() {
    return purgeCacheURLs.stream().map(URI::toASCIIString).collect(Collectors.toList());
  }

  @Nullable
  public String getSelfSufficient() {
    if (selfSufficient == null) {
      return null;
    }
    return selfSufficient.asString();
  }

  @NotNull
  @Override
  public Mono<Graph> call() {
    final Response<Void> response = httpClient.put(this);
    return response.toPublisher().thenReturn(SimpleGraph.of(httpClient, pixela, graph.id()));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String usersUri = pixela.usersUri(baseUrl).toASCIIString();
    final String subPath = graph.subPath();
    return URI.create(usersUri + subPath);
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.of(pixela.token());
  }

  @NotNull
  @Override
  public String errorRequest() {
    final StringBuilder stringBuilder =
        new StringBuilder("PUT ").append(pixela.usersUri()).append(graph.subPath());
    if (name != null) {
      stringBuilder.append('\n').append("  name: ").append(name);
    }
    if (unit != null) {
      stringBuilder.append('\n').append("  unit: ").append(unit);
    }
    if (color != null) {
      stringBuilder.append('\n').append("  color: ").append(color.value());
    }
    if (timezone != null) {
      stringBuilder.append('\n').append("  timezone: ").append(timezone);
    }
    if (!purgeCacheURLs.isEmpty()) {
      stringBuilder.append('\n').append("  purgeCacheURLs: ").append(purgeCacheURLs);
    }
    if (selfSufficient != null) {
      stringBuilder.append('\n').append("  selfSufficient: ").append(selfSufficient.asString());
    }
    return stringBuilder.toString();
  }
}
