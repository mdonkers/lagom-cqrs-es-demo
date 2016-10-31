package nl.codecentric.lagom.voter.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;


import com.lightbend.lagom.javadsl.api.CircuitBreaker;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import akka.Done;
import akka.NotUsed;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public interface FrameworkVoterService extends Service {

    ServiceCall<StoreFrameworkVoteMessage, Done> storeFrameworkVote();
    ServiceCall<NotUsed, String> getFrameworkAverage(String framework);

    @Override
    default Descriptor descriptor() {
        return named("framework-voter").withCalls(
                restCall(Method.POST, "/api/framework-vote", this::storeFrameworkVote),
                restCall(Method.GET, "/api/framework-vote/:framework", this::getFrameworkAverage)
        ).withAutoAcl(true).withCircuitBreaker(CircuitBreaker.perNode());
    }
}
