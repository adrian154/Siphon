package dev.bithole.siphon.core.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.bithole.siphon.core.api.APIException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HttpString;

// parse JSON body
public class JsonBodyHandler implements HttpHandler {

    public static final AttachmentKey<JsonElement> BODY = AttachmentKey.create(JsonElement.class);
    private final HttpHandler next;

    public JsonBodyHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        
        String contentType = exchange.getRequestHeaders().getFirst(new HttpString("Content-Type"));
        if(contentType != null && contentType.equals("application/json")) {

            // move this to a worker thread to avoid blocking the I/O thread
            if(exchange.isInIoThread()) {
                exchange.dispatch(this);
                return;
            }

            exchange.startBlocking();
            String json = new String(exchange.getInputStream().readAllBytes());
            try {
                exchange.putAttachment(BODY, JsonParser.parseString(json));
            } catch(JsonParseException ex) {
                throw new APIException(400, "Invalid JSON");
            }

        }

        next.handleRequest(exchange);

    }

}
