/*
 * ParameterOption.java
 *
 * Copyright 2017 Martin Rowlinson. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.adeptions.colors.api.options;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A parameter option for an endpoint
 */
public class ParameterOption {
	private String name;
	private String description;
	private String type = "string";
	private boolean multipleAllowed;
	private String validation;
	private String range;
	private Map<String,Object> valuesRef;
	private List<Object> values;

	public ParameterOption(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public ParameterOption(String name, String description, String type) {
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public ParameterOption(String name, String description, String type, boolean multipleAllowed) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.multipleAllowed = multipleAllowed;
	}

	public ParameterOption addValidation(String validation) {
		this.validation = validation;
		return this;
	}

	public ParameterOption addRange(String range) {
		this.range = range;
		return this;
	}

	public ParameterOption addValues(Object... values) {
		this.values = new ArrayList<Object>();
		for (Object value: values) {
			this.values.add(value);
		}
		return this;
	}

	public ParameterOption addValuesRef(String valuesRef) {
		this.valuesRef = new HashMap<String,Object>();
		this.valuesRef.put("$ref", valuesRef);
		return this;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getType() {
		return type;
	}

	public boolean isMultipleAllowed() {
		return multipleAllowed;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getValidation() {
		return validation;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getRange() {
		return range;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Object getValues() {
		if (valuesRef != null) {
			return valuesRef;
		} else if (values != null) {
			return values;
		}
		return null;
	}
}
