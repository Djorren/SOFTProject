/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.api;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Immutable
@JsonDeserialize
public final class Board {

  public String title;
  public final String id;
  public final String test;
  public final String test2;

  @JsonCreator
  public Board(@JsonProperty("id") String id, @JsonProperty("title") String title) {
    this.title = Preconditions.checkNotNull(title, "title");
    this.id = Preconditions.checkNotNull(id, "id");
    this.test = "komaanheseg";
    this.test2 = "werk pls";
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof Board && equalTo((Board) another);
  }

  private boolean equalTo(Board another) {
    return id.equals(another.id);
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + id.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("Board").add("id", id).add("title", title).toString();
  }
}
