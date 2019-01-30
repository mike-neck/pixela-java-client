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
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public interface ParameterRequest {

  @NotNull
  Class<?> type();

  @NotNull
  Collection<Annotation> annotations();

  @NotNull
  static ParameterRequest of(@NotNull final Class<?> type) {
    return of(type, Collections.emptyList());
  }

  @NotNull
  static ParameterRequest of(
      @NotNull final Class<?> type, @NotNull final Collection<Annotation> annotations) {
    return new ParameterRequest() {
      @NotNull
      @Override
      public Class<?> type() {
        return type;
      }

      @NotNull
      @Override
      public Collection<Annotation> annotations() {
        return annotations;
      }
    };
  }
}
