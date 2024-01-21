package com.example.demo.page.test.exec;

import static com.example.demo.page.PageConstant.FIELD_TEST_ID;
import static com.example.demo.page.PageConstant.TEST_NAME_DIALOG;
import static com.example.demo.page.PageConstant.TEST_NAME_DIALOG_BUTTON;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.engine.jdbc.ReaderInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.example.demo.ApiCallerService;
import com.example.demo.ApiCallerService.RequestResult;
import com.example.demo.entity.Test;
import com.example.demo.entity.TestAssertResult;
import com.example.demo.entity.TestAssertion;
import com.example.demo.entity.TestParameter;
import com.example.demo.entity.TestResult;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.TestAssertResultRepository;
import com.example.demo.repository.TestAssertionRepository;
import com.example.demo.repository.TestParameterRepository;
import com.example.demo.repository.TestRepository;
import com.example.demo.repository.TestResultRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

@Route("test/exec")

public class TestExecPage extends VerticalPageBase {
	@Autowired
	TestRepository testRepository;
	@Autowired
	ApiCallerService apiCallerService;
	@Autowired
	TestParameterRepository testParameterRepository;
	@Autowired
	TestAssertionRepository testAssertionRepository;
	@Autowired
	TestResultRepository testResultRepository;
	@Autowired
	TestAssertResultRepository testAssertResultRepository;
	Test test = new Test();

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = new FormLayout();
		addComponent(layout, FIELD_TEST_ID, componentService.createIntegerField(getTranslation("testId"), 300));
		BeanValidationBinder<Test> binder = new BeanValidationBinder<>(Test.class);
		binder.bind((IntegerField) getComponent(FIELD_TEST_ID), "id");

		addComponent(layout, TEST_NAME_DIALOG, createTestSelectDialog());
		addComponent(layout, TEST_NAME_DIALOG_BUTTON, componentService
				.createOpenDialogButton((Dialog) getComponent(TEST_NAME_DIALOG), getTranslation("selectTest")));

		addComponent(layout, "executebutton", componentService.createButton(getTranslation("execute"), i -> {
			try {
				binder.writeBean(test);
			} catch (ValidationException e) {
				return;
			}
			exec();
			this.getUI().ifPresent(ui -> ui.navigate(TestResultListPage.class));
		}));
		return layout;
	}

	private void exec() {
		test = testRepository.findById(test.getId()).get();
		RequestResult result;
		List<TestParameter> parameterList = testParameterRepository.findByTest_id(test.getId());
		if ("POST".equals(test.getMethod())) {
			Map<String, Object> requestParams = new HashMap<>();
			parameterList.forEach(i -> {
				if (i.getFile() != null && i.getFile().length > 0) {
					requestParams.put(i.getName(), createFileByteArrayResource(i.getFile(), i.getFileName()));
				} else {
					requestParams.put(i.getName(), i.getValue());
				}
			});
			result = apiCallerService.post(test.getEndpointurl(), test.getApiname(), requestParams);
		} else {
			Map<String, String> requestParams = new HashMap<>();
			parameterList.forEach(i -> requestParams.put(i.getName(), i.getValue()));
			result = apiCallerService.get(test.getEndpointurl(), test.getApiname(), requestParams);
		}
		execAssertions(test, result);
	}

	private void execAssertions(Test test, RequestResult result) {
		TestResult testResult = new TestResult();
		testResult.setResponse(result.responseString());
		testResult.setTest(test);
		testResult.setRequestUrl(result.requestUrl());
		testResult.setExecDateTime(result.execDateTime());
		testResult = testResultRepository.save(testResult);
		if (result.succeeded()) {
			List<TestAssertion> assertionList = testAssertionRepository.findByTest_id(test.getId());
			boolean testResultFlag = true;
			for (TestAssertion assertion : assertionList) {
				TestAssertResult assertionResult = execAssertion(result, testResult, assertion);
				testAssertResultRepository.save(assertionResult);
				if (!assertionResult.isResult()) {
					testResultFlag = false;
				}
			}
			testResult.setResult(testResultFlag);
			testResultRepository.save(testResult);
		}
	}

	private TestAssertResult execAssertion(RequestResult requestResult, TestResult testResult,
			TestAssertion assertion) {
		TestAssertResult assertResult = new TestAssertResult();
		assertResult.setXpath(assertion.getXpath());
		assertResult.setExpectedValue(assertion.getExpectedValue());
		assertResult.setTestResult(testResult);
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new ReaderInputStream(new StringReader(requestResult.responseString())));
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodelist = (NodeList) xPath.compile(assertion.getXpath()).evaluate(doc, XPathConstants.NODESET);
			if (nodelist.getLength() == 1) {
				String value = nodelist.item(0).getTextContent();
				assertResult.setActualValue(value);
				assertResult.setResult(value.equals(assertion.getExpectedValue()));
			}
		} catch (Exception e) {
			assertResult.setActualValue(e.getStackTrace().toString());
		}
		return assertResult;
	}

	protected Dialog createTestSelectDialog() {
		Grid<Test> grid = new Grid<>(Test.class, false);
		grid.addColumn(createTestNameButtonComponent()).setHeader(getTranslation("testName"));
		grid.setAllRowsVisible(true);
		List<Test> apiList = new ArrayList<>();
		testRepository.findAll().forEach(i -> apiList.add(i));
		grid.setItems(apiList);
		return componentService.createSelectDialog(grid);
	}

	protected ComponentRenderer<Button, Test> createTestNameButtonComponent() {
		return new ComponentRenderer<Button, Test>(Button::new, (button, api) -> {
			button.setText(api.getMemo());
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(i -> {
				((IntegerField) getComponent(FIELD_TEST_ID)).setValue(api.getId());
				((Dialog) getComponent(TEST_NAME_DIALOG)).close();
			});
		});
	}
}
