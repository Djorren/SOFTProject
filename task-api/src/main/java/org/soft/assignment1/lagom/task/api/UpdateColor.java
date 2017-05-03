package org.soft.assignment1.lagom.task.api;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class UpdateColor {

	  public final String id;
	  public final String color;
	 
	 	  
	  @JsonCreator
	  public UpdateColor(@JsonProperty("id") String id, 
			  	   		 @JsonProperty("color") String color) {
		  this.id = Preconditions.checkNotNull(id, "id");
		  this.color = Preconditions.checkNotNull(color, "color");
	  }

	  @Override
	  public boolean equals(@Nullable Object another) {
	    if (this == another)
	      return true;
	    return another instanceof UpdateColor && equalTo((UpdateColor) another);
	  }

	  private boolean equalTo(UpdateColor another) {
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
	    return MoreObjects.toStringHelper("UpdateColor").add("id", id).toString();
	  }
}
