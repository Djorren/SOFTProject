package org.soft.assignment1.lagom.task.api;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class GetInfo {

	  public final String id;
	 
	 	  
	  @JsonCreator
	  public GetInfo(@JsonProperty("id") String id) {
		  this.id = Preconditions.checkNotNull(id, "id");
	  }

	  @Override
	  public boolean equals(@Nullable Object another) {
	    if (this == another)
	      return true;
	    return another instanceof GetInfo && equalTo((GetInfo) another);
	  }

	  private boolean equalTo(GetInfo another) {
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
	    return MoreObjects.toStringHelper("GetInfo").add("id", id).toString();
	  }
}
