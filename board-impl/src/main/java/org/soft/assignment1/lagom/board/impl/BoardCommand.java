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
//import com.lightbend.lagom.serialization.CompressedJsonable;
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
   * A command to switch the greeting message.
   * <p>
   * It has a reply type of {@link akka.Done}, which is sent back to the caller
   * when all the events emitted by this command are successfully persisted.
   */
 /* @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class UseGreetingMessage implements BoardCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
    public final String message;
    
    @JsonCreator
    public UseGreetingMessage(String message) {
      this.message = Preconditions.checkNotNull(message, "message");
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof UseGreetingMessage && equalTo((UseGreetingMessage) another);
    }

    private boolean equalTo(UseGreetingMessage another) {
      return message.equals(another.message);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + message.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("UseGreetingMessage").add("message", message).toString();
    }
  }
*/
  /**
   * A command to say hello to someone using the current greeting message.
   * <p>
   * The reply type is String, and will contain the message to say to that
   * person.
   */
  /*@SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class Hello implements BoardCommand, PersistentEntity.ReplyType<String> {
    public final String name;
    public final Optional<String> organization;

    @JsonCreator
    public Hello(String name, Optional<String> organization) {
      this.name = Preconditions.checkNotNull(name, "name");
      this.organization = Preconditions.checkNotNull(organization, "organization");
    }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof Hello && equalTo((Hello) another);
    }

    private boolean equalTo(Hello another) {
      return name.equals(another.name) && organization.equals(another.organization);
    }

    @Override
    public int hashCode() {
      int h = 31;
      h = h * 17 + name.hashCode();
      h = h * 17 + organization.hashCode();
      return h;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("Hello").add("name", name).add("organization", organization).toString();
    }
  }*/
  
  
  
  // ADDED
  
  /**
   * A command to create a board.
   * <p>
   * The reply type is String, and will contain the message to say to that
   * person.
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
  
  
//ADDED
  
 /**
  * A command to get the board object when a certain board-id is given.
  * <p>
  * The reply type is String, and will contain the message to say to that
  * person.
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
