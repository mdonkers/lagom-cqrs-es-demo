package nl.codecentric.lagom.helloread.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;


import com.lightbend.lagom.javadsl.api.CircuitBreaker;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import akka.Done;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public interface HelloReadService extends Service{

    ServiceCall<StoreHelloMessage, Done> storeHello(String id);

    @Override
    default Descriptor descriptor() {
        return named("hello-read").withCalls(
                restCall(Method.PUT, "/api/hello-read/:id", this::storeHello)
        ).withAutoAcl(true).withCircuitBreaker(CircuitBreaker.perNode());
    }
}
