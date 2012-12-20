package edu.upenn.mkse212.pennbook.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;


import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.upenn.mkse212.pennbook.shared.FeedItemType;

/** Class for containing a feed item **/
public class FeedPanel extends VerticalPanel {
	
	String postId;
	String posterId;
	String posterName;
	String posterPicURL;
	FeedItemType type;
	String receiverId;
	String receiverName;
	String message;
	ArrayList<String> commentsJson;
	
	VerticalPanel feedItemDescription = new VerticalPanel();
	FlowPanel taggedMessage = new FlowPanel();
	
	VerticalPanel wrapper = new VerticalPanel();
	
	HorizontalPanel main = new HorizontalPanel();
	VerticalPanel commentsPanel = new VerticalPanel();
	
	HorizontalPanel commentForm = new HorizontalPanel();
	LabeledTextBox commentTbx = new LabeledTextBox("Write a comment...", false);
	
	PennBook pennBook;
	
	public FeedPanel(String postIdArg, String messageArg, String posterIdArg,
			String receiverIdArg, String posterNameArg, String receiverNameArg, String posterPicURLArg, ArrayList<String> commentsJsonArg, FeedItemType typeArg, PennBook pennBookArg) {
		super();
		postId = postIdArg;
		posterId = posterIdArg;
		posterName = posterNameArg;
		posterPicURL = posterPicURLArg;
		type = typeArg;
		receiverId = receiverIdArg;
		receiverName = receiverNameArg;
		message = messageArg;
		commentsJson = commentsJsonArg;
		pennBook = pennBookArg;

		// Styling
		this.add(wrapper);
		this.setStylePrimaryName("feed-box");
		wrapper.setStylePrimaryName("feed-box-wrapper");
		commentsPanel.setStylePrimaryName("comments-panel");
		commentTbx.setStylePrimaryName("comment-tbx");
		commentForm.add(commentTbx);
		
		// Poster Info
		main.add(new HTML("<img src="+posterPicURL+" width=40px border=1 class=feed-pic>"));
		
		main.add(feedItemDescription);
		
		HorizontalPanel feedItemHeader = new HorizontalPanel();
		feedItemDescription.add(feedItemHeader);
		HTML posterLink = new HTML("<a href=#>"+posterName+"</a>");
		posterLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				pennBook.setProfileUserId(posterId);
			}
		});
		
		// Receiver Info
		HTML receiverLink = new HTML("<a href=#>"+receiverName+"</a>");
		receiverLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				pennBook.setProfileUserId(receiverId);
			}
		});
		
		wrapper.add(main);
		// Add receiver, content, and comments space depending on the type of post
		switch(type){
		case STATUS:
			feedItemHeader.add(posterLink);
			// Parse for tags
			addTaggedMessage(message, posterId);
			feedItemDescription.add(taggedMessage);
			wrapper.add(commentsPanel);
			wrapper.add(commentForm);
			break;
		case POST:
			feedItemHeader.add(posterLink);
			feedItemHeader.add(new HTML("&nbsp;&nbsp;<img src=bootstrap/img/right_arrow width=35%>"));
			feedItemHeader.add(receiverLink);
			// Parse for tags
			addTaggedMessage(message, posterId);
			feedItemDescription.add(taggedMessage);
			wrapper.add(commentsPanel);
			wrapper.add(commentForm);
			break;
		case PROFILE_UPDATE:
			feedItemHeader.add(posterLink);
			feedItemHeader.add(new HTML("&nbsp;made a profile update."));
			feedItemDescription.add(feedItemHeader);
			break;
		case NEW_FRIENDSHIP:
			feedItemHeader.add(posterLink);
			feedItemHeader.add(new HTML("&nbsp;is now friends with&nbsp;"));
			feedItemHeader.add(receiverLink);
			break;
		}
		
		// Add comments if any exist
		if (commentsJson != null) {
			// Iterate over all the comments
			for (String comment : commentsJson) {
				JSONValue commentsValue = JSONParser.parseStrict(comment);
				JSONObject commentMap = commentsValue.isObject();
				
				final String commenterId = commentMap.get("posterId").toString().replace("\"", "");
				String commenterPictureURL;
				if (pennBook.viewingAProfile) {
					commenterPictureURL = pennBook.pics.get(commenterId);
				}
				else {
					commenterPictureURL = pennBook.pics.get(commenterId);
				}

				String commenterName = commentMap.get("posterName").toString().replace("\"", "");
				String commentMessageQuoted = commentMap.get("message").toString();
				String commentMessage = commentMessageQuoted.substring(1, commentMessageQuoted.length()-1);
				
				// Show the comment
				addComment(commenterId, commenterName, commenterPictureURL, commentMessage);
			}
		}
		
		// Handler for adding a comment when user presses enter key
		commentTbx.addKeyDownHandler(new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					if(!commentTbx.isDefaultOrEmpty()) {
						// Add immediately to feed
						addComment(pennBook.userId, pennBook.userName, pennBook.pictureURL, SafeHtmlUtils.htmlEscape(commentTbx.getText()));
						// Add comment to feed db
						pennBook.feedService.addComment(postId, pennBook.userId, pennBook.userName,
								SafeHtmlUtils.htmlEscape(commentTbx.getText()), new AsyncCallback<String>(){
									public void onFailure(Throwable caught) {
										Window.alert("Failed to add comment");
									}
									public void onSuccess(String result) {
									}	
								});
						// Clear box
						commentTbx.setToDefaultText();
					}
				}
			}
		});		
	}
	
	// Helper function for parsing a tagged message and adding tokenized HTML strings
	private void addTaggedMessage(final String message, String posterId) {
		String[] tokens = message.split("@");
		taggedMessage.add(new InlineHTML(tokens[0]));
		if (tokens.length <= 1) {
			// No @ symbol found
			return;
		}
		// An @ symbol was found
		final String[] afterAt = tokens[1].split(" ");
		if (afterAt.length <= 1) {
			// Only one word after @, can't be a valid name
			taggedMessage.add(new InlineHTML("@"+afterAt[0]));
			return;
		}
		// Check for a name after the @ symbol
		final String name = afterAt[0]+" "+afterAt[1];
		pennBook.friendshipService.getFriendByName(posterId, name, new AsyncCallback<String>(){
			public void onFailure(Throwable caught) {
				GWT.log("Failed to get tagged friend");
			}
			public void onSuccess(final String result) {
				if (!result.equals("")) {
					// Name is a valid friend of poster, make a link to friend's profile
					InlineHTML taggedLink = new InlineHTML("<a href=#>"+name+"</a>");
					taggedLink.addClickHandler(new ClickHandler(){
						public void onClick(ClickEvent event) {
							pennBook.setProfileUserId(result);
						}
					});
					taggedMessage.add(taggedLink);
				}
				else {
					// Name is not a friend, do nothing
					taggedMessage.add(new InlineHTML("@"+name));
				}
				// Check for leftover tokens
				for (int i = 2; i < afterAt.length; i++) {
					taggedMessage.add(new InlineHTML(" "+afterAt[i]));
				}
			}
		});
	}
	
	// Helper function for adding a comment to a feed item
	private void addComment(final String commenterId, String commenterName, String commenterPictureURL, String commentMessage) {
		FlowPanel newComment = new FlowPanel();
		newComment.setStylePrimaryName("comment-box");
		newComment.add(new HTML("<img src="+commenterPictureURL+" width=30px class=float-left>"));

		HorizontalPanel commentContentWrapper = new HorizontalPanel();
		VerticalPanel commentContent = new VerticalPanel();
		commentContent.setStylePrimaryName("comment-content");
		
		// A link to the commenter's profile
		HTML commenterLink = new HTML("<a href=#>"+commenterName+"</a>");
		commenterLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				pennBook.setProfileUserId(commenterId);
			}
		});
		
		// Show the comment
		commentContent.add(commenterLink);
		commentContent.add(new HTML("<span class=float-left>"+commentMessage+"</span>"));
		commentContentWrapper.add(commentContent);
		newComment.add(commentContentWrapper);
		commentsPanel.add(newComment);
	}
}
