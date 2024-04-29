package com.example.demo.page.test;

import static com.example.demo.page.PageConstant.BUTTON_UPDATE;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG_BUTTON;
import static com.example.demo.page.PageConstant.FIELD_TEST_ID;
import static com.example.demo.page.PageConstant.FIELD_TEST_MEMO;
import static com.example.demo.page.PageConstant.GRID;
import static com.example.demo.page.PageConstant.GRID2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.DataNotFoundException;
import com.example.demo.entity.Test;
import com.example.demo.entity.TestAssertion;
import com.example.demo.entity.TestParameter;
import com.example.demo.page.test.exec.ApiTestPageBase;
import com.example.demo.repository.TestAssertionRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

/**
 * Test update page.
 */
@Route("test/update/:testid?")
public class TestUpdatePage extends ApiTestPageBase implements BeforeEnterObserver {
	private static final long serialVersionUID = 1L;

	@Autowired
	transient TestAssertionRepository testAssertionRepository;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();

		addComponent(layout, FIELD_TEST_MEMO, componentService.createTextField(getTranslation("memo"), 300, 300));

		IntegerField integerField = componentService.createIntegerField("", 300);
		integerField.setVisible(false);
		addComponent(layout, FIELD_TEST_ID, integerField);

		testBinder.bind((TextField) getComponent(FIELD_TEST_MEMO), "memo");
		testBinder.bind((IntegerField) getComponent(FIELD_TEST_ID), "id");

		addComponent(layout, BUTTON_UPDATE, componentService.createButton(getTranslation("update"), i -> {
			if (!writeBeansForRegisterOrUpdate()) {
				return;
			}
			try {
				execUpdate();
			} catch (DataNotFoundException | IOException e) {
				return;
			}
			this.getUI().ifPresent(ui -> ui.navigate(TestListPage.class));
		}));
		addComponent(layout, CONFIRM_DIALOG, componentService.createConfirmDialog(getTranslation("apiUpdatePage.01"),
				getTranslation("delete"), getTranslation("cancel"), i -> {
					testRepository.delete(test);
					this.getUI().ifPresent(ui -> ui.navigate(TestListPage.class));
				}));
		addComponent(layout, CONFIRM_DIALOG_BUTTON, componentService
				.createOpenConfirmDialogButton(getTranslation("delete"), (ConfirmDialog) getComponent(CONFIRM_DIALOG)));
		return layout;
	}

	private void execUpdate() throws DataNotFoundException, IOException {
		test = testRepository.save(test);

		List<TestParameter> parameterList = createRegisterOrUpdateTestParameterList(test);
		testParameterRepository.deleteBytest_id(test.getId());
		testParameterRepository.saveAll(parameterList);

		testAssertionRepository.deleteBytest_id(test.getId());
		testAssertionRepository.saveAll(createRegisterOrUpdateTestAssertionList(test));
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		String testid = event.getRouteParameters().get(FIELD_TEST_ID).orElse("");
		Optional<Test> optTest = testRepository.findById(Integer.parseInt(testid));
		if (optTest.isEmpty()) {
			return;
		}
		test = optTest.get();
		testBinder.readBean(test);

		List<TestParameter> parameterList = testParameterRepository.findByTest_id(test.getId());
		for (int x = 0; x < parameterList.size(); x++) {
			parameterList.get(x).setFieldIndex(x + 1);
		}
		testParameterList.addAll(parameterList);

		for (int i = parameterList.size() + 1; i < 21; i++) {
			TestParameter newParam = new TestParameter();
			newParam.setFieldIndex(i);
			testParameterList.add(newParam);
		}
		Grid<TestParameter> grid = (Grid<TestParameter>) getComponent(GRID);
		grid.setItems(testParameterList);

		List<TestAssertion> assertionList = testAssertionRepository.findByTest_id(test.getId());
		for (int x = 0; x < assertionList.size(); x++) {
			assertionList.get(x).setFieldIndex(x + 1);
		}
		testAssertionList.addAll(assertionList);

		for (int i = assertionList.size() + 1; i < 21; i++) {
			TestAssertion param = new TestAssertion();
			param.setFieldIndex(i);
			testAssertionList.add(param);
		}
		Grid<TestAssertion> grid2 = (Grid<TestAssertion>) getComponent(GRID2);
		grid2.setItems(testAssertionList);

		if (!test.getTestSetDetail().isEmpty()) {
			((Button) getComponent(CONFIRM_DIALOG_BUTTON)).setVisible(false);
		}
	}

	@Override
	protected Grid<TestParameter> createTestParameterGrid() {
		Grid<TestParameter> grid = new Grid<>(TestParameter.class, false);
		grid.addColumn(createTestParamFieldRenderer()).setFlexGrow(0).setWidth("400px")
				.setHeader(getTranslation("parameterAndName"));
		grid.addColumn(createFileLinkRenderer()).setFlexGrow(0);
		grid.addColumn(createUploadRenderer());
		registerComponent(GRID, grid);
		return grid;
	}

	protected ComponentRenderer<Button, TestParameter> createFileLinkRenderer() {
		return new ComponentRenderer<>(Button::new, (link, testParameter) -> {
			link.setText(getTranslation("download"));
			byte[] fileBytes = testParameter.getFile();
			if (fileBytes != null && fileBytes.length > 0) {
				link.addClickListener(event -> {
					final StreamResource resource = new StreamResource(testParameter.getFileName(),
							() -> new ByteArrayInputStream(fileBytes));
					final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry()
							.registerResource(resource);
					UI.getCurrent().getPage().open(registration.getResourceUri().toString());
				});
			} else {
				link.setVisible(false);
			}
		});
	}

	@Override
	protected Component createGuideComponent() {
		VerticalLayout layout = new VerticalLayout();
		getCommonDetails().forEach(layout::add);

		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "apiTestPageBase.01", "guide.apiTestSendPage.01" });
		detailContentList.add(new String[] { "expectedValue", "guide.apiTestSendPage.expectedValue" });

		createGuideDetails(detailContentList).forEach(layout::add);

		return layout;
	}
}