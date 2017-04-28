/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

import akka.Done;
import akka.NotUsed;


import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;
//import org.soft.assignment1.lagom.board.api.GreetingMessage;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.soft.assignment1.lagom.board.api.Board;
import org.soft.assignment1.lagom.board.api.BoardService;
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
		  CompletionStage<PSequence<String>> result =
				  cassandrasession.selectAll("SELECT * FROM board")
				  .thenApply(rows -> {
					  List<String> boards = rows.stream()
							  .map(row -> row.getString("id"))
							  .collect(Collectors.toList());
					  return TreePVector.from(boards);
				  });
		  return result;
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
  
  private PersistentEntityRef<BoardCommand> boardEntityRef(String id) {
	    PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
	    return ref;
	  }

}
