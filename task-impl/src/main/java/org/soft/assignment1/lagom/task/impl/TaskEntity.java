/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.impl;

import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;


import org.soft.assignment1.lagom.task.api.Task;
import org.soft.assignment1.lagom.task.impl.TaskCommand.CreateTask;
import org.soft.assignment1.lagom.task.impl.TaskCommand.GetTask;
import org.soft.assignment1.lagom.task.impl.TaskCommand.GetTaskReply;
import org.soft.assignment1.lagom.task.impl.TaskCommand.UpdateTask;
import org.soft.assignment1.lagom.task.impl.TaskEvent.TaskCreated;
import org.soft.assignment1.lagom.task.impl.TaskEvent.TaskUpdated;


/**
 * This is an event sourced entity. It has a state, {@link TaskState}, which
 * stores what the different field of a board (taskid, title, details, color, boardid, status)
 * <p>
 * Event sourced entities are interacted with by sending them commands.
 * <p>
 * Commands get translated to events, and it's the events that get persisted by
 * the entity. Each event will have an event handler registered for it, and an
 * event handler simply applies an event to the current state. This will be done
 * when the event is first created, and it will also be done when the entity is
 * loaded from the database - each event will be replayed to recreate the state
 * of the entity.
 */
public class TaskEntity extends PersistentEntity<TaskCommand, TaskEvent, TaskState> {

	/**
	 * An entity can define different behaviours for different states, but it will
	 * always start with an initial behaviour. This entity only has one behaviour.
	 */
	@Override
	public Behavior initialBehavior(Optional<TaskState> snapshotState) {

		/*
		 * Behaviour is defined using a behaviour builder. The behaviour builder
		 * starts with a state, if this entity supports snapshotting (an
		 * optimisation that allows the state itself to be persisted to combine many
		 * events into one), then the passed in snapshotState may have a value that
		 * can be used.
		 */
		BehaviorBuilder b = newBehaviorBuilder(
				snapshotState.orElse(new TaskState(Optional.empty())));

		
		b.setCommandHandler(CreateTask.class, (cmd, ctx) ->
		// In response to this command, we want to first persist it as a
		// TaskCreated event
		ctx.thenPersist(new TaskCreated(cmd.id, cmd.title, cmd.details, cmd.color, cmd.boardid),
				// Then once the event is successfully persisted, we respond with done.
				evt -> ctx.reply(Done.getInstance())));

		
		b.setEventHandler(TaskCreated.class, 
				evt -> new TaskState(Optional.of(new Task(evt.id, evt.title, evt.details, evt.color, evt.boardid, evt.status))));


		b.setCommandHandler(UpdateTask.class, (cmd, ctx) ->
		// In response to this command, we want to first persist it as a
		// TaskUpdated event
		ctx.thenPersist(new TaskUpdated(cmd.id, cmd.title, cmd.details, cmd.color, cmd.boardid, cmd.status),
				// Then once the event is successfully persisted, we respond with done.
				evt -> ctx.reply(Done.getInstance())));


		b.setEventHandler(TaskUpdated.class, 
				evt -> new TaskState(Optional.of(new Task(evt.id, evt.title, evt.details, evt.color, evt.boardid, evt.status))));


		b.setReadOnlyCommandHandler(GetTask.class, (cmd, ctx) -> {
			ctx.reply(new GetTaskReply(state().task));
		});

		/*
		 * We've defined all our behaviour, so build and return it.
		 */

		return b.build();
	}

}
