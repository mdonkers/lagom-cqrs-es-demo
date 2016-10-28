package nl.codecentric.lagom.helloread.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import nl.codecentric.lagom.helloread.api.HelloReadService;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public class HelloReadModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(HelloReadService.class, HelloReadServiceImpl.class));
    }

}
