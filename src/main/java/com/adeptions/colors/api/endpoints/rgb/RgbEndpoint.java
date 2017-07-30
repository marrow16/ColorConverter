/*
 * RgbEndpoint.java
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
package com.adeptions.colors.api.endpoints.rgb;

import com.adeptions.colors.api.endpoints.AbstractColorEndpoint;
import com.adeptions.colors.api.endpoints.profiles.ProfilesEndpoint;
import com.adeptions.colors.api.options.EndpointOptions;
import com.adeptions.colors.api.options.ParameterOption;
import com.adeptions.colors.dtos.ColorParams;
import com.adeptions.colors.enums.ColorType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Endpoint for conversions to RGB
 */
@Controller
@RequestMapping("/" + RgbEndpoint.ENDPOINT_URI_RGB)
public class RgbEndpoint extends AbstractColorEndpoint {
	public static final String ENDPOINT_URI_RGB = "rgb";

	public RgbEndpoint() {
		super(new EndpointOptions(RequestMethod.GET)
				.addParameters(
						new ParameterOption(ColorParams.PARAM_NAME_CYAN,
								"The CYAN component of the color to be converted",
								"float|string")
								.addRange("0 - 1 or ('0%' to '100%')")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_MAGENTA + "', '" + ColorParams.PARAM_NAME_YELLOW + "' and '" + ColorParams.PARAM_NAME_BLACK + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_MAGENTA,
								"The MAGENTA component of the color to be converted",
								"float|string")
								.addRange("0 - 1 or ('0%' to '100%')")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_CYAN + "', '" + ColorParams.PARAM_NAME_YELLOW + "' and '" + ColorParams.PARAM_NAME_BLACK + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_YELLOW,
								"The YELLOW component of the color to be converted",
								"float|string")
								.addRange("0 - 1 or ('0%' to '100%')")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_CYAN + "', '" + ColorParams.PARAM_NAME_MAGENTA + "' and '" + ColorParams.PARAM_NAME_BLACK + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_BLACK,
								"The BLACK component of the color to be converted",
								"float|string")
								.addRange("0 - 1 or ('0%' to '100%')")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_CYAN + "', '" + ColorParams.PARAM_NAME_MAGENTA + "' and '" + ColorParams.PARAM_NAME_YELLOW + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_RED,
								"The RED component of the color to be converted",
								"integer")
								.addRange("0 - 255")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_GREEN + "' and '" + ColorParams.PARAM_NAME_BLUE + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_GREEN,
								"The GREEN component of the color to be converted",
								"integer")
								.addRange("0 - 255")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_RED + "' and '" + ColorParams.PARAM_NAME_BLUE + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_BLUE,
								"The BLUE component of the color to be converted",
								"integer")
								.addRange("0 - 255")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_RED + "' and '" + ColorParams.PARAM_NAME_GREEN + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_X,
								"The cie-xyz X component of the color to be converted",
								"float")
								.addRange("0 - 1")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_Y + "' and '" + ColorParams.PARAM_NAME_Z + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_Y,
								"The cie-xyz Y component of the color to be converted",
								"float")
								.addRange("0 - 1")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_X + "' and '" + ColorParams.PARAM_NAME_Z + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_Z,
								"The cie-xyz Z component of the color to be converted",
								"float")
								.addRange("0 - 1")
								.addValidation("Must be used with '" + ColorParams.PARAM_NAME_X + "' and '" + ColorParams.PARAM_NAME_Y + "' parameters"),
						new ParameterOption(ColorParams.PARAM_NAME_PROFILE,
								"The target RGB color profile for the conversion",
								"string")
								.addValuesRef("/" + ProfilesEndpoint.ENDPOINT_URI_PROFILES),
						new ParameterOption(ColorParams.PARAM_NAME_SOURCE_PROFILE,
								"The source color profile for the conversion",
								"string")
								.addValidation("If parameters '" + ColorParams.PARAM_NAME_RED + "', '" + ColorParams.PARAM_NAME_GREEN + "' and '" + ColorParams.PARAM_NAME_BLUE + "' are specified then this must be an RGB profile, " +
										"if parameters '" + ColorParams.PARAM_NAME_CYAN + "', '" + ColorParams.PARAM_NAME_MAGENTA + "', '" + ColorParams.PARAM_NAME_YELLOW + "' and '" + ColorParams.PARAM_NAME_BLACK + "' are specified then this must be a CMYK profile")
								.addValuesRef("/" + ProfilesEndpoint.ENDPOINT_URI_PROFILES),
						new ParameterOption(ColorParams.PARAM_NAME_CRUDE,
								"Whether to use crude mathematical conversion algorithm",
								"boolean")
								.addValidation("Default is 'false' if omitted - if this parameter is specified without value then 'true' is assumed")
								.addValues(Boolean.TRUE, Boolean.FALSE)
				));
	}

	@Override
	protected ColorType getTargetColorType() {
		return ColorType.RGB;
	}
}
