package com.rduda.frontend.VertChat;

import com.rduda.frontend.VertChat.Protocol.Register;
import com.rduda.frontend.VertChat.Protocol.Serializer;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;

/**
 * Created by Robin on 2015-12-16.
 * <p>
 * Handles events from the backend and emits events to the backend.
 */
class EventVerticle implements Verticle {
    private static final String REGISTER_NAME = "VERT.X";
    private static final Integer CONNECTOR_PORT = 5050;
    private Vertx vertx;
    private HttpClient client;

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        connectToBackend();
    }

    // todo reconnect must be set so that operation may resume.
    private void connectToBackend() {
        client = vertx.createHttpClient();

        client.websocketStream(CONNECTOR_PORT, "localhost", "/").handler(event -> {

            // listen for events from the backend connector service.
            event.handler(data -> {
                vertx.eventBus().send(NamedBus.EVENT(), data);
            });

            // forward emitted events onto the connector.
            vertx.eventBus().consumer(NamedBus.NOTIFY(), handler -> {
                vertx.eventBus().send(event.textHandlerID(), handler.body().toString());
            });

            // register this server to the connector for events.
            vertx.eventBus().send(event.textHandlerID(),
                    Serializer.pack(new Register(REGISTER_NAME, ChatServer.LISTEN_PORT + "")));
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        client.close();
    }
}
