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
import java.time.ZoneId;
import org.jetbrains.annotations.NotNull;
import pixela.client.*;
import pixela.client.http.HttpClient;

public class NewGraph implements Graph {

  static final String PATH = "/graphs";

  @NotNull private final SimpleGraph simpleGraph;

  @NotNull private final GraphName name;

  @NotNull private final GraphUnit unit;

  @NotNull private final Graph.Type type;

  @NotNull private final Graph.Color color;

  @NotNull private final ZoneId timezone;

  NewGraph(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final GraphId graphId,
      @NotNull final GraphName name,
      @NotNull final GraphUnit unit,
      @NotNull final Type type,
      @NotNull final Color color,
      @NotNull final ZoneId timezone) {
    this.simpleGraph = new SimpleGraph(httpClient, pixela, graphId);
    this.name = name;
    this.unit = unit;
    this.type = type;
    this.color = color;
    this.timezone = timezone;
  }

  @NotNull
  @Override
  public Pixela pixela() {
    return simpleGraph.pixela();
  }

  @NotNull
  @Override
  public DeleteGraph delete() {
    return simpleGraph.delete();
  }

  @NotNull
  @Override
  public GetGraphSvg.NoOption getGraphSvg() {
    return simpleGraph.getGraphSvg();
  }

  @NotNull
  @Override
  public URI viewUri() {
    return simpleGraph.viewUri();
  }

  @NotNull
  @Override
  public PostPixel.PixelDate postPixel() {
    return simpleGraph;
  }

  @NotNull
  @Override
  public GetPixel getPixel(@NotNull final LocalDate date) {
    return simpleGraph.pixel(date).apply(this);
  }

  @NotNull
  @Override
  public String subPath() {
    return simpleGraph.subPath();
  }

  @NotNull
  @Override
  public IncrementPixel incrementPixel() {
    return simpleGraph.incrementPixel();
  }

  @NotNull
  @Override
  public DecrementPixel decrementPixel() {
    return simpleGraph.decrementPixel();
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("Graph[");
    sb.append(simpleGraph.string());
    sb.append(", name=").append(name);
    sb.append(", unit=").append(unit);
    sb.append(", type=").append(type);
    sb.append(", color=").append(color);
    sb.append(", timezone=").append(timezone);
    sb.append(']');
    return sb.toString();
  }
}
