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
package pixela.client.api;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public interface QueryParams {

  @NotNull
  static String concatAll(@NotNull final QueryParam... queryParams) {
    return Arrays.stream(queryParams)
        .map(QueryParam::asQuery)
        .flatMap(queryString -> queryString.map(Stream::of).orElseGet(Stream::empty))
        .collect(Collectors.joining("&"));
  }
}
