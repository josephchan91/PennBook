package edu.upenn.mkse212.pennbook.client;

import java.io.Serializable;

// Represents a serializable chat message
public class ChatMessage implements Serializable {
	private static final long serialVersionUID = -1741682874903010139L;
	public String senderId;
	public String senderName;
	public String receiverId;
	public String message;
}
