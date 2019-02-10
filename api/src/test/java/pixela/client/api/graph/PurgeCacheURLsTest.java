package pixela.client.api.graph;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PurgeCacheURLsTest {

  @Nested
  class NotUpdate {

    final PurgeCacheURLs notUpdate = PurgeCacheURLs.NOT_UPDATE;

    @Test
    void toBeUpdatedIsFalse() {
      assertThat(notUpdate.toBeUpdated()).isFalse();
    }

    @Test
    void valuesWillReturnNull() {
      assertThat(notUpdate.values()).isNull();
    }

    @Test
    void asStringListReturnsNull() {
      assertThat(notUpdate.asStringList()).isNull();
    }
  }

  @Nested
  class Empty {

    final PurgeCacheURLs empty = PurgeCacheURLs.remove();

    @Test
    void toBeUpdatedIsTrue() {
      assertThat(empty.toBeUpdated()).isTrue();
    }

    @Test
    void valuesWillReturnEmpty() {
      assertThat(empty.values()).isEmpty();
    }

    @Test
    void asStringListReturnsEmpty() {
      assertThat(empty.asStringList()).isEmpty();
    }
  }

  @Nested
  class Update {

    final PurgeCacheURLs update =
        PurgeCacheURLs.update(
            Arrays.asList(
                URI.create("https://example.com/test"),
                URI.create("https://www.example.com/test")));

    @Test
    void toBeUpdatedIsTrue() {
      assertThat(update.toBeUpdated()).isTrue();
    }

    @Test
    void valuesWillReturnNonEmpty() {
      assertThat(update.values())
          .containsExactly(
              URI.create("https://example.com/test"), URI.create("https://www.example.com/test"));
    }

    @Test
    void asStringListReturnsNonEmpty() {
      assertThat(update.asStringList())
          .containsExactly("https://example.com/test", "https://www.example.com/test");
    }
  }
}
