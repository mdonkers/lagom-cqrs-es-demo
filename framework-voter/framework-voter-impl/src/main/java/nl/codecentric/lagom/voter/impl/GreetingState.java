/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package nl.codecentric.lagom.voter.impl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;

/**
 * The state for the {@link Greeting} entity.
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class GreetingState implements CompressedJsonable {

  public final String message;
  public final String timestamp;

  @JsonCreator
  public GreetingState(String message, String timestamp) {
    this.message = Preconditions.checkNotNull(message, "message");
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof GreetingState && equalTo((GreetingState) another);
  }

  private boolean equalTo(GreetingState another) {
    return message.equals(another.message) && timestamp.equals(another.timestamp);
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + message.hashCode();
    h = h * 17 + timestamp.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("GreetingState").add("message", message).add("timestamp", timestamp).toString();
  }
}
