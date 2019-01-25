package pixela.client.api.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.*;
import pixela.client.http.HttpClient;

class NewGraphTest {

  @Nested
  class ViewUriTest {

    private HttpClient httpClient;
    private Pixela pixela;
    private Graph graph;

    @BeforeEach
    void setup() {
      this.httpClient = mock(HttpClient.class);
      this.pixela = mock(Pixela.class);
      this.graph =
          new NewGraph(
              httpClient,
              pixela,
              GraphId.of("abc123"),
              GraphName.of("name"),
              GraphUnit.of("commit"),
              Graph.Type.INT,
              Graph.Color.PURPLE,
              ZoneId.of("Asia/Tokyo"));
    }

    @Test
    void correctUri() {
      final URI baseUri = URI.create("https://pixe.la");
      when(httpClient.baseUri()).thenReturn(baseUri);
      when(pixela.usersUri(baseUri)).thenReturn(baseUri.resolve("/v1/users/user-name"));

      final URI uri = graph.viewUri();
      assertThat(uri).hasHost("pixe.la").hasPath("/v1/users/user-name/graphs/abc123.html");
    }
  }
}
