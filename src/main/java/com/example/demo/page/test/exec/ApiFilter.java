package com.example.demo.page.test.exec;

import com.example.demo.entity.Api;
import com.vaadin.flow.component.grid.dataview.GridListDataView;

/**
 * Grid filter for api select dialog.
 */
public class ApiFilter {
	/**
	 * Grid data.
	 */
	private final GridListDataView<Api> dataView;
	/**
	 * Api name which is input on search column.
	 */
	private String apiName = "";

	public ApiFilter(GridListDataView<Api> dataView) {
		this.dataView = dataView;
		this.dataView.addFilter(this::test);
	}

	/**
	 * Check if the input name matches each api data.
	 * 
	 * @param api Api object
	 * @return true if it matches.
	 */
	public boolean test(Api api) {
		return matches(api.getName(), this.apiName);
	}

	/**
	 * Set search target api name.
	 * 
	 * @param apiName Api object
	 */
	public void setApiName(String apiName) {
		this.apiName = apiName;
		this.dataView.refreshAll();
	}

	private boolean matches(String value, String searchTerm) {
		return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
	}
}
