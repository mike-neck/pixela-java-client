package pixela.client.api.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.ApiException;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UpdateUserTest {

  private HttpClient httpClient;
  private Pixela pixela;

  private UpdateUser updateUser;

  private final UserToken newToken = UserToken.of("new-token");

  @BeforeEach
  void setup() {
    httpClient = mock(HttpClient.class);
    pixela = mock(Pixela.class);
    this.updateUser = new UpdateUser(httpClient, pixela, newToken);
  }

  @Nested
  class ApiEndpoint {

    private final URI baseUrl = URI.create("https://example.com");

    private final URI expected = URI.create("https://example.com/v1/users/test-user");

    @BeforeEach
    void given() {
      when(pixela.usersUri(baseUrl)).thenReturn(expected);
    }

    @Test
    void then() {
      final URI endpoint = updateUser.apiEndpoint(baseUrl);
      assertThat(endpoint).isEqualTo(expected);
    }
  }

  @Nested
  class ErrorRequest {

    @BeforeEach
    void given() {
      when(pixela.usersUri()).thenReturn("/v1/users/test-user");
    }

    @Test
    void then() {
      final String request = updateUser.errorRequest();
      assertThat(request).contains("PUT /v1/users/test-user", "newToken: new-token");
    }
  }

  @Nested
  class Call {

    @Nested
    class Success {

      private final Pixela newPixela = mock(Pixela.class);

      @SuppressWarnings("Convert2MethodRef")
      @BeforeEach
      void given() {
        when(pixela.updateToken(newToken)).thenReturn(newPixela);
        when(httpClient.put(updateUser)).thenReturn(() -> Mono.empty());
      }

      @Test
      void then() {
        final Mono<Pixela> mono = updateUser.call();
        StepVerifier.create(mono)
            .assertNext(px -> assertThat(px).isEqualTo(newPixela))
            .verifyComplete();
      }
    }

    @Nested
    class Failure {

      @BeforeEach
      void given() {
        when(httpClient.put(updateUser))
            .thenReturn(() -> Mono.error(ApiException.of("user is not existing.")));
      }

      @Test
      void then() {
        final Mono<Pixela> mono = updateUser.call();
        StepVerifier.create(mono)
            .expectErrorSatisfies(
                e ->
                    assertThat(e)
                        .hasMessageContaining("user is not existing.")
                        .isInstanceOf(ApiException.class))
            .verify();
      }
    }
  }
}
