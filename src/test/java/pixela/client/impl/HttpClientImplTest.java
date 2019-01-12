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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pixela.client.ApiException;
import pixela.client.http.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class HttpClientImplTest {

  private final AutoCloseable doNothing = () -> {};

  private JdkHttpClient jdkHttpClient;
  private JdkRequestBuilder jdkRequestBuilder;

  @BeforeEach
  void setup() {
    this.jdkHttpClient = mock(JdkHttpClient.class);
    this.jdkRequestBuilder = mock(JdkRequestBuilder.class);
  }

  @Nested
  class PostTest {

    private Post<Void> postRequest;
    private HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
      this.postRequest = mock(Post.class);
      when(postRequest.errorRequest()).thenReturn("error-request");

      this.httpClient = new HttpClientImpl(doNothing, jdkHttpClient, jdkRequestBuilder);
    }

    @Nested
    class ApiExceptionWillBeCustomized {

      @BeforeEach
      void given() {
        when(jdkRequestBuilder.post(postRequest))
            .thenReturn(Mono.error(ApiException.of("building request")));
      }

      @Test
      void then() {
        final Response<Void> response = httpClient.post(postRequest);
        StepVerifier.create(response.toPublisher())
            .expectErrorSatisfies(
                e ->
                    assertThat(e)
                        .hasMessageContaining("building request")
                        .hasMessageContaining("error-request"))
            .verify();
      }
    }

    @Nested
    class IOExceptionWontBeCustomized {

      @BeforeEach
      void given() {
        when(jdkRequestBuilder.post(postRequest))
            .thenReturn(Mono.error(new IOException("building request")));
      }

      @Test
      void then() {
        final Response<Void> response = httpClient.post(postRequest);
        StepVerifier.create(response.toPublisher())
            .expectErrorSatisfies(
                e ->
                    assertAll(
                        () -> assertThat(e).hasMessageContaining("building request"),
                        () -> assertThat(e.getMessage()).doesNotContain("error-request")))
            .verify();
      }
    }
  }

  @Nested
  class DeleteTest {

    private Delete<Void> deleteRequest;
    private HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
      this.deleteRequest = mock(Delete.class);
      when(deleteRequest.errorRequest()).thenReturn("error-request");

      this.httpClient = new HttpClientImpl(doNothing, jdkHttpClient, jdkRequestBuilder);
    }

    @Nested
    class ApiExceptionWillBeCustomized {

      @BeforeEach
      void given() {
        when(jdkRequestBuilder.delete(deleteRequest))
            .thenReturn(Mono.error(ApiException.of("building request")));
      }

      @Test
      void then() {
        final Response<Void> response = httpClient.delete(deleteRequest);
        StepVerifier.create(response.toPublisher())
            .expectErrorSatisfies(
                e ->
                    assertThat(e)
                        .hasMessageContaining("building request")
                        .hasMessageContaining("error-request"))
            .verify();
      }
    }

    @Nested
    class IOExceptionWontBeCustomized {

      @BeforeEach
      void given() {
        when(jdkRequestBuilder.delete(deleteRequest))
            .thenReturn(Mono.error(new IOException("building request")));
      }

      @Test
      void then() {
        final Response<Void> response = httpClient.delete(deleteRequest);
        StepVerifier.create(response.toPublisher())
            .expectErrorSatisfies(
                e ->
                    assertAll(
                        () -> assertThat(e).hasMessageContaining("building request"),
                        () -> assertThat(e.getMessage()).doesNotContain("error-request")))
            .verify();
      }
    }
  }

  @Nested
  class GetTest {

    private Get<Void> getRequest;
    private HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
      this.getRequest = mock(Get.class);
      when(getRequest.errorRequest()).thenReturn("error-request");

      this.httpClient = new HttpClientImpl(doNothing, jdkHttpClient, jdkRequestBuilder);
    }

    @Nested
    class ApiExceptionWillBeCustomized {

      @BeforeEach
      void given() {
        when(jdkRequestBuilder.get(getRequest))
            .thenReturn(Mono.error(ApiException.of("building request")));
      }

      @Test
      void then() {
        final Response<Void> response = httpClient.get(getRequest);
        StepVerifier.create(response.toPublisher())
            .expectErrorSatisfies(
                e ->
                    assertThat(e)
                        .hasMessageContaining("building request")
                        .hasMessageContaining("error-request"))
            .verify();
      }
    }

    @Nested
    class IOExceptionWontBeCustomized {

      @BeforeEach
      void given() {
        when(jdkRequestBuilder.get(getRequest))
            .thenReturn(Mono.error(new IOException("error-request")));
      }

      @Test
      void then() {
        final Response<Void> response = httpClient.get(getRequest);
        StepVerifier.create(response.toPublisher())
            .expectErrorSatisfies(
                e ->
                    assertAll(
                        () -> assertThat(e).hasMessageContaining("building request"),
                        () -> assertThat(e.getMessage()).doesNotContain("error-request")))
            .verify();
      }
    }
  }

  @Nested
  class CloseTest {

    private final int[] array = new int[1];

    private HttpClient httpClient;

    @BeforeEach
    void setup() {
      array[0] = 0;
      final AutoCloseable autoCloseable = () -> array[0] = 100;
      this.httpClient = new HttpClientImpl(autoCloseable, jdkHttpClient, jdkRequestBuilder);
    }

    @Test
    void canClose() throws Exception {
      httpClient.close();
      assertThat(array[0]).isEqualTo(100);
    }
  }
}
