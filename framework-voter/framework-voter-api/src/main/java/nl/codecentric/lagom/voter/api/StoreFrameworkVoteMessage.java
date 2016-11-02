package nl.codecentric.lagom.voter.api;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Immutable
@JsonDeserialize
public final class StoreFrameworkVoteMessage {

    public final String framework;
    public final Integer score;
    public final Optional<String> comment;

    @JsonCreator
    public StoreFrameworkVoteMessage(String framework, Integer score, Optional<String> comment) {
        this.framework = Preconditions.checkNotNull(framework, "framework");
        this.score = Preconditions.checkNotNull(score, "score");
        this.comment = Preconditions.checkNotNull(comment, "comment");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof StoreFrameworkVoteMessage && equalTo((StoreFrameworkVoteMessage) another);
    }

    private boolean equalTo(StoreFrameworkVoteMessage another) {
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
        return MoreObjects.toStringHelper("StoreFrameworkVoteMessage").add("framework", framework).add("score", score).add("comment", comment.toString()).toString();
    }
}
