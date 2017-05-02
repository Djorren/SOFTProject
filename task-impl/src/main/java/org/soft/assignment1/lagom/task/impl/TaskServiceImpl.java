/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;


import org.soft.assignment1.lagom.task.impl.TaskCommand;
import org.soft.assignment1.lagom.task.impl.TaskEntity;
import org.soft.assignment1.lagom.task.impl.TaskCommand.CreateTask;
import org.soft.assignment1.lagom.board.api.BoardService;
import org.soft.assignment1.lagom.task.api.Task;
import org.soft.assignment1.lagom.task.api.TaskService;

import javax.inject.Inject;


import java.util.concurrent.CompletionStage;

/**
 * Implementation of the HelloString.
 */
public class TaskServiceImpl implements TaskService {

	private final PersistentEntityRegistry persistentEntityRegistry;
	public final CassandraSession cassandrasession;
	public final BoardService boardservice;

	@Inject
	public TaskServiceImpl(PersistentEntityRegistry persistentEntityRegistry, CassandraSession cassandrasession, 
			ReadSide readSide, BoardService boardservice) {
		this.persistentEntityRegistry = persistentEntityRegistry;
		this.cassandrasession = cassandrasession;
		this.boardservice = boardservice;
		persistentEntityRegistry.register(TaskEntity.class);
		readSide.register(TaskEventProcessor.class);
	}
	
	// ADDED
	@Override
	public ServiceCall<Task, Done> create() {
		return request -> {
			PersistentEntityRef<TaskCommand> ref = persistentEntityRegistry.refFor(TaskEntity.class, request.id);
			CompletionStage<Boolean> booltemp = boardservice.CheckBoardid(request.boardid).invoke();
			// Ask the entity the Create command.
			return booltemp.thenApply(bool -> {
				if (bool) {
					ref.ask(new CreateTask(request.id, request.title, request.details, request.color, request.boardid));
					return Done.getInstance();
				} else { throw new NotFound("Unknown or ARCHIVED board id");		
				}
			});
			
		};
	}
	
}
