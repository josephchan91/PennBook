package edu.upenn.mkse212.pennbook.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ChatService")
public interface ChatService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static ChatServiceAsync instance;
		public static ChatServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(ChatService.class);
			}
			return instance;
		}
	}

	public String getUserId() throws ChatException;
	
	public void login(String userId) throws ChatException;
	
	public void logout(String userId) throws ChatException;
	
	public void send(String senderId, String senderName, String receiverId, String message) throws ChatException;
	
}
