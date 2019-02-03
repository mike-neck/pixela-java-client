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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class JsonResolver implements ParameterResolver {

  private final ClassLoader loader = getClass().getClassLoader();

  @Override
  public boolean supportsParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Stream<Annotation> typeAnnotationStream =
        extensionContext
            .getTestClass()
            .stream()
            .map(Class::getDeclaredAnnotations)
            .flatMap(Arrays::stream);
    final Stream<Annotation> methodAnnotationStream =
        Arrays.stream(parameterContext.getDeclaringExecutable().getDeclaredAnnotations());
    final boolean annotationFound =
        Stream.of(typeAnnotationStream, methodAnnotationStream)
            .flatMap(s -> s)
            .anyMatch(JsonResource.class::isInstance);
    if (!annotationFound) {
      return false;
    }
    return String.class.equals(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      @NotNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Optional<Annotation> fromClass =
        extensionContext
            .getTestClass()
            .stream()
            .map(Class::getDeclaredAnnotations)
            .flatMap(Arrays::stream)
            .filter(JsonResource.class::isInstance)
            .findAny();
    final Optional<Annotation> fromMethod =
        Arrays.stream(parameterContext.getDeclaringExecutable().getDeclaredAnnotations())
            .filter(JsonResource.class::isInstance)
            .findAny();
    final Annotation jsonResource =
        fromMethod.orElseGet(
            () ->
                fromClass.orElseThrow(
                    () -> new ParameterResolutionException("JsonResource not found")));
    final String resourceName = ((JsonResource) jsonResource).name();
    if (loader.getResource(resourceName) == null) {
      throw new ParameterResolutionException(resourceName + " not found in resource.");
    }
    try (final Stream<String> lines =
        new BufferedReader(
                new InputStreamReader(
                    Objects.requireNonNull(loader.getResourceAsStream(resourceName))))
            .lines()) {
      return lines.collect(Collectors.joining());
    }
  }
}
