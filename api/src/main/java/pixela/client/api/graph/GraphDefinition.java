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

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import pixela.client.*;
import pixela.client.http.HttpClient;

@SuppressWarnings("WeakerAccess")
public class GraphDefinition {

  @NotNull private String id;
  @NotNull private String name;
  @NotNull private String unit;
  @NotNull private String type;
  @NotNull private String color;
  @NotNull private String timezone;
  @NotNull private List<String> purgeCacheURLs;

  public GraphDefinition(
      @NotNull final String id,
      @NotNull final String name,
      @NotNull final String unit,
      @NotNull final String type,
      @NotNull final String color,
      @NotNull final String timezone,
      @NotNull final List<String> purgeCacheURLs) {
    this.id = id;
    this.name = name;
    this.unit = unit;
    this.type = type;
    this.color = color;
    this.timezone = timezone;
    this.purgeCacheURLs = purgeCacheURLs;
  }

  public GraphDefinition() {
    this.id = "";
    this.name = "";
    this.unit = "";
    this.type = "int";
    this.color = "shibafu";
    this.timezone = "UTC";
    this.purgeCacheURLs = Collections.emptyList();
  }

  @NotNull
  public Graph asGraph(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    return new NewGraph(
        httpClient,
        pixela,
        GraphId.of(id),
        GraphName.of(name),
        GraphUnit.of(unit),
        Graph.Type.fromString(type),
        Graph.Color.fromString(color),
        ZoneId.of(timezone));
  }

  @NotNull
  public String getId() {
    return id;
  }

  public void setId(@NotNull final String id) {
    this.id = id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull final String name) {
    this.name = name;
  }

  @NotNull
  public String getUnit() {
    return unit;
  }

  public void setUnit(@NotNull final String unit) {
    this.unit = unit;
  }

  @NotNull
  public String getType() {
    return type;
  }

  public void setType(@NotNull final String type) {
    this.type = type;
  }

  @NotNull
  public String getColor() {
    return color;
  }

  public void setColor(@NotNull final String color) {
    this.color = color;
  }

  @NotNull
  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(@NotNull final String timezone) {
    this.timezone = timezone;
  }

  @NotNull
  public List<String> getPurgeCacheURLs() {
    return purgeCacheURLs;
  }

  public void setPurgeCacheURLs(@NotNull final List<String> purgeCacheURLs) {
    this.purgeCacheURLs = purgeCacheURLs;
  }
}
