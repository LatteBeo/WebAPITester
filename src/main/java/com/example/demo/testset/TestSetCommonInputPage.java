package com.example.demo.testset;

import static com.example.demo.page.PageConstant.*;
import static com.example.demo.page.PageConstant.FIELD_TEST_NAME;
import static com.example.demo.page.PageConstant.FIELD_TEST_SET_NAME;
import static com.example.demo.page.PageConstant.GRID;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Test;
import com.example.demo.entity.TestSet;
import com.example.demo.entity.TestSetDetail;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.TestRepository;
import com.example.demo.repository.TestSetDetailRepository;
import com.example.demo.repository.TestSetRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import lombok.Getter;
import lombok.Setter;

public abstract class TestSetCommonInputPage extends VerticalPageBase {
	private static final long serialVersionUID = 1L;
	TestSet testSet = new TestSet();
	ArrayList<TestSetDetail> testSetDetailList = new ArrayList<>();
	@Autowired
	transient TestRepository testRepository;
	@Autowired
	transient TestSetRepository testSetRepository;
	@Autowired
	transient TestSetDetailRepository testSetDetailRepository;
	BeanValidationBinder<TestSet> testSetBinder = new BeanValidationBinder<>(TestSet.class);

	ArrayList<BeanValidationBinder<TestSetDetail>> testSetDetailBinderList = new ArrayList<>();

	protected FormLayout createForm() {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new ResponsiveStep("0", 2));
		addComponent(layout, FIELD_TEST_SET_NAME,
				componentService.createTextField(getTranslation("testSetName"), 300, 300));
		setColSpan(layout, FIELD_TEST_SET_NAME, 2);
		addComponent(layout, TEST_NAME_DIALOG, createTestSelectDialog());
		Grid<TestSetDetail> grid = new Grid<>(TestSetDetail.class, false);
		grid.addColumn(createTestParamFieldRenderer()).setHeader(getTranslation("testId"));
		grid.addColumn(createTestParamFieldRenderer2()).setHeader(getTranslation("testName"));
		grid.addColumn(createTestParamFieldRenderer3()).setHeader("");
		layout.add(grid);
		registerComponent(GRID, grid);
		setColSpan(layout, GRID, 2);
		testSetBinder.bind((TextField) getComponent(FIELD_TEST_SET_NAME), "name");
		return layout;
	}

	protected ComponentRenderer<TextField, TestSetDetail> createTestParamFieldRenderer() {
		return new ComponentRenderer<>(TextField::new, (component, parameter) -> {
			registerComponent(FIELD_TEST_ID + parameter.getFieldIndex(), component);
			BeanValidationBinder<TestSetDetail> binder = new BeanValidationBinder<>(TestSetDetail.class);
			binder.bind((TextField) getComponent(FIELD_TEST_ID + parameter.getFieldIndex()), "testid");
			testSetDetailBinderList.add(parameter.getFieldIndex() - 1, binder);
			binder.readBean(parameter);

		});
	}

	protected ComponentRenderer<TextField, TestSetDetail> createTestParamFieldRenderer2() {
		return new ComponentRenderer<>(TextField::new, (component, parameter) -> {
			registerComponent(FIELD_TEST_NAME + parameter.getFieldIndex(), component);
			component.setEnabled(false);
		});
	}

	protected ComponentRenderer<Button, TestSetDetail> createTestParamFieldRenderer3() {
		return new ComponentRenderer<>(Button::new, (component, parameter) -> {
			component.setText(getTranslation("selectTest"));
			component.addClickListener(i -> {
				TestSelectDialog dialog = (TestSelectDialog) getComponent("testNameDialog");
				dialog.setIndex(parameter.getFieldIndex());
				dialog.open();
			});

		});
	}

	protected Dialog createTestSelectDialog() {
		TestSelectDialog dialog = new TestSelectDialog();
		VerticalLayout layout = new VerticalLayout();
		Grid<Test> grid = new Grid<>(Test.class, false);
		grid.addColumn(createTestNameButtonComponent()).setHeader(getTranslation("testName"));
		grid.setAllRowsVisible(true);
		List<Test> apiList = new ArrayList<>();
		testRepository.findAll().forEach(apiList::add);
		grid.setItems(apiList);
		layout.add(grid);
		dialog.add(layout);
		return dialog;
	}

	protected ComponentRenderer<Button, Test> createTestNameButtonComponent() {
		return new ComponentRenderer<>(Button::new, (button, api) -> {
			button.setText(api.getMemo());
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(i -> {
				TestSelectDialog dialog = (TestSelectDialog) getComponent(TEST_NAME_DIALOG);
				int targetindex = dialog.getIndex();
				((TextField) getComponent(FIELD_TEST_ID + String.valueOf(targetindex)))
						.setValue(String.valueOf(api.getId()));
				((TextField) getComponent(FIELD_TEST_NAME + String.valueOf(targetindex))).setValue(api.getMemo());
				((Dialog) getComponent(TEST_NAME_DIALOG)).close();
			});
		});
	}

	protected void writeBeans() throws ValidationException {
		testSetBinder.writeBean(testSet);
		for (int x = 0; x < 20; x++) {
			testSetDetailBinderList.get(x).writeBean(testSetDetailList.get(x));
		}
	}

	protected void saveDetails() {
		testSetDetailList.stream().filter(x -> x.getTestid() != null && !x.getTestid().isBlank()).forEach(x -> {
			Test test = testRepository.findById(Integer.parseInt(x.getTestid())).get();
			x.setTest(test);
			x.setTestSet(testSet);
			testSetDetailRepository.save(x);
		});

	}

	@Getter
	@Setter
	static class TestSelectDialog extends Dialog {
		private static final long serialVersionUID = 1L;
		private int index;
	}
}