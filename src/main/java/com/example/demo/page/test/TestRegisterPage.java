package com.example.demo.page.test;

import static com.example.demo.page.PageConstant.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.DataNotFoundException;
import com.example.demo.entity.TestAssertion;
import com.example.demo.entity.TestParameter;
import com.example.demo.page.test.exec.ApiTestPageBase;
import com.example.demo.repository.TestAssertionRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

/**
 * Test register page.
 */
@Route("test/register")
public class TestRegisterPage extends ApiTestPageBase implements BeforeEnterObserver {
	@Autowired
	transient TestAssertionRepository testAssertionRepository;

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();

		addComponent(layout, FIELD_TEST_MEMO, componentService.createTextField(getTranslation("memo"), 300, 300));
		setColSpan(layout, FIELD_TEST_MEMO, max_col);
		componentService.addBlankLabel(layout, max_col);
		testBinder.bind((TextField) getComponent(FIELD_TEST_MEMO), "memo");

		addComponent(layout, BUTTON_REGISTER, componentService.createButton(getTranslation("register"), i -> {
			if (!writeBeansForRegisterOrUpdate()) {
				return;
			}
			try {
				execRegister();
			} catch (DataNotFoundException | IOException e) {
				return;
			}
			this.getUI().ifPresent(ui -> ui.navigate(TestListPage.class));
		}));
		return layout;
	}

	private void execRegister() throws DataNotFoundException, IOException {
		test = testRepository.save(test);
		testParameterRepository.saveAll(createRegisterOrUpdateTestParameterList(test));
		testAssertionRepository.saveAll(createRegisterOrUpdateTestAssertionList(test));
	}

	@Override
	protected Grid<TestParameter> createTestParameterGrid() {
		return createTestParameterGridForRegister();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		initTestParameterListAndGrid();
		for (int i = 0; i < 20; i++) {
			TestAssertion assertion = new TestAssertion();
			assertion.setFieldIndex(i + 1);
			testAssertionList.add(assertion);
		}
		Grid<TestAssertion> grid2 = (Grid<TestAssertion>) getComponent(GRID2);
		grid2.setItems(testAssertionList);
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