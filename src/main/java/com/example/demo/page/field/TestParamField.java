package com.example.demo.page.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.demo.entity.TestParameter;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestParamField extends CustomField<TestParameter> {

	private static final long serialVersionUID = 1L;
	private ComboBox<String> nameField = new ComboBox<>();
	private TextField valueField = new TextField();
	private IntegerField idField = new IntegerField();

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public TestParamField() {
		nameField.setItems(Collections.emptyList());
		nameField.setAllowCustomValue(true);
		nameField.addCustomValueSetListener(e -> {
			String customValue = e.getDetail();
			List<String> items = new ArrayList<>();
			e.getSource().getGenericDataView().getItems().forEach(items::add);
			items.add(customValue);
			e.getSource().setItems(items);
			e.getSource().setValue(customValue);
		});
		add(nameField);
		add(new Text(" = "));
		add(valueField);

		idField.setVisible(false);
		add(idField);
	}

	@Override
	protected TestParameter generateModelValue() {
		TestParameter pp = new TestParameter();
		pp.setName(nameField.getValue());
		pp.setValue(valueField.getValue());
		pp.setId(idField.getValue());
		return pp;
	}

	@Override
	public void setPresentationValue(TestParameter newPresentationValue) {
		nameField.setValue(newPresentationValue.getName());
		valueField.setValue(newPresentationValue.getValue());
		idField.setValue(newPresentationValue.getId());

	}
}