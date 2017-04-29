package org.soft.assignment1.lagom.board.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

import akka.Done;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.soft.assignment1.lagom.board.impl.BoardEvent.BoardCreated;


public class BoardEventProcessor extends ReadSideProcessor<BoardEvent> {
	
	private final CassandraSession session;
	private final CassandraReadSide readSide;
	private PreparedStatement info = null; // initialized in prepare
	 
	@Inject
	public BoardEventProcessor(CassandraSession session, CassandraReadSide readSide) {
		this.session = session;
		this.readSide = readSide;
	}
	 
	private void setInfo(PreparedStatement info) {
		this.info = info;
	}
	 
	private CompletionStage<Done> prepareInfo() {
		System.out.println("INSERTED INTO TABLE LOL XD");
		return session.prepare("INSERT INTO board (id) VALUES (?)").thenApply(ps -> {
			setInfo(ps);
			return Done.getInstance();
		});
	}
	
	private CompletionStage<List<BoundStatement>> processInfoChanged(BoardCreated event) {
		 BoundStatement bindInfo = info.bind();
		 bindInfo.setString("id", event.id);
		 return completedStatement(bindInfo);
	}
	 
	@Override
	public PSequence<AggregateEventTag<BoardEvent>> aggregateTags() {
		return TreePVector.singleton(BoardEventTag.INSTANCE);
	}
	
	@Override
	public ReadSideHandler<BoardEvent> buildHandler() {
		return readSide.<BoardEvent>builder("board_offset")
			 .setGlobalPrepare(this::prepareCreateTables)
			 .setPrepare((ignored) -> prepareInfo())
			 .setEventHandler(BoardCreated.class, this::processInfoChanged)
			 .build();
	}
	
	private CompletionStage<Done> prepareCreateTables() {
		System.out.println("TABLE CREATED LOL XD");
		return session.executeCreateTable(
				"CREATE TABLE IF NOT EXISTS board ("
				+ "id text, "
				+ "PRIMARY KEY (id))"
		);
	 }
};