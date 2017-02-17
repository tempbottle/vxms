package org.jacpfx.failure;


import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.fakecluster.FakeClusterManager;
import org.jacpfx.common.ServiceEndpoint;
import org.jacpfx.entity.Payload;
import org.jacpfx.entity.encoder.ExampleByteEncoder;
import org.jacpfx.entity.encoder.ExampleStringEncoder;
import org.jacpfx.vertx.rest.annotation.OnRestError;
import org.jacpfx.vertx.rest.response.RestHandler;
import org.jacpfx.vertx.services.VxmsEndpoint;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Andy Moncsek on 23.04.15.
 */

public class RESTServiceOnFailureStringResponseTest extends VertxTestBase {
    private final static int MAX_RESPONSE_ELEMENTS = 4;
    public static final String SERVICE_REST_GET = "/wsService";
    private static final String HOST = "127.0.0.1";
    public static final int PORT = 9998;

    protected int getNumNodes() {
        return 1;
    }

    protected Vertx getVertx() {
        return vertices[0];
    }

    @Override
    protected ClusterManager getClusterManager() {
        return new FakeClusterManager();
    }


    private HttpClient client;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        startNodes(getNumNodes());

    }

    @Before
    public void startVerticles() throws InterruptedException {


        CountDownLatch latch2 = new CountDownLatch(1);
        DeploymentOptions options = new DeploymentOptions().setInstances(1);
        options.setConfig(new JsonObject().put("clustered", false).put("host", HOST));
        // Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
        // don'failure have to hardecode it in your tests

        getVertx().deployVerticle(new WsServiceOne(), options, asyncResult -> {
            // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
            System.out.println("start service: " + asyncResult.succeeded());
            assertTrue(asyncResult.succeeded());
            assertNotNull("deploymentID should not be null", asyncResult.result());
            // If deployed correctly then start the tests!
            //   latch2.countDown();

            latch2.countDown();

        });

        awaitLatch(latch2);


    }



    @Test
    public void simpleOnFailureResponse() throws InterruptedException {
        HttpClientOptions options = new HttpClientOptions();
       options.setDefaultPort(PORT);         options.setDefaultHost(HOST);
        HttpClient client = vertx.
                createHttpClient(options);

        HttpClientRequest request = client.get("/wsService/simpleOnFailureResponse", new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse resp) {
                resp.bodyHandler(body -> {
                    String val = body.getString(0, body.length());
                    System.out.println("--------catchedAsyncByteErrorDelay: " + val);
                    assertEquals("on failure", val);
                    testComplete();
                });


            }
        });
        request.end();

        await(10000, TimeUnit.MILLISECONDS);

    }

    @Test
    public void simpleOnFailureResponseBlocking() throws InterruptedException {
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultPort(PORT);         options.setDefaultHost(HOST);
        HttpClient client = vertx.
                createHttpClient(options);

        HttpClientRequest request = client.get("/wsService/simpleOnFailureResponseBlocking", new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse resp) {
                resp.bodyHandler(body -> {
                    String val = body.getString(0, body.length());
                    System.out.println("--------catchedAsyncByteErrorDelay: " + val);
                    assertEquals("on failure", val);
                    testComplete();
                });


            }
        });
        request.end();

        await(10000, TimeUnit.MILLISECONDS);

    }
    @Test
    public void simpleOnFailureResponse400() throws InterruptedException {
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultPort(PORT);         options.setDefaultHost(HOST);
        HttpClient client = vertx.
                createHttpClient(options);

        HttpClientRequest request = client.get("/wsService/simpleOnFailureResponse400", new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse resp) {
                resp.bodyHandler(body -> {
                    String val = body.getString(0, body.length());
                    System.out.println("--------catchedAsyncByteErrorDelay: " + val+"  status: "+resp.statusMessage()+"  status nr.:"+resp.statusCode());
                    assertEquals("on failure", val);
                    assertEquals(400, resp.statusCode());
                    testComplete();
                });


            }
        });
        request.end();

        await(10000, TimeUnit.MILLISECONDS);

    }


    @Test
    public void simpleOnFailureResponse400RetryStateless() throws InterruptedException {
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultPort(PORT);         options.setDefaultHost(HOST);
        HttpClient client = vertx.
                createHttpClient(options);

        HttpClientRequest request = client.get("/wsService/simpleOnFailureResponse400RetryStateless", new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse resp) {
                resp.bodyHandler(body -> {
                    String val = body.getString(0, body.length());
                    System.out.println("--------catchedAsyncByteErrorDelay: " + val+"  status: "+resp.statusMessage()+"  status nr.:"+resp.statusCode());
                    assertEquals("on failure", val);
                    assertEquals(400, resp.statusCode());
                    testComplete();
                });


            }
        });
        request.end();

        await(10000, TimeUnit.MILLISECONDS);

    }

    public HttpClient getClient() {
        return client;
    }


    @ServiceEndpoint(name = SERVICE_REST_GET, contextRoot = SERVICE_REST_GET, port = PORT)
    public class WsServiceOne extends VxmsEndpoint {



        @Path("/simpleOnFailureResponseBlocking")
        @GET
        public void simpleOnFailureResponseBlocking(RestHandler reply) {
            reply.
                    response().
                    blocking().
                    stringResponse(() -> {
                        throw new NullPointerException("test");
                    }).
                    onFailureRespond((t) -> {
                        System.out.print("the stack trace --> ");
                        t.printStackTrace();
                        return "on failure";
                    }).
                    execute();
        }


        @Path("/simpleOnFailureResponse")
        @GET
        public void simpleOnFailureResponse(RestHandler reply) {
            reply.
                    response().
                    stringResponse((future) -> {
                        throw new NullPointerException("test");
                    }).
                    onFailureRespond((t, future) -> {
                        System.out.print("the stack trace --> ");
                        t.printStackTrace();
                        future.complete("on failure");
                    }).
                    execute();
        }

        @Path("/simpleOnFailureResponse400")
        @GET
        public void simpleOnFailureResponse400(RestHandler reply) {
            reply.
                    response().
                    stringResponse((future) -> {
                        throw new NullPointerException("test");
                    }).
                    onFailureRespond((t, future) -> {
                        System.out.print("the stack trace --> ");
                        t.printStackTrace();
                        future.complete("on failure");
                    }).
                    httpErrorCode(HttpResponseStatus.BAD_REQUEST).
                    execute();
        }

        @Path("/simpleOnFailureResponse400RetryStateless")
        @GET
        public void simpleOnFailureResponse400RetryStateless(RestHandler reply) {
            reply.
                    response().
                    stringResponse((future) -> {
                        throw new NullPointerException("test");
                    }).
                    onError(t -> System.out.println(t.getMessage())).
                    onFailureRespond((t, future) -> {
                        System.out.print("the stack trace --> ");
                        t.printStackTrace();
                        future.complete("on failure");
                    }).
                    httpErrorCode(HttpResponseStatus.BAD_REQUEST).
                    retry(3).
                    execute();
        }


    }


}
