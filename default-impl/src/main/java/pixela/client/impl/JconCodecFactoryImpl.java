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
package pixela.client.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.jetbrains.annotations.NotNull;
import pixela.client.http.json.JsonCodecFactory;

import java.util.concurrent.ExecutorService;

public class JconCodecFactoryImpl implements JsonCodecFactory {


    static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @NotNull
    @Override
    public JsonCodec create(@NotNull final ExecutorService executorService) {
        return null;
    }
}
