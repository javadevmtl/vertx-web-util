# vertx-web-util
Simple utilities to remove some of the boilerplate code revolving around building vertx.io REST services.
# Easy to use
It's the same vertx API with 1 additional factory method and a couple annotations. It's just abstracting away some of the boilerplate code required to setup the HttpServer and wire the REST routes.

**AbstractRestVerticle** is a wrapper around **AbstractVerticle**, in fact you still get access to all the available properties and methods of **AbstractVerticle**
```
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
				startFuture.complete();
			} else {
				startFuture.fail(build.cause());
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
```
