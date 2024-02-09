package com.example.demo.page;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog.ConfirmEvent;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;

/**
 * Component creation service class.
 */
@Service
public class ComponentService {
	/**
	 * Create text field.
	 * 
	 * @param label  Field label
	 * @param length Field input length
	 * @param width  Field width
	 * @return TextField
	 */
	public TextField createTextField(String label, int length, int width) {
		TextField textField = new TextField(label);
		textField.setMaxLength(length);
		textField.setWidth(width, Unit.PIXELS);
		return textField;
	}

	/**
	 * Create Integer field.
	 * 
	 * @param label Field label
	 * @param width Field width
	 * @return IntegerField
	 */
	public IntegerField createIntegerField(String label, int width) {
		IntegerField integerField = new IntegerField(label);
		integerField.setWidth(width, Unit.PIXELS);
		return integerField;
	}

	/**
	 * Create text area.
	 * 
	 * @param label Field label
	 * @param width Field width
	 * @return TextArea
	 */
	public TextArea createTextArea(String label, int width) {
		TextArea textArea = new TextArea(label);
		if (width > 0) {
			if (width == 999999) {
				textArea.setWidthFull();
			} else {
				textArea.setWidth(width, Unit.PIXELS);
			}

		}
		return textArea;
	}

	/**
	 * Create button.
	 * 
	 * @param label    Button text
	 * @param listener Event listner when the button is clicked.
	 * @return Button
	 */
	public Button createButton(String label, ComponentEventListener<ClickEvent<Button>> listener) {
		Button button = new Button(label);
		button.addClickListener(listener);
		return button;
	}

	/**
	 * Create link
	 * 
	 * @param text        Link text
	 * @param classObject Link destination component
	 * @return RouterLink
	 */
	public RouterLink createLink(String text, Class<? extends Component> classObject) {
		RouterLink link = new RouterLink();
		link.setText(text);
		link.setRoute(classObject);
		return link;
	}

	/**
	 * Create confirm dialog.
	 * 
	 * @param message           Dialog message
	 * @param confirmButtonText Text of confirm button
	 * @param cancelButtonText  Text of cancel button
	 * @param listener          Event listner when the button is clicked.
	 * @return
	 */
	public ConfirmDialog createConfirmDialog(String message, String confirmButtonText, String cancelButtonText,
			ComponentEventListener<ConfirmEvent> listener) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setText(message);
		dialog.setCancelable(true);
		if (cancelButtonText != null) {
			dialog.setCancelText(cancelButtonText);
		}
		if (confirmButtonText != null) {
			dialog.setConfirmText(confirmButtonText);
		}
		dialog.addConfirmListener(listener);
		return dialog;
	}

	/**
	 * Create button which opens confirm dialog.
	 * 
	 * @param text   Button text
	 * @param dialog Opened confirm dialog
	 * @return Button
	 */
	public Button createOpenConfirmDialogButton(String text, ConfirmDialog dialog) {
		Button button = new Button(text);
		button.addClickListener(i -> dialog.open());
		return button;
	}

	/**
	 * Create Select component
	 * 
	 * @param label       Lavel
	 * @param items       Selectable items
	 * @param defautValue Default value
	 * @return Select component
	 */
	public Select<String> createSelect(String label, List<String> items, String defautValue) {
		Select<String> select = new Select<>();
		select.setLabel(label);
		select.setItems(items);
		if (defautValue != null && !defautValue.isEmpty()) {
			select.setValue(defautValue);
		}
		return select;
	}

	/**
	 * Create item select dialog.
	 * 
	 * @param grid Grid
	 * @return Dialog
	 */
	public Dialog createSelectDialog(Grid<?> grid) {
		Dialog dialog = new Dialog();
		VerticalLayout layout = new VerticalLayout();
		layout.add(grid);
		dialog.add(layout);
		return dialog;
	}

	/**
	 * Create dialog open button.
	 * 
	 * @param dialog Dialog to be opend.
	 * @param text   Button text
	 * @return Button
	 */
	public Button createOpenDialogButton(Dialog dialog, String text) {
		Button button = new Button(text);
		button.addClickListener(i -> dialog.open());
		return button;
	}

	/**
	 * Create Details instance for guide page.
	 * 
	 * @param title   Detail title
	 * @param content Detail content
	 * @return Details instance
	 */
	public Details createGuideDetails(String title, String content) {
		TextArea textArea = createTextArea("", 999999);
		textArea.setWidthFull();
		textArea.setEnabled(false);

		textArea.getStyle().set("--lumo-disabled-text-color", "black");

		textArea.setValue(content);
		Details details = new Details(title, textArea);
		details.setWidthFull();
		return details;
	}
}