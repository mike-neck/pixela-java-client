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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.*;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreateGraphTest {

  private HttpClient httpClient;
  private Pixela pixela;

  @BeforeEach
  void setup() {
    httpClient = mock(HttpClient.class);
    pixela = mock(Pixela.class);
  }

  @Nested
  class CallTest {

    private CreateGraph createGraph;

    @BeforeEach
    void setup() {
      createGraph =
          CreateGraph.builder(httpClient, pixela)
              .id("test-graph-id")
              .name("test-graph-name")
              .unit("nights")
              .integer()
              .black()
              .increment();
    }

    @Nested
    class FailureTest {

      @BeforeEach
      void given() {
        when(httpClient.post(createGraph))
            .thenReturn(() -> Mono.error(ApiException.of("Failure.")));
      }

      @Test
      void test() {
        final Mono<Graph> mono = createGraph.call();
        StepVerifier.create(mono)
            .expectErrorSatisfies(
                e ->
                    assertThat(e).isInstanceOf(ApiException.class).hasMessageContaining("Failure."))
            .verify();
      }
    }

    @Nested
    class SuccessTest {

      @SuppressWarnings("Convert2MethodRef")
      @BeforeEach
      void given() {
        when(httpClient.post(createGraph)).thenReturn(() -> Mono.empty());
      }

      @Test
      void test() {
        final Mono<Graph> mono = createGraph.call();
        StepVerifier.create(mono)
            .assertNext(graph -> assertThat(graph.id()).isEqualTo(GraphId.of("test-graph-id")))
            .verifyComplete();
      }
    }
  }

  @Nested
  class BuilderTest {

    @BeforeEach
    void setup() {
      when(pixela.usersUri()).thenReturn("/v1/users/test-user");
    }

    @Nested
    class ColorTest {

      private CreateGraph.GraphColor graphColor;

      @BeforeEach
      void setup() {
        graphColor =
            CreateGraph.builder(httpClient, pixela)
                .id("test-id")
                .name("test-name")
                .unit("test-unit")
                .integer();
      }

      @Test
      void shibafu() {
        final CreateGraph createGraph = graphColor.shibafu().none();
        assertThat(createGraph.toString()).contains("shibafu");
      }

      @Test
      void green() {
        final CreateGraph createGraph = graphColor.green().none();
        assertThat(createGraph.toString()).contains("shibafu");
      }

      @Test
      void momiji() {
        final CreateGraph createGraph = graphColor.momiji().none();
        assertThat(createGraph.toString()).contains("momiji");
      }

      @Test
      void red() {
        final CreateGraph createGraph = graphColor.red().none();
        assertThat(createGraph.toString()).contains("momiji");
      }

      @Test
      void sora() {
        final CreateGraph createGraph = graphColor.sora().none();
        assertThat(createGraph.toString()).contains("sora");
      }

      @Test
      void blue() {
        final CreateGraph createGraph = graphColor.blue().none();
        assertThat(createGraph.toString()).contains("sora");
      }

      @Test
      void ichou() {
        final CreateGraph createGraph = graphColor.ichou().none();
        assertThat(createGraph.toString()).contains("ichou");
      }

      @Test
      void yellow() {
        final CreateGraph createGraph = graphColor.yellow().none();
        assertThat(createGraph.toString()).contains("ichou");
      }

      @Test
      void ajisai() {
        final CreateGraph createGraph = graphColor.ajisai().none();
        assertThat(createGraph.toString()).contains("ajisai");
      }

      @Test
      void purple() {
        final CreateGraph createGraph = graphColor.purple().none();
        assertThat(createGraph.toString()).contains("ajisai");
      }

      @Test
      void kuro() {
        final CreateGraph createGraph = graphColor.kuro().none();
        assertThat(createGraph.toString()).contains("kuro");
      }

      @Test
      void black() {
        final CreateGraph createGraph = graphColor.black().none();
        assertThat(createGraph.toString()).contains("kuro");
      }
    }

    @Nested
    class SelfSufficientTest {

      private CreateGraph.SelfSufficient selfSufficient;

      @BeforeEach
      void setup() {
        selfSufficient =
            CreateGraph.builder(httpClient, pixela)
                .id("test-id")
                .name("test-name")
                .unit("test-unit")
                .integer()
                .black()
                .timezone("PST8PDT");
      }

      @Test
      void increment() {
        final CreateGraph createGraph = selfSufficient.increment();
        assertThat(createGraph.toString()).contains("increment");
      }

      @Test
      void decrement() {
        final CreateGraph createGraph = selfSufficient.decrement();
        assertThat(createGraph.toString()).contains("decrement");
      }

      @Test
      void none() {
        final CreateGraph createGraph = selfSufficient.none();
        assertThat(createGraph.toString()).contains("none");
      }
    }
  }
}
