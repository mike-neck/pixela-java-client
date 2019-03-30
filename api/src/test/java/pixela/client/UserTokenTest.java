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

class UserTokenTest {

  @Test
  void shortTokenWillFail() {
    assertThatCode(() -> UserToken.validated("1234567")).isInstanceOf(ApiException.class);
  }

  @Test
  void longTokenWillFail() {
    final String token = Stream.generate(() -> "1").limit(129).collect(Collectors.joining());
    assertThatCode(() -> UserToken.validated(token)).isInstanceOf(ApiException.class);
  }

  @TestFactory
  Stream<DynamicTest> success() {
    final Instant now = Instant.now(Clock.systemUTC());
    final long seed = now.getEpochSecond();
    final Random random = new Random(seed);

    return Stream.generate(
            () ->
                Stream.generate(() -> new String(new char[] {(char) (32 + random.nextInt(95))}))
                    .limit((long) (8 + random.nextInt(121)))
                    .collect(Collectors.joining()))
        .limit(100L)
        .map(
            string ->
                DynamicTest.dynamicTest(
                    String.format("seed(%d) pattern(%s)", seed, string),
                    () -> assertThat(UserToken.of(string)).isNotNull()));
  }
}
