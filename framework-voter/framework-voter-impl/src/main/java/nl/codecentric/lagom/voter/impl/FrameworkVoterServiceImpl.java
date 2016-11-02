package nl.codecentric.lagom.voter.impl;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.jdbc.JdbcSession;

import akka.Done;
import akka.NotUsed;
import nl.codecentric.lagom.voter.api.FrameworkVoterService;
import nl.codecentric.lagom.voter.api.StoreFrameworkVoteMessage;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public class FrameworkVoterServiceImpl implements FrameworkVoterService {
    private final Logger log = LoggerFactory.getLogger(FrameworkVoterService.class);

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final JdbcSession jdbcSession;

    @Inject
    public FrameworkVoterServiceImpl(PersistentEntityRegistry persistentEntityRegistry, JdbcSession jdbcSession, ReadSide readSide) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.persistentEntityRegistry.register(FrameworkVotingEntity.class);
        this.jdbcSession = jdbcSession;
        readSide.register(FrameworkVotingEventProcessor.class);
    }

    @Override
    public ServiceCall<StoreFrameworkVoteMessage, Done> storeFrameworkVote() {
        return request -> {
            log.info("===> Store Framework Vote Request {}", request.toString());
            PersistentEntityRef<FrameworkVotingCommand> ref = persistentEntityRegistry.refFor(FrameworkVotingEntity.class, request.framework);
            return ref.ask(new FrameworkVotingCommand.AddFrameworkVote(request.score, request.comment));
        };
    }

    @Override
    public ServiceCall<NotUsed, String> getFrameworkVotes(String framework) {
        return request -> {
            PersistentEntityRef<FrameworkVotingCommand> ref = persistentEntityRegistry.refFor(FrameworkVotingEntity.class, framework);
            // Ask the entity the Framework Voting command (current state of votes).
            return ref.ask(new FrameworkVotingCommand.FrameworkVoting(framework));
        };
    }


    @Override
    public ServiceCall<NotUsed, String> getFrameworkAverages() {
        return request -> {
            return jdbcSession.withConnection(connection -> {
//                ResultSet rs = connection.prepareStatement("SELECT id, title FROM blogsummary")
//                        .executeQuery();
//                PSequence<PostSummary> summaries = TreePVector.empty();
//
//                while (rs.next()) {
//                    summaries = summaries.plus(
//                            new PostSummary(rs.getString("id"), rs.getString("title"))
//                    );
//                }

                return "Number of votes: 5";
            });
        };
    }

}
