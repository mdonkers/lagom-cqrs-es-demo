/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.hello.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import nl.codecentric.lagom.hello.api.HelloService;
import nl.codecentric.lagom.helloread.api.HelloReadService;

/**
 * The module that binds the HelloService so that it can be served.
 */
public class HelloModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindServices(serviceBinding(HelloService.class, HelloServiceImpl.class));
    // Bind the Hello Read Service
    bindClient(HelloReadService.class);
  }
}
