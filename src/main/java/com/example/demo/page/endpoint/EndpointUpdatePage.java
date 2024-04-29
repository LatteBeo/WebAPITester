package com.example.demo.page.endpoint;

import static com.example.demo.page.PageConstant.BUTTON_UPDATE;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG;
import static com.example.demo.page.PageConstant.CONFIRM_DIALOG_BUTTON;
import static com.example.demo.page.PageConstant.FIELD_ENDPOINT_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Endpoint;
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
 * Endpoint update page.
 */
@Route("endpoint/update/:endpointid?")
public class EndpointUpdatePage extends EndpointCommonInputPage implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	@Override
	protected Component createComponent() {
		FormLayout layout = createForm();
		binder.bind((IntegerField) getComponent(FIELD_ENDPOINT_ID), "id");
		componentService.addBlankLabel(layout, maxColSize);
		addComponent(layout, BUTTON_UPDATE, componentService.createButton(getTranslation("update"), i -> {
			try {
				binder.writeBean(endpoint);
			} catch (ValidationException e) {
				return;
			}
			endpointRepository.save(endpoint);
			this.getUI().ifPresent(ui -> ui.navigate(EndpointListPage.class));
		}));
		addComponent(layout, CONFIRM_DIALOG,
				componentService.createConfirmDialog(getTranslation("endpointUpdatePage.01"), getTranslation(PageConstant.DELETE), getTranslation("cancel"), i -> {
					endpointRepository.delete(endpoint);
					this.getUI().ifPresent(ui -> ui.navigate(EndpointListPage.class));
				}));
		addComponent(layout, CONFIRM_DIALOG_BUTTON, componentService.createOpenConfirmDialogButton(getTranslation(PageConstant.DELETE),
				(ConfirmDialog) getComponent(CONFIRM_DIALOG)));
		setColSpan(layout, BUTTON_UPDATE, maxColSize / 4);
		setColSpan(layout, CONFIRM_DIALOG_BUTTON, maxColSize / 4);
		return layout;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		String id = event.getRouteParameters().get(FIELD_ENDPOINT_ID).orElse("");
		Optional<Endpoint> optEndpoint = endpointRepository.findById(Integer.parseInt(id));
		if (optEndpoint.isEmpty()) {
			return;
		}
		endpoint = optEndpoint.get();
		binder.readBean(endpoint);
	}
	@Override
	protected Component createGuideComponent() {
		VerticalLayout layout = new VerticalLayout();
		getCommonDetails().forEach(layout::add);
		
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "update", "guide.endpointUpdate.update" });
		detailContentList.add(new String[] { "delete", "guide.endpointUpdate.delete" });
		createGuideDetails(detailContentList).forEach(layout::add);
		
		return layout;
	}
}