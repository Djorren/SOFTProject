/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

/** 
 * The Board service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the Board.
 */
public interface BoardService extends Service {

  /**
   * Example: curl http://localhost:9000/api/board/Alice
   */
   ServiceCall<NotUsed, String> hello(String id);



  /**
   * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
   * "Hi"}' http://localhost:9000/api/board/Alice
   */
  ServiceCall<GreetingMessage, Done> useGreeting(String id);

  
  
  /**
   * Create: curl -H "Content-Type: application/json" -X POST -d '{"id": "MyId", "title": "MyTitle"}' http://localhost:9000/api/board/create/
   */
  ServiceCall<Board, Done> create();
  
  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("board").withCalls(
        pathCall("/api/board/:id",  this::hello),
        pathCall("/api/board/:id", this::useGreeting),
        pathCall("/api/board/create/", this::create)
      ).withAutoAcl(true);
    // @formatter:on
  }
}
