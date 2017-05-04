/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.soft.assignment1.lagom.task.api.Task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * The state for the {@link Task} entity.
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class TaskState implements Jsonable {

  public final Optional<Task> task;

  @JsonCreator
  public TaskState(Optional<Task> task) {
    this.task = Preconditions.checkNotNull(task, "task");
  }


  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another)
      return true;
    return another instanceof TaskState && equalTo((TaskState) another);
  }

  private boolean equalTo(TaskState another) {
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
    return MoreObjects.toStringHelper("TaskState").add("task", task).toString();
  }
}