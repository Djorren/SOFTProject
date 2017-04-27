package org.soft.assignment1.lagom.board.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

public interface CassandraEvent extends AggregateEvent<CassandraEvent>, Jsonable {

	int NUM_SHARDS = 20;

	  AggregateEventShards<CassandraEvent> TAG =
	          AggregateEventTag.sharded(CassandraEvent.class, NUM_SHARDS);

	  @Override
	  default AggregateEventShards<CassandraEvent> aggregateTag() {
	    return TAG;
	  }
}