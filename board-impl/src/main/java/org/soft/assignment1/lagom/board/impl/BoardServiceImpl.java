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


import java.util.Optional;


import javax.inject.Inject;
import org.soft.assignment1.lagom.board.api.GreetingMessage;
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

  @Override
  public ServiceCall<NotUsed, String> hello(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
      // Ask the entity the Hello command.
      return ref.ask(new Hello(id, Optional.empty()));
    };
  }

  // our own
  @Override
  public ServiceCall<Board, Done> create() {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, request.id);
      // Ask the entity the Create command.
      return ref.ask(new CreateBoard(request.id, request.title));
    };
  }
  
  @Override
  public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<BoardCommand> ref = persistentEntityRegistry.refFor(BoardEntity.class, id);
      // Tell the entity to use the greeting message specified.
      return ref.ask(new UseGreetingMessage(request.message));
    };

  }
  
  
  
  

}
