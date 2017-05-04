/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import org.soft.assignment1.lagom.board.api.BoardService;
import org.soft.assignment1.lagom.task.api.TaskService;

/**
 * The module that binds the BoardService so that it can be served.
 */
public class BoardModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindServices(serviceBinding(BoardService.class, BoardServiceImpl.class));
    bindClient(TaskService.class);
  }
}
