package pixela.client.impl;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import pixela.client.UserToken;
import pixela.client.http.Delete;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JdkDeleteRequestBuilderTest {

  private final URI uri = URI.create("https://example.com");

  private final JdkDeleteRequestBuilder deleteRequestBuilder = JdkDeleteRequestBuilder.of(uri);

  @Test
  void applyRequest() {
    final Mono<HttpRequest> actual = deleteRequestBuilder.apply(new Del());
    assertAll(
        () ->
            StepVerifier.create(actual.map(HttpRequest::method))
                .expectNext("DELETE")
                .verifyComplete(),
        () ->
            StepVerifier.create(actual.map(HttpRequest::bodyPublisher))
                .expectNext(Optional.empty())
                .verifyComplete(),
        () ->
            StepVerifier.create(actual.map(HttpRequest::uri))
                .expectNext(URI.create("https://example.com/delete"))
                .verifyComplete(),
        () ->
            StepVerifier.create(actual.map(HttpRequest::headers).map(HttpHeaders::map))
                .expectNext(Map.of(UserToken.X_USER_TOKEN, List.of("test-token")))
                .verifyComplete());
  }

  static class Del implements Delete<Void> {

    @NotNull
    @Override
    public URI apiEndpoint(@NotNull final URI baseUrl) {
      return baseUrl.resolve("/delete");
    }

    @NotNull
    @Override
    public Optional<UserToken> userToken() {
      return Optional.of(UserToken.of("test-token"));
    }

    @NotNull
    @Override
    public Class<? extends Void> responseType() {
      return Void.class;
    }

    @NotNull
    @Override
    public String errorRequest() {
      return "";
    }
  }
}
