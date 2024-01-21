package com.example.demo.testset;

import static com.example.demo.page.PageConstant.BUTTON_UPDATE;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG_BUTTON;
import static com.example.demo.page.PageConstant.FIELD_TEST_SET_ID;
import static com.example.demo.page.PageConstant.GRID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.TestSetDetail;
import com.example.demo.repository.TestRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("/testset/update/:testsetid?")
public class TestSetUpdatePage extends TestSetCommonInputPage implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	@Autowired
	TestRepository testRepository;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();
				
		addComponent(layout, FIELD_TEST_SET_ID, componentService.createIntegerField("", 300));
		getComponent(FIELD_TEST_SET_ID).setVisible(false);
		testSetBinder.bind((IntegerField) getComponent(FIELD_TEST_SET_ID), "id");

		addComponent(layout, BUTTON_UPDATE, componentService.createButton(getTranslation("update"), i -> {
			try {
				writeBeans();
			} catch (ValidationException e) {
				return;
			}
			testSet = testSetRepository.save(testSet);
			testSetDetailRepository.deleteBytestSetId(testSet.getId());
			saveDetails();
			this.getUI().ifPresent(ui -> ui.navigate(TestSetListPage.class));
		}));
		
		addComponent(layout, CONFIRM_DIALOG, componentService.createConfirmDialog(
				getTranslation("apiUpdatePage.01"), getTranslation("delete"), getTranslation("cancel"), i -> {
					testSetRepository.delete(testSet);
					this.getUI().ifPresent(ui -> ui.navigate(TestSetListPage.class));
				}));
		addComponent(layout, CONFIRM_DIALOG_BUTTON, componentService.createOpenConfirmDialogButton(
				getTranslation("delete"), (ConfirmDialog) getComponent(CONFIRM_DIALOG)));
	
		
		registerComponent("LAYOUT", layout);
		
		return layout;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		String testsetid = event.getRouteParameters().get("testsetid").orElse("");
		testSet = testSetRepository.findById(Integer.parseInt(testsetid)).get();
		testSetBinder.readBean(testSet);

		List<TestSetDetail> detailList = testSet.getTestList();
		for (int i = 0; i < detailList.size(); i++) {
			TestSetDetail d = detailList.get(i);
			d.setFieldIndex(i + 1);
			d.setTestid(String.valueOf(d.getTest().getId()));
			testSetDetailList.add(d);
		}
		for (int i = detailList.size() + 1; i < 21; i++) {
			TestSetDetail detail = new TestSetDetail();
			detail.setFieldIndex(i);
			testSetDetailList.add(detail);
		}
		Grid<TestSetDetail> grid = (Grid<TestSetDetail>) getComponent(GRID);
		grid.setItems(testSetDetailList);
		
		
		HorizontalLayout hl = new HorizontalLayout();
		Anchor anchor = new Anchor("/rest/testset/download?testsetid=" + testSet.getId(), getTranslation("download"));
		anchor.setTarget("_blank");
		hl.add(anchor);
		
	((FormLayout)getComponent("LAYOUT")).addComponentAsFirst(anchor);
		
		
	}
}
