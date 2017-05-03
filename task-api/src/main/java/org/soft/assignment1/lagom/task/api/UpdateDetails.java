package org.soft.assignment1.lagom.task.api;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class UpdateDetails {

	  public final String id;
	  public final String details;
	 
	 	  
	  @JsonCreator
	  public UpdateDetails(@JsonProperty("id") String id, 
			  	   		 @JsonProperty("details") String details) {
		  this.id = Preconditions.checkNotNull(id, "id");
		  this.details = Preconditions.checkNotNull(details, "details");
	  }

	  @Override
	  public boolean equals(@Nullable Object another) {
	    if (this == another)
	      return true;
	    return another instanceof UpdateDetails && equalTo((UpdateDetails) another);
	  }

	  private boolean equalTo(UpdateDetails another) {
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
	    return MoreObjects.toStringHelper("UpdateDetails").add("id", id).toString();
	  }
}
