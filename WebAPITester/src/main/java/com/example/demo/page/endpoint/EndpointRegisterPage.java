package com.example.demo.page.endpoint;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import static com.example.demo.page.PageConstant.*;

/**
 * Endpoint register page.
 */
@Route("endpoint/register")
public class EndpointRegisterPage extends EndpointCommonInputPage {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();
		addComponent(layout, BUTTON_REGISTER, componentService.createButton(getTranslation("register"), i -> {
			try {
				binder.writeBean(endpoint);
			} catch (ValidationException e) {
				return;
			}
			endpointRepository.save(endpoint);
			this.getUI().ifPresent(ui -> ui.navigate(EndpointListPage.class));
		}));
		return layout;
	}
}