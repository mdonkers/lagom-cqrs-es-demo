package nl.codecentric.lagom.voter.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;


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
    ServiceCall<NotUsed, String> getFrameworkVotes(String framework);
    ServiceCall<NotUsed, String> getFrameworkAverages();

    @Override
    default Descriptor descriptor() {
        return named("framework-voter").withCalls(
                pathCall("/api/framework-vote/averages", this::getFrameworkAverages),
                restCall(Method.POST, "/api/framework-vote", this::storeFrameworkVote),
                restCall(Method.GET, "/api/framework-vote/:framework", this::getFrameworkVotes)
        ).withAutoAcl(true).withCircuitBreaker(CircuitBreaker.perNode());
    }
}
