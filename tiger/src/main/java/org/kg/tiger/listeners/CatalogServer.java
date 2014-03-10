package org.kg.tiger.listeners;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.LogLevel;
import org.kg.tiger.listeners.model.Catalog;

@ServerEndpoint("/catalog")
public class CatalogServer {

	private static volatile List<Catalog> catalogList = new ArrayList<Catalog>();

	private static Logger logger = Logger.getLogger(CatalogServer.class
			.getName());

	private static final String CAT_NAME = "name";
	private static final String CAT_PRICE = "price";

	@OnMessage
	public String OnMessage(String message, Session session) {
		logger.debug(message);
		try {
			if (message != null && !message.isEmpty()) {
				JsonReader reader = Json
						.createReader(new StringReader(message));
				final JsonObject node = reader.readObject();
				Catalog catalog = new Catalog();
				catalog.setCatalogItem(node.getString(CAT_NAME));
				catalog.setSalesPrice(node.getString(CAT_PRICE));
				catalogList.add(catalog);
			}
			StringWriter writer = new StringWriter();
			JsonGenerator jsongen = Json.createGenerator(writer);

			jsongen.writeStartArray();
			for (Catalog c : catalogList) {
				jsongen.writeStartObject().write(CAT_NAME, c.getCatalogItem());
				jsongen.write(CAT_PRICE, c.getSalesPrice()).writeEnd();
			}
			jsongen.writeEnd();
			jsongen.flush();
			String response = writer.getBuffer().toString();
			logger.debug(response);
			session.getBasicRemote().sendText(response);
			return response;
		} catch (Exception ex) {

			logger.error(LogLevel.SEVERE, ex);

		}
		return null;
	}

	@OnClose
	public void onClose(CloseReason reason) {
		System.out.println("Connection closed " + reason.getReasonPhrase());
	}

	@OnError
	public void onError(Session session, Throwable t) {
		logger.error("Error " + session.getId(), t);
		RemoteEndpoint.Basic peer = session.getBasicRemote();
		try {
			peer.sendText("oops! something didn't work, you may want to try again.");
		} catch (IOException ex) {

			logger.error(LogLevel.SEVERE, ex);
		}
	}

}
