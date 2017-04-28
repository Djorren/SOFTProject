package org.soft.assignment1.lagom.board.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

public class BoardEventTag {

  public static final AggregateEventTag<BoardEvent> INSTANCE = 
    AggregateEventTag.of(BoardEvent.class);

}