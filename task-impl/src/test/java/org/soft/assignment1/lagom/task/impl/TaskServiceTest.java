/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import akka.Done;

import org.soft.assignment1.lagom.board.api.Board;
import org.soft.assignment1.lagom.board.api.BoardService;
import org.soft.assignment1.lagom.board.api.BoardStatus;


import org.soft.assignment1.lagom.task.api.*;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;

public class TaskServiceTest {

    private static TestServer server;

    @BeforeClass
    public static void setUp() {
        server = startServer(defaultSetup().withCassandra(true));
    }

	@AfterClass
    public static void tearDown() {
        server.stop();
        server = null;
    }
	
	

  @Test
  public void TestCreateTask() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
  }
  
  
  @Test
  public void TestUpdateTitleSucceeds() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // we should get done, because the ID o the task exists and the status is BACKLOG
      org.soft.assignment1.lagom.task.api.UpdateTitle request1 = new org.soft.assignment1.lagom.task.api.UpdateTitle("TaskId", "NewTitle");
      Done msg3 = taskservice.updateTitle().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
  }

  
  
  @Test(expected=ExecutionException.class)
  public void TestUpdateTitleFailed() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // we should get done, because the ID o the task exists and the status is BACKLOG
      org.soft.assignment1.lagom.task.api.ChangeStatus request1 = new org.soft.assignment1.lagom.task.api.ChangeStatus("TaskId", "ARCHIVED");
      Done msg3 = taskservice.changeStatus().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
      
      // will cause error because the task is archived
      org.soft.assignment1.lagom.task.api.UpdateTitle request2 = new org.soft.assignment1.lagom.task.api.UpdateTitle("TaskId", "NewTitle");
      taskservice.updateTitle().invoke(request2).toCompletableFuture().get(5, SECONDS);
  }
  
 

  @Test
  public void TestUpdateDetailsSucceeds() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // we should get done, because the ID o the task exists and the status is BACKLOG
      org.soft.assignment1.lagom.task.api.UpdateDetails request1 = new org.soft.assignment1.lagom.task.api.UpdateDetails("TaskId", "NewDetails");
      Done msg3 = taskservice.updateDetails().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
  }

  
  
  @Test(expected=ExecutionException.class)
  public void TestUpdateDetailsFailed() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // we should get done, because the ID o the task exists and the status is BACKLOG
      org.soft.assignment1.lagom.task.api.ChangeStatus request1 = new org.soft.assignment1.lagom.task.api.ChangeStatus("TaskId", "ARCHIVED");
      Done msg3 = taskservice.changeStatus().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
      
      // will cause error because the task is archived
      org.soft.assignment1.lagom.task.api.UpdateDetails request2 = new org.soft.assignment1.lagom.task.api.UpdateDetails("TaskId", "NewDetails");
      taskservice.updateDetails().invoke(request2).toCompletableFuture().get(5, SECONDS);
  }
  
  
  
  @Test
  public void TestUpdateColorSucceeds() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // we should get done, because the ID o the task exists and the status is BACKLOG
      org.soft.assignment1.lagom.task.api.UpdateColor request1 = new org.soft.assignment1.lagom.task.api.UpdateColor("TaskId", "NewColor");
      Done msg3 = taskservice.updateColor().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
  }

  
  
  @Test(expected=ExecutionException.class)
  public void TestUpdateColorFailed() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);
      
      // we first make a board where we can assign a task to
      Board b = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // we should get done, because the ID o the task exists and the status is BACKLOG
      org.soft.assignment1.lagom.task.api.ChangeStatus request1 = new org.soft.assignment1.lagom.task.api.ChangeStatus("TaskId", "ARCHIVED");
      Done msg3 = taskservice.changeStatus().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
      
      // will cause error because the task is archived
      org.soft.assignment1.lagom.task.api.UpdateColor request2 = new org.soft.assignment1.lagom.task.api.UpdateColor("TaskId", "NewColor");
      taskservice.updateColor().invoke(request2).toCompletableFuture().get(5, SECONDS);
  }
  
 
  
  
  
  @Test
  public void TestChangeStatusSucceeds() throws Exception {
      BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      org.soft.assignment1.lagom.task.api.ChangeStatus request2 = new org.soft.assignment1.lagom.task.api.ChangeStatus("MyId", "ARCHIVED");
      Done msg3 = taskservice.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg3);
  } 
  
  @Test(expected=ExecutionException.class)
  public void TestChangeStatusWrongStatus() throws Exception {
      BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      org.soft.assignment1.lagom.task.api.ChangeStatus request2 = new org.soft.assignment1.lagom.task.api.ChangeStatus("MyId", "UNKNOWN STATUS");
      taskservice.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
  } 
  
  
  @Test(expected=ExecutionException.class)
  public void TestChangeStatusWrongTask() throws Exception {
      BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      org.soft.assignment1.lagom.task.api.ChangeStatus request2 = new org.soft.assignment1.lagom.task.api.ChangeStatus("Unknown TaskId", "ARCHIVED");
      taskservice.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
  } 
  
  
  
  @Test(expected=ExecutionException.class)
  public void TestGetInfoFails() throws Exception {
      BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // now we create the task
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      org.soft.assignment1.lagom.task.api.GetInfo request2 = new org.soft.assignment1.lagom.task.api.GetInfo("Unknown TaskId");
      taskservice.getInfo().invoke(request2).toCompletableFuture().get(5, SECONDS);
  } 
  
  
  public void TestListAll() throws Exception {
	  BoardService boardservice = server.client(BoardService.class);
      TaskService taskservice = server.client(TaskService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = boardservice.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      System.out.println(msg1);
      assertEquals(Done.getInstance(), msg1);
      	
      Task t = new Task("TaskId", "MyTitle", "details", "green", "MyId", TaskStatus.BACKLOG);
      Done msg2 = taskservice.create().invoke(t).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      /* because the cassandra DB does not update that quickly, we use sleep(5000)
       * this way, we wait 5 seconds before we invoke listAll
       * this way we are sure that all the created boards and tasks are in the DB
      */
      Thread.sleep(5000); 
      PSequence<String> msg3 = taskservice.listAll().invoke().toCompletableFuture().get(5, SECONDS);
      System.out.println("listall" + msg3);
      // we are going to construct the result we expect from the listAll serviceCall
      List<String> temp = new ArrayList<>();
      temp.add( "Task{id=taskId, title=MyTitle, details=detail, color=green, boardid=MyId, status=BACKLOG}");
      PSequence<String> result = TreePVector.from(temp);
      assertEquals(result, msg3);
      
  }
 
}
