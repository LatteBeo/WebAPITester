package com.example.demo.page.test.exec;

import static com.example.demo.page.PageConstant.BUTTON_SCREENSHOT;
import static com.example.demo.page.PageConstant.BUTTON_SUBMIT;
import static com.example.demo.page.PageConstant.FIELD_DECODED_URL;
import static com.example.demo.page.PageConstant.FIELD_RESULT;
import static com.example.demo.page.PageConstant.FIELD_RESULT_URL;
import static com.example.demo.page.PageConstant.GRID;
import static com.example.demo.page.PageConstant.PREFIX_PARAM;
import static com.example.demo.page.PageConstant.SUFFIX_UPLOAD;
import static com.example.demo.page.PageConstant.TABSHEET;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.core.io.ByteArrayResource;

import com.example.demo.ApiCallerService.RequestResult;
import com.example.demo.entity.TestAssertion;
import com.example.demo.entity.TestParameter;
import com.example.demo.page.UploadedFile;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

/**
 * Single API call test page.
 */
@Route(value = "test")
public class ApiTestSendPage extends ApiTestPageBase implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();
		setColSpan(layout, GRID, max_col);

		Button submitButton = componentService.createButton(getTranslation("submitRequest"), i -> {
			try {
				testBinder.writeBean(test);
				for (int x = 0; x < 20; x++) {
					testParameterBinderList.get(x).writeBean(testParameterList.get(x));
				}
			} catch (ValidationException e) {
				return;
			}
			callAPI();
		});
		addComponent(layout, BUTTON_SUBMIT, submitButton);
		setColSpan(layout, BUTTON_SUBMIT, max_col / 2);

		addComponent(layout, BUTTON_SCREENSHOT,
				componentService.createButton(getTranslation("screenshot"), i -> downloadScreenshot()));
		setColSpan(layout, BUTTON_SCREENSHOT, max_col / 2);

		TextArea resultUrlArea = createDisabledTextArea(getTranslation("resultUrl"), 999999);
		registerComponent(FIELD_RESULT_URL, resultUrlArea);
		TextArea resultDecodedUrlArea = createDisabledTextArea(getTranslation("decodedResultUrl"), 999999);
		registerComponent(FIELD_DECODED_URL, resultDecodedUrlArea);

		TabSheet tabsheet = new TabSheet();
		tabsheet.add(getTranslation("resultUrl"), resultUrlArea);
		tabsheet.add(getTranslation("decodedResultUrl"), resultDecodedUrlArea);
		resultUrlArea.setEnabled(false);
		resultDecodedUrlArea.setEnabled(false);
		addComponent(layout, TABSHEET, tabsheet);
		setColSpan(layout, TABSHEET, max_col);
		setColSpan(layout, FIELD_RESULT_URL, max_col);
		setColSpan(layout, FIELD_DECODED_URL, max_col);
		addComponent(layout, FIELD_RESULT, createDisabledTextArea(getTranslation("result"), -1));
		setColSpan(layout, FIELD_RESULT, max_col);
		return layout;
	}

	private TextArea createDisabledTextArea(String label, int width) {
		TextArea textArea = componentService.createTextArea(label, width);
		textArea.setEnabled(false);
		textArea.getStyle().set("--lumo-disabled-text-color", "black");
		return textArea;
	}

	private void callAPI() {
		RequestResult result;
		if ("POST".equals(test.getMethod())) {
			Map<String, Object> requestParams = new HashMap<>();
			for (int i = 1; i < 21; i++) {
				TestParameter parameter = testParameterList.get(i - 1);
				String paramName = parameter.getName();
				if (paramName != null && !paramName.isBlank()) {
					UploadedFile uploadedFile = getUploadedFile(PREFIX_PARAM + String.valueOf(i) + SUFFIX_UPLOAD);
					byte[] fileBytes = uploadedFile.getFile();
					if (fileBytes != null && fileBytes.length > 0) {
						ByteArrayResource byteStream = createFileByteArrayResource(fileBytes,
								uploadedFile.getFileName());
						requestParams.put(parameter.getName(), byteStream);
					} else {
						requestParams.put(parameter.getName(), parameter.getValue());
					}
				}
			}
			result = apiCallerService.post(test.getEndpointurl(), test.getApiname(), requestParams);
		} else {
			Map<String, String> requestParams = new HashMap<>();
			testParameterList.stream().filter(i -> i.getName() != null && !i.getName().isBlank())
					.forEach(i -> requestParams.put(i.getName(), i.getValue()));
			result = apiCallerService.get(test.getEndpointurl(), test.getApiname(), requestParams);
		}
		setValue(FIELD_RESULT_URL, result.requestUrl());
		setValue(FIELD_RESULT, result.responseString());
		try {
			setValue(FIELD_DECODED_URL, URLDecoder.decode(result.requestUrl(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return;
		}
	}

	@Override
	protected Grid<TestParameter> createTestParameterGrid() {
		return createTestParameterGridForRegister();
	}

	@Override
	protected Grid<TestAssertion> createAssertionGrid() {
		// Can't assert on this page.
		return null;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		initTestParameterListAndGrid();
	}

	private void downloadScreenshot() {
		try {
			Path tmpFile = Files.createTempFile("tmp", "png");
			BufferedImage screenShot = new Robot()
					.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(screenShot, "png", tmpFile.toFile());
			try {
				FileInputStream fis = new FileInputStream(tmpFile.toFile());
				final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry()
						.registerResource(new StreamResource("download.png", () -> fis));
				UI.getCurrent().getPage().open(registration.getResourceUri().toString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected Component createGuideComponent() {
		VerticalLayout layout = new VerticalLayout();
		getCommonDetails().forEach(i -> layout.add(i));
		
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "apiTestPageBase.01", "guide.apiTestSendPage.01" });
		detailContentList.add(new String[] { "expectedValue", "guide.apiTestSendPage.expectedValue" });
		
		
		
		return layout;
	}
	
	
}