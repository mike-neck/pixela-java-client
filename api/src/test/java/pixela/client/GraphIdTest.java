package pixela.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Clock;
import java.time.Instant;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class GraphIdTest {

  @Test
  void shortIdFails() {
    assertThatCode(() -> GraphId.validated("a")).isInstanceOf(ApiException.class);
  }

  @Test
  void startsWithNumberFails() {
    assertThatCode(() -> GraphId.validated("1a0")).isInstanceOf(ApiException.class);
  }

  @Test
  void hasUpperFails() {
    assertThatCode(() -> GraphId.validated("s1U")).isInstanceOf(ApiException.class);
  }

  @Test
  void longIdFails() {
    final String longId =
        Stream.generate(() -> "a").limit(1 + 16 + 1L).collect(Collectors.joining());
    assertThatCode(() -> GraphId.validated(longId)).isInstanceOf(ApiException.class);
  }

  @TestFactory
  Stream<DynamicTest> success() {
    final Instant now = Instant.now(Clock.systemUTC());
    final long seed = now.getEpochSecond();
    final Random random = new Random(seed);
    return Stream.generate(() -> Klass.random(16, random))
        .limit(100L)
        .map(
            string ->
                DynamicTest.dynamicTest(
                    String.format("seed(%d), pattern(%s)", seed, string),
                    () -> assertThat(GraphId.validated(string)).isNotNull()));
  }
}
