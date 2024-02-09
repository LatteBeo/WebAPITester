package com.example.demo.page.endpoint;

import static com.example.demo.page.PageConstant.FIELD_ENDPOINT_ID;
import static com.example.demo.page.PageConstant.FIELD_ENDPOINT_NAME;
import static com.example.demo.page.PageConstant.FIELD_ENDPOINT_URL;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Endpoint;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.EndpointRepository;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

/**
 * Abstract class for Endpoint input class.
 */
abstract class EndpointCommonInputPage extends VerticalPageBase {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	transient EndpointRepository endpointRepository;
	Endpoint endpoint = new Endpoint();
	BeanValidationBinder<Endpoint> binder = new BeanValidationBinder<>(Endpoint.class);
	
	protected FormLayout createForm() {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new ResponsiveStep("0", 2));
		
		addComponent(layout, FIELD_ENDPOINT_NAME, componentService.createTextField(getTranslation("endpointName"), 300, 300));
		addComponent(layout, FIELD_ENDPOINT_URL, componentService.createTextField(getTranslation("endpointUrl"), 300, 300));

		IntegerField endpointIdField = componentService.createIntegerField("", 300);
		endpointIdField.setVisible(false);
		addComponent(layout, FIELD_ENDPOINT_ID, endpointIdField);

		setColSpan(layout, FIELD_ENDPOINT_NAME, 2);
		setColSpan(layout, FIELD_ENDPOINT_URL, 2);
		
		binder.bind((TextField) getComponent(FIELD_ENDPOINT_NAME), "name");
		binder.bind((TextField) getComponent(FIELD_ENDPOINT_URL), "url");
		return layout;
	}
	protected List<Details> getCommonDetails() {
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "endpointName", "guide.endpointRegister.endpointName" });
		detailContentList.add(new String[] { "endpointUrl", "guide.endpointRegister.endpointUrl" });
		return createGuideDetails(detailContentList);
	}

}
