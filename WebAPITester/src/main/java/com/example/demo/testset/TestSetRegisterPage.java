package com.example.demo.testset;

import static com.example.demo.page.PageConstant.BUTTON_REGISTER;
import static com.example.demo.page.PageConstant.GRID;

import com.example.demo.entity.TestSetDetail;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("testset/register")
public class TestSetRegisterPage extends TestSetCommonInputPage implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();

		for (int i = 0; i < 20; i++) {
			testSetDetailBinderList.add(null);
		}
		addComponent(layout, BUTTON_REGISTER, componentService.createButton(getTranslation("register"), i -> {
			try {
				writeBeans();
			} catch (ValidationException e) {
				return;
			}
			testSet = testSetRepository.save(testSet);
			saveDetails();
			this.getUI().ifPresent(ui -> ui.navigate(TestSetListPage.class));
		}));
		return layout;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		for (int i = 0; i < 20; i++) {
			TestSetDetail detail = new TestSetDetail();
			detail.setFieldIndex(i + 1);
			testSetDetailList.add(detail);
		}
		Grid<TestSetDetail> grid = (Grid<TestSetDetail>) getComponent(GRID);
		grid.setItems(testSetDetailList);

	}
}