package com.example.demo.page.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Endpoint;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.EndpointRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import static com.example.demo.page.PageConstant.*;

/**
 * Endpoint list page.
 */
@Route("endpoint/list")
public class EndpointListPage extends VerticalPageBase {

	private static final long serialVersionUID = 1L;
	@Autowired
	transient EndpointRepository endpointRepository;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(1000, Unit.PIXELS);
		layout.add(new RouterLink(getTranslation("registerEndpoint"), EndpointRegisterPage.class));

		Grid<Endpoint> grid = new Grid<>(Endpoint.class, false);
		grid.addColumn(createLinkComponent()).setHeader(getTranslation("endpointName"));
		grid.addColumn(Endpoint::getUrl).setHeader(getTranslation("url"));
		List<Endpoint> endpointList = new ArrayList<>();
		endpointRepository.findAll().forEach(endpointList::add);
		grid.setItems(endpointList);
		layout.add(grid);
		registerComponent(GRID, grid);
		return layout;
	}

	private ComponentRenderer<RouterLink, Endpoint> createLinkComponent() {
		return new ComponentRenderer<>(RouterLink::new, (linkComponent, endPoint) -> {
			Map<String, String> map = new HashMap<>();
			map.put(FIELD_ENDPOINT_ID, String.valueOf(endPoint.getId()));
			linkComponent.setText(endPoint.getName());
			linkComponent.setRoute(EndpointUpdatePage.class, new RouteParameters(map));
		});
	}

	@Override
	protected Component createGuideComponent() {
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "registerEndpoint", "guide.endpointList.registerEndpoint" });
		detailContentList.add(new String[] { "endpointName", "guide.endpointList.endpointName" });
		VerticalLayout layout = new VerticalLayout();
		createGuideDetails(detailContentList).forEach(layout::add);
		return layout;
	}
}