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
package pixela.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SupportedType {

  default boolean matches(
      @NotNull final Class<?> parameterType, @NotNull final Collection<Annotation> annotations) {
    return matches(ParameterRequest.of(parameterType, annotations));
  }

  default boolean matches(@NotNull final ParameterRequest request) {
    return Stream.of(typeMatcher(), conditionMatcher())
        .allMatch(predicate -> predicate.test(request));
  }

  default Predicate<ParameterRequest> typeMatcher() {
    return request -> supportedType().equals(request.type());
  }

  default Predicate<ParameterRequest> conditionMatcher() {
    return request -> condition(request.annotations());
  }

  @NotNull
  Class<?> supportedType();

  boolean condition(@NotNull Collection<Annotation> methodAnnotations);

  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static SupportedType of(@NotNull final Class<?> type) {
    return new SupportedType() {
      @NotNull
      @Override
      public Class<?> supportedType() {
        return type;
      }

      @Override
      public boolean condition(@NotNull final Collection<Annotation> methodAnnotations) {
        return true;
      }
    };
  }

  @NotNull
  static SupportedType typeWithMethodAnnotation(
      @NotNull final Class<?> klass, @NotNull final Class<? extends Annotation> annotation) {
    return new SupportedType() {
      @NotNull
      @Override
      public Class<?> supportedType() {
        return klass;
      }

      @Override
      public boolean condition(@NotNull final Collection<Annotation> methodAnnotations) {
        return methodAnnotations.stream().anyMatch(annotation::isInstance);
      }
    };
  }
}
