package com.example.demo.page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import static com.example.demo.page.PageConstant.*;

/**
 * Single file upload page base class.
 */
public abstract class SingleFileUploadPageBase extends VerticalPageBase {

	private static final long serialVersionUID = 1L;
	protected static final int MAX_COL = 2;
	
	@Override
	protected Component createComponent() {
		VerticalLayout vlayout = new VerticalLayout();
		vlayout.add(createExplainMessage());
		FormLayout layout = new FormLayout();
		Upload upload = new Upload();
		upload.setReceiver(new FileBuffer());
		addComponent(layout, FIELD_FILE, upload);
		setColSpan(layout, FIELD_FILE, MAX_COL);
		componentService.addBlankLabel(layout, MAX_COL);
		addComponent(layout, BUTTON_REGISTER, createRegisterButton());
		vlayout.add(layout);
		return vlayout;
	}
	/**
	 * Create register button.
	 * @return Button
	 */
	protected abstract Button createRegisterButton();
	/**
	 * Create explain message.
	 * @return Message
	 */
	protected abstract Text createExplainMessage();
}