package pixela.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
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

  static class Range {
    final int min;
    final int size;

    Range(final int min, final int size) {
      this.min = min;
      this.size = size;
    }

    @Override
    public String toString() {
      return "range[min:" + min + ",size:" + size + "]";
    }
  }

  static class Choice {
    final int minInclusive;
    final int maxExclusive;
    final Range range;

    Choice(final int minInclusive, final int maxExclusive, final Range range) {
      this.minInclusive = minInclusive;
      this.maxExclusive = maxExclusive;
      this.range = range;
    }

    static Choices choices(final Range... ranges) {
      int max = 0;
      final List<Choice> choices = new ArrayList<>();
      for (final Range range : ranges) {
        choices.add(new Choice(max, max + range.size, range));
        max += range.size;
      }
      final List<Choice> list = Collections.unmodifiableList(choices);
      return new Choices(max, list);
    }

    @Override
    public String toString() {
      return "choice[min:" + minInclusive + ",max:" + maxExclusive + ",range:" + range + "]";
    }
  }

  static class Choices {
    final int max;
    final List<Choice> choices;

    Choices(final int max, final List<Choice> choices) {
      this.max = max;
      this.choices = choices;
    }

    String random(final Random random) {
      final int index = random.nextInt(max);
      final Choice choice =
          choices
              .stream()
              .filter(ch -> ch.minInclusive <= index && index < ch.maxExclusive)
              .findAny()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "index = "
                              + index
                              + ", max = "
                              + max
                              + "choice = "
                              + choices.toString()));
      final int i = index - choice.minInclusive;
      return new String(new char[] {(char) (choice.range.min + i)});
    }
  }

  enum Klass {
    LOWER {
      @Override
      Range range() {
        return new Range(97, 26);
      }
    },
    NUMBER {
      @Override
      Range range() {
        return new Range(48, 10);
      }
    },
    HYPHEN {
      @Override
      Range range() {
        return new Range(45, 1);
      }
    },
    ;

    abstract Range range();

    @NotNull
    static String random(final Random random) {
      final StringBuilder stringBuilder = new StringBuilder();
      final String firstChar = Choice.choices(LOWER.range()).random(random);
      stringBuilder.append(firstChar);

      final int length = random.nextInt(31) + 1;
      final Choices choices = Choice.choices(LOWER.range(), NUMBER.range(), HYPHEN.range());
      Stream.generate(() -> choices.random(random)).limit(length).forEach(stringBuilder::append);
      return stringBuilder.toString();
    }
  }
}
