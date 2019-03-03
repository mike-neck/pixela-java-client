/*
 * Copyright 2018 Shinya Mochida
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

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import pixela.client.AutoCloseables;
import pixela.client.PixelaClientConfig;
import pixela.client.http.*;
import pixela.client.http.json.JsonCodec;
import pixela.client.http.json.JsonCodecFactory;
import pixela.client.http.json.JsonDecoder;
import pixela.client.http.json.JsonEncoder;
import reactor.core.publisher.Mono;

class HttpClientImpl implements pixela.client.http.HttpClient {

  @NotNull private final AutoCloseable executors;

  @NotNull private final JdkHttpClient httpClient;
  @NotNull private final JdkRequestBuilder jdkRequestBuilder;

  @NotNull private final SupplierExecutor executor;

  HttpClientImpl(@NotNull final PixelaClientConfig config) {
    final ExecutorService executorServiceForJackson = Executors.newSingleThreadExecutor();
    final ExecutorService executorServiceForHttpClient =
        Executors.newFixedThreadPool(config.getThreadsNum());
    this.executors =
        AutoCloseables.of(
            executorServiceForHttpClient::shutdown, executorServiceForJackson::shutdown);
    final JsonCodec codec = JsonCodecFactory.getInstance().create(executorServiceForJackson);
    this.httpClient = JdkHttpClient.create(executorServiceForHttpClient, codec, config);
    this.jdkRequestBuilder = JdkRequestBuilder.create(config.baseUri(), codec);
    this.executor = SupplierExecutor.fromExecutorService(executorServiceForHttpClient);
  }

  @TestOnly
  HttpClientImpl(
      @NotNull final AutoCloseable executors,
      @NotNull final JdkHttpClient httpClient,
      @NotNull final JdkRequestBuilder jdkRequestBuilder) {
    this.executors = executors;
    this.httpClient = httpClient;
    this.jdkRequestBuilder = jdkRequestBuilder;
    this.executor = SupplierExecutor.noExecutor();
  }

  @Override
  @NotNull
  public Mono<String> encodeJson(@NotNull final Object object) {
    return jdkRequestBuilder.encodeJson(object);
  }

  @NotNull
  @Override
  public JsonEncoder encoder() {
    return jdkRequestBuilder::encodeJson;
  }

  @NotNull
  @Override
  public <T> Mono<T> decodeJson(@NotNull final String json, @NotNull final Class<T> type) {
    return httpClient.decodeJson(json, type);
  }

  @NotNull
  @Override
  public JsonDecoder decoder() {
    return httpClient::decodeJson;
  }

  @NotNull
  @Override
  public <T> Mono<T> runAsync(@NotNull final Supplier<? extends T> supplier) {
    return executor.runSupplier(supplier);
  }

  @NotNull
  @Override
  public URI baseUri() {
    return jdkRequestBuilder.baseUri();
  }

  @NotNull
  @Override
  public <T> Mono<HttpResponse> runGet(@NotNull final Get<T> getRequest) {
    final Mono<HttpRequest> httpRequest = jdkRequestBuilder.get(getRequest);
    return httpRequest.flatMap(httpClient::sendRequest);
  }

  @NotNull
  @Override
  public <T> Mono<HttpResponse> runPost(@NotNull final Post<T> postRequest) {
    final Mono<HttpRequest> httpRequest = jdkRequestBuilder.post(postRequest);
    return httpRequest.flatMap(httpClient::sendRequest);
  }

  @NotNull
  @Override
  public <T> Mono<HttpResponse> runPut(@NotNull final Put<T> putRequest) {
    final Mono<HttpRequest> httpRequest = jdkRequestBuilder.put(putRequest);
    return httpRequest.flatMap(httpClient::sendRequest);
  }

  @NotNull
  @Override
  public <T> Mono<HttpResponse> runDelete(@NotNull final Delete<T> deleteRequest) {
    final Mono<HttpRequest> delete = jdkRequestBuilder.delete(deleteRequest);
    return delete.flatMap(httpClient::sendRequest);
  }

  @Override
  public void close() throws Exception {
    executors.close();
  }
}
