package org.soft.assignment1.lagom.board.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import javax.inject.Inject;

import org.soft.assignment1.lagom.board.impl.CassandraEvent;


public class BoardEventProcessor extends ReadSideProcessor<CassandraEvent> {
	
	private final CassandraSession session;
    private final ReadSide readSide;

    @Inject
    public BoardEventProcessor(CassandraSession session, ReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }
    
    @Override
    public ReadSideProcessor.ReadSideHandler<CassandraEvent> buildHandler() {
        // TODO build read side handler
        return null;
    }
	
    @Override
    public PSequence<AggregateEventTag<CassandraEvent>> aggregateTags() {
        return CassandraEvent.TAG.allTags();
    }
}