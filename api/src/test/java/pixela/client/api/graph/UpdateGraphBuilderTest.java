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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;

class UpdateGraphBuilderTest {

  private Graph graph;

  @BeforeEach
  void setup() {
    final HttpClient httpClient = mock(HttpClient.class);
    final Pixela pixela = mock(Pixela.class);
    graph = SimpleGraph.of(httpClient, pixela, GraphId.of("test-graph"));
  }

  @Contract(pure = true)
  @NotNull
  private Collection<OpAndExpectation> tests() {
    return Arrays.asList(
        test("name only")
            .operation(builder -> builder.name("new-graph-name"))
            .expecting("name: new-graph-name"),
        test("name-unit")
            .operation(builder -> builder.name("new-graph-name").unit("commit"))
            .expecting("name: new-graph-name", "unit: commit"),
        test("unit").operation(builder -> builder.unit("commit")).expecting("unit: commit"),
        test("unit-color[shibafu]")
            .operation(builder -> builder.unit("commit").shibafu())
            .expecting("unit: commit", "color: shibafu"),
        test("color[shibafu]")
            .operation(builder -> builder.color().shibafu())
            .expecting("color: shibafu"),
        test("color[momiji]")
            .operation(builder -> builder.color().momiji())
            .expecting("color: momiji"),
        test("color[sora]").operation(buildefr -> buildefr.color().sora()).expecting("color: sora"),
        test("color[ichou]")
            .operation(builder -> builder.color().ichou())
            .expecting("color: ichou"),
        test("color[ajisai]")
            .operation(builder -> builder.color().ajisai())
            .expecting("color: ajisai"),
        test("color[kuro]-timezone")
            .operation(builder -> builder.color().kuro().timezone("Asia/Tokyo"))
            .expecting("color: kuro", "timezone: Asia/Tokyo"),
        test("timezone")
            .operation(builder -> builder.timezone("PST8PDT"))
            .expecting("timezone: PST8PDT"),
        test("timezone-purgeCacheURLs")
            .operation(
                builder ->
                    builder
                        .timezone("UTC")
                        .purgeCacheURLs(
                            "https://example.com",
                            "https://github.com/mike-neck/pixela-java-client"))
            .expecting(
                "timezone: UTC",
                "purgeCacheURLs: [https://example.com, https://github.com/mike-neck/pixela-java-client]"),
        test("purgeCacheURLs")
            .operation(builder -> builder.purgeCacheURLs("https://example.com/test"))
            .expecting("purgeCacheURLs: [https://example.com/test]"),
        test("purgeCacheURLs-selfSufficient")
            .operation(
                builder ->
                    builder
                        .purgeCacheURLs(Collections.singletonList("https://example.com/test"))
                        .increment())
            .expecting("purgeCacheURLs: [https://example.com/test]", "selfSufficient: increment"),
        test("selfSufficient")
            .operation(builder -> builder.selfSufficient().decrement())
            .expecting("selfSufficient: decrement"));
  }

  @Test
  void unitOnly() {
    final String requestString = graph.updateGraph().unit("commit").errorRequest();

    assertThat(requestString).contains("unit: commit");
  }

  private static class OpAndExpectation {
    @NotNull final String title;
    @NotNull final Supplier<UpdateGraph> operation;
    @NotNull final List<String> containing;

    private OpAndExpectation(
        @NotNull final String title,
        @NotNull final Supplier<UpdateGraph> operation,
        @NotNull final List<String> containing) {
      this.title = title;
      this.operation = operation;
      this.containing = containing;
    }
  }

  @NotNull
  @Contract(pure = true)
  private Operation test(@NotNull final String title) {
    return operation ->
        containing ->
            new OpAndExpectation(
                title, () -> operation.apply(graph.updateGraph()), Arrays.asList(containing));
  }

  private interface Operation {
    @NotNull
    Expecting operation(@NotNull final Function<UpdateGraphBuilder, UpdateGraph> operation);
  }

  private interface Expecting {
    @NotNull
    OpAndExpectation expecting(@NotNull final String... containing);
  }

  @NotNull
  @TestFactory
  Iterable<DynamicTest> allTests() {
    return tests()
        .stream()
        .map(
            ope ->
                dynamicTest(
                    ope.title,
                    () -> assertThat(ope.operation.get().errorRequest()).contains(ope.containing)))
        .collect(Collectors.toList());
  }
}
