/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.soft.assignment1.lagom.board.api.BoardService;
import org.soft.assignment1.lagom.task.api.TaskService;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the HelloString.
 */
public class TaskServiceImpl implements TaskService {

  private final BoardService boardService;

  @Inject
  public TaskServiceImpl(BoardService boardService) {
    this.boardService = boardService;
  }

  @Override
  public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream() {
    return hellos -> completedFuture(
        hellos.mapAsync(8, name -> boardService.hello(name).invoke()));
  }
}
