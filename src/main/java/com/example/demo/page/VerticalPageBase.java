package com.example.demo.page;

import static com.example.demo.page.PageConstant.GUIDELAYOUT;
import static com.example.demo.page.PageConstant.MAINLAYOUT;
import static com.example.demo.page.PageConstant.TOPLAYOUT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.PostConstruct;

/**
 * Base page for VerticalLayout.
 */
abstract public class VerticalPageBase extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	@Autowired
	protected ComponentService componentService;
	/**
	 * Stores created component.(Key:Field name, Value:Component)
	 */
	protected Map<String, Component> componentMap = new HashMap<>();

	/**
	 * Create component after executing constructor.
	 */
	@PostConstruct
	public void postConstructor() {
		HorizontalLayout topLayout = new HorizontalLayout();
		registerComponent(TOPLAYOUT, topLayout);
		topLayout.setWidthFull();

		Component mainComponent = createComponent();
		registerComponent(MAINLAYOUT, mainComponent);
		((HasSize)mainComponent).setWidth("95%");
		
		topLayout.add(mainComponent);

		VerticalLayout guideLayout = new VerticalLayout();
		guideLayout.setWidth("5%");
		// guideLayout.addClassName(LumoUtility.Border.ALL);
		// guideLayout.addClassName(LumoUtility.BorderColor.CONTRAST_20);
		
		Component guideContents = createGuideComponent();

		Icon icon = VaadinIcon.CHEVRON_CIRCLE_RIGHT.create();
		Icon icon2 = VaadinIcon.CHEVRON_CIRCLE_LEFT.create();
		icon.addClickListener(i -> {
			guideContents.setVisible(false);
			((HasSize)mainComponent).setWidth("95%");
			guideLayout.setWidth("5%");
			icon2.setVisible(true);
			icon.setVisible(false);
					guideLayout.removeClassName(LumoUtility.Border.ALL);
		guideLayout.removeClassName(LumoUtility.BorderColor.CONTRAST_20);
		});
		icon2.addClickListener(i -> {
			guideContents.setVisible(true);
			((HasSize)mainComponent).setWidth("70%");
			guideLayout.setWidth("30%");
			icon2.setVisible(false);
			icon.setVisible(true);
					guideLayout.addClassName(LumoUtility.Border.ALL);
		guideLayout.addClassName(LumoUtility.BorderColor.CONTRAST_20);
			
		});
		icon.setVisible(false);
		guideContents.setVisible(false);
		
		guideLayout.add(icon);
		guideLayout.add(icon2);
		guideLayout.add(guideContents);

		
		registerComponent(GUIDELAYOUT, guideLayout);
		topLayout.add(guideLayout);

		add(topLayout);
	}

	/**
	 * Create displayed component.
	 * 
	 * @return Component
	 */
	protected abstract Component createComponent();
	protected Component createGuideComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.add(new Text("NO DATA"));
		return layout;
	}

	/**
	 * Set component colspan.
	 * 
	 * @param layout    Layout
	 * @param fieldName Field name
	 * @param colSpan   colspan
	 */
	protected void setColSpan(FormLayout layout, String fieldName, int colSpan) {
		layout.setColspan(getComponent(fieldName), colSpan);
	}

	/**
	 * Get component.
	 * 
	 * @param fieldName Field name
	 * @return Component
	 */
	protected Component getComponent(String fieldName) {
		return componentMap.get(fieldName);
	}

	/**
	 * Create dialog open button.
	 * @param fieldName Field name
	 * @param dialogComponentName Dialog component name
	 * @param text Dialog message
	 * @return Button
	 */
	protected Button createOpenDialogButton(String fieldName, String dialogComponentName, String text) {
		Button button = new Button(text);
		button.addClickListener(i -> {
			((Dialog) getComponent(dialogComponentName)).open();
		});
		registerComponent(fieldName, button);
		return button;
	}
	/**
	 * Set value to the supecified component.
	 * @param fieldName Field name
	 * @param value Value
	 */
	protected void setValue(String fieldName, String value) {
		Component component = getComponent(fieldName);
		if (component == null) {
			return;
		}
		if (component instanceof TextField field) {
			field.setValue(value);
		}
		if (component instanceof TextArea area) {
			area.setValue(value);
		}
	}
	/**
	 * Register component.
	 * @param fieldName Field name
	 * @param component Component
	 */
	protected void registerComponent(String fieldName, Component component) {
		componentMap.put(fieldName, component);
	}
	/**
	 * Get file from upload field.
	 * @param fieldName Field name
	 * @return Uploaded file
	 */
	protected UploadedFile getUploadedFile(String fieldName) {
		Upload uploadField = (Upload) getComponent(fieldName);
		InputStream inputStream = ((FileBuffer) uploadField.getReceiver()).getInputStream();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		try {
			while ((length = inputStream.read(buffer)) != -1) {
				byteOutputStream.write(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new RuntimeException("ERROR at getUploadedFileBytes");
		}
		UploadedFile file = new UploadedFile();
		file.setFile(byteOutputStream.toByteArray());
		file.setFileName(((FileBuffer) uploadField.getReceiver()).getFileName());
		return file;
	}
	/**
	 * Create FileByteArrayResource
	 * @param bytes byte array length
	 * @param fileName File name
	 * @return ByteArrayResource
	 */
	protected ByteArrayResource createFileByteArrayResource(byte[] bytes, String fileName) {
		ByteArrayResource bs = new ByteArrayResource(bytes) {
			@Override
			public String getFilename() {
				return fileName;
			}
		};
		return bs;
	}
	/**
	 * Add component to the layout.
	 * @param layout Layout
	 * @param fieldName Field name
	 * @param component Component
	 */
	protected void addComponent(HasComponents layout, String fieldName, Component component) {
		layout.add(component);
		registerComponent(fieldName, component);
	}
}
