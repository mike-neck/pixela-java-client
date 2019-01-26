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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class GraphDetailsTest {

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
          .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Test
  void mappingTest() throws IOException {
    final String json =
        "{\"graphs\":[{"
            + "\"id\":\"test-graph\","
            + "\"name\":\"graph-name\","
            + "\"unit\":\"commit\","
            + "\"type\":\"int\","
            + "\"color\":\"shibafu\","
            + "\"timezone\":\"Asia/Tokyo\","
            + "\"purgeCacheURLs\":[\"https://camo.githubusercontent.com/xxx/xxxx\"]}]}";
    final GraphDetails graphDetails = objectMapper.readValue(json, GraphDetails.class);

    assertThat(graphDetails.getGraphs())
        .hasSize(1)
        .satisfies(
            (GraphDetail graphDetail) ->
                assertAll(
                    () -> assertThat(graphDetail.getId()).isEqualTo("test-graph"),
                    () -> assertThat(graphDetail.getName()).isEqualTo("graph-name"),
                    () -> assertThat(graphDetail.getUnit()).isEqualTo("commit"),
                    () -> assertThat(graphDetail.getType()).isEqualTo("int"),
                    () -> assertThat(graphDetail.getColor()).isEqualTo("shibafu"),
                    () -> assertThat(graphDetail.getTimezone()).isEqualTo("Asia/Tokyo"),
                    () ->
                        assertThat(graphDetail.getPurgeCacheURLs())
                            .containsExactly("https://camo.githubusercontent.com/xxx/xxxx")),
            atIndex(0));
  }
}
