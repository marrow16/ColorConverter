/*
 * EndpointOptions.java
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
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes options for an endpoint
 */
public class EndpointOptions {
	private List<RequestMethod> allowedMethods = new ArrayList<RequestMethod>();
	private List<SubCollectionOption> subCollections = null;
	private List<ParameterOption> parameters = null;
	private String allowedMethodsHeader;

	public EndpointOptions(RequestMethod... allowMethods) {
		StringBuilder allowedMethodsHeaderBuilder = new StringBuilder(RequestMethod.OPTIONS.name());
		allowedMethods.add(RequestMethod.OPTIONS);
		for (RequestMethod method: allowMethods) {
			if (!method.equals(RequestMethod.OPTIONS) && !method.equals(RequestMethod.HEAD)) {
				allowedMethods.add(method);
				allowedMethodsHeaderBuilder.append(",").append(method.name());
				if (method.equals(RequestMethod.GET)) {
					allowedMethods.add(RequestMethod.HEAD);
					allowedMethodsHeaderBuilder.append(",").append(RequestMethod.HEAD.name());
				}
			}
		}
		allowedMethodsHeader = allowedMethodsHeaderBuilder.toString();
	}

	public EndpointOptions addSubCollections(SubCollectionOption... subCollectionOptions) {
		subCollections = new ArrayList<SubCollectionOption>();
		for (SubCollectionOption subCollectionOption: subCollectionOptions) {
			this.subCollections.add(subCollectionOption);
		}
		return this;
	}

	public EndpointOptions addParameters(ParameterOption... parameterOptions) {
		parameters = new ArrayList<ParameterOption>();
		for (ParameterOption parameterOption: parameterOptions) {
			this.parameters.add(parameterOption);
		}
		return this;
	}

	public String getAllowedMethods() {
		return allowedMethodsHeader;
	}

	public List<SubCollectionRef> getRefCollections(String parentPath) {
		if (subCollections == null) {
			return null;
		}
		List<SubCollectionRef> result = new ArrayList<SubCollectionRef>();
		for (SubCollectionOption subCollectionOption: subCollections) {
			result.add(new SubCollectionRef(subCollectionOption, parentPath));
		}
		return result;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public List<SubCollectionOption> getCollections() {
		return subCollections;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public List<ParameterOption> getParameters() {
		return parameters;
	}
}
