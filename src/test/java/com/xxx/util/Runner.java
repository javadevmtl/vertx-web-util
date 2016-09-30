package com.xxx.util;
import io.vertx.core.Vertx;

import java.util.function.Consumer;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Runner {

  public static void runExample(Class clazz) {
    Consumer<Vertx> runner = vertx -> {
      try {
          vertx.deployVerticle(clazz.getName());
      } catch (Throwable t) {
        t.printStackTrace();
      }
    };

    Vertx vertx = Vertx.vertx();
     runner.accept(vertx);
  }
}