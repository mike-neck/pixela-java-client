package pixela.client.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JdkHttpClientTest {

  private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

  @AfterAll
  static void closeTest() {
    executorService.shutdown();
  }

  private final JsonDecoder decoder =
      JsonDecoder.forJackson(executorService, HttpClientImpl.objectMapper);

  private HttpClient httpClient;

  private JdkHttpClient client;

  @BeforeEach
  void setup() {
    this.httpClient = mock(HttpClient.class);
    this.client = JdkHttpClient.create(httpClient, decoder);
  }

  @Nested
  class HttpClientFinishedInIOException {

    @BeforeEach
    void given() {
      when(httpClient.sendAsync(any(), any()))
          .thenReturn(CompletableFuture.failedFuture(new IOException("test")));
    }

    @Test
    void responseIsError() {
      final Mono<JdkHttpResponse> response = client.sendRequest(mock(HttpRequest.class));

      StepVerifier.create(response).expectError(IOException.class).verify();
    }
  }

  interface Res extends HttpResponse<String> {}

  @Nested
  class HttpClientFinishedInNormalResponse {

    @BeforeEach
    void given() {
      final Res response = mock(Res.class);
      when(httpClient.sendAsync(any(), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
          .thenReturn(CompletableFuture.completedFuture(response));
    }

    @Test
    void responseIsEvent() {
      final Mono<JdkHttpResponse> response = client.sendRequest(mock(HttpRequest.class));

      StepVerifier.create(response).expectNextCount(1).verifyComplete();
    }
  }
}
