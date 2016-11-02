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
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

public interface FrameworkVotedEvent extends Jsonable, AggregateEvent<FrameworkVotedEvent> {

    AggregateEventTag<FrameworkVotedEvent> VOTED_EVENT_TAG = AggregateEventTag.of(FrameworkVotedEvent.class);

    @Override
    default AggregateEventTag<FrameworkVotedEvent> aggregateTag() {
        return VOTED_EVENT_TAG;
    }

    /**
     * An event that represents an added vote.
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class FrameworkVotedAddition implements FrameworkVotedEvent {
        public final String framework;
        public final Integer score;
        public final Optional<String> comment;

        @JsonCreator
        public FrameworkVotedAddition(String framework, Integer score, Optional<String> comment) {
            this.framework = Preconditions.checkNotNull(framework, "framework");
            this.score = Preconditions.checkNotNull(score, "score");
            this.comment = Preconditions.checkNotNull(comment, "comment");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof FrameworkVotedAddition && equalTo((FrameworkVotedAddition) another);
        }

        private boolean equalTo(FrameworkVotedAddition another) {
            return framework.equals(another.framework) && score.equals(another.score) && comment.equals(another.comment);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + framework.hashCode();
            h = h * 17 + score.hashCode();
            h = h * 17 + comment.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("FrameworkVotedEvent").add("framework", framework).add("score", score).add("comment", comment.toString()).toString();
        }
    }
}
