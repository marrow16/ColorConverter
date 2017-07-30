/*
 * ProfileEndpoint.java
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

import com.adeptions.colors.dtos.ColorParams;
import com.adeptions.colors.exceptions.ConversionException;
import com.adeptions.colors.domain.services.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Endpoint for info on a specific profile
 */
@Controller
@RequestMapping("/" + ProfilesEndpoint.ENDPOINT_URI_PROFILES + "/{profile}")
public class ProfileEndpoint {
	@Autowired
	Profiles profiles;

	@RequestMapping(method= RequestMethod.GET)
	public @ResponseBody Map<String,Object> doGet(@PathVariable("profile") String profile) throws Exception {
		String profileName = profile + (!profile.endsWith(ColorParams.ICC_EXT) ? ColorParams.ICC_EXT : "");
		return profiles.getProfileInfo(profileName);
	}

	@ExceptionHandler
	void handleConversionException(ConversionException e, HttpServletResponse response) throws IOException {
		response.sendError(e.getStatusCode(), e.getMessage());
	}
}
