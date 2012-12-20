package edu.upenn.mkse212.pennbook.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/** Class for containing a chat conversation **/
public class ChatBoxPanel extends VerticalPanel {
	
	String senderId;
	String receiverId;
	String receiverName;
	PennBook pennBook;
	
	Label chatHeader = new Label();
	VerticalPanel chatMessages = new VerticalPanel();
	TextBox chatInput = new TextBox();
	
	public ChatBoxPanel(String sid, String sname, final String rid, String rname, final PennBook pb) {
		this.senderId = sid;
		this.receiverId = rid;
		this.receiverName = rname;
		this.pennBook = pb;
		
		chatHeader.setText(receiverName);
		chatHeader.setStylePrimaryName("chat-header");
		// Handler for sending a message when user presses enter key
		chatInput.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					String message = chatInput.getText();
					chatMessages.add(new HTML("<span class=chat-message><b>"+pennBook.userName+": </b>"+message+"</span>"));
					chatInput.setText("");
					// Sent the message with chat service
					pennBook.chatService.send(senderId, pennBook.userName, receiverId, message, new AsyncCallback<Void>(){
						public void onFailure(Throwable caught) {
							GWT.log("Failed to send message");
						}
						public void onSuccess(Void result) {
							GWT.log("Sent message");
						}
					});
				}
			}
		});
		
		this.add(chatHeader);
		this.add(chatMessages);
		this.add(chatInput);
		this.setStylePrimaryName("chat-box-panel");
	}
	
	public void addMessage(String senderName, String message) {
		chatMessages.add(new HTML("<b>"+senderName+": </b>"+message));
	}
}