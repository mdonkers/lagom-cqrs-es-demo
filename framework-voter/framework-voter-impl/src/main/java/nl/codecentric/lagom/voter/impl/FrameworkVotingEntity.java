/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.voter.impl;

import java.util.List;
import java.util.Optional;


import com.google.common.collect.ImmutableList;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;

public class FrameworkVotingEntity extends PersistentEntity<FrameworkVotingCommand, FrameworkVotedEvent, FrameworkVotingState> {

    /**
     * An entity can define different behaviours for different states, but it will
     * always start with an initial behaviour. This entity only has one behaviour.
     */
    @Override
    public Behavior initialBehavior(Optional<FrameworkVotingState> snapshotState) {

    /*
     * Behaviour is defined using a behaviour builder. The behaviour builder
     * starts with a state, if this entity supports snapshotting (an
     * optimisation that allows the state itself to be persisted to combine many
     * events into one), then the passed in snapshotState may have a value that
     * can be used.
     *
     * Otherwise, the default state is to use an empty list of votes.
     */
        BehaviorBuilder b = newBehaviorBuilder(
                snapshotState.orElse(new FrameworkVotingState(ImmutableList.of())));

    /*
     * Command handler for the AddFrameworkVote command.
     */
        b.setCommandHandler(FrameworkVotingCommand.AddFrameworkVote.class, (cmd, ctx) ->
                // In response to this command, we want to first persist it as a FrameworkVotedAddition event
                ctx.thenPersist(new FrameworkVotedEvent.FrameworkVotedAddition(entityId(), cmd.score, cmd.comment),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(Done.getInstance())));

    /*
     * Event handler for the FrameworkVotedAddition event.
     */
        b.setEventHandler(FrameworkVotedEvent.FrameworkVotedAddition.class,
                // We simply update the current state to use the greeting message from the event.
                evt -> {
                    final List<FrameworkVotingState.Vote> newVotesList =
                            ImmutableList.<FrameworkVotingState.Vote>builder().addAll(state().votes).add(new FrameworkVotingState.Vote(evt.score, evt
                                    .comment)).build();
                    return new FrameworkVotingState(newVotesList);
                });

    /*
     * Command handler for the FrameworkVoting query command.
     */
        b.setReadOnlyCommandHandler(FrameworkVotingCommand.FrameworkVoting.class,
                // Get the greeting from the current state, and prepend it to the name
                // that we're sending
                // a greeting to, and reply with that message.
                (cmd, ctx) -> ctx.reply(state().votes.toString()));

    /*
     * We've defined all our behaviour, so build and return it.
     */
        return b.build();
    }

}
