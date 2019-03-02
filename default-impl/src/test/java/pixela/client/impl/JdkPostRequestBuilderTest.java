package pixela.client.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pixela.client.UserToken;
import pixela.client.http.Post;
import pixela.client.http.Request;
import pixela.client.http.json.JsonEncoder;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JdkPostRequestBuilderTest {

  private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

  @AfterAll
  static void closeTest() {
    executorService.shutdown();
  }

  private final JsonEncoder encoder = new JsonCodecFactoryImpl().create(executorService);

  @Test
  void noBodyRequest() throws InterruptedException {
    final Req req = new Req(Request.WithBody.FALSE);
    final RequestConfigurer configurer =
        (request, pub) -> {
          final Flux<ByteBuffer> publisher = JdkFlowAdapter.flowPublisherToFlux(pub);
          StepVerifier.create(publisher).expectComplete().verify();
          return mock(HttpRequest.class);
        };

    final JdkPostRequestBuilder builder = new JdkPostRequestBuilder(encoder, configurer);

    final CountDownLatch latch = new CountDownLatch(1);

    builder.apply(req).doOnTerminate(latch::countDown).subscribe();
    latch.await();
  }

  @Test
  void withBodyRequest() {
    final Req req = new Req(Request.WithBody.TRUE);
    final RequestConfigurer configurer =
        (request, pub) -> {
          final Mono<String> publisher =
              JdkFlowAdapter.flowPublisherToFlux(pub)
                  .buffer()
                  .next()
                  .map(JdkPostRequestBuilderTest::concatBuffer)
                  .map(buf -> new String(buf.array(), StandardCharsets.UTF_8))
                  .log();
          StepVerifier.create(publisher).expectNext("{\"value\":\"test\"}").verifyComplete();
          return HttpRequest.newBuilder(URI.create("https://example.com")).build();
        };

    final JdkPostRequestBuilder builder = new JdkPostRequestBuilder(encoder, configurer);

    final Mono<HttpRequest> request = builder.apply(req);
    StepVerifier.create(request).expectNextCount(1L).verifyComplete();
  }

  static ByteBuffer concatBuffer(final List<ByteBuffer> buffers) {
    final int size = buffers.stream().mapToInt(ByteBuffer::remaining).sum();
    final ByteBuffer newBuffer = ByteBuffer.allocate(size);
    buffers.forEach(newBuffer::put);
    newBuffer.flip();
    return newBuffer;
  }

  static class ConfigurerTest {

    private final URI baseUri = URI.create("https://example.com");

    private RequestConfigurer configurer;

    @BeforeEach
    void setup() {
      this.configurer = new JdkPostRequestBuilder.ReqConfigurer(baseUri);
    }

    static Stream<Req> testData() {
      return Stream.of(
          new Req(
              "/v1/users/create",
              null,
              Request.WithBody.FALSE,
              expectedHeaders(header("Content-Type", "application/json"))),
          new Req(
              "/v1/users/100",
              "user-token",
              Request.WithBody.TRUE,
              expectedHeaders(
                  header("X-USER-TOKEN", "user-token"),
                  header("Content-Type", "application/json"))));
    }

    @ParameterizedTest
    @MethodSource("testData")
    void expectedRequest(final Req request) {
      final HttpRequest httpRequest =
          configurer.configureRequest(request, HttpRequest.BodyPublishers.noBody());
      final Map<String, List<String>> actualHeaders = httpRequest.headers().map();
      assertThat(actualHeaders).containsAllEntriesOf(request.expectedHeaders);
    }
  }

  private static ExpectedHeader header(final String name, final String... values) {
    return new ExpectedHeader(name, List.of(values));
  }

  private static Map<String, List<String>> expectedHeaders(final ExpectedHeader... headers) {
    return Arrays.stream(headers)
        .filter(ExpectedHeader::hasValue)
        .collect(Collectors.toMap(h -> h.name, h -> h.values));
  }

  static class ExpectedHeader {
    final String name;
    final List<String> values;

    ExpectedHeader(final String name, final List<String> values) {
      this.name = name;
      this.values = values;
    }

    boolean hasValue() {
      return values.size() > 0;
    }
  }

  static class Req implements Post<Void> {
    private final String path;
    @Nullable private final String userToken;
    @NotNull private final WithBody withBody;
    @NotNull private final Map<String, List<String>> expectedHeaders;

    Req(@NotNull final WithBody withBody) {
      this.path = "/v1/users/create";
      this.userToken = null;
      this.withBody = withBody;
      this.expectedHeaders = Collections.emptyMap();
    }

    Req(
        final String path,
        @Nullable final String userToken,
        @NotNull final WithBody withBody,
        @NotNull final Map<String, List<String>> expectedHeaders) {
      this.path = path;
      this.userToken = userToken;
      this.withBody = withBody;
      this.expectedHeaders = expectedHeaders;
    }

    public String getValue() {
      return "test";
    }

    @NotNull
    @Override
    public URI apiEndpoint(@NotNull final URI baseUrl) {
      return baseUrl.resolve(path);
    }

    @NotNull
    @Override
    public Optional<UserToken> userToken() {
      return Optional.ofNullable(userToken).map(UserToken::of);
    }

    @NotNull
    @Override
    public WithBody withBody() {
      return withBody;
    }

    @NotNull
    @Override
    public Class<Void> responseType() {
      return Void.class;
    }

    @NotNull
    @Override
    public String errorRequest() {
      return "";
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("Req{");
      sb.append("path='").append(path).append('\'');
      sb.append(", userToken='").append(userToken).append('\'');
      sb.append(", withBody=").append(withBody);
      sb.append('}');
      return sb.toString();
    }
  }
}
