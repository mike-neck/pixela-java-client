package pixela.client.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JdkHttpResponseTest {

  private HttpResponse<String> response;

  private JdkHttpResponse jdkHttpResponse;

  private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

  @BeforeEach
  void setup() {
    response = mock(MockHttpResponse.class);
    final JsonDecoder decoder = new JsonCodecFactoryImpl().create(executorService);
    jdkHttpResponse = JdkHttpResponse.create(response, decoder);
  }

  interface MockHttpResponse extends HttpResponse<String> {}

  @AfterAll
  static void closeTest() {
    executorService.shutdown();
  }

  @Nested
  class Status4xx {

    @BeforeEach
    void setup() {
      when(response.statusCode()).thenReturn(400);
    }

    @Nested
    class ValidJson {

      @BeforeEach
      void setup() {
        when(response.body())
            .thenReturn("{" + "\"message\":\"error-message\"," + "\"isSuccess\":false" + "}");
      }

      @Test
      void apiExceptionWillReturned() {
        final Req request = mock(Req.class);
        final Mono<Model> mono = jdkHttpResponse.readObject(request);
        StepVerifier.create(mono)
            .expectErrorSatisfies(
                exception ->
                    assertThat(exception)
                        .isNotInstanceOf(IOException.class)
                        .hasMessage("error-message"))
            .verify();
      }
    }

    @Nested
    class InvalidJson {

      @BeforeEach
      void setup() {
        when(response.body()).thenReturn("{\"invalid json");
      }

      @Test
      void ioExceptionWillReturned() {
        final Req request = mock(Req.class);
        final Mono<Model> mono = jdkHttpResponse.readObject(request);
        StepVerifier.create(mono).expectError(IOException.class).verify();
      }
    }

    @Nested
    class NullMessage {

      @BeforeEach
      void setup() {
        when(response.body()).thenReturn("{\"isSuccess\":false}");
      }

      @Test
      void apiExceptionWillReturned() {
        final Req request = mock(Req.class);
        final Mono<Model> mono = jdkHttpResponse.readObject(request);
        StepVerifier.create(mono).expectErrorMessage("").verify();
      }
    }
  }

  @Nested
  class Status2xx {

    @BeforeEach
    void setup() {
      when(response.statusCode()).thenReturn(200);
    }

    @Nested
    class VoidType {

      private Request<Void> request;

      @BeforeEach
      void setup() {
        request = mock(VoidReq.class);
        doCallRealMethod().when(request).responseType();
      }

      @Nested
      class SuccessJson {

        @BeforeEach
        void setup() {
          when(response.body())
              .thenReturn("{" + "\"message\":\"error-message\"," + "\"isSuccess\":true" + "}");
        }

        @Test
        void emptyWillReturn() {
          final Mono<Void> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectComplete().verify();
        }
      }

      @Nested
      class NotSuccessJson {

        @BeforeEach
        void setup() {
          when(response.body())
              .thenReturn("{" + "\"message\":\"error-message\"," + "\"isSuccess\":false" + "}");
        }

        @Test
        void apiErrorWillReturn() {
          final Mono<Void> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectErrorMessage("error-message").verify();
        }
      }

      @Nested
      class InvalidJson {

        @BeforeEach
        void setup() {
          when(response.body()).thenReturn("{\"invalid json");
        }

        @Test
        void ioExceptionWillReturned() {
          final Mono<Void> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectError(IOException.class).verify();
        }
      }
    }

    @Nested
    class DataType {

      private Request<Model> request;

      @BeforeEach
      void setup() {
        request = mock(Req.class);
        doCallRealMethod().when(request).responseType();
      }

      @Nested
      class ValidJson {

        @BeforeEach
        void setup() {
          when(response.body()).thenReturn("{\"value\":\"test-value\"}");
        }

        @Test
        void modelWillReturned() {
          final Mono<Model> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectNext(new Model("test-value")).verifyComplete();
        }
      }

      @Nested
      class NullValueJson {

        @BeforeEach
        void setup() {
          when(response.body()).thenReturn("{}");
        }

        @Test
        void modelWillReturned() {
          final Mono<Model> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectNext(new Model("")).verifyComplete();
        }
      }

      @Nested
      class InvalidJson {

        @BeforeEach
        void setup() {
          when(response.body())
              .thenReturn("{" + "\"message\":\"error-message\"," + "\"isSuccess\":false" + "}");
        }

        @Test
        void ioExceptionWillReturned() {
          final Mono<Model> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectError(IOException.class).verify();
        }
      }
    }

    @Nested
    class StringType {

      private Request<String> request;

      @BeforeEach
      void setup() {
        request = mock(StringReq.class);
        doCallRealMethod().when(request).responseType();
      }

      @Nested
      class ReadObjectWillReturnTheSameStringAsResponse {

        @BeforeEach
        void setup() {
          when(response.body()).thenReturn("Response String");
        }

        @Test
        void theyAreSame() {
          final Mono<String> mono = jdkHttpResponse.readObject(request);
          StepVerifier.create(mono).expectNext("Response String").verifyComplete();
        }
      }
    }
  }

  interface Req extends Request<Model> {
    @NotNull
    @Override
    default Class<? extends Model> responseType() {
      return Model.class;
    }
  }

  static class Model {

    @NotNull private String value = "";

    Model() {}

    Model(@NotNull final String value) {
      this.value = value;
    }

    public void setValue(@NotNull final String value) {
      this.value = value;
    }

    @NotNull
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      @SuppressWarnings("StringBufferReplaceableByString")
      final StringBuilder sb = new StringBuilder("Model{");
      sb.append("value='").append(value).append('\'');
      sb.append('}');
      return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof Model)) return false;

      final Model model = (Model) o;

      return value.equals(model.value);
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

  interface VoidReq extends Request<Void> {
    @NotNull
    @Override
    default Class<? extends Void> responseType() {
      return Void.class;
    }
  }

  interface StringReq extends Request<String> {
    @NotNull
    @Override
    default Class<? extends String> responseType() {
      return String.class;
    }
  }
}
