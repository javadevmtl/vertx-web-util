package com.xxx.vertx.ext.web;

import java.lang.annotation.Annotation;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import com.xxx.vertx.ext.web.annotations.GET;
import com.xxx.vertx.ext.web.annotations.POST;
import com.xxx.vertx.ext.web.annotations.Path;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class AbstractRestVerticle extends AbstractVerticle {

	protected HttpServerOptions httpOptions = new HttpServerOptions();
	protected Router apiRouter = null;
	protected Router mainRouter = null;
	protected HttpServer httpServer = null;

	public void buildEndpoint(Handler<AsyncResult<Void>> completionHandler) {
		apiRouter = Router.router(vertx);
		mainRouter = Router.router(vertx);

		try {
			Class<? extends AbstractRestVerticle> clazz = this.getClass();
			Method[] methods = clazz.getMethods();

			for (Method method : methods) {
				Annotation[] annotations = method.getAnnotations();

				for(Annotation annotation : annotations)
				{
					if(annotation instanceof GET) {
						GET get = (GET)annotation;

						Handler<RoutingContext> handler = createRoutingHandler(method);
						apiRouter.route(get.value()).handler(handler);
					} else if(annotation instanceof POST) {
						POST post = (POST)annotation;
						String path = post.value();

						if(post.body())
							apiRouter.route(path).handler(BodyHandler.create());
						
						Handler<RoutingContext> handler = createRoutingHandler(method);
						apiRouter.route(path).handler(handler);
					}

				}
				
			}

			Path path = (Path) clazz.getAnnotation(Path.class);
			mainRouter.mountSubRouter(path.value(), apiRouter);

			httpServer = vertx.createHttpServer(httpOptions).requestHandler(mainRouter::accept)
					.listen(listenHandler -> {
						if (listenHandler.succeeded())
							completionHandler.handle(Future.succeededFuture());
						else
							completionHandler.handle(Future.failedFuture(listenHandler.cause()));
					});

		} catch (Throwable t) {
			completionHandler.handle(Future.failedFuture(t));
		}

	}
	
	private Handler<RoutingContext> createRoutingHandler(Method method) throws IllegalAccessException, LambdaConversionException, Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		return (Handler<RoutingContext>) LambdaMetafactory
		.metafactory(lookup, "handle", MethodType.methodType(Handler.class, getClass()),
				MethodType.methodType(void.class, Object.class), lookup.unreflect(method),
				MethodType.methodType(void.class, RoutingContext.class))
		.getTarget().invoke(this);
	}
}
