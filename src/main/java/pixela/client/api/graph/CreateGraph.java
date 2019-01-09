/*
 * Copyright 2018 Shinya Mochida
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
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import pixela.client.*;
import pixela.client.http.HttpClient;
import pixela.client.http.Post;
import pixela.client.http.Response;
import reactor.core.publisher.Mono;

public class CreateGraph implements Post<Void>, Api<Graph> {

  private static final String ENDPOINT_SUFFIX = "/graphs";

  @NotNull private final HttpClient httpClient;
  @NotNull private final Pixela pixela;

  @NotNull private final String id;
  @NotNull private final String name;
  @NotNull private final String unit;
  @NotNull private final Graph.Type type;
  @NotNull private final Graph.Color color;
  @NotNull private final ZoneId timezone;

  private CreateGraph(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final String id,
      @NotNull final String name,
      @NotNull final String unit,
      @NotNull final Graph.Type type,
      @NotNull final Graph.Color color,
      @NotNull final ZoneId timezone) {
    this.httpClient = httpClient;
    this.pixela = pixela;
    this.id = id;
    this.name = name;
    this.unit = unit;
    this.type = type;
    this.color = color;
    this.timezone = timezone;
  }

  @NotNull
  public String getId() {
    return id;
  }

  @NotNull
  public String getUnit() {
    return unit;
  }

  @NotNull
  public String getType() {
    return type.value();
  }

  @NotNull
  public String getColor() {
    return color.value();
  }

  @NotNull
  public String getTimezone() {
    return timezone.toString();
  }

  @NotNull
  @Override
  public Mono<Graph> call() {
    final Response<Void> response = httpClient.post(this);
    return response
        .toPublisher()
        .map(
            v ->
                new NewGraph(
                    httpClient,
                    pixela,
                    GraphId.of(id),
                    GraphName.of(name),
                    GraphUnit.of(unit),
                    type,
                    color,
                    timezone));
  }

  @NotNull
  @Override
  public URI apiEndpoint(@NotNull final URI baseUrl) {
    final String uri = pixela.usersUri(baseUrl) + NewGraph.PATH;
    return URI.create(uri);
  }

  @NotNull
  @Override
  public Optional<UserToken> userToken() {
    return Optional.of(pixela.token());
  }

  @NotNull
  @Override
  public WithBody withBody() {
    return WithBody.TRUE;
  }

  @NotNull
  @Override
  public Class<? extends Void> responseType() {
    return Void.class;
  }

  @NotNull
  @Override
  public String errorRequest() {
    return "POST "
        + pixela.usersUri()
        + NewGraph.PATH
        + '\n'
        + "  id: "
        + id
        + '\n'
        + "  name: "
        + name
        + '\n'
        + "  unit: "
        + unit
        + '\n'
        + "  type: "
        + type.value()
        + '\n'
        + "  color: "
        + color.value()
        + '\n'
        + "  timezone: "
        + timezone.getId();
  }

  @NotNull
  @Override
  public String toString() {
    return errorRequest();
  }

  public static Id builder(@NotNull final HttpClient httpClient, @NotNull final Pixela pixela) {
    Objects.requireNonNull(httpClient, "httpClient is null");
    Objects.requireNonNull(pixela, "pixela is null");
    return id ->
        name ->
            unit ->
                type ->
                    color ->
                        timezone -> {
                          Objects.requireNonNull(id, "id is null");
                          Objects.requireNonNull(name, "name is null");
                          Objects.requireNonNull(unit, "unit is null");
                          Objects.requireNonNull(type, "type is null");
                          Objects.requireNonNull(color, "color is null");
                          return new CreateGraph(
                              httpClient, pixela, id, name, unit, type, color, timezone);
                        };
  }

  public interface Id {

    @NotNull
    Name id(@NotNull final String id);
  }

  public interface Name {
    @NotNull
    GraphUnitDesc name(@NotNull final String name);
  }

  public interface GraphUnitDesc {
    @NotNull
    GraphType unit(@NotNull final String unit);
  }

  public interface GraphType {
    @NotNull
    GraphColor type(@NotNull final Graph.Type type);

    @NotNull
    default GraphColor integer() {
      return type(Graph.Type.INT);
    }

    @NotNull
    default GraphColor floating() {
      return type(Graph.Type.FLOAT);
    }
  }

  public interface GraphColor {

    @NotNull
    Timezone color(@NotNull final Graph.Color color);

    @NotNull
    default Timezone shibafu() {
      return color(Graph.Color.SHIBAFU);
    }

    @NotNull
    default Timezone green() {
      return color(Graph.Color.GREEN);
    }

    @NotNull
    default Timezone momoji() {
      return color(Graph.Color.MOMIJI);
    }

    @NotNull
    default Timezone red() {
      return color(Graph.Color.RED);
    }

    @NotNull
    default Timezone sora() {
      return color(Graph.Color.SORA);
    }

    @NotNull
    default Timezone blue() {
      return color(Graph.Color.BLUE);
    }

    @NotNull
    default Timezone ichou() {
      return color(Graph.Color.ICHOU);
    }

    @NotNull
    default Timezone yellow() {
      return color(Graph.Color.YELLOW);
    }

    @NotNull
    default Timezone ajisai() {
      return color(Graph.Color.AJISAI);
    }

    @NotNull
    default Timezone purple() {
      return color(Graph.Color.PURPLE);
    }

    @NotNull
    default Timezone kuro() {
      return color(Graph.Color.KURO);
    }

    @NotNull
    default Timezone black() {
      return color(Graph.Color.BLACK);
    }
  }

  public interface Timezone extends Api<Graph> {

    @NotNull
    CreateGraph timezone(@NotNull final ZoneId timezone);

    @NotNull
    default CreateGraph timezone(@NotNull final String timezone) throws DateTimeException {
      final ZoneId tz = ZoneId.of(timezone);
      return timezone(tz);
    }

    @NotNull
    @Override
    default Mono<Graph> call() {
      return timezone(ZoneId.of("UTC")).call();
    }
  }
}
