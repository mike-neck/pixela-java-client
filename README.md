pixela-java-client
---

[Pixela](https://pixe.la/) API async Client for Java.

[![CircleCI](https://circleci.com/gh/mike-neck/pixela-java-client.svg?style=svg)](https://circleci.com/gh/mike-neck/pixela-java-client)
[![codecov](https://codecov.io/gh/mike-neck/pixela-java-client/branch/master/graph/badge.svg)](https://codecov.io/gh/mike-neck/pixela-java-client)

Requirements
---

* Java 11
    * Since this client uses Java 11's `java.net.http` package, user application is required to use Java11. But 
    optionally this client has extension point for those who not using Java11, you can use this library on Java 8 
    implementing `pixela.client.http.HttpClient` interface.

Current version
---

`0.1`

Installation
---

### Maven

```xml
<dependency>
  <groupId>org.mikeneck.pixela</groupId>
  <artifactId>pixela-java-client-api</artifactId>
  <version>0.1</version>
</dependency>
<dependency>
  <groupId>org.mikeneck.pixela</groupId>
  <artifactId>pixela-java-client-default-impl</artifactId>
  <version>0.1</version>
</dependency>
```

### Gradle

```groovy
dependencies {
  implementation "org.mikeneck.pixela:pixela-java-client-api:$PIXELA_CLIENT_VERSION"
  implementation "org.mikeneck.pixela:pixela-java-client-default-impl:$PIXELA_CLIENT_VERSION"
}
```

Usages
---

```java
import pixela.client.*;
import java.time.ZoneId;

class Main {
    public static void main(final String... args) throws Exception {
        final PixelaClient client = Pixela.withDefaultJavaClient();

        // create a Pixela instance
        final Pixela pixela = client.username("YOUR_NAME").token("YOUR_TOKEN");

        // create a new user
        final Mono<Pixela> newUser =
            client.createUser()
                .withToken("new-token")
                .username("new-user-name")
                .agreeTermsOfService()
                .notMinor()
                .call();

        // create graph
        final Mono<Graph> graph =
            pixela
                .createGraph()
                .id("test-graph")
                .name("graph-name")
                .unit("commit")
                .integer()
                .shibafu()
                .timezone(ZoneId.of("Asia/Tokyo"))
                .call();
        final Mono<Pixel> pixel = graph
                .map(Graph::postPixel)
                .map(postPixel -> postPixel.date(LocalDate.of(2019, 1, 2)))
                .map(pixelQuantity -> pixelQuantity.quantity(5))
                .flatMap(PostPixel::call);

        // increment pixel
        final Mono<Grap> incrementPixel =
            pixel.map(Pixel::graph)
                .map(Graph::incrementPixel)
                .flatMap(IncrementPixel::call);

        // delete graph
        final Mono<Pixela> deleteGraph =
            incrementPixel
                .map(Graph::delete)
                .flatMap(DeleteGraph::call);

        // delete user
        final Mono<Void> deleteUser =
            deleteGraph.map(Pixela::deleteUser)
                .flatMap(DeleteUser::call);

        // subscribe
        final CountDownLatch latch = new CountDownLatch(1);
        final Disposable disposable = deleteUser
                .doOnTerminate(latch::countDown)
                .subscribe();

        latch.await();
        Disposables.composit(disposable, client).dispose();
    }    
}
```
