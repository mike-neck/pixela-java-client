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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.Pixela;
import pixela.client.http.HttpClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FloatPostPixelTest {

  private final HttpClient httpClient = mock(HttpClient.class);

  private final Pixela pixela = mock(Pixela.class);

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
          .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  private String toJson(final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void jsonWithoutOptionalData() throws IOException {
    final FloatPostPixel postPixel =
        new FloatPostPixel(
            httpClient,
            pixela,
            Graph.simple(httpClient, pixela, GraphId.of("test")),
            LocalDate.of(2019, 1, 22),
            20.12);
    final String json = objectMapper.writeValueAsString(postPixel);
    assertAll(
        () -> assertThatJson(json).node("date").isString().isEqualTo("20190122"),
        () -> assertThatJson(json).node("quantity").isString().isEqualTo("20.12"),
        () -> assertThatJson(json).node("optionalData").isAbsent());
  }

  @SuppressWarnings({"UnassignedFluxMonoInstance", "Convert2MethodRef"})
  @Test
  void jsonWithOptionalData() {
    doAnswer(
            invocation -> {
              final Object object = invocation.getArgument(0);
              return Mono.just(toJson(object));
            })
        .when(httpClient)
        .encodeJson(any());

    final Map<String, String> map = Map.of("test", "value");

    final Mono<PostPixel> postPixel =
        new FloatPostPixel(
                httpClient,
                pixela,
                Graph.simple(httpClient, pixela, GraphId.of("test")),
                LocalDate.of(2019, 1, 22),
                20.12)
            .optionData(map);

    final Mono<String> mono = postPixel.map(post -> toJson(post));

    StepVerifier.create(mono)
        .assertNext(
            json ->
                assertAll(
                    () -> assertThatJson(json).node("date").isString().isEqualTo("20190122"),
                    () -> assertThatJson(json).node("quantity").isString().isEqualTo("20.12"),
                    () ->
                        assertThatJson(json)
                            .node("optionalData")
                            .isString()
                            .isEqualTo(toJson(map))))
        .verifyComplete();
  }
}
