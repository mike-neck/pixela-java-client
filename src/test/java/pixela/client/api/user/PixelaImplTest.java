package pixela.client.api.user;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.Username;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class NewPixelaTest {

  private HttpClient httpClient;

  @BeforeEach
  void setup() {
    this.httpClient = mock(HttpClient.class);
  }

  @Nested
  class PersistAsFileTest {

    @Test
    void existsNewFile() throws IOException {
      final Pixela pixela =
          NewPixela.of(httpClient, UserToken.of("abc123"), Username.of("test-user"));
      final Path directory = Files.createTempDirectory("test");
      final Path propertyFile = directory.resolve("pixela.properties");

      final Mono<Void> mono = pixela.persistAsFile(propertyFile);
      final Mono<Boolean> result = mono.thenReturn(propertyFile).map(path -> Files.exists(path));

      StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    void fileContents() throws IOException {
      final Pixela pixela =
          NewPixela.of(httpClient, UserToken.of("abc123"), Username.of("test-user"));
      final Path directory = Files.createTempDirectory("test");
      final Path propertyFile = directory.resolve("pixela.properties");

      final Mono<Void> mono = pixela.persistAsFile(propertyFile);
      final Flux<String> result =
          mono.thenReturn(propertyFile)
              .filter(path -> Files.exists(path))
              .flatMap(path -> Mono.fromCallable(() -> load(path)))
              .flatMapMany(
                  p ->
                      Flux.just(
                          p.getProperty(UserToken.USER_TOKEN_PROPERTY_KEY, ""),
                          p.getProperty(Username.USER_NAME_PROPERTY_KEY)))
              .log();

      StepVerifier.create(result).expectNext("abc123").expectNext("test-user").verifyComplete();
    }

    private Properties load(final Path path) throws IOException {
      final Properties properties = new Properties();
      try (final Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
        properties.load(reader);
        return properties;
      }
    }
  }
}
