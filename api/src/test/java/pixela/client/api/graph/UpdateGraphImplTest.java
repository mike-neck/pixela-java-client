package pixela.client.api.graph;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.net.URI;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import pixela.client.*;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UpdateGraphImplTest {

  private HttpClient httpClient;
  private Pixela pixela;
  private Graph graph;

  @BeforeEach
  void setup() {
    httpClient = mock(HttpClient.class);
    pixela = mock(Pixela.class);
    graph = mock(Graph.class);
  }

  @Nested
  class JsonTest {

    private final ObjectMapper objectMapper =
        new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    @Test
    void noEntries() throws JsonProcessingException {
      final UpdateGraph updateGraph =
          new UpdateGraphImpl(
              httpClient, pixela, graph, null, null, null, null, Collections.emptyList(), null);

      final String json = objectMapper.writeValueAsString(updateGraph);

      assertThatJson(json).isEqualTo("{}");
    }

    @Test
    void purgeCacheURLs() throws JsonProcessingException {
      final UpdateGraphImpl updateGraph =
          new UpdateGraphImpl(
              httpClient,
              pixela,
              graph,
              null,
              null,
              null,
              null,
              Collections.singletonList(URI.create("https://example.com")),
              null);

      final String json = objectMapper.writeValueAsString(updateGraph);

      assertThatJson(json)
          .node("purgeCacheURLs")
          .isArray()
          .hasSize(1)
          .containsExactly("https://example.com");
    }

    @Test
    void misc() throws JsonProcessingException {
      final UpdateGraphImpl updateGraph =
          new UpdateGraphImpl(
              httpClient,
              pixela,
              graph,
              "test-name",
              "test-unit",
              Graph.Color.PURPLE,
              ZoneId.of("Asia/Tokyo"),
              Collections.emptyList(),
              GraphSelfSufficient.DECREMENT);

      final String json = objectMapper.writeValueAsString(updateGraph);

      assertAll(
          () -> assertThatJson(json).node("name").isString().isEqualTo("test-name"),
          () -> assertThatJson(json).node("unit").isString().isEqualTo("test-unit"),
          () -> assertThatJson(json).node("color").isString().isEqualTo("ajisai"),
          () -> assertThatJson(json).node("timezone").isString().isEqualTo("Asia/Tokyo"),
          () -> assertThatJson(json).node("purgeCacheURLs").isAbsent(),
          () -> assertThatJson(json).node("selfSufficient").isString().isEqualTo("decrement"));
    }
  }

  @Nested
  class ErrorRequest {

    @BeforeEach
    void given() {
      when(pixela.usersUri()).thenReturn("/v1/users/112233");
      when(graph.subPath()).thenReturn("/graphs/112233");
    }

    @Test
    void noEntries() {
      final UpdateGraph updateGraph =
          new UpdateGraphImpl(
              httpClient, pixela, graph, null, null, null, null, Collections.emptyList(), null);

      assertThat(updateGraph.errorRequest()).contains("PUT /v1/users/112233").doesNotContain("\n");
    }

    @Test
    void name() {
      final UpdateGraph updateGraph =
          new UpdateGraphImpl(
              httpClient,
              pixela,
              graph,
              "test-name",
              null,
              null,
              null,
              Collections.emptyList(),
              null);

      assertThat(updateGraph.errorRequest())
          .contains("PUT /v1/users/112233", "\n", "name: test-name");
    }

    @Test
    void misc() {
      final UpdateGraph updateGraph =
          new UpdateGraphImpl(
              httpClient,
              pixela,
              graph,
              null,
              "test-unit",
              Graph.Color.PURPLE,
              ZoneId.of("Asia/Tokyo"),
              Arrays.asList(URI.create("https://example.com"), URI.create("https://google.com")),
              GraphSelfSufficient.NONE);

      assertThat(updateGraph.errorRequest())
          .doesNotContain("  name:")
          .contains(
              "PUT /v1/users/112233",
              "\n",
              "unit: test-unit",
              "color: ajisai",
              "timezone: Asia/Tokyo",
              "purgeCacheURLs: [https://example.com, https://google.com]",
              "selfSufficient: none");
    }
  }

  @Nested
  class ApiEndpointTest {

    @BeforeEach
    void given() {
      when(pixela.usersUri(any()))
          .thenAnswer(
              (Answer<URI>)
                  invocation -> {
                    final URI uri = invocation.getArgument(0);
                    return uri.resolve("/v1/users/112233");
                  });
      when(graph.subPath()).thenReturn("/graphs/445566");
    }

    @Test
    void test() {
      final UpdateGraph updateGraph =
          new UpdateGraphImpl(
              httpClient, pixela, graph, null, null, null, null, Collections.emptyList(), null);

      assertThat(updateGraph.apiEndpoint(URI.create("https://example.com")))
          .hasPath("/v1/users/112233/graphs/445566")
          .hasScheme("https")
          .hasHost("example.com");
    }
  }

  @Nested
  class CallTest {

    @Nested
    class Success {

      @SuppressWarnings("Convert2MethodRef")
      @BeforeEach
      void given() {
        when(graph.id()).thenReturn(GraphId.of("test-graph"));
        when(httpClient.put(any())).thenReturn(() -> Mono.empty());
      }

      @Test
      void test() {
        final UpdateGraph updateGraph =
            new UpdateGraphImpl(
                httpClient,
                pixela,
                graph,
                "test-name",
                null,
                null,
                null,
                Collections.emptyList(),
                null);

        final Mono<Graph> mono = updateGraph.call();

        StepVerifier.create(mono)
            .assertNext(gr -> assertThat(gr.id()).isEqualTo(GraphId.of("test-graph")))
            .verifyComplete();
      }
    }

    @Nested
    class Failure {

      @BeforeEach
      void given() {
        when(graph.id()).thenReturn(GraphId.of("test-graph"));
        when(httpClient.put(any())).thenReturn(() -> Mono.error(ApiException.of("api error")));
      }

      @Test
      void test() {
        final UpdateGraph updateGraph =
            new UpdateGraphImpl(
                httpClient,
                pixela,
                graph,
                "test-name",
                null,
                null,
                null,
                Collections.emptyList(),
                null);

        final Mono<Graph> mono = updateGraph.call();

        StepVerifier.create(mono)
            .expectErrorSatisfies(
                e ->
                    assertThat(e)
                        .hasMessageContaining("api error")
                        .isInstanceOf(ApiException.class))
            .verify();
      }
    }
  }
}
