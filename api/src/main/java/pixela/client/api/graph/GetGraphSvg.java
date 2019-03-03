/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pixela.client.api.graph;

import static java.time.temporal.ChronoField.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.UserToken;
import pixela.client.api.QueryParam;
import pixela.client.http.Get;
import pixela.client.http.HttpClient;
import reactor.util.function.Tuple2;

public interface GetGraphSvg extends Get<String>, Api<Tuple2<Graph, String>> {

  @Contract("_, _, _ -> new")
  @NotNull
  static GetGraphSvg.NoOption noOption(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph) {
    return new GetGraphSvgImpl(httpClient, pixela, graph, DateOption.empty(), ModeOption.NONE);
  }

  interface NoOption extends WithDateOption {

    @NotNull
    WithDateOption date(@NotNull final LocalDate date);
  }

  interface WithDateOption extends GetGraphSvg {

    @NotNull
    default GetGraphSvg shortMode() {
      return mode(ModeOption.SHORT);
    }

    @NotNull
    GetGraphSvg mode(@NotNull final ModeOption mode);
  }

  @NotNull
  @Override
  default Optional<UserToken> userToken() {
    return Optional.empty();
  }

  @NotNull
  @Override
  default Class<String> responseType() {
    return String.class;
  }

  @NotNull
  @Override
  default WithBody withBody() {
    return WithBody.FALSE;
  }

  enum ModeOption implements QueryParam {
    NONE {
      @NotNull
      @Override
      public Optional<String> asString() {
        return Optional.empty();
      }
    },
    SHORT {
      @NotNull
      @Override
      public Optional<String> asString() {
        return Optional.of("short");
      }
    },
    ;

    @NotNull
    @Override
    public String queryName() {
      return "mode";
    }

    @NotNull
    @Override
    public abstract Optional<String> asString();
  }

  @FunctionalInterface
  interface DateOption extends QueryParam {
    @NotNull
    @Override
    default String queryName() {
      return "date";
    }

    DateTimeFormatter FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter();

    @SuppressWarnings("NullableProblems")
    @NotNull
    @Contract(pure = true)
    static DateOption empty() {
      return Optional::empty;
    }

    @NotNull
    static DateOption of(@NotNull final LocalDate date) {
      return new DateOption() {
        @NotNull
        @Override
        public Optional<String> asString() {
          return Optional.of(date.format(FORMATTER));
        }

        @Override
        public String toString() {
          return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
      };
    }
  }
}
