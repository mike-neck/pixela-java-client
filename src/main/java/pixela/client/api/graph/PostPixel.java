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

import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import pixela.client.Api;
import pixela.client.ApiException;
import pixela.client.Graph;
import pixela.client.GraphId;
import pixela.client.http.Post;
import reactor.core.publisher.Mono;

public interface PostPixel extends Post<Void>, Api<Graph> {

  interface PixelGraphId {
    @NotNull
    PixelDate graphId(@NotNull final GraphId graphId);
  }

  interface PixelDate {
    @NotNull
    PixelQuantity date(@NotNull final LocalDate date);
  }

  interface PixelQuantity {

    @NotNull
    OptionData quantity(final int quantity) throws ApiException;

    @NotNull
    OptionData quantity(final double quantity) throws ApiException;
  }

  interface OptionData extends PostPixel {

    @NotNull
    Mono<PostPixel> optionData(@NotNull final Object pojo);

    @NotNull
    PostPixel optionDataJson(@NotNull final String json);

    @NotNull
    PostPixel noOptionData();
  }
}
