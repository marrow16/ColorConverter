/*
 * RootEndpoint.java
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
package com.adeptions.colors.api.endpoints;

import com.adeptions.colors.api.endpoints.cmyk.CmykEndpoint;
import com.adeptions.colors.api.endpoints.profiles.ProfilesEndpoint;
import com.adeptions.colors.api.endpoints.rgb.RgbEndpoint;
import com.adeptions.colors.api.endpoints.xyz.XyzEndpoint;
import com.adeptions.colors.api.options.EndpointOptions;
import com.adeptions.colors.api.options.SubCollectionOption;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint for root
 */
@Controller
@RequestMapping("/")
public class RootEndpoint {
	private static final EndpointOptions options = new EndpointOptions(RequestMethod.GET)
		.addSubCollections(
				new SubCollectionOption(CmykEndpoint.ENDPOINT_URI_CMYK, "Converts RGB or CIE-XYZ colors to CMYK"),
				new SubCollectionOption(ProfilesEndpoint.ENDPOINT_URI_PROFILES, "Collection of color profiles (for use when converting colors)"),
				new SubCollectionOption(RgbEndpoint.ENDPOINT_URI_RGB, "Converts CMYK or CIE-XYZ colors to RGB"),
				new SubCollectionOption(XyzEndpoint.ENDPOINT_URI_XYZ, "Converts CMYK or RGB colors to CIE-XYZ")
		);

	@RequestMapping(method= RequestMethod.GET)
	public @ResponseBody Map<String,Object> doGet() throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("$collections", options.getRefCollections("/"));
		return result;
	}

	/**
	 * Options for endpoint
	 * @param response
	 * @return
	 */
	@RequestMapping(method= RequestMethod.OPTIONS)
	public @ResponseBody EndpointOptions doOptions(HttpServletResponse response) {
		response.setHeader("Allow", options.getAllowedMethods());
		return options;
	}

}
