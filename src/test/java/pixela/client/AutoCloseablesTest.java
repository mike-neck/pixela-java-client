package pixela.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class AutoCloseablesTest {

  @Test
  void noResources() throws Exception {
    try (final AutoCloseable autoCloseable = AutoCloseables.of()) {
      assertThat(autoCloseable).isNotNull();
    }
  }

  @Test
  void singleResource() throws Exception {
    final List<Integer> list = new ArrayList<>();
    try (final AutoCloseable autoCloseable = AutoCloseables.of(autoCloseable(0, list))) {
      assertThat(autoCloseable).isNotNull();
    }
    assertThat(list).containsExactly(0);
  }

  @Test
  void multipleResources() throws Exception {
    final List<Integer> list = new ArrayList<>();
    try (final AutoCloseable autoCloseable =
        AutoCloseables.of(
            autoCloseable(0, list),
            autoCloseable(1, list),
            autoCloseable(2, list),
            autoCloseable(3, list))) {
      assertThat(autoCloseable).isNotNull();
    }
    assertThat(list).containsExactly(3, 2, 1, 0);
  }

  @Test
  void multipleResourcesWithExceptionOccurring() {
    final List<Integer> list = new ArrayList<>();
    try (final AutoCloseable autoCloseable =
        AutoCloseables.of(
            autoCloseable(0, list),
            autoCloseable(1, list),
            autoCloseable(
                2,
                list,
                () -> {
                  throw new RuntimeException();
                }),
            autoCloseable(3, list))) {
      assertThat(autoCloseable).isNotNull();
    } catch (final Exception ignored) {
      list.add(100);
    }
    assertThat(list).containsExactly(3, 2, 1, 0, 100);
  }

  @Contract(pure = true)
  @NotNull
  static AutoCloseable autoCloseable(final int number, @NotNull final List<Integer> list) {
    return () -> list.add(number);
  }

  @Contract(pure = true)
  @NotNull
  static AutoCloseable autoCloseable(
      final int number,
      @NotNull final List<Integer> list,
      @NotNull final Runnable additionalAction) {
    return () -> {
      list.add(number);
      additionalAction.run();
    };
  }
}
