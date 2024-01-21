package com.example.demo.page.test;

import static com.example.demo.page.PageConstant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Test;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.TestRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

/**
 * Test list page.
 */
@Route("test/list")
public class TestListPage extends VerticalPageBase {
	
	private static final long serialVersionUID = 1L;
	@Autowired
	TestRepository testRepository;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(1000, Unit.PIXELS);
		
		layout.add(new RouterLink(getTranslation("registerTest"), TestRegisterPage.class));
		
		Grid<Test> grid = new Grid<>(Test.class, false);
		grid.addColumn(createLinkComponent()).setHeader(getTranslation("endpointName"));
		grid.addColumn(Test::getMethod).setHeader(getTranslation("method"));
		grid.addColumn(Test::getMemo).setHeader(getTranslation("memo"));
		List<Test> testList = new ArrayList<>();
		testRepository.findAll().forEach(i -> testList.add(i));
		grid.setItems(testList);
		layout.add(grid);
		registerComponent(GRID, grid);
		return layout;
	}
	private ComponentRenderer<RouterLink, Test> createLinkComponent() {
		return new ComponentRenderer<RouterLink, Test>(RouterLink::new, (linkComponent, endPoint) -> {
			Map<String, String> map = new HashMap<>();
			map.put(FIELD_TEST_ID, String.valueOf(endPoint.getId()));
			linkComponent.setText(String.valueOf(endPoint.getId()));
			linkComponent.setRoute(TestUpdatePage.class, new RouteParameters(map));
		});
	}

}
