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
package org.mikeneck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

class Codec {

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  <T> List<T> loadFileAsList(final File file, final Class<T> klass) {
    final TypeFactory typeFactory = objectMapper.getTypeFactory();
    final CollectionType collectionType = typeFactory.constructCollectionType(List.class, klass);
    try {
      return objectMapper.readValue(file, collectionType);
    } catch (final IOException e) {
      throw new SlackException("failed to read file: " + file.getAbsolutePath(), e);
    }
  }

  String encode(final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (final IOException e) {
      throw new SlackException("failed to write json: " + object.toString(), e);
    }
  }
}
