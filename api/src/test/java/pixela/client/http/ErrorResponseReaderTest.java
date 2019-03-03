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

class ErrorResponseReaderTest {

  private ErrorResponseReader<Obj> responseReader;

  @BeforeEach
  void setup() {
    final Request<Obj> request = newReq();
    responseReader = new ErrorResponseReader<>(request, decoder());
  }

  @Test
  void responseType() {
    assertThat(responseReader.responseType()).isEqualTo(Obj.class);
  }

  @Test
  void normalResponse() {
    final HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.isErrorResponse()).thenReturn(false);

    final Optional<Mono<Obj>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono).isEmpty();
  }

  @Test
  void errorResponse() {
    final HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.isErrorResponse()).thenReturn(true);
    when(httpResponse.body()).thenReturn("{\"message\":\"test\", \"isSuccess\":false}");

    final Optional<Mono<Obj>> mono = responseReader.readResponse(httpResponse);

    assertThat(mono)
        .isPresent()
        .hasValueSatisfying(
            m ->
                StepVerifier.create(m)
                    .expectErrorSatisfies(
                        throwable ->
                            assertThat(throwable)
                                .isInstanceOf(ApiException.class)
                                .hasMessageContaining("test")));
  }
}
