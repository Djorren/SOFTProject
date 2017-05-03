/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.soft.assignment1.lagom.task.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import org.soft.assignment1.lagom.task.api.Task;

import akka.Done;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

/**
 * The task interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the HelloStream service.
 */
public interface TaskService extends Service {

// curl -H "Content-Type: application/json" -X POST -d '{"id": "taskId", "title": "MyTitle", "details": "detail", "color": "green", "boardid": "MyId"}' http://localhost:9000/api/task/create/
	
// curl -H "Content-Type: application/json" -X POST -d '{"id": "taskId2", "title": "MyTitle2", "details": "detail", "color": "green", "boardid": "Unknown"}' http://localhost:9000/api/task/create/
	ServiceCall<Task, Done> create();

  @Override
  default Descriptor descriptor() {
	// @formatter:off
			return named("task").withCalls(
					//pathCall("/api/board/:id",  this::hello),
					//pathCall("/api/board/:id", this::useGreeting),
					pathCall("/api/task/create/", this::create)
					).withAutoAcl(true);
			// @formatter:on
  }
}
