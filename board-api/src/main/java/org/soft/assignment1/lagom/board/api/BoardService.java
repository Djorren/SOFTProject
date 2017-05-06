/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import org.pcollections.PSequence;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

/**
 * The Board service interface.
 * This describes everything that Lagom needs to know about how to serve and
 * consume the Board.
 */
public interface BoardService extends Service {


	/**
	 * curl -H "Content-Type: application/json" -X POST -d '{"id": "MyId", "title": "MyTitle"}' http://localhost:9000/api/board/create/
	 */
	ServiceCall<Board, Done> create();


	/**
	 * curl -H "Content-Type: application/json" -X POST -d '{"id": "MyId", "title": "NewTitle"}' http://localhost:9000/api/board/updatetitle/
	 */
	ServiceCall<UpdateTitle, Done> updateTitle();


	/**
	 * curl -H "Content-Type: application/json" -X POST -d '{"id": "MyId", "status": "ARCHIVED"}' http://localhost:9000/api/board/changestatus/
	 */
	ServiceCall<ChangeStatus, Done> changeStatus();


	/**
	 * curl http://localhost:9000/api/board/listall/
	 */
	ServiceCall<NotUsed, PSequence<String>> listAll();
	
	/**
	 * Used by task to check if the given board id exists and if it is not archived -> returns a boolean
	 */
	
	ServiceCall<NotUsed, Boolean> CheckBoardid(String id);

	@Override
	default Descriptor descriptor() {
		// @formatter:off
		return named("board").withCalls(
				pathCall("/api/board/create/", this::create),
				pathCall("/api/board/updatetitle/", this::updateTitle),
				pathCall("/api/board/changestatus/", this::changeStatus),
				pathCall("/api/board/listall/", this::listAll),
				pathCall("/api/board/checkboardid/:id", this::CheckBoardid)
				).withAutoAcl(true);
		// @formatter:on
	}
}
