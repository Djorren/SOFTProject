/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

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
import org.soft.assignment1.lagom.board.api.ChangeStatus;
import org.soft.assignment1.lagom.board.api.UpdateTitle;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;

public class BoardServiceTest {

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
  public void TestCreateBoard() throws Exception {
      BoardService service = server.client(BoardService.class);
      
      // we make our request class board, invoke the create method with it, and we expect Done
      Board b = new Board("MyId", "MyTitle", BoardStatus.ARCHIVED);
      Done m1 = service.create().invoke(b).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), m1);
  }
  
  @Test
  public void TestUpdateTitleSucceeds() throws Exception {
      BoardService service = server.client(BoardService.class);

      // We first make a board, which default has a status of Created
      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
     // the update will succeed because the Id exists and the board is not archived
      UpdateTitle request2 = new UpdateTitle("MyId", "NewTitle");
      Done msg2 = service.updateTitle().invoke(request2).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
  } 
  
  
  @Test(expected=ExecutionException.class)
  public void TestUpdateTitleFailed() throws Exception {
      BoardService service = server.client(BoardService.class);

      // We first make a board, which default has a status of Created
      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // we will now change the status to ARCHIVED
      ChangeStatus request2 = new ChangeStatus("MyId", "ARCHIVED");
      Done msg2 = service.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // this will cause an error because we want to update the title of an archived board
      UpdateTitle request3 = new UpdateTitle("MyId", "NewTitle");
      service.updateTitle().invoke(request3).toCompletableFuture().get(5, SECONDS);
  } 
  
  
  @Test
  public void TestChangeStatusSucceeds() throws Exception {
      BoardService service = server.client(BoardService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      ChangeStatus request2 = new ChangeStatus("MyId", "ARCHIVED");
      Done msg2 = service.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
  } 
  
  
  // we will try to change the status to something that does not exist, so we expect an exception
  @Test(expected=ExecutionException.class)
  public void TestChangeStatusWrongStatus() throws Exception {
      BoardService service = server.client(BoardService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      ChangeStatus request2 = new ChangeStatus("MyId", "UNKNOWNSTATUS");
      service.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
  } 
  
  
  // we will try to change the status of a board that does not exist, so we expect an exception
  @Test(expected=ExecutionException.class)
  public void TestChangeStatusWrongBoard() throws Exception {
      BoardService service = server.client(BoardService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      ChangeStatus request2 = new ChangeStatus("UnknownId", "ARCHIVED");
      service.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
  } 
  
  
  @Test
  
  public void TestListAll() throws Exception {
      BoardService service = server.client(BoardService.class);

      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      System.out.println(msg1);
      assertEquals(Done.getInstance(), msg1);
      
      Board request2 = new Board("MyId2", "MyTitle2", BoardStatus.CREATED);
      Done msg2 = service.create().invoke(request2).toCompletableFuture().get(5, SECONDS);
      System.out.println(msg2);
      assertEquals(Done.getInstance(), msg2);
      	
      
      /* because the cassandra DB does not update that quickly, we use sleep(5000)
       * this way, we wait 5 seconds before we invoke listAll
       * this way we are sure that all the created boards are in the DB
      */
      Thread.sleep(5000); 
      PSequence<String> msg3 = service.listAll().invoke().toCompletableFuture().get(5, SECONDS);
      System.out.println("listall" + msg3);
      // we are going to construct the result we expect from the listAll serviceCall
      List<String> temp = new ArrayList<>();
      temp.add("Board{id=MyId2, title=MyTitle2}");
      temp.add("Board{id=MyId, title=MyTitle}");
      PSequence<String> result = TreePVector.from(temp);
      assertEquals(result, msg3);
      
  }
  
  @Test
  public void TestCheckBoardId() throws Exception {
      BoardService service = server.client(BoardService.class);

      // first we create a board with status CREATED
      Board request1 = new Board("MyId", "MyTitle", BoardStatus.CREATED);
      Done msg1 = service.create().invoke(request1).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg1);
      
      // the board id function returns true if the ID exists and the status is not archived
      Boolean b1 = service.CheckBoardid("MyId").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals(true, b1);
      
      // the id does not exist so we need to get back false
      Boolean b2 = service.CheckBoardid("MyIddddd").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals(false, b2);
    
      // we change the boardstatus to ARCHIVED
      ChangeStatus request2 = new ChangeStatus("MyId", "ARCHIVED");
      Done msg2 = service.changeStatus().invoke(request2).toCompletableFuture().get(5, SECONDS);
      assertEquals(Done.getInstance(), msg2);
      
      // now we need to get false because the board is archived
      Boolean b3 = service.CheckBoardid("MyId").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals(false, b3);
      
  }
  

}
