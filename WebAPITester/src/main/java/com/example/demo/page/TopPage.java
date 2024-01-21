package com.example.demo.page;

import java.util.Locale;

import com.example.demo.page.api.ApiListPage;
import com.example.demo.page.endpoint.EndpointListPage;
import com.example.demo.page.test.TestListPage;
import com.example.demo.page.test.exec.ApiTestSendPage;
import com.example.demo.page.test.exec.TestExecPage;
import com.example.demo.page.test.exec.TestResultListPage;
import com.example.demo.testset.TestSetListPage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import static com.example.demo.page.PageConstant.*;

/**
 * Top page.
 */
@Route(value = "")
public class TopPage extends VerticalPageBase {
	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.add(componentService.createLink(getTranslation("endpointList"), EndpointListPage.class));
		layout.add(componentService.createLink(getTranslation("apiList"), ApiListPage.class));
		layout.add(componentService.createLink(getTranslation("testList"), TestListPage.class));
		layout.add(componentService.createLink(getTranslation("execTest"), TestExecPage.class));
		layout.add(componentService.createLink(getTranslation("testResultList"), TestResultListPage.class));
		layout.add(componentService.createLink(getTranslation("testAPI"), ApiTestSendPage.class));
		layout.add(componentService.createLink(getTranslation("testSetList"), TestSetListPage.class));

		if (VaadinSession.getCurrent().getLocale() == Locale.JAPANESE) {
			addComponent(layout, BUTTON_EN, componentService.createButton(getTranslation("english"), i -> {
				VaadinSession.getCurrent().setLocale(Locale.ENGLISH);
				UI.getCurrent().getPage().reload();
			}));
		} else {
			addComponent(layout, BUTTON_JP, componentService.createButton(getTranslation("japanese"), i -> {
				VaadinSession.getCurrent().setLocale(Locale.JAPANESE);
				UI.getCurrent().getPage().reload();
			}));
		}

		return layout;
	}
}