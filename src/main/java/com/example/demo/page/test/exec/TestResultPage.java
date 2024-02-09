package com.example.demo.page.test.exec;

import static com.example.demo.page.PageConstant.*;
import static com.example.demo.page.PageConstant.FIELD_RESULT_URL;
import static com.example.demo.page.PageConstant.FIELD_TEST_RESULT;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.TestAssertResult;
import com.example.demo.entity.TestResult;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.TestAssertResultRepository;
import com.example.demo.repository.TestAssertionRepository;
import com.example.demo.repository.TestResultRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("/test/result/view/:testresultid?")
public class TestResultPage extends VerticalPageBase implements BeforeEnterObserver {
	private static final long serialVersionUID = 1L;
	@Autowired
	transient TestResultRepository testResultRepository;
	@Autowired
	transient TestAssertionRepository testAssertionRepository;
	@Autowired
	transient TestAssertResultRepository testAssertResultRepository;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();

		addComponent(layout, FIELD_TEST_RESULT, new Text(""));

		Grid<TestAssertResult> grid = new Grid<>();
		grid.addColumn(TestAssertResult::isResult).setHeader(getTranslation("result"));
		grid.addColumn(TestAssertResult::getXpath).setHeader(getTranslation("apiTestPageBase.01"));
		grid.addColumn(TestAssertResult::getExpectedValue).setHeader(getTranslation("expectedValue"));
		grid.addColumn(TestAssertResult::getActualValue).setHeader(getTranslation("actualValue"));
		layout.add(grid);
		registerComponent(GRID, grid);

		addComponent(layout, FIELD_RESULT_URL, createDisabledTextArea(getTranslation("resultUrl"), 500));
		addComponent(layout, FIELD_DECODED_URL, createDisabledTextArea(getTranslation("decodedResultUrl"), 500));
		addComponent(layout, FIELD_RESULT, createDisabledTextArea(getTranslation("result"), 2000));
		return layout;
	}

	private TextArea createDisabledTextArea(String label, int width) {
		TextArea textArea = componentService.createTextArea(label, width);
		textArea.setEnabled(false);
		textArea.getStyle().set("--lumo-disabled-text-color", "black");
		return textArea;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<String> optId = event.getRouteParameters().get("testresultid");
		if (optId.isEmpty()) {
			return;
		}
		String testResultId = optId.get();
		Optional<TestResult> optResult = testResultRepository.findById(Integer.parseInt(testResultId));
		if (optResult.isEmpty()) {
			return;
		}
		TestResult testResult = optResult.get();

		String resultString;
		if (testResult.isResult()) {
			resultString = "OK";
		} else {
			resultString = "NG";
		}
		((Text) getComponent(FIELD_TEST_RESULT)).setText(String.valueOf(resultString));
		List<TestAssertResult> assertionResultList = testAssertResultRepository.findByTestResultId(testResult.getId());
		((Grid<TestAssertResult>) getComponent("grid")).setItems(assertionResultList);
		setValue(FIELD_RESULT, testResult.getResponse());
		setValue(FIELD_RESULT_URL, testResult.getRequestUrl());
		try {
			setValue(FIELD_DECODED_URL, URLDecoder.decode(testResult.getRequestUrl(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			//NOP
		}
	}
}
