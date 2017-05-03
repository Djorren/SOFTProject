/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.soft.assignment1.lagom.board.api.Board;
import org.soft.assignment1.lagom.board.api.BoardStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;

/**
 * This interface defines all the commands that the Board entity supports.
 * 
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface BoardCommand extends Jsonable {
  
  /**
   * A command to create a board.
   */
  @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class CreateBoard implements BoardCommand, PersistentEntity.ReplyType<Done> {
	  
	public final String id;
    public final String title;
    public final BoardStatus status;

    @JsonCreator
    public CreateBoard(@JsonProperty("id") String id, @JsonProperty("title") String title) {
      this.id = Preconditions.checkNotNull(id, "id");
      this.title = Preconditions.checkNotNull(title, "title");
      this.status = BoardStatus.CREATED;
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof CreateBoard && equalTo((CreateBoard) another);
    }

    private boolean equalTo(CreateBoard another) {
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
      return MoreObjects.toStringHelper("Create").add("id", id).add("title", title).toString();
    }
  }
  
  /**
   * A command to update the fields of a board.
   */
  @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class UpdateBoard implements BoardCommand, PersistentEntity.ReplyType<Done> {
	  public final String id;
	  public final String title;
	  public final BoardStatus status;
    
    
    @JsonCreator
    public UpdateBoard(String id, String title, BoardStatus status) {
      this.title = Preconditions.checkNotNull(title, "title");
      this.id = Preconditions.checkNotNull(id, "id");
      this.status = Preconditions.checkNotNull(status, "status");
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof UpdateBoard && equalTo((UpdateBoard) another);
    }

    private boolean equalTo(UpdateBoard another) {
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
      return MoreObjects.toStringHelper("DoChangeStatus").add("id", id).toString();
    }
  }
  
 /**
  * A command to get the board object when a certain board-id is given.
  */
  
 @SuppressWarnings("serial")
 @Immutable
 @JsonDeserialize
 public final class GetBoard implements BoardCommand, PersistentEntity.ReplyType<GetBoardReply> {

   @Override
   public boolean equals(@Nullable Object another) {
     return this instanceof GetBoard;
   }

   @Override
   public int hashCode() {
     return 2053226012;
   }

   @Override
   public String toString() {
     return "GetBoard{}";
   } 
 }
 
 @SuppressWarnings("serial")
 @Immutable
 @JsonDeserialize
 public final class GetBoardReply implements Jsonable {
   public final Optional<Board> board;

   @JsonCreator
   public GetBoardReply(Optional<Board> board) {
     this.board = Preconditions.checkNotNull(board, "board");
   }

   @Override
   public boolean equals(@Nullable Object another) {
     if (this == another)
       return true;
     return another instanceof GetBoardReply && equalTo((GetBoardReply) another);
   }

   private boolean equalTo(GetBoardReply another) {
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
     return MoreObjects.toStringHelper("GetBoardReply").add("board", board).toString();
   }
 }
  

}
