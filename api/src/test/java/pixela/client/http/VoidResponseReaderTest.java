package pixela.client.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pixela.client.http.JsonDec.decoder;
import static pixela.client.http.Req.newReq;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pixela.client.ApiException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class VoidResponseReaderTest {

  private Request<Void> request;

  @BeforeEach
  void setup() {
    request = newRequest();
  }

  @Test
  void responseType() {
    final VoidResponseReader<Void> responseReader = new VoidResponseReader<>(request, decoder());
    assertThat(responseReader.responseType()).isEqualTo(Void.class);
  }

  @Test
  void readNonVoidTypeRequest() {
    final VoidResponseReader<Obj> responseReader = new VoidResponseReader<>(newReq(), decoder());
    final HttpResponse httpResponse = mock(HttpResponse.class);

    final Optional<Mono<Obj>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono).isEmpty();
  }

  @Test
  void readVoidTypeSuccessRequest() {
    final VoidResponseReader<Void> responseReader = new VoidResponseReader<>(request, decoder());
    final HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.body()).thenReturn("{\"message\":\"test\", \"isSuccess\":true}");

    final Optional<Mono<Void>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono).isPresent().hasValueSatisfying(m -> StepVerifier.create(m).verifyComplete());
  }

  @Test
  void readVoidTypeFailureRequest() {
    final VoidResponseReader<Void> responseReader = new VoidResponseReader<>(request, decoder());
    final HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.body()).thenReturn("{\"message\":\"test\", \"isSuccess\":false}");

    final Optional<Mono<Void>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono)
        .isPresent()
        .hasValueSatisfying(m -> StepVerifier.create(m).expectError(ApiException.class));
  }

  static Request<Void> newRequest() {
    final Req req = mock(Req.class);
    when(req.responseType()).thenReturn(Void.class);
    return req;
  }

  interface Req extends Request<Void> {}
}
