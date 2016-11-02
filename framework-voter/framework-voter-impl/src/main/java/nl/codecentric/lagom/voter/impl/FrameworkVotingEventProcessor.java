package nl.codecentric.lagom.voter.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.inject.Inject;


import org.pcollections.PSequence;
import org.pcollections.TreePVector;


import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.jdbc.JdbcReadSide;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public class FrameworkVotingEventProcessor extends ReadSideProcessor<FrameworkVotedEvent> {

    private final JdbcReadSide readSide;

    @Inject
    public FrameworkVotingEventProcessor(JdbcReadSide readSide) {
        this.readSide = readSide;
    }

    @Override
    public ReadSideHandler<FrameworkVotedEvent> buildHandler() {
        JdbcReadSide.ReadSideHandlerBuilder<FrameworkVotedEvent> builder = readSide.builder("votesoffset");

        builder.setGlobalPrepare(this::createTable);
        builder.setEventHandler(FrameworkVotedEvent.FrameworkVotedAddition.class, this::processPostAdded);

        return builder.build();
    }

    private void createTable(Connection connection) throws SQLException {
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS votes ( "
                        + "id MEDIUMINT NOT NULL AUTO_INCREMENT, "
                        + "framework VARCHAR(64) NOT NULL, "
                        + "score SMALLINT NOT NULL, "
                        + "comment VARCHAR(256), "
                        + "dt_created DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        + " PRIMARY KEY (id))").execute();
    }

    private void processPostAdded(Connection connection, FrameworkVotedEvent.FrameworkVotedAddition event) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO votes (framework, score, comment) VALUES (?, ?, ?)");
        statement.setString(1, event.framework);
        statement.setInt(2, event.score);
        statement.setString(3, event.comment.orElse(""));
        statement.execute();
    }

    @Override
    public PSequence<AggregateEventTag<FrameworkVotedEvent>> aggregateTags() {
        return TreePVector.singleton(FrameworkVotedEvent.VOTED_EVENT_TAG);
    }
}
