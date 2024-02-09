package com.example.demo.page.api;

import static com.example.demo.page.PageConstant.BUTTON_REGISTER;

import com.example.demo.entity.Parameter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

/**
 * API register page.
 */
@Route(value = "api/register")
public class ApiRegisterPage extends ApiCommonInputPage implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();

		addComponent(layout, BUTTON_REGISTER, componentService.createButton(getTranslation("register"), i -> {
			try {
				writeBeans();
			} catch (ValidationException e) {
				return;
			}
			execRegister();
			this.getUI().ifPresent(ui -> ui.navigate(ApiListPage.class));
		}));
		return layout;
	}

	private void execRegister() {
		api = apiRepository.save(api);
		parameterList.stream().filter(i -> (i.getName() != null && !i.getName().isBlank())).forEach(i -> {
			i.setApi(api);
			parameterRepository.save(i);
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		for (int i = 0; i < 20; i++) {
			parameterList.add(new Parameter());
		}
	}
	@Override
	protected Component createGuideComponent() {
		VerticalLayout layout = new VerticalLayout();
		getCommonGuideDetails().forEach(layout::add);
		return layout;
	}
}