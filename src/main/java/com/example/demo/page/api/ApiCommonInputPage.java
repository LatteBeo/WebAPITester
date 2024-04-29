package com.example.demo.page.api;

import static com.example.demo.page.PageConstant.FIELD_API_ID;
import static com.example.demo.page.PageConstant.FIELD_API_NAME;
import static com.example.demo.page.PageConstant.PREFIX_PARAM;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Api;
import com.example.demo.entity.Parameter;
import com.example.demo.page.VerticalPageBase;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.ParameterRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

/**
 * Abstract class for API input class.
 */
abstract class ApiCommonInputPage extends VerticalPageBase {
	private static final long serialVersionUID = 1L;
	protected static int MAX_COL = 2;
	@Autowired
	transient ApiRepository apiRepository;
	@Autowired
	transient ParameterRepository parameterRepository;
	Api api = new Api();
	ArrayList<Parameter> parameterList = new ArrayList<>(20);
	BeanValidationBinder<Api> apiBinder = new BeanValidationBinder<>(Api.class);
	ArrayList<BeanValidationBinder<Parameter>> parameterBinderList = new ArrayList<>(20);

	/**
	 * Create form.
	 * 
	 * @return FromLayout object
	 */
	protected FormLayout createForm() {
		FormLayout layout = new FormLayout();
		layout.setWidth(1000, Unit.PIXELS);
		layout.setResponsiveSteps(new ResponsiveStep("0", MAX_COL));

		addComponent(layout, FIELD_API_NAME, componentService.createTextField(getTranslation("apiName"), 300, 300));
		setColSpan(layout, FIELD_API_NAME, MAX_COL);
		apiBinder.bind((TextField) getComponent(FIELD_API_NAME), "name");

		addComponent(layout, FIELD_API_ID, componentService.createIntegerField("", 300));
		getComponent(FIELD_API_ID).setVisible(false);
		setColSpan(layout, FIELD_API_ID, 2);

		VerticalLayout paramTitleArea = new VerticalLayout();
		paramTitleArea.add(new Text(getTranslation("parameterName")));
		layout.add(paramTitleArea);
		layout.setColspan(paramTitleArea, MAX_COL);

		for (int i = 1; i < 21; i++) {
			addComponent(layout, PREFIX_PARAM + String.valueOf(i), componentService.createTextField("", 300, 300));
			BeanValidationBinder<Parameter> binder = new BeanValidationBinder<>(Parameter.class);
			parameterBinderList.add(binder);
			binder.bind((TextField) getComponent(PREFIX_PARAM + String.valueOf(i)), "name");
		}
		return layout;
	}

	/**
	 * Write input data to the beans.
	 * 
	 * @throws ValidationException
	 */
	protected void writeBeans() throws ValidationException {
		apiBinder.writeBean(api);
		for (int x = 0; x < 20; x++) {
			parameterBinderList.get(x).writeBean(parameterList.get(x));
		}
	}

	/**
	 * Get common guide details.
	 * 
	 * @return Guide details list
	 */
	protected List<Details> getCommonGuideDetails() {
		List<String[]> detailContentList = new ArrayList<>();
		detailContentList.add(new String[] { "apiName", "guide.apiRegister.apiName" });
		detailContentList.add(new String[] { "parameterName", "guide.apiRegister.parameterName" });
		return createGuideDetails(detailContentList);
	}
}