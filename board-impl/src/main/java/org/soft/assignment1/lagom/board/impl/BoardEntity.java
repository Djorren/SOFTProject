/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.board.impl;

//import java.time.LocalDateTime;
import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;

import org.soft.assignment1.lagom.board.api.Board;
import org.soft.assignment1.lagom.board.impl.BoardCommand.CreateBoard;
import org.soft.assignment1.lagom.board.impl.BoardCommand.GetBoard;
import org.soft.assignment1.lagom.board.impl.BoardCommand.GetBoardReply;
import org.soft.assignment1.lagom.board.impl.BoardCommand.UpdateBoard;
import org.soft.assignment1.lagom.board.impl.BoardEvent.BoardCreated;
import org.soft.assignment1.lagom.board.impl.BoardEvent.BoardUpdated;

/**
 * This is an event sourced entity. It has a state, {@link BoardState}, which
 * stores what the different field of a board (boarid, title, status)
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
public class BoardEntity extends PersistentEntity<BoardCommand, BoardEvent, BoardState> {

	/**
	 * An entity can define different behaviours for different states, but it will
	 * always start with an initial behaviour. This entity only has one behaviour.
	 */

	@Override
	public Behavior initialBehavior(Optional<BoardState> snapshotState) {

		/*
		 * Behaviour is defined using a behaviour builder. The behaviour builder
		 * starts with a state, if this entity supports snapshotting (an
		 * optimisation that allows the state itself to be persisted to combine many
		 * events into one), then the passed in snapshotState may have a value that
		 * can be used.
		 */

		BehaviorBuilder b = newBehaviorBuilder(
				snapshotState.orElse(new BoardState(Optional.empty())));


		b.setCommandHandler(CreateBoard.class, (cmd, ctx) ->
		// In response to this command, we want to first persist it as a
		// BoardCreated event
		ctx.thenPersist(new BoardCreated(cmd.id, cmd.title),
				// Then once the event is successfully persisted, we respond with done.
				evt -> ctx.reply(Done.getInstance())));

		// Set a handler for the event when a board is created (BoardCreated),
		// which will create a new state for the board
		b.setEventHandler(BoardCreated.class, 
				evt -> new BoardState(Optional.of(new Board(evt.id, evt.title, evt.status))));


		b.setCommandHandler(UpdateBoard.class, (cmd, ctx) ->
		// In response to this command, we want to first persist it as a
		// BoardUpdated event
		ctx.thenPersist(new BoardUpdated(cmd.id, cmd.title, cmd.status),
				// Then once the event is successfully persisted, we respond with done.
				evt -> ctx.reply(Done.getInstance())));

		// Set a handler for the event when a board is created (BoardCreated),
		// which will create a new state for the board
		b.setEventHandler(BoardUpdated.class, 
				evt -> new BoardState(Optional.of(new Board(evt.id, evt.title, evt.status))));


		b.setReadOnlyCommandHandler(GetBoard.class, (cmd, ctx) -> {
			// In response to this command, we want to first persist it as a
			// GetBoardReply event
			ctx.reply(new GetBoardReply(state().board));
		});





		/*
		 * Command handler for the Hello command.
		 */
		// b.setReadOnlyCommandHandler(Hello.class,
		// Get the greeting from the current state, and prepend it to the name
		// that we're sending
		// a greeting to, and reply with that message.
		//  (cmd, ctx) -> ctx.reply(state().board.title + ", " + cmd.name + "!"));

		/*
		 * We've defined all our behaviour, so build and return it.
		 */

		return b.build();
	}

}
