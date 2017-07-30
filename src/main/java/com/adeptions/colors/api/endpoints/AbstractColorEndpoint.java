/*
 * AbstractColorEndpoint.java
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

import com.adeptions.colors.api.options.EndpointOptions;
import com.adeptions.colors.dtos.ConversionResult;
import com.adeptions.colors.domain.services.ColorConverter;
import com.adeptions.colors.dtos.ColorParams;
import com.adeptions.colors.exceptions.BadRequestException;
import com.adeptions.colors.exceptions.ConversionException;
import com.adeptions.colors.enums.ColorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractColorEndpoint {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected EndpointOptions options;

	public AbstractColorEndpoint(EndpointOptions options) {
		this.options = options;
	}

	@Autowired
	protected ColorConverter converter;

	protected abstract ColorType getTargetColorType();

	/**
	 * Conversion GET
	 * @param request
	 * @return the result of the conversion
	 * @throws Exception
	 */
	@RequestMapping(method= RequestMethod.GET)
	public @ResponseBody ConversionResult doGet(HttpServletRequest request) throws Exception {
		ColorParams params = new ColorParams(getTargetColorType(), request.getParameterMap());
		return converter.convert(params);
	}

	/**
	 * Options for endpoint
	 * @param response
	 * @return the options
	 */
	@RequestMapping(method= RequestMethod.OPTIONS)
	public @ResponseBody
	EndpointOptions doOptions(HttpServletResponse response) {
		response.setHeader("Allow", options.getAllowedMethods());
		return options;
	}

	@ExceptionHandler
	void handleConversionException(ConversionException e, HttpServletResponse response) throws IOException {
		logger.error(e.getMessage());
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}

	@ExceptionHandler
	void handleBadRequestException(BadRequestException e, HttpServletResponse response) throws IOException {
		logger.error(e.getMessage());
		response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
}
