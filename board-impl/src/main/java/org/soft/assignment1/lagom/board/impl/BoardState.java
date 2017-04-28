/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.soft.assignment1.lagom.board.api.Board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * The state for the {@link Board} entity.
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class BoardState implements Jsonable {

  public final Optional<Board> board;

  @JsonCreator
  public BoardState(Optional<Board> board) {
    this.board = Preconditions.checkNotNull(board, "board");
  }


  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof BoardState && equalTo((BoardState) another);
  }

  private boolean equalTo(BoardState another) {
    return board.equals(another.board);
  }

  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + board.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("BoardState").add("board", board).toString();
  }
}