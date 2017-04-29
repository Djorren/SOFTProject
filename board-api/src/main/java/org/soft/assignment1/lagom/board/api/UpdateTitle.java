package org.soft.assignment1.lagom.board.api;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class UpdateTitle {

	  public final String id;
	  public final String title;
	  
	  @JsonCreator
	  public UpdateTitle(@JsonProperty("id") String id, @JsonProperty("title") String title) {
	    this.id = Preconditions.checkNotNull(id, "id");
	    this.title = Preconditions.checkNotNull(title, "title");
	  }

	  @Override
	  public boolean equals(@Nullable Object another) {
	    if (this == another)
	      return true;
	    return another instanceof UpdateTitle && equalTo((UpdateTitle) another);
	  }

	  private boolean equalTo(UpdateTitle another) {
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
	    return MoreObjects.toStringHelper("UpdateTitle").add("id", id).toString();
	  }
}
