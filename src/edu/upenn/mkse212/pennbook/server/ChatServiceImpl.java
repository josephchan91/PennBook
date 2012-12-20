package edu.upenn.mkse212.pennbook.server;

import java.util.Map;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometSession;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.upenn.mkse212.pennbook.client.ChatException;
import edu.upenn.mkse212.pennbook.client.ChatMessage;
import edu.upenn.mkse212.pennbook.client.ChatService;

public class ChatServiceImpl extends RemoteServiceServlet implements ChatService {
	
	private ConcurrentMap<String, CometSession> users = new ConcurrentHashMap<String, CometSession>();
	
	@Override
	public String getUserId() throws ChatException {
		HttpSession httpSession = getThreadLocalRequest().getSession(false);
		if (httpSession == null) {
			return null;
		}
		
		return (String) httpSession.getAttribute("userId");
	}
	
	@Override
	public void login(String userId) throws ChatException {
		HttpSession httpSession = getThreadLocalRequest().getSession();
		httpSession.setAttribute("userId", userId);
	}
	
	@Override
	public void logout(String userId) throws ChatException {
		HttpSession httpSession = getThreadLocalRequest().getSession(false);
		if (httpSession == null) {
			throw new ChatException("UserId: " + userId + " is not logged in: no http session");
		}
		CometSession cometSession = CometServlet.getCometSession(httpSession, false);
		if (cometSession == null) {
			throw new ChatException("UserId: " + userId + " is not logged in: no comet session");
		}
		if (!userId.equals(httpSession.getAttribute("userId"))) {
			throw new ChatException("UserId: " + userId + " is not logged in on this session");
		}
		
		// remove the mapping of user name to CometSession
		users.remove(userId, cometSession);
		httpSession.invalidate();
	}
	
	@Override
	public void send(String senderId, String senderName, String receiverId, String message) throws ChatException {
		HttpSession httpSession = getThreadLocalRequest().getSession(false);
		if (httpSession == null) {
			throw new ChatException("not logged in: no http session");
		}
		String userId = (String) httpSession.getAttribute("userId");
		if (userId == null) {
			throw new ChatException("not logged in: no http session userId");
		}
		// create the chat message
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.senderId = senderId;
		chatMessage.senderName = senderName;
		chatMessage.receiverId = receiverId;
		chatMessage.message = message;
		
		// send the chat message to the proper receiver
		for (Map.Entry<String, CometSession> entry : users.entrySet()) {
			GWT.log("sender id = "+userId);
			GWT.log("entry key = "+entry.getKey());
			GWT.log("receiver id = "+receiverId);
			if (entry.getKey().equals(receiverId)) {
				entry.getValue().enqueue(chatMessage);
			}
		}
	}

}