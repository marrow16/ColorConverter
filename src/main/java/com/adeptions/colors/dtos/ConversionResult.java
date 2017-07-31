/*
 * ConversionResult.java
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
package com.adeptions.colors.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The result of a color conversion
 */
public class ConversionResult {
	private Float c;
	private Float m;
	private Float y;
	private Float k;
	private Integer r;
	private Integer g;
	private Integer b;
	private Float rPercent;
	private Float gPercent;
	private Float bPercent;
	private Float cieX;
	private Float cieY;
	private Float cieZ;

	private ConversionResult() {

	}

	public static ConversionResult createRgbResult(float[] rgb) {
		ConversionResult result = new ConversionResult();
		result.r = Math.round(rgb[0] * 255f);
		result.g = Math.round(rgb[1] * 255f);
		result.b = Math.round(rgb[2] * 255f);
		result.rPercent = rgb[0];
		result.gPercent = rgb[1];
		result.bPercent = rgb[2];
		return result;
	}

	public static ConversionResult createRgbResult(Integer r, Integer g, Integer b) {
		ConversionResult result = new ConversionResult();
		result.r = r;
		result.g = g;
		result.b = b;
		result.rPercent = r.floatValue() / 255f;
		result.gPercent = g.floatValue() / 255f;
		result.bPercent = b.floatValue() / 255f;
		return result;
	}

	public static ConversionResult createRgbResult(float r, float g, float b) {
		ConversionResult result = new ConversionResult();
		result.r = Math.round(r * 255f);
		result.g = Math.round(g * 255f);
		result.b = Math.round(b * 255f);
		result.rPercent = r;
		result.gPercent = g;
		result.bPercent = b;
		return result;
	}

	public static ConversionResult createCmykResult(float[] cmyk) {
		ConversionResult result = new ConversionResult();
		result.c = cmyk[0];
		result.m = cmyk[1];
		result.y = cmyk[2];
		result.k = cmyk[3];
		return result;
	}

	public static ConversionResult createCmykResult(Float c, Float m, Float y, Float k) {
		ConversionResult result = new ConversionResult();
		result.c = c;
		result.m = m;
		result.y = y;
		result.k = k;
		return result;
	}

	public static ConversionResult createXyzResult(float[] xyz) {
		ConversionResult result = new ConversionResult();
		result.cieX = xyz[0];
		result.cieY = xyz[1];
		result.cieZ = xyz[2];
		return result;
	}

	public static ConversionResult createXyzResult(Float x, Float y, Float z) {
		ConversionResult result = new ConversionResult();
		result.cieX = x;
		result.cieY = y;
		result.cieZ = z;
		return result;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getC() {
		return c;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getM() {
		return m;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getY() {
		return y;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getK() {
		return k;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Integer getR() {
		return r;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Integer getG() {
		return g;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Integer getB() {
		return b;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getrPercent() {
		return rPercent;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getgPercent() {
		return gPercent;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getbPercent() {
		return bPercent;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getCieX() {
		return cieX;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getCieY() {
		return cieY;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Float getCieZ() {
		return cieZ;
	}
}
