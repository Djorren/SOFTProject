/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


import org.soft.assignment1.lagom.task.api.TaskStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * This interface defines all the events that the Board entity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface TaskEvent extends Jsonable, AggregateEvent<TaskEvent> {

	@Override
    default public AggregateEventTag<TaskEvent> aggregateTag() {
        return TaskEventTag.INSTANCE;
    }
  
  /**
   * An event that represents the creation of a task.
   */
  @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class TaskCreated implements TaskEvent {
	  
	  public final String id;
	  public final String title;
	  public final String details;
	  public final String color;
	  public final String boardid;
	  public final TaskStatus status;

	  @JsonCreator
	  public TaskCreated(@JsonProperty("id") String id, 
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
      return another instanceof TaskCreated && equalTo((TaskCreated) another);
    }

    private boolean equalTo(TaskCreated another) {
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
      return MoreObjects.toStringHelper("TaskCreated").add("id", id).toString();
    }
  }
  
  
  /**
   * An event that represents a change the fields of a board.
   */
  @SuppressWarnings("serial")
  @Immutable
  @JsonDeserialize
  public final class TaskUpdated implements TaskEvent {
	
	  public final String id;
	  public final String title;
	  public final String details;
	  public final String color;
	  public final String boardid;
	  public final TaskStatus status;

	  @JsonCreator
	  public TaskUpdated(@JsonProperty("id") String id, 
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
      return another instanceof TaskUpdated && equalTo((TaskUpdated) another);
    }

    private boolean equalTo(TaskUpdated another) {
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
      return MoreObjects.toStringHelper("TaskUpdated").add("id", id).toString();
    }
  }
  
  
}
