package com.example.demo.testset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.TestSet;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.TestSetRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

@Route("testset/list")
public class TestSetListPage extends VerticalPageBase {
	
	private static final long serialVersionUID = 1L;
	@Autowired
	transient TestSetRepository testSetRepository;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(1000, Unit.PIXELS);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.add(new RouterLink(getTranslation("registerTestSet"), TestSetRegisterPage.class));
		hl.add(new RouterLink(getTranslation("upload"), TestSetUpload.class));
		layout.add(hl);
		
		
		Grid<TestSet> grid = new Grid<>(TestSet.class, false);
		grid.addColumn(createLinkComponent()).setHeader(getTranslation("testSetName"));
		grid.addColumn(TestSet::getId).setHeader(getTranslation("testSetId"));
		List<TestSet> list = new ArrayList<>();
		testSetRepository.findAll().forEach(list::add);
		layout.add(grid);
		grid.setItems(list);
		return layout;
	}
	private ComponentRenderer<RouterLink, TestSet> createLinkComponent() {
		return new ComponentRenderer<>(RouterLink::new, (linkComponent, endPoint) -> {
			Map<String, String> map = new HashMap<>();
			map.put("testsetid", String.valueOf(endPoint.getId()));
			linkComponent.setText(String.valueOf(endPoint.getName()));
			linkComponent.setRoute(TestSetUpdatePage.class, new RouteParameters(map));
		});
	}
}
