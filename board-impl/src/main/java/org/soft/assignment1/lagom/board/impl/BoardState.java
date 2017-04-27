/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;

/**
 * The state for the {@link Board} entity.
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class BoardState implements CompressedJsonable {

  public String title;
  public final String timestamp;

  @JsonCreator
  public BoardState(String title, String timestamp) {
    this.title = Preconditions.checkNotNull(title, "title");
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof BoardState && equalTo((BoardState) another);
  }

  private boolean equalTo(BoardState another) {
    return title.equals(another.title) && timestamp.equals(another.timestamp);
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + title.hashCode();
    h = h * 17 + timestamp.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("BoardState").add("title", title).add("timestamp", timestamp).toString();
  }
}
