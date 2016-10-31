/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.hello.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;


import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;

import akka.Done;
import akka.NotUsed;
import nl.codecentric.lagom.hello.api.GreetingMessage;
import nl.codecentric.lagom.hello.api.HelloService;
import nl.codecentric.lagom.helloread.api.HelloReadService;
import nl.codecentric.lagom.helloread.api.StoreHelloMessage;

public class HelloServiceTest {

  private static class HelloReadServiceStub implements HelloReadService {
    @Override
    public ServiceCall<StoreHelloMessage, Done> storeHello(String id) {
      return req -> CompletableFuture.completedFuture(Done.getInstance());
    }
  }

  private final ServiceTest.Setup setup = defaultSetup()
          .withCassandra(true)
          .withConfigureBuilder(b -> b.overrides(
                  bind(HelloReadService.class).to(HelloReadServiceStub.class)));

  @Test
  public void shouldStorePersonalizedGreeting() throws Exception {
    withServer(setup, server -> {
      HelloService service = server.client(HelloService.class);

      String msg1 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hello, Alice!", msg1); // default greeting

      service.useGreeting("Alice").invoke(new GreetingMessage("Hi")).toCompletableFuture().get(5, SECONDS);
      String msg2 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hi, Alice!", msg2);

      String msg3 = service.hello("Bob").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hello, Bob!", msg3); // default greeting
    });
  }

}
