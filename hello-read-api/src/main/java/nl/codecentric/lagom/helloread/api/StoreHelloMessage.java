package nl.codecentric.lagom.helloread.api;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Immutable
@JsonDeserialize
public final class StoreHelloMessage {

    public final String id;
    public final String message;

    @JsonCreator
    public StoreHelloMessage(String id, String message) {
        this.id = Preconditions.checkNotNull(id, "id");
        this.message = Preconditions.checkNotNull(message, "message");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof StoreHelloMessage && equalTo((StoreHelloMessage) another);
    }

    private boolean equalTo(StoreHelloMessage another) {
        return id.equals(another.id) && message.equals(another.message);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + id.hashCode();
        h = h * 17 + message.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("StoreHelloMessage").add("id", id).add("message", message).toString();
    }
}
