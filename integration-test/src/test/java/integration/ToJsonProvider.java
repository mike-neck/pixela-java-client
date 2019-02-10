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
package integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.lang.reflect.Parameter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ToJsonProvider implements ParameterResolver {

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Override
  public boolean supportsParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    final Class<?> type = parameter.getType();
    return ToJson.class.equals(type);
  }

  @Override
  public Object resolveParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    final Class<?> type = parameter.getType();
    if (!ToJson.class.equals(type)) {
      throw new ParameterResolutionException("Only ObjectMapper is supported.");
    }
    return ToJson.of(objectMapper);
  }
}
