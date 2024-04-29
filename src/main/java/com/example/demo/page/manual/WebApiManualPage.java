package com.example.demo.page.manual;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.page.VerticalPageBase;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * WebAPI manual page.
 */
@Route("/manual/api")
public class WebApiManualPage extends VerticalPageBase {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		Text title = new Text("/rest/api/register");
		layout.add(title);

		Grid<watGridItem> grid = new Grid<>();
		grid.addColumn(watGridItem::getField1).setHeader(getTranslation("guide.manual.api.paramName"));
		grid.addColumn(watGridItem::getField2).setHeader(getTranslation("guide.manual.api.paramExplain"));
		List<watGridItem> list = new ArrayList<>();
		watGridItem item1 = new watGridItem();
		item1.setField1(getTranslation("guide.manual.api.register.param.name"));
		item1.setField2(getTranslation("guide.manual.api.register.param.name.explain"));
		list.add(item1);

		watGridItem item2 = new watGridItem();
		item2.setField1(getTranslation("guide.manual.api.register.param.param"));
		item2.setField2(getTranslation("guide.manual.api.register.param.param.explain"));
		list.add(item2);
		grid.setItems(list);
		grid.setAllRowsVisible(true);
		layout.add(grid);
		return layout;
	}
}