package edu.upenn.mkse212.pennbook.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChatServiceAsync {
	public void getUserId(AsyncCallback<String> callback);
	
	public void login(String userId, AsyncCallback<Void> callback);
	
	public void logout(String userId, AsyncCallback<Void> callback);
	
	public void send(String senderId, String senderName, String receiverId, String message, AsyncCallback<Void> callback);
	
}
