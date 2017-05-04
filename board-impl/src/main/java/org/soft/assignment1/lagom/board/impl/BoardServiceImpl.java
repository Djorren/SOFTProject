/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import akka.Done;
import akka.NotUsed;
import scala.collection.parallel.Tasks;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
//import org.soft.assignment1.lagom.board.api.GreetingMessage;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.soft.assignment1.lagom.board.api.Board;
import org.soft.assignment1.lagom.board.api.BoardService;
import org.soft.assignment1.lagom.board.api.BoardStatus;
import org.soft.assignment1.lagom.board.api.ChangeStatus;
import org.soft.assignment1.lagom.board.api.UpdateTitle;
import org.soft.assignment1.lagom.board.impl.BoardCommand.*;
import org.soft.assignment1.lagom.board.impl.BoardEventProcessor;
import org.soft.assignment1.lagom.task.api.ListAll;
import org.soft.assignment1.lagom.task.api.TaskService;
import org.soft.assignment1.lagom.task.api.TaskStatus;

/**
 * Implementation of the BoardService.
 */
public class BoardServiceImpl implements BoardService {

	private final PersistentEntityRegistry persistentEntityRegistry;
	public final CassandraSession cassandrasession;
	public final TaskService taskservice;

	@Inject
	public BoardServiceImpl(PersistentEntityRegistry persistentEntityRegistry, CassandraSession cassandrasession, 
			ReadSide readSide, TaskService taskservice) {
		this.persistentEntityRegistry = persistentEntityRegistry;
		this.cassandrasession = cassandrasession;
		this.taskservice = taskservice;
		persistentEntityRegistry.register(BoardEntity.class);
		readSide.register(BoardEventProcessor.class);
	}

	@Override
	public ServiceCall<Board, Done> create() {
		return request -> {
			// Look up the board entity for the given id.
			PersistentEntityRef<BoardCommand> ref = boardEntityRef(request.id);
			// Ask the entity the CreateBoard command.
			return ref.ask(new CreateBoard(request.id, request.title));
		};
	}



	@Override
	public ServiceCall<NotUsed, PSequence<String>> listAll() {
		return req -> {
			// get all the board ids that were saved in the board table
			CompletionStage<List<String>> allBoardIds =
					cassandrasession.selectAll("SELECT * FROM board")
					.thenApply(rows -> {
						List<String> boards = rows.stream()
								.map(row -> row.getString("id"))
								.collect(Collectors.toList());
						return boards;
					});
			// filter the ids so that we only have those who belong to non-archived boards
			CompletionStage<PSequence<String>> createdBoardIds =
					allBoardIds.thenApply(boardids -> {
						// get the board objects that correspond with the ids we got from the boards table
						List<Board> allBoards = boardids.stream()
								.map( boardid -> {
									CompletionStage<Board> completionBoard = boardEntityRef(boardid).ask(new GetBoard()).thenApply(reply -> {
										return reply.board.get();
									});
									try {
										Board board = completionBoard.toCompletableFuture().get();
										return board; // return the object we got from the completed future
									} catch (InterruptedException | ExecutionException e) {
										e.printStackTrace();
										return null;
									}
								})
								.collect( Collectors.toList());
						
						// create a list containing only the boards with status CREATED
						List<String> temp = new ArrayList<>();
						allBoards.forEach((board) -> {
							if(board.status == BoardStatus.CREATED) {
								temp.add(board.toString());
							}
						});

						return TreePVector.from(temp);
					});	  
			return createdBoardIds;
		};
	}

	
	@Override
	  public ServiceCall<ChangeStatus, Done> changeStatus() {
	    return request -> {
	      // Look up the hello world entity for the given id.
	      PersistentEntityRef<BoardCommand> ref = boardEntityRef(request.id);
	      // Look up the current values of the board object
	      return ref.ask(new GetBoard()).thenApply(reply -> {
	    	  if (!reply.board.isPresent()) {
	    		  throw new NotFound("Unknown board id"); 
	    	  } else if (reply.board.get().status.equals(BoardStatus.ARCHIVED)) { // board is already archieved
	    		  throw new NotFound("Board is already ARCHIVED");
	    	  } else if (request.status.equals("CREATED")) {
	    		  System.out.println(reply.board.get().status);
	    		  ref.ask(new UpdateBoard(request.id,  reply.board.get().title, BoardStatus.CREATED));
	    		  return Done.getInstance();
	  	    } else if (request.status.equals("ARCHIVED")) {
	  	    	/*CompletionStage<PSequence<String>> allTasks = taskservice.listAll().invoke();
	  	    	allTasks.thenApply(tas -> {
	  	    		
	  	    	});*/
	  	    	ref.ask(new UpdateBoard(request.id,  reply.board.get().title, BoardStatus.ARCHIVED));
	    		  return Done.getInstance();
	  	    } else {
	  	    	throw new NotFound("Unknown status");
	    	  }
	      });
	  };
	}
	
	@Override
	  public ServiceCall<UpdateTitle, Done> updateTitle() {
	    return request -> {
	      // Look up the board entity for the given id.
	      PersistentEntityRef<BoardCommand> ref = boardEntityRef(request.id);
	      // Look up the current values of the board object
	      return ref.ask(new GetBoard()).thenApply(reply -> {
	    	  if (reply.board.get().status.equals(BoardStatus.ARCHIVED)) {
	    		  throw new NotFound("Board is archived");
	    	  } else if (reply.board.isPresent()) {
	    		  ref.ask(new UpdateBoard(request.id, request.title, reply.board.get().status));
	    		  return Done.getInstance();
	    	  } else {
	    		  throw new NotFound("Board id"); 
	    	  }
	      });
	  };
	}
	
	@Override
	public ServiceCall<NotUsed, Boolean> CheckBoardid(String id) {
		return req -> {
			CompletionStage<Boolean> bool = boardEntityRef(id).ask(new GetBoard()).thenApply(reply -> {
				if(reply.board.isPresent()) {
					if (reply.board.get().status == BoardStatus.CREATED) {
						return true; // the board exists and the status is CREATED -> true
					} else {
						return false; // the board exists but the status is not CREATED -> false
					}
				} else {
					return false; // the board does not exist -> false
				} 
			});
			return bool;
		};
	}
	
	
	// Look up the Board Entity that corresponds with a given id
	private PersistentEntityRef<BoardCommand> boardEntityRef(String id) {
		PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
		return ref;
	}

}
