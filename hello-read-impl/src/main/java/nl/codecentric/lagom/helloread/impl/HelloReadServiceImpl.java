package nl.codecentric.lagom.helloread.impl;

import java.util.concurrent.CompletableFuture;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lightbend.lagom.javadsl.api.ServiceCall;

import akka.Done;
import nl.codecentric.lagom.helloread.api.HelloReadService;
import nl.codecentric.lagom.helloread.api.StoreHelloMessage;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public class HelloReadServiceImpl implements HelloReadService {
    private final Logger log = LoggerFactory.getLogger(HelloReadService.class);

    @Override
    public ServiceCall<StoreHelloMessage, Done> storeHello(final String id) {
        return request -> {
            log.info("===> PUT Request");
            return CompletableFuture.completedFuture(Done.getInstance());
        };
    }
}
