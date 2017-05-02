/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.api;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Immutable
@JsonDeserialize
public final class Task {

  public final String id;
  public final String title;
  public final String details;
  public final String color;
  public final String boardid;
  public final TaskStatus status;
 
  

  /*@JsonCreator
  public Board(@JsonProperty("id") String id, @JsonProperty("title") String title) {
    this.title = Preconditions.checkNotNull(title, "title");
    this.id = Preconditions.checkNotNull(id, "id");
    this.status = BoardStatus.CREATED;
  }*/
  
  @JsonCreator
  public Task(@JsonProperty("id") String id, 
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
    this.status = status;
  }

  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof Task && equalTo((Task) another);
  }

  private boolean equalTo(Task another) {
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
    return MoreObjects.toStringHelper("Task").add("id", id).add("title", title).
    		add("details", details).add("color", color).add("boardid", boardid).add("status", status).toString();
  }
}
