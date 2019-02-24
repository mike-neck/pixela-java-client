package pixela.client.java8;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import pixela.client.ApiException;
import pixela.client.UserToken;
import pixela.client.http.Request;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JsonCodecTest {

  private final JsonCodec jsonCodec = JsonCodec.ofDefaultJackson();

  @Test
  void hasBody() {
    final MockRequest mockRequest = new MockRequest(true);
    final Mono<String> mono = jsonCodec.encode(mockRequest);

    StepVerifier.create(mono)
        .assertNext(json -> assertThatJson(json).node("name").isString().isEqualTo("mock-request"))
        .verifyComplete();
  }

  @Test
  void hasNoBody() {
    final MockRequest mockRequest = new MockRequest(false);
    final Mono<String> mono = jsonCodec.encode(mockRequest);
    StepVerifier.create(mono).verifyComplete();
  }

  @Test
  void decodeSuccess() {
    final String json = "{\"name\":\"test\"}";
    final Mono<Res> mono = jsonCodec.decode(json, Res.class);
    StepVerifier.create(mono)
        .assertNext(obj -> assertThat(obj.name).isEqualTo("test"))
        .verifyComplete();
  }

  @Test
  void decodeFailure() {
    final String json = "{[]}";
    final Mono<Res> mono = jsonCodec.decode(json, Res.class);
    StepVerifier.create(mono)
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("While decoding json, error occurred"))
        .verify();
  }

  static class MockRequest implements Request<Void> {

    private final boolean body;

    MockRequest(final boolean body) {
      this.body = body;
    }

    public String getName() {
      return "mock-request";
    }

    @Override
    public boolean hasBody() {
      return body;
    }

    @Override
    public @NotNull URI apiEndpoint(@NotNull final URI baseUrl) {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Optional<UserToken> userToken() {
      return Optional.empty();
    }

    @Override
    public @NotNull WithBody withBody() {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Class<? extends Void> responseType() {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String errorRequest() {
      throw new UnsupportedOperationException();
    }
  }

  static class Res {
    String name;

    public void setName(final String name) {
      this.name = name;
    }
  }
}
