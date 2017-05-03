/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import akka.Done;
import akka.NotUsed;


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

/**
 * Implementation of the BoardService.
 */
public class BoardServiceImpl implements BoardService {

	private final PersistentEntityRegistry persistentEntityRegistry;
	public final CassandraSession cassandrasession;

	@Inject
	public BoardServiceImpl(PersistentEntityRegistry persistentEntityRegistry, CassandraSession cassandrasession, 
			ReadSide readSide) {
		this.persistentEntityRegistry = persistentEntityRegistry;
		this.cassandrasession = cassandrasession;
		persistentEntityRegistry.register(BoardEntity.class);
		readSide.register(BoardEventProcessor.class);
	}

	/*@Override
  public ServiceCall<NotUsed, String> hello(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
      // Ask the entity the Hello command.
      return ref.ask(new Hello(id, Optional.empty()));
    };
  }*/

	// ADDED
	@Override
	public ServiceCall<Board, Done> create() {
		return request -> {
			// Look up the hello world entity for the given ID.
			PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, request.id);
			// Ask the entity the Create command.
			return ref.ask(new CreateBoard(request.id, request.title));
		};
	}



	// ADDED
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



	/*@Override
  public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
      // Tell the entity to use the greeting message specified.
      return ref.ask(new UseGreetingMessage(request.message));
    };

  }*/
	
	@Override
	  public ServiceCall<ChangeStatus, Done> changeStatus() {
	    return request -> {
	      // Look up the hello world entity for the given ID.
	      PersistentEntityRef<BoardCommand> ref = boardEntityRef(request.id);
	      // Look up the current values of the board object
	      return ref.ask(new GetBoard()).thenApply(reply -> {
	    	  if (!reply.board.isPresent()) {
	    		  throw new NotFound("Unknown board id"); 
	    	  } else if (request.status.equals("CREATED")) {
	    		  ref.ask(new UpdateBoard(request.id,  reply.board.get().title, BoardStatus.CREATED));
	    		  return Done.getInstance();
	  	    } else if (request.status.equals("ARCHIVED")) {
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
	      // Look up the hello world entity for the given ID.
	      PersistentEntityRef<BoardCommand> ref = boardEntityRef(request.id);
	      // Look up the current values of the board object
	      return ref.ask(new GetBoard()).thenApply(reply -> {
	    	  if (reply.board.isPresent()) {
	    		  ref.ask(new UpdateBoard(request.id, request.title, reply.board.get().status));
	    		  return Done.getInstance();
	    	  } else {
	    		  throw new NotFound("Board id"); 
	    	  }
	      });
	  };
	}
	/*
	
	@Override
	public ServiceCall<NotUsed, Boolean> CheckBoardid(String id) {
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
			CompletionStage<Boolean> bool =
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
						
						return temp.contains(id);
					});	  
			return bool;
		};
	}
	*/
	
	@Override
	public ServiceCall<NotUsed, Boolean> CheckBoardid(String id) {
		return req -> {
			CompletionStage<Boolean> bool = boardEntityRef(id).ask(new GetBoard()).thenApply(reply -> {
				System.out.println(id);
				if(reply.board.isPresent()) {
					if (reply.board.get().status == BoardStatus.CREATED) {
						return true;
					} else { return false;
					}
				} else {return false;
				} 
			});
			return bool;
		};
	}
	
	

	private PersistentEntityRef<BoardCommand> boardEntityRef(String id) {
		PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
		return ref;
	}

}
