package com.example.demo.page.api;

import static com.example.demo.page.PageConstant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Api;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.ApiRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

/**
 * API list page.
 */
@Route(value = "api/list")
public class ApiListPage extends VerticalPageBase {
	@Autowired
	ApiRepository apiRepository;

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(1000, Unit.PIXELS);

		HorizontalLayout hl = new HorizontalLayout();
		hl.add(new RouterLink(getTranslation("registerAPI"), ApiRegisterPage.class));
		Anchor anchor = new Anchor("/rest/api/download", getTranslation("download"));
		anchor.setTarget("_blank");
		hl.add(anchor);
		layout.add(hl);
		hl.add(new RouterLink(getTranslation("upload"), ApiUploadPage.class));

		layout.add(createGrid());
		return layout;
	}

	private Grid<Api> createGrid() {
		Grid<Api> grid = new Grid<>(Api.class, false);
		grid.addColumn(createLinkComponent()).setHeader(getTranslation("apiName"));

		List<Api> apiList = new ArrayList<>();
		apiRepository.findAll().forEach(i -> apiList.add(i));
		grid.setItems(apiList);
		registerComponent(GRID, grid);
		return grid;
	}

	private ComponentRenderer<RouterLink, Api> createLinkComponent() {
		return new ComponentRenderer<RouterLink, Api>(RouterLink::new, (link, api) -> {
			Map<String, String> map = new HashMap<>();
			map.put(FIELD_API_ID, String.valueOf(api.getId()));
			link.setText(api.getName());
			link.setRoute(ApiUpdatePage.class, new RouteParameters(map));
		});
	}

	@Override
	protected Component createGuideComponent() {
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "registerAPI", "guide.apiList.registerAPI" });
		detailContentList.add(new String[] { "download", "guide.apiList.download" });
		detailContentList.add(new String[] { "upload", "guide.apiList.upload" });
		VerticalLayout layout = new VerticalLayout();
		createGuideDetails(detailContentList).forEach(i -> layout.add(i));
		return layout;
	}
}