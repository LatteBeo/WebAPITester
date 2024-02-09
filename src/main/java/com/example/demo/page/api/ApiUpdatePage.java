package com.example.demo.page.api;

import static com.example.demo.page.PageConstant.BUTTON_UPDATE;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG_BUTTON;
import static com.example.demo.page.PageConstant.FIELD_API_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Api;
import com.example.demo.entity.Parameter;
import com.example.demo.page.PageConstant;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

/**
 * API update page.
 */
@Route("api/update/:apiid?/")
public class ApiUpdatePage extends ApiCommonInputPage implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();
		apiBinder.bind((IntegerField) getComponent(FIELD_API_ID), "id");

		addComponent(layout, BUTTON_UPDATE, componentService.createButton(getTranslation("update"), i -> {
			try {
				writeBeans();
			} catch (ValidationException e) {
				return;
			}
			execUpdate();
			this.getUI().ifPresent(ui -> ui.navigate(ApiListPage.class));
		}));
		addComponent(layout, CONFIRM_DIALOG, componentService.createConfirmDialog(getTranslation("apiUpdatePage.01"),
				getTranslation(PageConstant.DELETE), getTranslation("cancel"), i -> {
					apiRepository.delete(api);
					this.getUI().ifPresent(ui -> ui.navigate(ApiListPage.class));
				}));
		addComponent(layout, CONFIRM_DIALOG_BUTTON, componentService
				.createOpenConfirmDialogButton(getTranslation(PageConstant.DELETE), (ConfirmDialog) getComponent(CONFIRM_DIALOG)));
		return layout;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<Api> optApi = apiRepository.findById(Integer.parseInt(event.getRouteParameters().get(FIELD_API_ID).orElse("")));
		if (optApi.isEmpty()) {
			return;
		}
		api = optApi.get();
		apiBinder.readBean(api);
		List<Parameter> paramlist = parameterRepository.findByApi_id(api.getId());
		parameterList.addAll(paramlist);
		for (int i = 0; i < paramlist.size(); i++) {
			parameterBinderList.get(i).readBean(paramlist.get(i));
		}
		for (int i = paramlist.size(); i < 21; i++) {
			parameterList.add(new Parameter());
		}
	}

	private void execUpdate() {
		api = apiRepository.save(api);
		parameterRepository.deleteByApi_id(api.getId());
		parameterList.stream().filter(i -> i.getName() != null && !i.getName().isBlank()).forEach(i -> {
			i.setApi(api);
			parameterRepository.save(i);
		});
	}
	@Override
	protected Component createGuideComponent() {
		VerticalLayout layout = new VerticalLayout();
		getCommonGuideDetails().forEach(layout::add);
		
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "update", "guide.apiUpdate.update" });
		detailContentList.add(new String[] { "delete", "guide.apiUpdate.delete" });
		createGuideDetails(detailContentList).forEach(layout::add);
		return layout;
	}
}