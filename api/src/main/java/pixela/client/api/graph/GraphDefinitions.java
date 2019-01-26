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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;

@SuppressWarnings("WeakerAccess")
public class GraphDefinitions {

  @NotNull private List<GraphDefinition> graphs;

  public GraphDefinitions(@NotNull final List<GraphDefinition> graphs) {
    this.graphs = graphs;
  }

  public GraphDefinitions() {
    this.graphs = Collections.emptyList();
  }

  @NotNull
  public List<GraphDefinition> getGraphs() {
    return graphs;
  }

  public void setGraphs(@NotNull final List<GraphDefinition> graphs) {
    this.graphs = graphs;
  }

  @NotNull
  public List<Graph> asCollection(
      @NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    return graphs
        .stream()
        .map(detail -> detail.asGraph(httpClient, pixela))
        .collect(Collectors.toList());
  }
}
