package org.soft.assignment1.lagom.task.api;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class ListAll {

	  public final String boardid;
	 
	 	  
	  @JsonCreator
	  public ListAll(@JsonProperty("boardid") String boardid) {
		  this.boardid = Preconditions.checkNotNull(boardid, "boardid");
	  }

	  @Override
	  public boolean equals(@Nullable Object another) {
	    if (this == another)
	      return true;
	    return another instanceof ListAll && equalTo((ListAll) another);
	  }

	  private boolean equalTo(ListAll another) {
	    return boardid.equals(another.boardid);
	  }

	  @Override
	  public int hashCode() {
	    int h = 31;
	    h = h * 17 + boardid.hashCode();
	    return h;
	  }

	  @Override
	  public String toString() {
	    return MoreObjects.toStringHelper("ListAll").add("boardid", boardid).toString();
	  }
}
