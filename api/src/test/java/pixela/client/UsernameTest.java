package pixela.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Clock;
import java.time.Instant;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class UsernameTest {

  @Test
  void shortNameWillFail() {
    assertThatCode(() -> Username.validated("a")).isInstanceOf(ApiException.class);
  }

  @Test
  void startsWithNumberFail() {
    assertThatCode(() -> Username.validated("1a")).isInstanceOf(ApiException.class);
  }

  @Test
  void havingUpperCaseFail() {
    assertThatCode(() -> Username.validated("a1A")).isInstanceOf(ApiException.class);
  }

  @TestFactory
  Stream<DynamicTest> success() {
    final Instant now = Instant.now(Clock.systemUTC());
    final long seed = now.getEpochSecond();
    final Random random = new Random(seed);
    return Stream.generate(() -> Klass.random(random))
        .limit(100L)
        .map(
            string ->
                DynamicTest.dynamicTest(
                    String.format("seed(%d), pattern(%s)", seed, string),
                    () -> assertThat(Username.validated(string)).isNotNull()));
  }
}
