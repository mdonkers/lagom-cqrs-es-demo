package nl.codecentric.lagom.voter.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import nl.codecentric.lagom.voter.api.FrameworkVoterService;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public class FrameworkVoterModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(FrameworkVoterService.class, FrameworkVoterServiceImpl.class));
    }

}
