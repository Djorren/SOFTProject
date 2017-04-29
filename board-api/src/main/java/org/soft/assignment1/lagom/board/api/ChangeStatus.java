package org.soft.assignment1.lagom.board.api;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class ChangeStatus {

	  public final String id;
	  public final String status;
	  
	  @JsonCreator
	  public ChangeStatus(@JsonProperty("id") String id, @JsonProperty("status") String status) {
	    this.id = Preconditions.checkNotNull(id, "id");
	    this.status = Preconditions.checkNotNull(status, "status");
	  }

	  @Override
	  public boolean equals(@Nullable Object another) {
	    if (this == another)
	      return true;
	    return another instanceof ChangeStatus && equalTo((ChangeStatus) another);
	  }

	  private boolean equalTo(ChangeStatus another) {
	    return id.equals(another.id);
	  }

	  @Override
	  public int hashCode() {
	    int h = 31;
	    h = h * 17 + id.hashCode();
	    return h;
	  }

	  @Override
	  public String toString() {
	    return MoreObjects.toStringHelper("ChangeStatus").add("id", id).toString();
	  }
}
