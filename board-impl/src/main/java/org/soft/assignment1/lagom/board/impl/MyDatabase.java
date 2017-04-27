package org.soft.assignment1.lagom.board.impl;

import java.util.concurrent.CompletionStage;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;

import akka.Done;

public interface MyDatabase {
	
	CompletionStage<Done> createTables();
	
	CompletionStage<Offset> loadOffset(AggregateEventTag<CassandraEvent> tag);
	  
	CompletionStage<Done> handleEvent(CassandraEvent event, Offset offset);
}