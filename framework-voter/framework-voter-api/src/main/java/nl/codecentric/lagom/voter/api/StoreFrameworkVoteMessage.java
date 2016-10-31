package nl.codecentric.lagom.voter.api;

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
    public final String comment;
    public final Integer score;

    @JsonCreator
    public StoreFrameworkVoteMessage(String framework, String comment, Integer score) {
        this.framework = Preconditions.checkNotNull(framework, "framework");
        this.score = Preconditions.checkNotNull(score, "score");
        this.comment = comment;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof StoreFrameworkVoteMessage && equalTo((StoreFrameworkVoteMessage) another);
    }

    private boolean equalTo(StoreFrameworkVoteMessage another) {
        return framework.equals(another.framework) && comment.equals(another.comment) && score.equals(another.score);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + framework.hashCode();
        h = h * 17 + comment.hashCode();
        h = h * 17 + score.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("StoreFrameworkVoteMessage").add("framework", framework).add("comment", comment).add("score", score).toString();
    }
}
