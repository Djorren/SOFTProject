/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.soft.assignment1.lagom.task.api.Task;
import org.soft.assignment1.lagom.task.api.TaskStatus;

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
public interface TaskCommand extends Jsonable {

  /**
   * A command to switch the greeting message.
   * <p>
   * It has a reply type of {@link akka.Done}, which is sent back to the caller
   * when all the events emitted by this command are successfully persisted.
   */
  
  // ADDED
  
  /**
   * A command to create a task.
   * <p>
   * The reply type is String, and will contain the message to say to that
   * person.
   */
  @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class CreateTask implements TaskCommand, PersistentEntity.ReplyType<Done> {
	  
	  public final String id;
	  public final String title;
	  public final String details;
	  public final String color;
	  public final String boardid;
	  public final TaskStatus status;

	  @JsonCreator
	  public CreateTask(@JsonProperty("id") String id, 
			  	   @JsonProperty("title") String title, 
			  	   @JsonProperty("details") String details,
			  	   @JsonProperty("color") String color,
			  	   @JsonProperty("boardid") String boardid) {
	    this.title = Preconditions.checkNotNull(title, "title");
	    this.id = Preconditions.checkNotNull(id, "id");
	    this.details = Preconditions.checkNotNull(details, "details");
	    this.color = Preconditions.checkNotNull(color, "color");
	    this.boardid = Preconditions.checkNotNull(boardid, "boardid");
	    this.status = TaskStatus.BACKLOG;
	  }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof CreateTask && equalTo((CreateTask) another);
    }

    private boolean equalTo(CreateTask another) {
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
   * <p>
   * It has a reply type of {@link akka.Done}, which is sent back to the caller
   * when all the events emitted by this command are successfully persisted.
   */
  @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class UpdateTask implements TaskCommand, PersistentEntity.ReplyType<Done> {
	  
	  public final String id;
	  public final String title;
	  public final String details;
	  public final String color;
	  public final String boardid;
	  public final TaskStatus status;
    
    
	  @JsonCreator
	  public UpdateTask(@JsonProperty("id") String id, 
			  	   @JsonProperty("title") String title, 
			  	   @JsonProperty("details") String details,
			  	   @JsonProperty("color") String color,
			  	   @JsonProperty("boardid") String boardid,
			  	   @JsonProperty("status") TaskStatus status) {
	    this.title = Preconditions.checkNotNull(title, "title");
	    this.id = Preconditions.checkNotNull(id, "id");
	    this.details = Preconditions.checkNotNull(details, "details");
	    this.color = Preconditions.checkNotNull(color, "color");
	    this.boardid = Preconditions.checkNotNull(boardid, "boardid");
	    this.status = Preconditions.checkNotNull(status, "status");
	  }

    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another)
        return true;
      return another instanceof UpdateTask && equalTo((UpdateTask) another);
    }

    private boolean equalTo(UpdateTask another) {
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
 public final class GetTask implements TaskCommand, PersistentEntity.ReplyType<GetTaskReply> {

   @Override
   public boolean equals(@Nullable Object another) {
     return this instanceof GetTask;
   }

   @Override
   public int hashCode() {
     return 2053226013;
   }

   @Override
   public String toString() {
     return "GetTask{}";
   } 
 }
 
 @SuppressWarnings("serial")
 @Immutable
 @JsonDeserialize
 public final class GetTaskReply implements Jsonable {
   public final Optional<Task> task;

   @JsonCreator
   public GetTaskReply(Optional<Task> task) {
     this.task = Preconditions.checkNotNull(task, "task");
   }

   @Override
   public boolean equals(@Nullable Object another) {
     if (this == another)
       return true;
     return another instanceof GetTaskReply && equalTo((GetTaskReply) another);
   }

   private boolean equalTo(GetTaskReply another) {
     return task.equals(another.task);
   }

   @Override
   public int hashCode() {
     int h = 31;
     h = h * 17 + task.hashCode();
     return h;
   }

   @Override
   public String toString() {
     return MoreObjects.toStringHelper("GetBoardReply").add("task", task).toString();
   }
 }
 


}
