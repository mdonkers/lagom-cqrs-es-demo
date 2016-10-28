/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.hello.impl;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import nl.codecentric.lagom.hello.api.GreetingMessage;
import nl.codecentric.lagom.hello.api.HelloService;
import nl.codecentric.lagom.hello.impl.HelloCommand.*;
import nl.codecentric.lagom.helloread.api.HelloReadService;
import nl.codecentric.lagom.helloread.api.StoreHelloMessage;

/**
 * Implementation of the HelloService.
 */
public class HelloServiceImpl implements HelloService {

  private final PersistentEntityRegistry persistentEntityRegistry;

  private final HelloReadService helloReadService;

  @Inject
  public HelloServiceImpl(PersistentEntityRegistry persistentEntityRegistry, HelloReadService helloReadService) {
    this.persistentEntityRegistry = persistentEntityRegistry;
    persistentEntityRegistry.register(HelloEntity.class);
    this.helloReadService = helloReadService;
  }

  @Override
  public ServiceCall<NotUsed, String> hello(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
      // Ask the entity the Hello command.
      return ref.ask(new Hello(id, Optional.empty()));
    };
  }

  @Override
  public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
      // Tell the entity to use the greeting message specified.
      return ref.ask(new UseGreetingMessage(request.message)).thenCompose(
              // Store the entity also in the READ side
              a -> helloReadService.storeHello(id).invoke(new StoreHelloMessage(id, request.message))
      );
    };

  }

}
