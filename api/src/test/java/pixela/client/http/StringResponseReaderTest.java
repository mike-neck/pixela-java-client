package pixela.client.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static pixela.client.http.Req.newReq;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class StringResponseReaderTest {

  @Test
  void responseType() {
    final StringResponseReader<String> responseReader = new StringResponseReader<>(mock(Req.class));
    final Class<String> type = responseReader.responseType();
    assertThat(type).isEqualTo(String.class);
  }

  @Test
  void notStringTypeResponse() {
    final StringResponseReader<Obj> responseReader = new StringResponseReader<>(newReq());
    final HttpResponse httpResponse = mock(HttpResponse.class);

    final Optional<Mono<Obj>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono).isEmpty();
  }

  @Test
  void stringTypeResponse() {
    final Request<String> req = mock(Req.class);
    doCallRealMethod().when(req).responseType();

    final StringResponseReader<String> responseReader = new StringResponseReader<>(req);
    final HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.body()).thenReturn("test");

    final Optional<Mono<String>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono)
        .isPresent()
        .hasValueSatisfying(m -> StepVerifier.create(m).expectNext("test").verifyComplete());
  }

  interface Req extends Request<String> {
    @Override
    @NotNull
    default Class<String> responseType() {
      return String.class;
    }
  }
}
