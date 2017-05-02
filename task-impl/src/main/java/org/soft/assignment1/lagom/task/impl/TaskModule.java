/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;


import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;



//import org.soft.assignment1.lagom.board.impl.BoardServiceImpl;
import org.soft.assignment1.lagom.board.api.BoardService;
//import org.soft.assignment1.lagom.board.impl.BoardServiceImpl;
import org.soft.assignment1.lagom.task.api.TaskService;

/**
 * The module that binds the TaskService so that it can be served.
 */
public class TaskModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {

    bindServices(serviceBinding(TaskService.class, TaskServiceImpl.class));
   // bindServices(serviceBinding(BoardService.class, BoardServiceImpl.class));
    bindClient(BoardService.class);
    
  }
}
