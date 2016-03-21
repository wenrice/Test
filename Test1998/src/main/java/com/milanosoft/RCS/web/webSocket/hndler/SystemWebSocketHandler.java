package com.milanosoft.RCS.web.webSocket.hndler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SystemWebSocketHandler implements WebSocketHandler {
	public static Map<WebSocketSession, String> users;
	static {
		users = new HashMap<WebSocketSession, String>();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("connect to the websocket success......");
		session.sendMessage(new TextMessage("Server:connected OK!链接了！"));
		System.err.println(session.getUri());
		// 这里可以写入第一次连接的查询
	}

	@Override
	public void handleMessage(WebSocketSession wss, WebSocketMessage<?> wsm) throws Exception {
		final TextMessage returnMessage = new TextMessage(wsm.getPayload() + " received at server");
		if ("ttx" != returnMessage.toString()) {
//			if(true){
				users.put(wss, returnMessage.toString());
//			}
		}
		System.err.println(users);
		sendMessageToUsers(returnMessage);

	}

	@Override
	public void handleTransportError(WebSocketSession wss, Throwable thrwbl) throws Exception {
		if (wss.isOpen()) {
			wss.close();
		}
		users.remove(wss);
		System.out.println("websocket connection closed......");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession wss, CloseStatus cs) throws Exception {
		users.remove(wss);
		System.out.println("websocket connection closed......");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	public void sendMessageToUsers(TextMessage message) {
		for (Map.Entry user : users.entrySet()) {
			System.out.println("Key = " + user.getKey() + ", Value = " + user.getValue());  
			if (((WebSocketSession) user.getKey()).isOpen()) {
				try {
					((WebSocketSession) user.getKey()).sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	// public void sendMessageToUser(String username,TextMessage message) {
	// for (WebSocketSession user : users) {
	// if (user.getHandshakeAttributes().get("username").equals(username)) {
	// try {
	// if (user.isOpen()) {
	// user.sendMessage(message);
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// break;
	// }
	// }
	// }
}
