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


import org.soft.assignment1.lagom.task.impl.TaskCommand.*;
import org.soft.assignment1.lagom.task.impl.TaskEntity;
import org.soft.assignment1.lagom.task.impl.TaskCommand.UpdateTask;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.soft.assignment1.lagom.board.api.BoardService;
import org.soft.assignment1.lagom.task.api.ChangeStatus;
import org.soft.assignment1.lagom.task.api.GetInfo;
import org.soft.assignment1.lagom.task.api.ListAll;
import org.soft.assignment1.lagom.task.api.UpdateTitle;
import org.soft.assignment1.lagom.task.api.Task;
import org.soft.assignment1.lagom.task.api.TaskService;
import org.soft.assignment1.lagom.task.api.TaskStatus;
import org.soft.assignment1.lagom.task.api.UpdateColor;
import org.soft.assignment1.lagom.task.api.UpdateDetails;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implementation of the TaskService.
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
	
	
	@Override
	public ServiceCall<Task, Done> create() {
		return request -> {
			PersistentEntityRef<TaskCommand> ref = TaskEntityRef(request.id);
			// Ask the BoardService whether or not the board id exists AND is not ARCHIVED
			CompletionStage<Boolean> booltemp = boardservice.CheckBoardid(request.boardid).invoke();
			return booltemp.thenApply(bool -> {
				if (bool) {
					ref.ask(new CreateTask(request.id, request.title, request.details, request.color, request.boardid));
					return Done.getInstance();
				} else {
					throw new NotFound("Unknown or ARCHIVED board id");		
				}
			});
			
		};
	}
	
	
	@Override
	  public ServiceCall<UpdateTitle, Done> updateTitle() {
	    return request -> {
	      // Look up the task entity for the given id.
	      PersistentEntityRef<TaskCommand> ref = TaskEntityRef(request.id);
	      // Look up the current values of the task object
	      return ref.ask(new GetTask()).thenApply(reply -> {
	    	  // When the task is ARCHIVED we can not update the title
	    	  if (reply.task.get().status.equals(TaskStatus.ARCHIVED)) {
	    		  throw new NotFound("Task is archived");
	    	  } else if (reply.task.isPresent()) {
	    		  ref.ask(new UpdateTask(request.id, request.title, reply.task.get().details, reply.task.get().color, reply.task.get().boardid, reply.task.get().status));
	    		  return Done.getInstance();
	    	  } else {
	    		  throw new NotFound("Unkown board id"); 
	    	  }
	      });
	  };
	}
	
	
	@Override
	  public 	ServiceCall<UpdateDetails, Done> updateDetails(){
	    return request -> {
	      // Look up the task entity for the given id.
	      PersistentEntityRef<TaskCommand> ref = TaskEntityRef(request.id);
	      // Look up the current values of the task object
	      return ref.ask(new GetTask()).thenApply(reply -> {
	    	  // When the task is ARCHIVED we can not update the details
	    	  if (reply.task.get().status.equals(TaskStatus.ARCHIVED)) {
	    		  throw new NotFound("Task is archived");
	    	  } else if (reply.task.isPresent()) {
	    		  ref.ask(new UpdateTask(request.id, reply.task.get().details, request.details, reply.task.get().color, reply.task.get().boardid, reply.task.get().status));
	    		  return Done.getInstance();
	    	  } else {
	    		  throw new NotFound("Unknown board id"); 
	    	  }
	      });
	  };
	}
	
	@Override
	  public 	ServiceCall<UpdateColor, Done> updateColor(){
	    return request -> {
	      // Look up the task entity for the given id.
	      PersistentEntityRef<TaskCommand> ref = TaskEntityRef(request.id);
	      // Look up the current values of the task object
	      return ref.ask(new GetTask()).thenApply(reply -> {
	    	  // When the task is ARCHIVED we can not update the color
	    	  if (reply.task.get().status.equals(TaskStatus.ARCHIVED)) {
	    		  throw new NotFound("Task is archived");	  
	    	  } else if (reply.task.isPresent()) {
	    		  ref.ask(new UpdateTask(request.id, reply.task.get().details, reply.task.get().details, request.color, reply.task.get().boardid, reply.task.get().status));
	    		  return Done.getInstance();
	    	  } else {
	    		  throw new NotFound("Unknown board id"); 
	    	  }
	      });
	  };
	}
	
	@Override
	  public ServiceCall<ChangeStatus, Done> changeStatus() {
	    return request -> {
	      // Look up the task entity for the given ID.
	      PersistentEntityRef<TaskCommand> ref = TaskEntityRef(request.id);
	      // Look up the current values of the task object
	      return ref.ask(new GetTask()).thenApply(reply -> {
	    	  if (!reply.task.isPresent()) {
	    		  throw new NotFound("Unknown board id"); 
	    	  } else if (request.status.equals("BACKLOG") || request.status.equals("SCHEDULED") || request.status.equals("STARTED") || 
	    			     request.status.equals("COMPLETED") || request.status.equals("DELETED")) {
	    		  ref.ask(new UpdateTask(request.id, reply.task.get().title, reply.task.get().details,reply.task.get().color, reply.task.get().boardid, TaskStatus.BACKLOG));
	    		  return Done.getInstance();
	    		  // When the requested status is ARCHIVED we have to check that the current status is COMPLETED
	    	  } else if (request.status.equals("ARCHIVED")) {
	    		  if (reply.task.get().status == TaskStatus.COMPLETED) {
	    			  return Done.getInstance();
	    		  } else {
	    			  throw new NotFound("Task must be in COMPLETED status before changing to ARCHIVED status");
	    		  }
	    	  } else {
	    		  throw new NotFound("Unknown status");
	    	  }
	      });
	  };
	}
	
	@Override
	  public 	ServiceCall<GetInfo, Task> getInfo(){
	    return request -> {
	      // Look up the task entity for the given id.
	      PersistentEntityRef<TaskCommand> ref = TaskEntityRef(request.id);
	      // Look up the current values of the task object
	      return ref.ask(new GetTask()).thenApply(reply -> {
	    	  if (reply.task.isPresent()) {  
	    		  return reply.task.get();
	    	  } else {
	    		  throw new NotFound("Unknown Board id"); 
	    	  }
	      });
	  };
	}
	
	
	
	@Override
	public ServiceCall<ListAll, PSequence<String>> listAll() {
		return req -> {
			String reqboardid = req.boardid;
			// get all the task ids that were saved in the task table with the required board id
			CompletionStage<List<String>> allTaskIds =
					cassandrasession.selectAll("SELECT * FROM task WHERE boardid = ? ALLOW FILTERING", reqboardid)
					.thenApply(rows -> {
						List<String> tasks = rows.stream()
								.map(row -> row.getString("id"))
								.collect(Collectors.toList());
						return tasks;
					});
			
			// filter the ids so that we only have those who belong to non-deleted tasks
			CompletionStage<PSequence<String>> NotArchivedTaskIds =
					allTaskIds.thenApply(taskids -> {
						// get the task objects that correspond with the ids we got from the task table
						List<Task> allTasks = taskids.stream()
								.map( taskid -> {
									CompletionStage<Task> completionTask = TaskEntityRef(taskid).ask(new GetTask()).thenApply(reply -> {
										return reply.task.get();
									});
									try {
										Task task = completionTask.toCompletableFuture().get();
										return task; // return the object we got from the completed future
									} catch (InterruptedException | ExecutionException e) {
										e.printStackTrace();
										return null;
									}
								})
								.collect( Collectors.toList());
						
						// create a list containing only the tasks who are not DELETED
						List<String> temp = new ArrayList<>();
						allTasks.forEach((task) -> {
							if(task.status != TaskStatus.DELETED) {
								temp.add(task.toString());
							}
						});

						return TreePVector.from(temp);
					});
			return NotArchivedTaskIds;
		};
	}
	
	
	
	private PersistentEntityRef<TaskCommand> TaskEntityRef(String id) {
		PersistentEntityRef<TaskCommand> ref = persistentEntityRegistry.refFor(TaskEntity.class, id);
		return ref;
	}
	
}
