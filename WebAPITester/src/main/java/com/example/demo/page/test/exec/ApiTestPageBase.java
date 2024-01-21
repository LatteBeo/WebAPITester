package com.example.demo.page.test.exec;

import static com.example.demo.page.PageConstant.API_NAME_DIALOG;
import static com.example.demo.page.PageConstant.API_NAME_DIALOG_BUTTON;
import static com.example.demo.page.PageConstant.ENDPOINT_URL_DIALOG;
import static com.example.demo.page.PageConstant.ENDPOINT_URL_DIALOG_BUTTON;
import static com.example.demo.page.PageConstant.FIELD_API_NAME;
import static com.example.demo.page.PageConstant.FIELD_ASSERTION_EXPECTED_VALUE;
import static com.example.demo.page.PageConstant.FIELD_ASSERTION_XPATH;
import static com.example.demo.page.PageConstant.FIELD_ENDPOINT_URL;
import static com.example.demo.page.PageConstant.FIELD_METHOD;
import static com.example.demo.page.PageConstant.GRID;
import static com.example.demo.page.PageConstant.GRID2;
import static com.example.demo.page.PageConstant.PREFIX_PARAM;
import static com.example.demo.page.PageConstant.SUFFIX_UPLOAD;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.ApiCallerService;
import com.example.demo.entity.Api;
import com.example.demo.entity.Endpoint;
import com.example.demo.entity.Parameter;
import com.example.demo.entity.Test;
import com.example.demo.entity.TestAssertion;
import com.example.demo.entity.TestParameter;
import com.example.demo.page.UploadedFile;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.page.field.TestParamField;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.EndpointRepository;
import com.example.demo.repository.TestParameterRepository;
import com.example.demo.repository.TestRepository;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;

abstract public class ApiTestPageBase extends VerticalPageBase {
	private static final long serialVersionUID = 1L;
	@Autowired
	protected ApiRepository apiRepository;
	@Autowired
	protected EndpointRepository endpointRepository;
	@Autowired
	protected TestRepository testRepository;
	@Autowired
	protected TestParameterRepository testParameterRepository;
	@Autowired
	protected ApiCallerService apiCallerService;
	protected BeanValidationBinder<Test> testBinder = new BeanValidationBinder<>(Test.class);
	protected List<BeanValidationBinder<TestParameter>> testParameterBinderList = new ArrayList<>(20);
	protected List<BeanValidationBinder<TestAssertion>> testAssertionBinderList = new ArrayList<>(20);
	protected Test test = new Test();
	protected List<TestParameter> testParameterList = new ArrayList<>(20);
	protected List<TestAssertion> testAssertionList = new ArrayList<>(20);

	protected int max_col = 4;

	protected FormLayout createForm() {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new ResponsiveStep("0", max_col));
		// Enpoint URL
		addComponent(layout, FIELD_ENDPOINT_URL,
				componentService.createTextField(getTranslation("endpointUrl"), 300, 300));
		setColSpan(layout, FIELD_ENDPOINT_URL, max_col / 2);

		// Endpoint select dialog
		addComponent(layout, ENDPOINT_URL_DIALOG, createEndpointSelectDialog());
		addComponent(layout, ENDPOINT_URL_DIALOG_BUTTON, componentService
				.createOpenDialogButton((Dialog) getComponent(ENDPOINT_URL_DIALOG), getTranslation("selectEndpoint")));
		setColSpan(layout, ENDPOINT_URL_DIALOG_BUTTON, max_col / 2);

		// API Name
		TextField apiNameField = componentService.createTextField(getTranslation("apiName"), 300, 300);
		apiNameField.addValueChangeListener(i -> {
			changeSelectItem(i.getSource());
		});
		addComponent(layout, FIELD_API_NAME, apiNameField);
		setColSpan(layout, FIELD_API_NAME, max_col / 2);

		// API select dialog
		addComponent(layout, API_NAME_DIALOG, createApiSelectDialog());
		addComponent(layout, API_NAME_DIALOG_BUTTON, componentService
				.createOpenDialogButton((Dialog) getComponent(API_NAME_DIALOG), getTranslation("selectAPI")));
		setColSpan(layout, API_NAME_DIALOG_BUTTON, max_col / 2);

		// Method
		addComponent(layout, FIELD_METHOD,
				componentService.createSelect(getTranslation("method"), List.of("GET", "POST"), "GET"));
		setColSpan(layout, FIELD_METHOD, max_col);

		testBinder.bind((TextField) getComponent(FIELD_ENDPOINT_URL), "endpointurl");
		testBinder.bind((TextField) getComponent(FIELD_API_NAME), "apiname");
		testBinder.bind((Select<String>) getComponent(FIELD_METHOD), "method");

		layout.add(createTestParameterGrid());
		setColSpan(layout, GRID, max_col / 2);

		Grid<TestAssertion> grid2 = createAssertionGrid();
		if (grid2 != null) {
			layout.add(grid2);
			setColSpan(layout, GRID2, max_col / 2);
		}
		for (int x = 0; x < 20; x++) {
			testParameterBinderList.add(x, null);
			testAssertionBinderList.add(x, null);
		}
		return layout;
	}

	protected Dialog createApiSelectDialog() {
		Grid<Api> grid = new Grid<>(Api.class, false);
		grid.addColumn(createApiNameButtonComponent()).setHeader(getTranslation("apiName"));
		grid.setAllRowsVisible(true);
		List<Api> apiList = new ArrayList<>();
		apiRepository.findAll().forEach(i -> apiList.add(i));
		grid.setItems(apiList);
		return componentService.createSelectDialog(grid);
	}

	protected Dialog createEndpointSelectDialog() {
		Grid<Endpoint> grid = new Grid<>(Endpoint.class, false);
		grid.addColumn(createEndpointNameLinkComponent()).setHeader(getTranslation("endpointName"));
		grid.setAllRowsVisible(true);
		List<Endpoint> endpointList = new ArrayList<>();
		endpointRepository.findAll().forEach(i -> endpointList.add(i));
		grid.setItems(endpointList);
		return componentService.createSelectDialog(grid);
	}

	protected ComponentRenderer<Button, Api> createApiNameButtonComponent() {
		return new ComponentRenderer<Button, Api>(Button::new, (button, api) -> {
			button.setText(api.getName());
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(i -> {
				((TextField) getComponent(FIELD_API_NAME)).setValue(api.getName());
				((Dialog) getComponent(API_NAME_DIALOG)).close();
			});
		});
	}

	protected ComponentRenderer<Button, Endpoint> createEndpointNameLinkComponent() {
		return new ComponentRenderer<Button, Endpoint>(Button::new, (button, endpoint) -> {
			button.setText(endpoint.getName());
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(i -> {
				((TextField) getComponent(FIELD_ENDPOINT_URL)).setValue(endpoint.getUrl());
				((Dialog) getComponent(ENDPOINT_URL_DIALOG)).close();
			});
		});
	}

	protected ComponentRenderer<TestParamField, TestParameter> createTestParamFieldRenderer() {
		return new ComponentRenderer<TestParamField, TestParameter>(TestParamField::new, (component, parameter) -> {
			component.setValues(parameter);
			String fieldPrefix = PREFIX_PARAM + parameter.getFieldIndex();
			ComboBox<String> nameField = component.getNameField();
			TextField valueField = component.getValueField();
			IntegerField idField = component.getIdField();

			registerComponent(fieldPrefix + "name", nameField);
			registerComponent(fieldPrefix + "value", valueField);
			registerComponent(fieldPrefix + "id", idField);
			registerComponent(fieldPrefix, component);

			BeanValidationBinder<TestParameter> binder = new BeanValidationBinder<>(TestParameter.class);
			binder.bind(nameField, "name");
			binder.bind(valueField, "value");
			binder.bind(idField, "id");
			testParameterBinderList.add(parameter.getFieldIndex() - 1, binder);
			binder.readBean(parameter);

			List<Api> a = apiRepository.findByName(((TextField) getComponent(FIELD_API_NAME)).getValue());
			ComboBox<String> combobox = nameField;
			if (a.size() == 1) {
				Api api = a.get(0);
				List<String> items = new ArrayList<>();
				api.getParameter().forEach(v -> items.add(v.getName()));
				String nowValue = combobox.getValue();
				items.add(nowValue);
				combobox.setItems(items);
				combobox.setValue(nowValue);
			}

		});
	}

	protected ComponentRenderer<Upload, TestParameter> createUploadRenderer() {
		return new ComponentRenderer<Upload, TestParameter>(Upload::new, (component, parameter) -> {
			component.setReceiver(new FileBuffer());
			component.setDropAllowed(false);
			componentMap.put(PREFIX_PARAM + String.valueOf(parameter.getFieldIndex()) + SUFFIX_UPLOAD, component);
		});

	}

	protected ComponentRenderer<TextField, TestAssertion> createXPathFieldRenderer() {
		return new ComponentRenderer<TextField, TestAssertion>(TextField::new, (component, assertion) -> {
			component.setWidth(300, Unit.PIXELS);
			component.setMaxLength(2000);
			BeanValidationBinder<TestAssertion> binder = testAssertionBinderList.get(assertion.getFieldIndex() - 1);
			if (binder == null) {
				binder = new BeanValidationBinder<>(TestAssertion.class);
				testAssertionBinderList.add(assertion.getFieldIndex() - 1, binder);
			}
			binder.bind(component, "xpath");
			registerComponent(FIELD_ASSERTION_XPATH + assertion.getFieldIndex(), component);
			binder.readBean(assertion);
		});
	}

	protected ComponentRenderer<TextField, TestAssertion> createExpectedValueFieldRenderer() {
		return new ComponentRenderer<TextField, TestAssertion>(TextField::new, (component, assertion) -> {
			component.setWidth(300, Unit.PIXELS);
			component.setMaxLength(2000);
			registerComponent(FIELD_ASSERTION_EXPECTED_VALUE + assertion.getFieldIndex(), component);
			BeanValidationBinder<TestAssertion> binder = testAssertionBinderList.get(assertion.getFieldIndex() - 1);
			if (binder == null) {
				binder = new BeanValidationBinder<>(TestAssertion.class);
				testAssertionBinderList.add(assertion.getFieldIndex() - 1, binder);
			}
			binder.bind(component, "expectedValue");
			binder.readBean(assertion);
		});
	}

	protected abstract Grid<TestParameter> createTestParameterGrid();

	protected Grid<TestAssertion> createAssertionGrid() {
		Grid<TestAssertion> grid = new Grid<>(TestAssertion.class, false);
		grid.addColumn(createXPathFieldRenderer()).setHeader(getTranslation("apiTestPageBase.01"));
		grid.addColumn(createExpectedValueFieldRenderer()).setHeader(getTranslation("expectedValue"));
		registerComponent(GRID2, grid);
		return grid;
	}

	protected Grid<TestParameter> createTestParameterGridForRegister() {
		Grid<TestParameter> grid = new Grid<>(TestParameter.class, false);
		grid.addColumn(createTestParamFieldRenderer()).setHeader(getTranslation("parameterAndName"));
		grid.addColumn(createUploadRenderer());
		registerComponent(GRID, grid);
		return grid;
	}

	protected List<TestParameter> createRegisterOrUpdateTestParameterList(Test test) {
		List<TestParameter> parameterList = new ArrayList<>();
		for (int j = 1; j < 21; j++) {
			String fieldName = PREFIX_PARAM + String.valueOf(j);
			TestParameter parameter = testParameterList.get(j - 1);
			String paramName = parameter.getName();
			if (paramName != null && !paramName.isBlank()) {
				UploadedFile ds = getUploadedFile(fieldName + SUFFIX_UPLOAD);
				byte[] uploadedFile = ds.getFile();
				if (uploadedFile.length > 0) {
					parameter.setFile(uploadedFile);
					parameter.setFileName(ds.getFileName());
				} else if (parameter.getId() > 0) {
					// Use already registered file
					TestParameter formerRecord = testParameterRepository.findById(parameter.getId()).get();
					parameter.setFile(formerRecord.getFile());
					parameter.setFileName(formerRecord.getFileName());
				}
				parameter.setTest(test);
				parameterList.add(parameter);
			}
		}
		return parameterList;
	}

	protected List<TestAssertion> createRegisterOrUpdateTestAssertionList(Test test) {
		List<TestAssertion> list = new ArrayList<>();
		for (int j = 0; j < 20; j++) {
			TestAssertion assertion = testAssertionList.get(j);
			String xpath = assertion.getXpath();
			if (xpath == null || xpath.isBlank()) {
				continue;
			}
			assertion.setTest(test);
			list.add(assertion);
		}
		return list;
	}

	protected void writeBeansForRegisterOrUpdate() {
		try {
			testBinder.writeBean(test);
			for (int x = 0; x < 20; x++) {
				testParameterBinderList.get(x).writeBean(testParameterList.get(x));
				testAssertionBinderList.get(x).writeBean(testAssertionList.get(x));
			}
		} catch (ValidationException e) {
			return;
		}
	}

	protected void initTestParameterListAndGrid() {
		for (int i = 0; i < 20; i++) {
			TestParameter parameter = new TestParameter();
			parameter.setFieldIndex(i + 1);
			testParameterList.add(parameter);
		}
		Grid<TestParameter> grid = (Grid<TestParameter>) getComponent(GRID);
		grid.setItems(testParameterList);
	}

	protected void changeSelectItem(TextField apiNameField) {
		List<Api> list = apiRepository.findByName(apiNameField.getValue());
		if (list.size() == 1) {
			List<Parameter> paramLs = list.get(0).getParameter();
			List<String> nameList = new ArrayList<>();
			paramLs.forEach(g -> nameList.add(g.getName()));
			for (int x = 1; x < 21; x++) {
				ComboBox<String> combobox = (ComboBox<String>) getComponent(PREFIX_PARAM + String.valueOf(x) + "name");
				if (combobox != null) {
					combobox.setAllowCustomValue(true);
					String nowValue = combobox.getValue();
					nameList.add(combobox.getValue());
					combobox.setItems(nameList);
					combobox.setValue(nowValue);
				}
			}
		}

	}
}
