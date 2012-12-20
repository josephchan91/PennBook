package edu.upenn.mkse212.pennbook.client;


import java.io.Serializable;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;
import net.zschech.gwt.comet.client.CometSerializer;
import net.zschech.gwt.comet.client.SerialTypes;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.pennbook.server.FeedServiceImpl;
import edu.upenn.mkse212.pennbook.server.FriendRequestServiceImpl;
import edu.upenn.mkse212.pennbook.server.FriendshipServiceImpl;
import edu.upenn.mkse212.pennbook.server.UserServiceImpl;
import edu.upenn.mkse212.pennbook.shared.FeedItemType;

/**
 * Entry point classes define <code>showRegisterAndLogin()</code>.
 */
public class PennBook implements EntryPoint {
	
	// Session Variables
	String userId = "";
	String pictureURL = "";
	String firstName = "";
	String lastName = "";
	String userName = "";
	String email = "";
	String birthday = "";
	String userToken = "";
	String hometown = "";
	String interests = "";
	String affiliations = "";
	Set<String> friendIds = new HashSet<String>();
	List<Map<String,String>> onlineFriends = new ArrayList<Map<String,String>>();

	// Profile viewing
	boolean viewingAProfile = false;
	boolean viewingAFriendProfile = true;
	String profileUserId = "";
	String profilePictureURL = "";
	String profileFirstName = "";
	String profileLastName = "";
	String profileUserName = "";
	String profileBirthday = "";
	String profileEmail = "";
	String profileHometown = "";
	String profileInterests = "";
	String profileAffiliations = "";
	
	// Timer
	Timer poll;
	
	// Pics
	Map<String,String> pics = new HashMap<String,String>();
	
	// This page
	PennBook pennBook;
	
	// SERVICES
	private final UserServiceAsync userService = UserService.Util.getInstance();
	final FriendshipServiceAsync friendshipService = FriendshipService.Util.getInstance();
	private final FriendRequestServiceAsync friendRequestService = FriendRequestService.Util.getInstance();
	final FeedServiceAsync feedService = FeedService.Util.getInstance();
	final ChatServiceAsync chatService = ChatService.Util.getInstance();
	private final RecommendationServiceAsync recommendationService = RecommendationService.Util.getInstance();
	
	final RootPanel rootPanel = RootPanel.get();
	
	// Core Widgets
	final FlowPanel header = new FlowPanel();
	final VerticalPanel regPanel = new VerticalPanel(); // Show if not logged in
	final HorizontalPanel homePanel = new HorizontalPanel(); // Show if logged in
	final FlowPanel chatPanel = new FlowPanel();
	
	// Header Widgets
	FlowPanel container = new FlowPanel();
	final HorizontalPanel signinPanel = new HorizontalPanel(); // Show if not logged in
	FlowPanel menu = new FlowPanel(); // Show if logged in
	
	// Registration Widgets
	final LabeledTextBox firstNameTbx = new LabeledTextBox("Your First Name",false);
	final LabeledTextBox lastNameTbx = new LabeledTextBox("Your Last Name",false);
	final LabeledTextBox emailTbx = new LabeledTextBox("Your Email",false);
	final LabeledTextBox passwordTbx = new LabeledTextBox("Your Password",true);
	final ListBox monthBox = new ListBox();
	final ListBox dayBox = new ListBox();
	final ListBox yearBox = new ListBox();
	final HTML errorLabel = new HTML();
	
	// Friend Requests and Notifications
	final HorizontalPanel requestsAndNotificationsBar = new HorizontalPanel();
	final VerticalPanel requestsPanel = new VerticalPanel();
	VerticalPanel requestsPopup = new VerticalPanel();
	boolean requestsPopupIsOpen = false;
	final VerticalPanel notificationsPanel = new VerticalPanel();
	VerticalPanel notificationsPopup = new VerticalPanel();
	boolean notificationsPopupIsOpen = false;
	
	// Search
	VerticalPanel searchPanel = new VerticalPanel();
	VerticalPanel searchPopup = new VerticalPanel();
	
	// Profile
	final VerticalPanel profileContainer = new VerticalPanel();
	final HorizontalPanel nameAndPicPanel = new HorizontalPanel();
	final VerticalPanel profilePicEditable = new VerticalPanel();
	final VerticalPanel hometownEditable = new VerticalPanel();
	final VerticalPanel affiliationsEditable = new VerticalPanel();
	final VerticalPanel interestsEditable = new VerticalPanel();
	final HTML editProfilePicBtn = new HTML("<a href=#>Edit Profile Picture</a>");
	final HTML editHometownBtn = new HTML("<a href=#>Edit Hometown</a>");
	final HTML editAffiliationsBtn = new HTML("<a href=#>Edit Affiliations</a>");
	final HTML editInterestsBtn = new HTML("<a href=#>Edit Interests</a>");
	
	// Status and WallPost
	final VerticalPanel messagePanel = new VerticalPanel();
	// STATUS
	final LabeledTextBox statusBox = new LabeledTextBox("What's on your mind?",false);
	final Button submitStatus = new Button("Post");
	// MESSAGE
	final LabeledTextBox wallpostBox = new LabeledTextBox("Write something...",false);
	final Button submitWallpost = new Button("Post");
	
	// Feed 
	final VerticalPanel allFeeds = new VerticalPanel();
	
	// RightBar
	boolean justLoggedIn = true;
	final VerticalPanel rightSidebarPanel = new VerticalPanel();
	final VerticalPanel recommendedFriendsPanel = new VerticalPanel();
	final VerticalPanel onlineFriendsPanel = new VerticalPanel();
	
	// Friend Visualization Graph
	final PopupPanel modal = new PopupPanel();
	private JavaScriptObject graph = null;
	final HorizontalPanel visualizerPanel = new HorizontalPanel();
	VerticalPanel graphPanel = new VerticalPanel();
	final VerticalPanel nodeDetailPanel = new VerticalPanel();
	
	// Chat stuff
	private CometClient cometClient = null;
	Map<String,ChatBoxPanel> chatsOpen = new HashMap<String,ChatBoxPanel>();
	Map<Integer,VerticalPanel> chatContainerMap = new HashMap<Integer,VerticalPanel>();
	@SerialTypes( { ChatMessage.class })
	public static abstract class ChatCometSerializer extends CometSerializer {
	}
	// JSON Jackson Helper
	//ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * This is the entry point
	 */
	public void onModuleLoad() {
		pennBook = this;
		// Set up all the widgets for the app
		setUpLoginWidgets();
		setUpHomeWidgets();
		// Check if user is logged in
		boolean hasValidCookie = false;
		if(hasValidCookie){
			showHome();
		}
		else {
			showRegisterAndLogin();
		}
	}
	
	/**
	 * Show the registration/login page
	 */
	public void showRegisterAndLogin() {
		container.add(signinPanel);
		rootPanel.add(header);
		rootPanel.add(regPanel);
	}
	
	/**
	 * Show the home page
	 */
	public void showHome() {
		viewingAProfile = false;
		viewingAFriendProfile = true;
		refreshFriendRequests();
		refreshNotifications();
		refreshViewingMode();
		container.add(menu);
		rootPanel.add(header);
		rootPanel.add(homePanel);
		rootPanel.add(chatPanel);
		if (poll == null) {
			poll = new Timer() {
				// Poll every ten seconds for updates and refresh the page
				public void run() {
					GWT.log(userName+" is polling");
					refreshFriendIds();
					refreshFriendRequests();
					refreshNotifications();
					refreshFeedArea();
					friendshipService.getOnlineFriendships(userId, new AsyncCallback<List<Map<String,String>>>(){
						public void onFailure(Throwable caught) {
						}
						public void onSuccess(List<Map<String, String>> result) {
							GWT.log("Online friends: "+result.toString());
							onlineFriends = result;
							refreshRightSidebar();
						}
					});
				}
			};
			poll.scheduleRepeating(10000);
		}
	}
	
	/**
	 * Add cleared widgets from root panel
	 */
	private void setUpLoginWidgets() {
		// PennBook Header
		FlowPanel navBarInner = new FlowPanel();
		navBarInner.setStylePrimaryName("navbar-inner");
		navBarInner.add(container);
		
		header.setStylePrimaryName("navbar navbar-fixed-top");
		header.add(navBarInner);
		
		HTML pennbookHeader = new HTML("<a href=#><h3>PennBook</h3></a>");
		pennbookHeader.setStylePrimaryName("fixed-header float-left");
		pennbookHeader.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				viewingAProfile = false;
				viewingAFriendProfile = true;
				refreshViewingMode();
			}
		});
		
		container.add(pennbookHeader);
		container.setStylePrimaryName("container");
		
		// Log-in Form
		final LabeledTextBox emailSigninTbx = new LabeledTextBox("Email",false);
		emailSigninTbx.setStylePrimaryName("span2 short-input");
		
		final LabeledTextBox passwordSigninTbx = new LabeledTextBox("Password",true);
		passwordSigninTbx.setStylePrimaryName("span2 short-input");
		
		Button signinButton = new Button("Sign In");
		signinButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				// Fetch list of friends and display them
				boolean invalidSignin = false;
				if (invalidSignin) {
					
				}
				else {
					// Login
					userService.login(SafeHtmlUtils.htmlEscape(emailSigninTbx.getText()),
							SafeHtmlUtils.htmlEscape(passwordSigninTbx.getText()),
							new AsyncCallback<Map<String,ArrayList<String>>>() {
						public void onFailure(Throwable caught) {
							//Window.alert("Failed to login");
						}
						public void onSuccess(Map<String,ArrayList<String>> userMap) {
							// No errors!
							if (userMap == null) {
								Window.alert("That email and password combination can't be found on our system");
								return;
							}
							// Clear fields
							emailSigninTbx.setToDefaultText();
							passwordSigninTbx.setToDefaultText();
							DOM.setElementProperty(passwordSigninTbx.getElement(), "type", "text");
							
							userId = userMap.get(IKeyValueStorage.KEYWORD_ATTR).get(0);
							pictureURL = userMap.get(UserServiceImpl.PICTURE_ATTR).get(0);
							firstName = userMap.get(UserServiceImpl.FIRST_NAME_ATTR).get(0);
							lastName = userMap.get(UserServiceImpl.LAST_NAME_ATTR).get(0);
							userName = firstName+" "+lastName;
							email = userMap.get(UserServiceImpl.EMAIL_ATTR).get(0);
							birthday = userMap.get(UserServiceImpl.BIRTHDAY_ATTR).get(0);
							hometown = userMap.get(UserServiceImpl.HOMETOWN_ATTR).get(0);
							interests = userMap.get(UserServiceImpl.INTEREST_ATTR).get(0);
							affiliations = userMap.get(UserServiceImpl.AFFILIATION_ATTR).get(0);
							
							loginToChat();
							rootPanel.clear();
							container.remove(1);
							refreshFriendIds();
							refreshPics();
							refreshProfileInfo();
							showHome();
						}
					});
					
				}
			}
		});
		
		signinPanel.setStylePrimaryName("signin-panel");
		signinPanel.add(emailSigninTbx);
		signinPanel.add(passwordSigninTbx);
		signinPanel.add(signinButton);
		
		// Registration Form
		DOM.setElementProperty(regPanel.getElement(), "id", "regPanel");
		
		HTML signupHeader = new HTML("<h3>Sign Up</h3>");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		firstNameTbx.setStylePrimaryName("short-input");
		lastNameTbx.setStylePrimaryName("short-input");
		namePanel.add(firstNameTbx);
		namePanel.add(lastNameTbx);
		
		emailTbx.setStylePrimaryName("long-input");
		passwordTbx.setStylePrimaryName("long-input");
		
		HorizontalPanel birthdayBoxes = new HorizontalPanel();
		monthBox.setStylePrimaryName("bday-input");
		dayBox.setStylePrimaryName("bday-input");
		yearBox.setStylePrimaryName("bday-input");
		
		// Add month options
		monthBox.addItem("January");
		monthBox.addItem("February");
		monthBox.addItem("March");
		monthBox.addItem("April");
		monthBox.addItem("May");
		monthBox.addItem("June");
		monthBox.addItem("July");
		monthBox.addItem("August");
		monthBox.addItem("September");
		monthBox.addItem("October");
		monthBox.addItem("November");
		monthBox.addItem("December");
		// Add day options
		for (int i = 1; i < 32; i++) {
			dayBox.addItem(i+"");
		}
		// Add year options
		for (int i = 2011; i >= 1900; i--) {
			yearBox.addItem(i+"");
		}
		
		birthdayBoxes.add(monthBox);
		birthdayBoxes.add(dayBox);
		birthdayBoxes.add(yearBox);
		
		Button signupBtn = new Button("Sign Up");
		signupBtn.setStylePrimaryName("btn btn-primary");
		signupBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				// Fetch list of friends and display them
				ArrayList<String> errors = validateData();
				if(errors.size() > 0){
					// Error
					String errorString = "";
					for(String error : errors){
						errorString += error;
						errorString += "<br>";
					}
					errorLabel.setHTML(errorString);
					regPanel.add(errorLabel);
				}
				else {
					// Create a user account
					String birthdayText = monthBox.getItemText(monthBox.getSelectedIndex())+" "+dayBox.getItemText(dayBox.getSelectedIndex())+", "+yearBox.getItemText(yearBox.getSelectedIndex());
					userService.createUser(SafeHtmlUtils.htmlEscape(firstNameTbx.getText()),
							SafeHtmlUtils.htmlEscape(lastNameTbx.getText()),
							SafeHtmlUtils.htmlEscape(emailTbx.getText()),
							SafeHtmlUtils.htmlEscape(passwordTbx.getText()),
							birthdayText,
							new AsyncCallback<HashMap<String,String>>(){
								public void onFailure(Throwable caught) {
									// Registration failed
									//Window.alert("Registration Failed");
								}
								public void onSuccess(HashMap<String,String> userMap) {
									if (userMap == null) {
										Window.alert("That email address already exists in our database");
										return;
									}
									// Clear fields
									firstNameTbx.setToDefaultText();
									lastNameTbx.setToDefaultText();
									emailTbx.setToDefaultText();
									passwordTbx.setToDefaultText();
									// Reg success, go to home page
									userId = userMap.get(IKeyValueStorage.KEYWORD_ATTR);
									pictureURL = userMap.get(UserServiceImpl.PICTURE_ATTR);
									firstName = userMap.get(UserServiceImpl.FIRST_NAME_ATTR);
									lastName = userMap.get(UserServiceImpl.LAST_NAME_ATTR);
									userName = firstName+" "+lastName;
									email = userMap.get(UserServiceImpl.EMAIL_ATTR);
									birthday = userMap.get(UserServiceImpl.BIRTHDAY_ATTR);
									hometown = userMap.get(UserServiceImpl.HOMETOWN_ATTR);
									interests = userMap.get(UserServiceImpl.INTEREST_ATTR);
									affiliations = userMap.get(UserServiceImpl.AFFILIATION_ATTR);
									
									loginToChat();
									refreshProfileInfo();
									rootPanel.clear();
									container.remove(1);
									showHome();
								}
					});
				}
			}
		});
		
		DOM.setElementProperty(errorLabel.getElement(), "id", "errorLabel");
		errorLabel.setStylePrimaryName("alert alert-error");
		
		regPanel.add(signupHeader);
		regPanel.add(namePanel);
		regPanel.add(emailTbx);
		regPanel.add(passwordTbx);
		regPanel.add(birthdayBoxes);
		regPanel.add(signupBtn);
	}
	
	// Login to chat
	private void loginToChat() {
		chatService.login(userId, new AsyncCallback<Void>(){
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to login");
			}
			public void onSuccess(Void result) {
				// Set up comet listener
                CometSerializer serializer = GWT.create(ChatCometSerializer.class);
                CometListener listener = new CometListener(){
                    public void onConnected(int heartbeat) { GWT.log("connected "+heartbeat); }
	                public void onDisconnected() { GWT.log("disconnected"); }
	                public void onError(Throwable exception, boolean connected) {
	                	GWT.log("error " + connected + " " + exception);
	                }
	                public void onHeartbeat() {
	                    GWT.log("heartbeat");
	                }
	                public void onRefresh() {
	                	GWT.log("refresh");
	                }
	                // Message received, add ChatBox
	                public void onMessage(List<? extends Serializable> messages) {
	                        for (Serializable message : messages) {
	                                if (message instanceof ChatMessage) {
	                                        ChatMessage chatMessage = (ChatMessage) message;
	                                        String senderId = chatMessage.senderId;
	                                        String senderName = chatMessage.senderName;
	                                        String content = chatMessage.message;
	                                        // Check if a window is already open
	                                        if (!senderId.equals(userId)) {
		                                        if (chatsOpen.containsKey(senderId)) {
		                                        	// Add chat to already open chat box panel
		                                        	chatsOpen.get(senderId).addMessage(senderName, content);
		                                        }
		                                        else {
		                                        	// Create chat box panel
		                							ChatBoxPanel chatBox = new ChatBoxPanel(userId, userName, senderId, senderName, pennBook);
		                							int chatsOpenCount = chatsOpen.size();
													chatContainerMap.get(chatsOpenCount+1).add(chatBox);
		                							chatsOpen.put(senderId, chatBox);
		                							chatsOpen.get(senderId).addMessage(senderName, content);
		                                        }
	                                        }
	                                }
	                                else {
	                                       // Unrecognized message
	                                }
	                        }
	                }
                };
                cometClient = new CometClient(GWT.getModuleBaseURL() + "comet", serializer, listener);
                cometClient.start();
			}
		});
	}
	
	private void refreshFriendRequests() {
		friendRequestService.getFriendRequests(userId, new AsyncCallback<ArrayList<Map<String,String>>>(){ 
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to retrieve friend requests");
			}
			public void onSuccess(ArrayList<Map<String,String>> requestsList) {
				requestsPopup.clear();
				if(requestsList.size() != 0) {
					// Some requests exist
					for (Map<String,String> request : requestsList) {
						// Add to popup
						final String requestId = request.get(IKeyValueStorage.KEYWORD_ATTR);
						final String senderName = request.get(FriendRequestServiceImpl.SENDER_NAME_ATTR);
						final String senderId = request.get(FriendRequestServiceImpl.SENDER_ID_ATTR);
						final HorizontalPanel requestItem = new HorizontalPanel();
						FlowPanel requestItemWrapper = new FlowPanel();
						requestItem.add(new HTML("<img src='"+pics.get(senderId)+"' width=40px>"));
						requestItem.add(new HTML(senderName+" would like to add you as a friend&nbsp;&nbsp;"));
						final Button acceptFriendRequestBtn = new Button("Accept");
						acceptFriendRequestBtn.setStylePrimaryName("btn btn-primary");
						acceptFriendRequestBtn.addClickHandler(new ClickHandler(){
							public void onClick(ClickEvent event) {
								// Immediately disable button
								acceptFriendRequestBtn.setEnabled(false);
								friendIds.add(senderId);
								// Asyncronously add friendship and propagate feed
								friendshipService.addFriend(userId, senderId, userName, senderName, requestId, new AsyncCallback<Void>(){
									public void onFailure(Throwable caught) {
										//Window.alert("Failed to create friendship");
									}
									public void onSuccess(Void result) {
										refreshFeedArea();
									}
								});
							}
						});
						requestItem.add(acceptFriendRequestBtn);
						requestItem.setStylePrimaryName("popup-item");
						requestItemWrapper.add(requestItem);
						requestsPopup.add(requestItemWrapper);
					}
				}
			}
		});
	}
	
	private void refreshNotifications() {
		feedService.getNotifications(userId, new AsyncCallback<ArrayList<Map<String,ArrayList<String>>>>(){
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to gather notifications");
			}
			public void onSuccess(
					ArrayList<Map<String, ArrayList<String>>> notificationsList) {
				notificationsPopup.clear();
				for (Map<String,ArrayList<String>> notification : notificationsList) {
					// Add to popup
					final HorizontalPanel notificationItem = new HorizontalPanel();
					FlowPanel notificationItemWrapper = new FlowPanel();
					FeedItemType type = FeedItemType.values()[Integer.parseInt(notification.get(FeedServiceImpl.TYPE_ATTR).get(0)+"")];
					
					String posterId = notification.get(FeedServiceImpl.POSTER_ID_ATTR).get(0);
					String posterName = notification.get(FeedServiceImpl.POSTER_NAME_ATTR).get(0);
					notificationItem.add(new HTML("<img src='"+pics.get(posterId)+"' width=40px>"));
					switch(type){
						case NEW_FRIENDSHIP:
							notificationItem.add(new HTML("&nbsp;"+posterName+" accepted your friend request"));
							break;
						case POST:
							notificationItem.add(new HTML("&nbsp;"+posterName+" wrote on your wall"));
							break;
					}
					notificationItem.setStylePrimaryName("popup-item");
					notificationItemWrapper.add(notificationItem);
					notificationsPopup.add(notificationItemWrapper);
				}
			}			
		});
	}
	/**
	 * Setup widgets for home
	 */
	private void setUpHomeWidgets() {
		
		// Notifications and Friend Requests
		requestsPanel.setStylePrimaryName("float-left");
		HTML requestsIcon = new HTML("<img src='bootstrap/img/requests'>&nbsp;&nbsp;");
		requestsIcon.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				if (!requestsPopupIsOpen) {
					requestsPanel.add(requestsPopup);
					requestsPopupIsOpen = true;
				}
				else {
					requestsPanel.remove(1);
					requestsPopupIsOpen = false;
				}
			}
		});
		requestsPopup.addStyleName("popup");
		requestsPanel.add(requestsIcon);
		
		notificationsPanel.setStylePrimaryName("float-left");
		HTML notificationsIcon = new HTML("<img src='bootstrap/img/notifications'>&nbsp;&nbsp;");
		notificationsIcon.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				if (!notificationsPopupIsOpen) {
					notificationsPanel.add(notificationsPopup);
					notificationsPopupIsOpen = true;
				}
				else {
					notificationsPanel.remove(1);
					notificationsPopupIsOpen = false;
				}
			}
		});
		notificationsPopup.addStyleName("popup");
		notificationsPanel.add(notificationsIcon);
		
		requestsAndNotificationsBar.add(requestsPanel);
		requestsAndNotificationsBar.add(notificationsPanel);
		requestsAndNotificationsBar.setStylePrimaryName("requests-notifications-bar");
		
		// Search
		final LabeledTextBox searchBox = new LabeledTextBox("Search", false);
		searchBox.setStylePrimaryName("search-query search-box");
		searchBox.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
				searchPopup.clear();
				if (!SafeHtmlUtils.htmlEscape(searchBox.getText()).equals("")) {
					userService.search(SafeHtmlUtils.htmlEscape(searchBox.getText()).toLowerCase(), new AsyncCallback<List<Map<String, ArrayList<String>>>>(){
						public void onFailure(Throwable caught) {
							//Window.alert("Failed to search term");
						}
						public void onSuccess(
								List<Map<String, ArrayList<String>>> result) {
							if (result.size() > 0) {
								searchPopup.clear();
								// Dynamically show suggestions
								for (Map<String,ArrayList<String>> user : result) {
									FlowPanel searchItemWrapper = new FlowPanel();
									HorizontalPanel searchItem = new HorizontalPanel();
									final String userId = user.get(IKeyValueStorage.KEYWORD_ATTR).get(0);
									String userName = user.get(UserServiceImpl.FIRST_NAME_ATTR).get(0)+" "+user.get(UserServiceImpl.LAST_NAME_ATTR).get(0);
									searchItem.setStylePrimaryName("popup-item");
									searchItem.add(new HTML("<img src='"+pics.get(userId)+"' width=40px>"));
									HTML searchUserName = new HTML("<a href=#>"+userName+"</a>");
									searchUserName.addClickHandler(new ClickHandler(){
										public void onClick(ClickEvent event) {
											setProfileUserId(userId);
											searchPopup.clear();
											searchBox.setToDefaultText();
										}
									});
									searchItem.add(searchUserName);
									searchItemWrapper.add(searchItem);
									searchPopup.add(searchItemWrapper);
								}
							}
						}
					});
				}
			}
		});
		
		searchPopup.setStylePrimaryName("popup");
		searchPanel.setStylePrimaryName("float-left");
		searchPanel.add(searchBox);
		searchPanel.add(searchPopup);
		
		// View profile button
		HTML viewProfileButton = new HTML("<a href=#>My Profile</a>");
		viewProfileButton.setStylePrimaryName("dropdown-item float-right");
		viewProfileButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setProfileUserId(userId);
			}
		});

		// Logout button
		HTML logoutButton = new HTML("<a href=#>Logout</a>");
		logoutButton.setStylePrimaryName("dropdown-item float-right");
		logoutButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				rootPanel.clear();
				container.remove(1);
				showRegisterAndLogin();
				chatService.logout(userId, new AsyncCallback<Void>(){
					public void onFailure(Throwable caught) {
						//Window.alert("Failed to log out of chat");
					}
					public void onSuccess(Void result) {
					}
					
				});
				poll.cancel();
			}
			
		});

		menu.setStylePrimaryName("menu-panel");
		menu.add(requestsAndNotificationsBar);
		menu.add(searchPanel);
		menu.add(logoutButton);
		menu.add(viewProfileButton);
		
		DOM.setElementProperty(homePanel.getElement(), "id", "homePanel");
		homePanel.setStylePrimaryName("row");

		// Profile Section
		VerticalPanel profilePanel = new VerticalPanel();
		DOM.setElementProperty(profilePanel.getElement(), "id", "profile");

		profilePanel.add(profileContainer);
		profileContainer.setStylePrimaryName("profile-container");

		nameAndPicPanel.setStylePrimaryName("name-pic-panel");
		refreshProfileInfo();

		// Visualizer
		nodeDetailPanel.setStylePrimaryName("node-detail-panel");
		modal.setModal(true);
		modal.setWidth("400px");
		modal.setHeight("400px");
		modal.setTitle("Network Visualizer");
		
		VerticalPanel nodeDetailWrapper = new VerticalPanel();
		nodeDetailWrapper.setStylePrimaryName("node-detail-wrapper");
		nodeDetailWrapper.add(nodeDetailPanel);
		
		HTML closeVisualizer = new HTML("<a href=#>Close</a>");
		closeVisualizer.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				modal.hide();
			}
		});

		// Draw initial
		DOM.setElementProperty(graphPanel.getElement(), "id", "infovis");
		graphPanel.add(new HTML("&nbsp;"));
		visualizerPanel.add(closeVisualizer);
		visualizerPanel.add(graphPanel);
		visualizerPanel.add(nodeDetailPanel);
		visualizerPanel.add(nodeDetailWrapper);
		modal.add(visualizerPanel);

		// Feed Section
		VerticalPanel feedPanel = new VerticalPanel();
		DOM.setElementProperty(feedPanel.getElement(), "id", "feed");
		
		VerticalPanel feedContainer = new VerticalPanel();
		feedPanel.add(feedContainer);
		feedContainer.setStylePrimaryName("feed-panel");
		feedContainer.setStylePrimaryName("feed-container");
		
		// Status and Wall Post
		// MESSAGE - STATUS 
		statusBox.addStyleName("message-box");
		submitStatus.addStyleName("float-right");
		submitStatus.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(!statusBox.isDefaultOrEmpty()) {
					// Store in database
					feedService.postFeedItem(SafeHtmlUtils.htmlEscape(statusBox.getText()), userId, "", userName,
							"", FeedItemType.STATUS, new AsyncCallback<Void>(){
								public void onFailure(Throwable caught) {
									//Window.alert("Failed to post status update");
								}
								public void onSuccess(Void result) {
									refreshFeedArea();
								}});
					// Clear box
					statusBox.setToDefaultText();
				}
			}
		});
		// MESSAGE - STATUS 
		wallpostBox.addStyleName("message-box");
		submitWallpost.addStyleName("float-right");
		submitWallpost.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(!wallpostBox.isDefaultOrEmpty()) {
					// Store in database
					feedService.postFeedItem(SafeHtmlUtils.htmlEscape(wallpostBox.getText()), userId, profileUserId, userName,
							profileUserName, FeedItemType.POST, new AsyncCallback<Void>(){
								public void onFailure(Throwable caught) {
									//Window.alert("Failed to post status update");
								}
								public void onSuccess(Void result) {
									refreshFeedArea();
								}});
					// Clear box
					wallpostBox.setToDefaultText();
				}
			}
			
		});
		
		// MESSAGE - WALLPOST
		refreshMessagePanel();
		messagePanel.setStylePrimaryName("status-panel");
		feedContainer.add(messagePanel);
		
		allFeeds.setStylePrimaryName("all-feeds-container");
		feedContainer.add(allFeeds);
		
		// Recommendation Section
		rightSidebarPanel.setStylePrimaryName("span2");
		DOM.setElementProperty(rightSidebarPanel.getElement(), "id", "right-sidebar");
		recommendedFriendsPanel.setStylePrimaryName("recommend-friends-panel");
		rightSidebarPanel.add(recommendedFriendsPanel);
		rightSidebarPanel.add(onlineFriendsPanel);
		
		homePanel.add(profilePanel);
		homePanel.add(feedPanel);
		homePanel.add(rightSidebarPanel);
		
		// Chat section
		VerticalPanel chatContainer1 = new VerticalPanel();
		VerticalPanel chatContainer2 = new VerticalPanel();
		VerticalPanel chatContainer3 = new VerticalPanel();
		VerticalPanel chatContainer4 = new VerticalPanel();
		VerticalPanel chatContainer5 = new VerticalPanel();
		chatContainer1.add(new HTML("&nbsp;"));
		chatContainer2.add(new HTML("&nbsp;"));
		chatContainer3.add(new HTML("&nbsp;"));
		chatContainer4.add(new HTML("&nbsp;"));
		chatContainer5.add(new HTML("&nbsp;"));
		chatContainer1.setStylePrimaryName("chat-container");
		chatContainer2.setStylePrimaryName("chat-container");
		chatContainer3.setStylePrimaryName("chat-container");
		chatContainer4.setStylePrimaryName("chat-container");
		chatContainer5.setStylePrimaryName("chat-container");
		chatContainerMap.put(1, chatContainer1);
		chatContainerMap.put(2, chatContainer2);
		chatContainerMap.put(3, chatContainer3);
		chatContainerMap.put(4, chatContainer4);
		chatContainerMap.put(5, chatContainer4);
		chatPanel.add(chatContainer1);
		chatPanel.add(chatContainer2);
		chatPanel.add(chatContainer3);
		chatPanel.add(chatContainer4);
		chatPanel.add(chatContainer5);
		chatPanel.setStylePrimaryName("chat-panel");
	}
	
	// Call this when viewing another profile (or your own profile)
	public void setProfileUserId(final String profileId){
		userService.get(profileId, new AsyncCallback<Map<String,ArrayList<String>>>(){
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to retrieve profile information.");
			}
			public void onSuccess(Map<String, ArrayList<String>> profileUser) {
				profileUserId = profileId;
				profilePictureURL = profileUser.get(UserServiceImpl.PICTURE_ATTR).get(0);
				profileFirstName = profileUser.get(UserServiceImpl.FIRST_NAME_ATTR).get(0);
				profileLastName = profileUser.get(UserServiceImpl.LAST_NAME_ATTR).get(0);
				profileUserName = profileFirstName+" "+profileLastName;
				profileEmail = profileUser.get(UserServiceImpl.EMAIL_ATTR).get(0);
				profileBirthday = profileUser.get(UserServiceImpl.BIRTHDAY_ATTR).get(0);
				profileHometown = profileUser.get(UserServiceImpl.HOMETOWN_ATTR).get(0);
				profileAffiliations = profileUser.get(UserServiceImpl.AFFILIATION_ATTR).get(0);
				profileInterests = profileUser.get(UserServiceImpl.INTEREST_ATTR).get(0);
				viewingAProfile = true;
				if(friendIds.contains(profileUserId)||profileUserId.equals(userId)){
					viewingAFriendProfile = true;
				}
				else {
					viewingAFriendProfile = false;
				}
				refreshPics();
				refreshViewingMode();
			}
		});
	}
	
	// Update your friends
	private void refreshFriendIds() {
		friendshipService.getFriendIds(userId, new AsyncCallback<List<String>>(){
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to get friend ids");
			}
			public void onSuccess(List<String> result) {
				for (String friendId : result) {
					friendIds.add(friendId);
				}
			}
		});
	}
	
	// Call this after clicking to view a profile, this method renders the page appropriately
	private void refreshViewingMode() {
		refreshProfileInfo();
		if (viewingAFriendProfile || profileUserId.equals(userId)) {
			// Viewing a profile of a friend
			refreshMessagePanel();
			refreshFeedArea();
		}
		else {
			// Not a friend
			messagePanel.clear();
			allFeeds.clear();
			HorizontalPanel notFriendsPanel = new HorizontalPanel();
			Button addFriendBtn = new Button("Add Friend");
			addFriendBtn.setStylePrimaryName("btn btn-primary");
			addFriendBtn.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					friendRequestService.createFriendRequest(userId, profileUserId, userName, new AsyncCallback<HashMap<String, String>>(){
						public void onFailure(Throwable caught) {
							//Window.alert("Failed to send friend request");
						}
						public void onSuccess(HashMap<String, String> result) {
							viewingAProfile = false;
							viewingAFriendProfile = true;
							refreshViewingMode();
							Window.alert("Friend request sent!");
						}
					});
				}
			});
			
			notFriendsPanel.setStylePrimaryName("not-friends-panel");
			notFriendsPanel.add(new Label("You are not friends with "+profileUserName));
			notFriendsPanel.add(addFriendBtn);
			messagePanel.add(notFriendsPanel);
		}
		refreshRightSidebar();
	}
	
	private void refreshProfileInfo() {
		nameAndPicPanel.clear();
		profileContainer.clear();
		profilePicEditable.clear();
		hometownEditable.clear();
		affiliationsEditable.clear();
		interestsEditable.clear();
		
		String userIdToUse;
		String pictureURLToShow;
		String firstNameToShow;
		String lastNameToShow;
		String emailToShow;
		String birthdayToShow;
		String hometownToShow;
		String affiliationsToShow;
		String interestsToShow;
		if(viewingAProfile){
			userIdToUse = profileUserId;
			pictureURLToShow = profilePictureURL;
			firstNameToShow = profileFirstName;
			lastNameToShow = profileLastName;
			emailToShow = profileEmail;
			birthdayToShow = profileBirthday;
			hometownToShow = profileHometown;
			affiliationsToShow = profileAffiliations;
			interestsToShow = profileInterests;
		}
		else{
			userIdToUse = userId;
			pictureURLToShow = pictureURL;
			firstNameToShow = firstName;
			lastNameToShow = lastName;
			emailToShow = email;
			birthdayToShow = birthday;
			hometownToShow = hometown;
			affiliationsToShow = affiliations;
			interestsToShow = interests;
		}
		final HTML profilePicture = new HTML("<img src='"+pictureURLToShow+"' width=100px border=1 class=profile-pic>");
		nameAndPicPanel.add(profilePicture);
		nameAndPicPanel.add(new HTML("<h4>"+firstNameToShow+"<br>"+lastNameToShow+"</h4>"));
		
		// Picture
		profileContainer.add(nameAndPicPanel);
		profileContainer.add(profilePicEditable);
		editProfilePicBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				profilePicEditable.clear();
				final TextBox profilePicTbx = new TextBox();
				profilePicTbx.setText(pictureURL);
				profilePicEditable.add(profilePicTbx);
				profilePicTbx.addKeyDownHandler(new KeyDownHandler(){
					public void onKeyDown(KeyDownEvent event) {
						if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							pictureURL = profilePicTbx.getText();
							profilePicture.setHTML("<img src='"+pictureURL+"' width=100px border=1 class=profile-pic>");
							profilePicEditable.clear();
							userService.setPictureURL(userId, userName, pictureURL, new AsyncCallback<Void>(){
								public void onFailure(Throwable caught) {
									//Window.alert("Failed to update profile picture");
								}
								public void onSuccess(Void result) {
									refreshPics();
								}
							});
						}
					}
				});
			}
		});
		if (viewingAProfile == false) 
			profileContainer.add(editProfilePicBtn);
		
		profileContainer.add(new HTML("<b>Birthday:</b> "+birthdayToShow));
		profileContainer.add(new HTML("<b>Email:</b> "+emailToShow));
		
		// Hometown
		profileContainer.add(new HTML("<b>Hometown:</b>"));
		hometownEditable.add(new Label(hometownToShow));
		profileContainer.add(hometownEditable);
		editHometownBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				hometownEditable.clear();
				final TextBox hometownTbx = new TextBox();
				hometownTbx.setText(hometown);
				hometownTbx.addKeyDownHandler(new KeyDownHandler() {
					public void onKeyDown(KeyDownEvent event) {
						if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							// Edit hometown
							if(hometownTbx.getText().equals(hometown) == false) {
								hometownEditable.clear();
								hometownEditable.add(new HTML(hometownTbx.getText()));
								hometown = hometownTbx.getText();
								userService.setHometown(userId, userName, pictureURL, hometownTbx.getText(), new AsyncCallback<Void>(){
									public void onFailure(Throwable caught) {
										//Window.alert("Failed to update hometown");
									}
									public void onSuccess(Void result) {
									}
								});
							}
						}
					}
				});
				
				hometownEditable.add(hometownTbx);
			}
		});
		if (viewingAProfile == false) 
			profileContainer.add(editHometownBtn);
		
		// Affiliation
		profileContainer.add(new HTML("<b>Affiliation:</b>"));
		affiliationsEditable.add(new Label(affiliationsToShow));
		profileContainer.add(affiliationsEditable);
		editAffiliationsBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				affiliationsEditable.clear();
				final TextBox affiliationsTbx = new TextBox();
				affiliationsTbx.setText(affiliations);
				affiliationsTbx.addKeyDownHandler(new KeyDownHandler() {
					public void onKeyDown(KeyDownEvent event) {
						if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							// Edit affiliations
							affiliationsEditable.clear();
							affiliationsEditable.add(new HTML(affiliationsTbx.getText()));
							affiliations = affiliationsTbx.getText();
							userService.setAffiliations(userId, userName, pictureURL, affiliationsTbx.getText(), new AsyncCallback<Void>(){
								public void onFailure(Throwable caught) {
									//Window.alert("Failed to update affiliations");
								}
								public void onSuccess(Void result) {
								}
							});
						}
					}
				});
				
				affiliationsEditable.add(affiliationsTbx);
			}
		});
		if (viewingAProfile == false) 
			profileContainer.add(editAffiliationsBtn);
		
		// Interests
		profileContainer.add(new HTML("<b>Interests:</b>"));
		interestsEditable.add(new Label(interestsToShow));
		profileContainer.add(interestsEditable);
		editInterestsBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				interestsEditable.clear();
				final TextBox interestsTbx = new TextBox();
				interestsTbx.setText(interests);
				interestsTbx.addKeyDownHandler(new KeyDownHandler() {
					public void onKeyDown(KeyDownEvent event) {
						if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							// Edit interests
							interestsEditable.clear();
							interestsEditable.add(new HTML(interestsTbx.getText()));
							interests = interestsTbx.getText();
							userService.setInterests(userId, userName, pictureURL, interestsTbx.getText(), new AsyncCallback<Void>(){
								public void onFailure(Throwable caught) {
									//Window.alert("Failed to update interests");
								}
								public void onSuccess(Void result) {
								}
							});
							
						}
					}
				});
				
				interestsEditable.add(interestsTbx);
			}
		});
		if (viewingAProfile == false)
			profileContainer.add(editInterestsBtn);
		
		// Remove friend (if viewing a friend's profile)
		if (viewingAProfile && friendIds.contains(profileUserId) && !profileUserId.equals(userId)) {
			HTML deleteFriendBtn = new HTML("<a href=#>Remove friend</a>");
			deleteFriendBtn.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					// Delete friend here!!!
					friendshipService.deleteFriendship(userId, profileUserId, new AsyncCallback<Boolean>(){
						public void onFailure(Throwable caught) {
							//Window.alert("Failed to delete friend");
						}
						public void onSuccess(Boolean result) {
							friendIds.remove(profileUserId);
							viewingAProfile = false;
							viewingAFriendProfile = true;
							refreshViewingMode();
						}
						
					});
				}
			});
			profileContainer.add(deleteFriendBtn);
		}
		
		// Visualizer (if on home page)
		if (viewingAProfile == false) {
			HTML showVisualizerBtn = new HTML("<h4><a href=#>Network Visualizer</a></h4>");
			profileContainer.add(showVisualizerBtn);
			showVisualizerBtn.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
						//graphPanel.clear();
						nodeDetailPanel.clear();
						modal.center();
						drawNodeAndNeighbors(userId);
					}
			});
		}
	}
	
	// Method called when the visualizer is shown
	public void drawNodeAndNeighbors(final String s) {
		final PennBook view = this;
		friendshipService.getNetwork(userId,
				new AsyncCallback<ArrayList<String>>() {
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to get network");
			}
			public void onSuccess(ArrayList<String> trees) {
				// Visualize results
				GWT.log(trees.toString());
				for (String tree : trees) {
					if (graph != null) {
						FriendVisualization.addToGraph(graph, tree);
					}
					else {
						graph = FriendVisualization.createGraph(tree, view);
					}
				}
			}
		});
	}
	
	// Method called when a node is clicked
	public void showNodeDetails(final String s) {
		userService.getPublicDetails(s,
				new AsyncCallback<Map<String,String>>() {
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to get public details");
			}
			public void onSuccess(final Map<String,String> user) {
				// Show node info
				nodeDetailPanel.clear();
				HorizontalPanel visualizerNameAndPicPanel = new HorizontalPanel();
				HTML profilePicture = new HTML("<img src='"+user.get(UserServiceImpl.PICTURE_ATTR)+"' width=100px border=1 class=profile-pic>");
				visualizerNameAndPicPanel.add(profilePicture);
				visualizerNameAndPicPanel.add(new HTML("<h4>"+user.get(UserServiceImpl.FIRST_NAME_ATTR)+"<br>"+user.get(UserServiceImpl.LAST_NAME_ATTR)+"</h4>"));
				Button viewProfileFromVisualizerBtn = new Button("View Profile");
				viewProfileFromVisualizerBtn.setStylePrimaryName("btn btn-primary");
				viewProfileFromVisualizerBtn.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event) {
						setProfileUserId(user.get(UserServiceImpl.USER_ID_ATTR));
						modal.hide();
					}
				});
				
				nodeDetailPanel.add(visualizerNameAndPicPanel);
				nodeDetailPanel.add(new HTML("<b>Birthday:</b> "+user.get(UserServiceImpl.BIRTHDAY_ATTR)));
				nodeDetailPanel.add(new HTML("<b>Email:</b> "+user.get(UserServiceImpl.EMAIL_ATTR)));
				nodeDetailPanel.add(new HTML("<b>Affiliation:</b> "+user.get(UserServiceImpl.AFFILIATION_ATTR)));
				nodeDetailPanel.add(new HTML("<b>Interests:</b> "+user.get(UserServiceImpl.INTEREST_ATTR)));
				nodeDetailPanel.add(viewProfileFromVisualizerBtn);	
			}
		});
	}
	
	// Refresh between one's home page or a profile page
	private void refreshMessagePanel() {
		messagePanel.clear();
		if (viewingAProfile && profileUserId.equals(userId)==false) {
			messagePanel.add(wallpostBox);
			messagePanel.add(submitWallpost);
		}
		else {
			messagePanel.add(statusBox);
			messagePanel.add(submitStatus);
		}
	}
	
	// Refresh feeds
	public void refreshFeedArea() {
		if (!viewingAFriendProfile)
			return;
		if (viewingAProfile) {
			feedService.getWall(profileUserId, new AsyncCallback<ArrayList<Map<String,ArrayList<String>>>>(){
				public void onFailure(Throwable caught) {
					//Window.alert("Failure to gather wall");
				}
				public void onSuccess(ArrayList<Map<String,ArrayList<String>>> wallItems) {
					fillFeedAreaWith(wallItems);
				}
			});
		}
		else {
			feedService.getFeedItems(userId, new AsyncCallback<ArrayList<Map<String,ArrayList<String>>>>() {
				public void onFailure(Throwable caught) {
					//Window.alert("Failure to gather feed");
				}
				public void onSuccess(ArrayList<Map<String,ArrayList<String>>> feedItems) {
					fillFeedAreaWith(feedItems);
				}
			});
		}
	}
	
	private void refreshRightSidebar() {
		// Refresh recommendations (only used to refresh pictures)
		recommendedFriendsPanel.clear();
		recommendedFriendsPanel.add(new HTML("<b>People You May Know</b>"));
		recommendationService.getRecommendations(userId, new AsyncCallback<ArrayList<Map<String,String>>>(){
			public void onFailure(Throwable caught) {
			}
			public void onSuccess(ArrayList<Map<String, String>> result) {
				for (Map<String,String> user : result) {
					final String recId = user.get("id");
					if (friendIds.contains(recId)) continue;
					final String recName = user.get("name");
					HorizontalPanel recFriendPanel = new HorizontalPanel();
					recFriendPanel.setStylePrimaryName("online-friend-panel");
					HTML recFriendPic = new HTML("<img src='"+pics.get(recId)+"' width=40 height=40>");
					HTML recFriendLink = new HTML("<a href=#>"+recName+"</a>");
					recFriendLink.addClickHandler(new ClickHandler(){
						public void onClick(ClickEvent event) {
							setProfileUserId(recId);
						}
					});
					recFriendPanel.add(recFriendPic);
					recFriendPanel.add(recFriendLink);
					recommendedFriendsPanel.add(recFriendPanel);
				}
			}
		});

		// Refresh online friends
		onlineFriendsPanel.clear();
		onlineFriendsPanel.add(new HTML("<h6>Online Friends</h6>"));
		for (final Map<String,String> onlineFriend : onlineFriends) {
			final String friendId = onlineFriend.get(FriendshipServiceImpl.FRIEND_USER_ID_ATTR);
			final String friendName = onlineFriend.get(FriendshipServiceImpl.FIRST_NAME_ATTR)+" "+onlineFriend.get(FriendshipServiceImpl.LAST_NAME_ATTR);
			HorizontalPanel onlineFriendPanel = new HorizontalPanel();
			onlineFriendPanel.setStylePrimaryName("online-friend-panel");
			HTML onlineFriendPic = new HTML("<img src='"+pics.get(friendId)+"' width=40 height=40>");
			HTML onlineFriendLink = new HTML("<a href=#>"+friendName+"</a>");
			onlineFriendLink.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					if (!chatsOpen.containsKey(friendId)) {
						ChatBoxPanel chatBox = new ChatBoxPanel(userId, userName, friendId, friendName, pennBook);
						int chatsOpenCount = chatsOpen.size();
						chatContainerMap.get(chatsOpenCount+1).add(chatBox);
						chatsOpen.put(friendId, chatBox);
					}
				}
			});
			onlineFriendPanel.add(onlineFriendPic);
			onlineFriendPanel.add(onlineFriendLink);
			onlineFriendsPanel.add(onlineFriendPanel);
		}
	}
	
	// Refresh pictures of users
	private void refreshPics() {
		userService.getPics(new AsyncCallback<Map<String,String>>(){
			public void onFailure(Throwable caught) {
				//Window.alert("Failed to get friends' picture URLs");
			}
			public void onSuccess(Map<String, String> result) {
				pics = result;
				refreshFeedArea();
			}
		});
	}
	
	// Helper function for filling up a feed for one's own profile or other people's profile
	private void fillFeedAreaWith(ArrayList<Map<String,ArrayList<String>>> posts) {
		allFeeds.clear();
		for(Map<String,ArrayList<String>> post : posts) {
			FeedPanel feedItem = new FeedPanel(post.get(FeedServiceImpl.POST_ID_ATTR).get(0), post.get(FeedServiceImpl.MESSAGE_ATTR).get(0),
					post.get(FeedServiceImpl.POSTER_ID_ATTR).get(0), post.get(FeedServiceImpl.RECEIVER_ID_ATTR).get(0),
					post.get(FeedServiceImpl.POSTER_NAME_ATTR).get(0), post.get(FeedServiceImpl.RECEIVER_NAME_ATTR).get(0),
					pics.get(post.get(FeedServiceImpl.POSTER_ID_ATTR).get(0)),
					post.get(FeedServiceImpl.COMMENT_ATTR),
					FeedItemType.values()[Integer.parseInt(post.get(FeedServiceImpl.TYPE_ATTR).get(0)+"")], pennBook);
			allFeeds.add(feedItem);
		}
	}
	
	// Helper function for creating a tree to pass in to visualizer
	public static String treeFromUserAndFriends(Map<String,String> user, Set<Map<String,String>> friends) {
		String tree = "{\"id\": \""+user.get("userId")+"\", \"name\": \""+user.get("userName")+"\", \"children\": [";
		boolean first = true;
		for (Map<String,String> friend : friends) {
			if (!first) { tree += ","; }
			else { first = false; }
			tree += "{\"id\": \""+friend.get("friendId")+"\", \"name\": \""+friend.get("friendName")+"\", \"children\": []}";
		}
		tree += "]}";
		return tree;
	}
	
	// Validation method for registration fields
	private ArrayList<String> validateData() {
		String email = emailTbx.getText().trim();
		String password = passwordTbx.getText().toLowerCase();
		ArrayList<String> errors = new ArrayList<String>();
		if(firstNameTbx.isDefaultOrEmpty()){
			errors.add("Please enter your first name");
		}
		if(lastNameTbx.isDefaultOrEmpty()){
			errors.add("Please enter your last name");
		}
		if(passwordTbx.isDefaultOrEmpty() || password.length() < 5){
			errors.add("Password must be at least 5 characters long");
		}
		if(emailTbx.isDefaultOrEmpty() || email.matches("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})")==false){
			errors.add("Please enter a valid email");
		}
		return errors;
	}
}
