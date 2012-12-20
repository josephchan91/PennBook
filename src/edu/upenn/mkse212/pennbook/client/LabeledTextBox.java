package edu.upenn.mkse212.pennbook.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TextBox;

/** A class for containing a textbox with a guide text that disappears when focused **/
public class LabeledTextBox extends TextBox {
	String defaultText;
	boolean isPassword;
	TextBox textBox;
	
	public LabeledTextBox(String s, boolean b) {
		super();
		defaultText = s;
		isPassword = b;
		textBox = this;
		
		textBox.setText(defaultText);
		
		// Clear the text box upon focus if it contains the default text
		this.addFocusHandler(new FocusHandler(){
			public void onFocus(FocusEvent e) {
				if(textBox.getText().equals(defaultText)){
					textBox.setText("");
					if(isPassword){
						DOM.setElementProperty(textBox.getElement(), "type", "password");
					}
				}
			}
		});
		
		// On leaving focus, revert to default text if text box is empty
		this.addBlurHandler(new BlurHandler(){
			public void onBlur(BlurEvent event) {
				if(textBox.getText().equals("")){
					textBox.setText(defaultText);
					if(isPassword){
						DOM.setElementProperty(textBox.getElement(), "type", "text");
					}
				}
			}
		});
	}
	
	// Helper method for validating a LabeledTextBox
	public boolean isDefaultOrEmpty() {
		if (textBox.getText().equals(defaultText)) {
			return true;
		}
		if (textBox.getText().trim().isEmpty()) {
			return true;
		}
		return false;
	}
	
	// Helper method for returning a textbox to default text after the input has been processed
	public void setToDefaultText() {
		textBox.setText(defaultText);
	}
}

