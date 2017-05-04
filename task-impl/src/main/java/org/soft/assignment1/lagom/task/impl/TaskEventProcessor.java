package org.soft.assignment1.lagom.task.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import akka.Done;
import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.soft.assignment1.lagom.task.impl.TaskEvent.TaskCreated;


public class TaskEventProcessor extends ReadSideProcessor<TaskEvent> {
	
	private final CassandraSession session;
	private final CassandraReadSide readSide;
	private PreparedStatement info = null; // initialized in prepare
	 
	@Inject
	public TaskEventProcessor(CassandraSession session, CassandraReadSide readSide) {
		this.session = session;
		this.readSide = readSide;
	}
	 
	private void setInfo(PreparedStatement info) {
		this.info = info;
	}
	 
	private CompletionStage<Done> prepareInfo() {
		return session.prepare("INSERT INTO task (id, boardid) VALUES (?,?)").thenApply(ps -> {
			setInfo(ps);
			return Done.getInstance();
		});
	}
	
	private CompletionStage<List<BoundStatement>> processInfoChanged(TaskCreated event) {
		 BoundStatement bindInfo = info.bind();
		 bindInfo.setString("id", event.id);
		 bindInfo.setString("boardid", event.boardid);
		 return completedStatement(bindInfo);
	}
	 
	@Override
	public PSequence<AggregateEventTag<TaskEvent>> aggregateTags() {
		return TreePVector.singleton(TaskEventTag.INSTANCE);
	}
	
	@Override
	public ReadSideHandler<TaskEvent> buildHandler() {
		return readSide.<TaskEvent>builder("board_offset")
			 .setGlobalPrepare(this::prepareCreateTables)
			 .setPrepare((ignored) -> prepareInfo())
			 .setEventHandler(TaskCreated.class, this::processInfoChanged)
			 .build();
	}
	
	private CompletionStage<Done> prepareCreateTables() {
		return session.executeCreateTable(
				"CREATE TABLE IF NOT EXISTS task ("
				+ "id text, boardid text, "
				+ "PRIMARY KEY (id))"
		);
	 }
};