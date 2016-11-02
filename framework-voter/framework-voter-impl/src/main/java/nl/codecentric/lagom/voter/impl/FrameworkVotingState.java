/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.voter.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;

/**
 * The state for the {@link FrameworkVoting} entity.
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class FrameworkVotingState implements CompressedJsonable {

    @Immutable
    @JsonDeserialize
    public static final class Vote implements CompressedJsonable {
        public final Integer score;
        public final Optional<String> comment;

        @JsonCreator
        public Vote(Integer score, Optional<String> comment) {
            this.score = Preconditions.checkNotNull(score, "score");
            this.comment = Preconditions.checkNotNull(comment, "comment");
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("Vote").add("score", score).add("comment", comment.toString()).toString();
        }
    }

    public final List<Vote> votes;

    @JsonCreator
    public FrameworkVotingState(List<Vote> votes) {
        this.votes = Preconditions.checkNotNull(votes, "votes array");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof FrameworkVotingState && equalTo((FrameworkVotingState) another);
    }

    private boolean equalTo(FrameworkVotingState another) {
        return votes.equals(another.votes);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + votes.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("FrameworkVotingState").add("votes", votes.toString()).toString();
    }
}
