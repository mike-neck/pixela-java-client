package pixela.client.api.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.ApiException;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

class GetGraphSvgTest {

  private HttpClient httpClient;
  private Pixela pixela;
  private SimpleGraph graph;

  @BeforeEach
  void setup() {
    this.httpClient = mock(HttpClient.class);
    this.pixela = mock(Pixela.class);
    graph = SimpleGraph.of(httpClient, pixela, GraphId.of("test-graph"));
  }

  @Nested
  class ApiEndpointTest {

    final URI baseUri = URI.create("https://example.com");

    @BeforeEach
    void setup() {
      when(pixela.usersUri(baseUri)).thenReturn(URI.create("https://example.com/v1/users/1122"));
    }

    @Test
    void withDate() {
      final GetGraphSvg getGraphSvg =
          GetGraphSvg.of(httpClient, pixela, graph).date(LocalDate.of(2019, 1, 2));
      final URI uri = getGraphSvg.apiEndpoint(baseUri);

      assertThat(uri)
          .hasScheme("https")
          .hasHost("example.com")
          .hasPath("/v1/users/1122/graphs/test-graph")
          .hasQuery("date=20190102");
    }

    @Test
    void withMode() {
      final GetGraphSvg getGraphSvg = GetGraphSvg.of(httpClient, pixela, graph).shortMode();
      final URI uri = getGraphSvg.apiEndpoint(baseUri);

      assertThat(uri)
          .hasScheme("https")
          .hasHost("example.com")
          .hasPath("/v1/users/1122/graphs/test-graph")
          .hasQuery("mode=short");
    }

    @Test
    void noOption() {
      final GetGraphSvg getGraphSvg = GetGraphSvg.of(httpClient, pixela, graph);
      final URI uri = getGraphSvg.apiEndpoint(baseUri);

      assertThat(uri)
          .hasScheme("https")
          .hasHost("example.com")
          .hasPath("/v1/users/1122/graphs/test-graph")
          .hasNoQuery();
    }

    @Test
    void hasBothOption() {
      final GetGraphSvg getGraphSvg =
          GetGraphSvg.of(httpClient, pixela, graph).date(LocalDate.of(2019, 1, 2)).shortMode();
      final URI uri = getGraphSvg.apiEndpoint(baseUri);

      assertThat(uri)
          .hasScheme("https")
          .hasHost("example.com")
          .hasPath("/v1/users/1122/graphs/test-graph")
          .hasQuery("date=20190102&mode=short");
    }
  }

  @Nested
  class CallTest {

    @Test
    void success() {
      final GetGraphSvg.NoOption getGraphSvg = GetGraphSvg.of(httpClient, pixela, graph);
      when(httpClient.get(getGraphSvg)).thenReturn(() -> Mono.just("svg-data"));
      final Mono<Tuple2<Graph, String>> response = getGraphSvg.call();
      StepVerifier.create(response)
          .consumeNextWith(
              tuple ->
                  assertAll(
                      () -> assertThat(tuple.getT2()).isEqualTo("svg-data"),
                      () -> assertThat(tuple.getT1()).isEqualTo(graph)))
          .verifyComplete();
    }

    @Test
    void failure() {
      when(pixela.usersUri()).thenReturn("/v1/users/1122");

      final GetGraphSvg.NoOption getGraphSvg = GetGraphSvg.of(httpClient, pixela, graph);
      when(httpClient.get(getGraphSvg))
          .thenReturn(() -> Mono.error(() -> ApiException.of("Failure")));
      final Mono<Tuple2<Graph, String>> response = getGraphSvg.call();
      StepVerifier.create(response)
          .expectErrorSatisfies(error -> assertThat(error).hasMessageContaining("Failure"))
          .verify();
    }
  }
}
