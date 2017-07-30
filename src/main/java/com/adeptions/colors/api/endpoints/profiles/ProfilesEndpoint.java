/*
 * ProfilesEndpoint.java
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
package com.adeptions.colors.api.endpoints.profiles;

import com.adeptions.colors.exceptions.ConversionException;
import com.adeptions.colors.domain.services.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Endpoint for listing available profiles
 */
@Controller
@RequestMapping("/" + ProfilesEndpoint.ENDPOINT_URI_PROFILES)
public class ProfilesEndpoint {
	public static final String ENDPOINT_URI_PROFILES = "profiles";

	@Autowired
	Profiles profiles;

	@RequestMapping(method= RequestMethod.GET)
	public @ResponseBody List<Map<String,Object>> doGet() throws Exception {
		return profiles.listProfiles();
	}

	@ExceptionHandler
	void handleConversionException(ConversionException e, HttpServletResponse response) throws IOException {
		response.sendError(e.getStatusCode(), e.getMessage());
	}
}
