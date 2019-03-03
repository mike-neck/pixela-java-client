package pixela.client.http;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pixela.client.http.JsonDec.decoder;
import static pixela.client.http.Req.newReq;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pixela.client.ApiException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ResponseReaderTest {

  private Request<Obj> request;
  private HttpResponse response;

  @BeforeEach
  void setup() {
    this.request = newReq();
    this.response = mock(HttpResponse.class);
  }

  @Test
  void havingMatchingReader() {
    final ResponseReader<Obj> responseReader =
        new ResponseReader<>(
            res -> Mono.just(obj("default")),
            Arrays.asList(
                new StaticReader<>(request),
                new StaticReader<>(request, obj("test")),
                new StaticReader<>(request)));

    final Mono<Obj> mono = responseReader.read(response);

    StepVerifier.create(mono.map(o -> o.value)).expectNext("test").verifyComplete();
  }

  @Test
  void defaultReader() {
    final ResponseReader<Obj> responseReader =
        new ResponseReader<>(
            res -> Mono.just(obj("default")),
            Arrays.asList(
                new StaticReader<>(request),
                new StaticReader<>(request),
                new StaticReader<>(request)));

    final Mono<Obj> mono = responseReader.read(response);

    StepVerifier.create(mono.map(o -> o.value)).expectNext("default").verifyComplete();
  }

  @Test
  void viaJsonDecoderWithErrorResponse() {
    final ResponseReader<Obj> responseReader = ResponseReader.create(request, decoder());

    when(response.isErrorResponse()).thenReturn(true);
    when(response.body()).thenReturn("{\"isSuccess\":false,\"message\":\"test\"}");

    final Mono<Obj> mono = responseReader.read(response);

    StepVerifier.create(mono).expectError(ApiException.class).verify();
  }

  static Obj obj(final String value) {
    return new Obj(value);
  }

  static class StaticReader<T> implements HttpResponseReader<T> {

    @NotNull final Request<T> request;
    @Nullable final T object;

    StaticReader(@NotNull final Request<T> request) {
      this.request = request;
      this.object = null;
    }

    StaticReader(@NotNull final Request<T> request, @NotNull final T object) {
      this.request = request;
      this.object = object;
    }

    @Override
    public @NotNull Class<T> responseType() {
      return request.responseType();
    }

    @Override
    public boolean matchCondition(@NotNull final HttpResponse response) {
      return object != null;
    }

    @Override
    public @NotNull Mono<T> read(@NotNull final HttpResponse response) {
      if (object != null) {
        return Mono.just(object);
      }
      return Mono.empty();
    }
  }
}
