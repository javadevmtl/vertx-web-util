package com.xxx;

import com.xxx.util.Runner;
import com.xxx.vertx.ext.web.AbstractRestVerticle;
import com.xxx.vertx.ext.web.annotations.GET;
import com.xxx.vertx.ext.web.annotations.POST;
import com.xxx.vertx.ext.web.annotations.Path;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

@Path("/api")
public class TestRestVerticle extends AbstractRestVerticle {

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(TestRestVerticle.class);
	}
	
	public void start(Future<Void> startFuture) {
		httpOptions.setPort(8080);

		buildEndpoint(build -> {
			if(build.succeeded()) {
				System.out.println("Listening: " + httpOptions.getPort());
			} else {
				build.cause().printStackTrace();
			}
		});
	}
	
	@GET("/handleget")
	public void handleGet(RoutingContext rc) {
		rc.response().end("handleGet() called.");
	}

	@POST(value = "/handlepost/", body = true)
	public void handlePost(RoutingContext rc) {
		rc.response().end(rc.getBodyAsJson().encodePrettily());
	}
}
