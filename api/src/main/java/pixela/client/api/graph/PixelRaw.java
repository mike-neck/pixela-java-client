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
import org.jetbrains.annotations.Nullable;
import pixela.client.Graph;
import pixela.client.Pixela;
import pixela.client.Quantity;
import pixela.client.http.HttpClient;

public class PixelRaw implements PixelDetail {

  @NotNull private Quantity quantity = Quantity.integer(0);

  @Nullable private String optionalData;

  @SuppressWarnings("unused")
  public PixelRaw() {}

  PixelRaw(@NotNull final Quantity quantity, @Nullable final String optionalData) {
    this.quantity = quantity;
    this.optionalData = optionalData;
  }

  @NotNull
  public String getQuantity() {
    return quantity.asString();
  }

  public void setQuantity(@NotNull final String quantity) {
    this.quantity = Quantity.string(quantity);
  }

  @Nullable
  public String getOptionalData() {
    return optionalData;
  }

  public void setOptionalData(@Nullable final String optionalData) {
    this.optionalData = optionalData;
  }

  @NotNull
  pixela.client.Pixel toPixel(
      @NotNull final HttpClient httpClient,
      @NotNull final Pixela pixela,
      @NotNull final Graph graph,
      @NotNull final LocalDate date) {
    return new PixelImpl(httpClient, pixela, graph, date, this);
  }

  @NotNull
  @Override
  public PixelDetail increment() {
    return new PixelRaw(quantity.increment(), optionalData);
  }

  @NotNull
  @Override
  public PixelDetail decrement() {
    return new PixelRaw(quantity.decrement(), optionalData);
  }

  @NotNull
  @Override
  public String quantity() {
    return quantity.asString();
  }

  @Nullable
  @Override
  public String optionalDataString() {
    return optionalData;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("PixelRaw{");
    sb.append("quantity='").append(quantity).append('\'');
    sb.append(", optionalData='").append(optionalData).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
