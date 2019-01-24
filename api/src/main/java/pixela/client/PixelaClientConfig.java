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

import java.net.URI;
import org.jetbrains.annotations.NotNull;

// TODO consider proxy
public class PixelaClientConfig {

  @NotNull private static final String BASE_URI = "https://pixe.la";

  private static final int TIME_OUT = 5_000; // millisec

  private static final int THREADS_NUM = 1; // executors thread size for http-client

  public static ServiceUri builder() {
    return uri ->
        milliSeconds -> threadsNum -> () -> new PixelaClientConfig(uri, milliSeconds, threadsNum);
  }

  @FunctionalInterface
  interface ServiceUri extends TimeoutMillis {
    TimeoutMillis serviceUri(final String uri);

    @Override
    default ThreadsNum timeout(final int milliSeconds) {
      return serviceUri(BASE_URI).timeout(milliSeconds);
    }
  }

  @FunctionalInterface
  interface TimeoutMillis extends ThreadsNum {
    ThreadsNum timeout(final int milliSeconds);

    @Override
    default Builder threads(final int threadsNum) {
      return timeout(TIME_OUT).threads(threadsNum);
    }
  }

  @FunctionalInterface
  interface ThreadsNum extends Builder {
    Builder threads(final int threadsNum);

    @Override
    default PixelaClientConfig build() {
      return threads(THREADS_NUM).build();
    }
  }

  interface Builder {
    PixelaClientConfig build();
  }

  @NotNull private String baseUri = BASE_URI;

  private int timeout; // millisec

  private int threadsNum; // executors thread size for http-client

  public PixelaClientConfig() {}

  private PixelaClientConfig(
      @NotNull final String baseUri, final int timeout, final int threadsNum) {
    this.baseUri = baseUri;
    this.timeout = timeout;
    this.threadsNum = threadsNum;
  }

  public URI baseUri() {
    return URI.create(baseUri);
  }

  @NotNull
  public String getBaseUri() {
    return baseUri;
  }

  public void setBaseUri(@NotNull final String baseUri) {
    this.baseUri = baseUri;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(final int timeout) {
    this.timeout = timeout;
  }

  public int getThreadsNum() {
    return threadsNum;
  }

  public void setThreadsNum(final int threadsNum) {
    this.threadsNum = threadsNum;
  }
}
