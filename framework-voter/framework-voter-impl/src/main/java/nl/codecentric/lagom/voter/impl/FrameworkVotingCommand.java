/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.voter.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;

/**
 * This interface defines all the commands that the Framework Voting entity supports.
 * <p>
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface FrameworkVotingCommand extends Jsonable {

    /**
     * A command to add a new Vote.
     * <p>
     * It has a reply type of {@link Done}, which is sent back to the caller
     * when all the events emitted by this command are successfully persisted.
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class AddFrameworkVote implements FrameworkVotingCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public final Integer score;
        public final Optional<String> comment;

        @JsonCreator
        public AddFrameworkVote(Integer score, Optional<String> comment) {
            this.score = Preconditions.checkNotNull(score, "score");
            this.comment = Preconditions.checkNotNull(comment, "comment");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof AddFrameworkVote && equalTo((AddFrameworkVote) another);
        }

        private boolean equalTo(AddFrameworkVote another) {
            return score.equals(another.score) && comment.equals(another.comment);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + score.hashCode();
            h = h * 17 + comment.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("AddFrameworkVote").add("score", score).add("comment", comment).toString();
        }
    }

    /**
     * A command to request the current votes for a framework.
     * <p>
     * The reply type is String.
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class FrameworkVoting implements FrameworkVotingCommand, PersistentEntity.ReplyType<String> {
        public final String result;

        @JsonCreator
        public FrameworkVoting(String result) {
            this.result = Preconditions.checkNotNull(result, "result");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof FrameworkVoting && equalTo((FrameworkVoting) another);
        }

        private boolean equalTo(FrameworkVoting another) {
            return result.equals(another.result);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + result.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("Vote").add("result", result).toString();
        }
    }

}
