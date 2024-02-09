package com.example.demo.page.test.exec;

import static com.example.demo.page.PageConstant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.TestResult;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.TestResultRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

@Route("test/result/list")
public class TestResultListPage extends VerticalPageBase {
	private static final long serialVersionUID = 1L;
	@Autowired
	transient TestResultRepository testResultRepository;
 
	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		ConfirmDialog dialog = componentService.createConfirmDialog(getTranslation("testResultListPage.01"),
				getTranslation("delete"), getTranslation("cancel"), i -> {testResultRepository.deleteAll();
				UI.getCurrent().getPage().reload();
				});
		addComponent(layout, CONFIRM_DIALOG, dialog);
		addComponent(layout, CONFIRM_DIALOG_BUTTON,
				componentService.createOpenConfirmDialogButton(getTranslation("deleteAllTestResults"), dialog));

		Grid<TestResult> grid = new Grid<>();
		grid.addColumn(createLinkComponent()).setHeader(getTranslation("resultId"));
		grid.addColumn(TestResult::getExecDateTime).setHeader(getTranslation("execDate"));
		grid.addColumn(source -> source.getTest().getMemo()).setHeader(getTranslation("testName"));
		grid.addColumn(TestResult::isResult).setHeader(getTranslation("result"));
		List<TestResult> resultList = new ArrayList<>();
		testResultRepository.findAll().forEach(resultList::add);
		grid.setItems(resultList);
		layout.add(grid);
		return layout;
	}

	private ComponentRenderer<RouterLink, TestResult> createLinkComponent() {
		return new ComponentRenderer<>(RouterLink::new, (link, result) -> {
			Map<String, String> map = new HashMap<>();
			map.put(FIELD_TEST_RESULT_ID, String.valueOf(result.getId()));
			link.setText(String.valueOf(result.getId()));
			link.setRoute(TestResultPage.class, new RouteParameters(map));
		});
	}
}
