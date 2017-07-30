/*
 * ColorParams.java
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

import com.adeptions.colors.exceptions.BadRequestException;
import com.adeptions.colors.exceptions.ConversionException;
import com.adeptions.colors.enums.ColorType;

import java.util.Map;

public class ColorParams {
	public static final String ICC_EXT = ".icc";
	public static final String PARAM_NAME_RED = "r";
	public static final String PARAM_NAME_GREEN = "g";
	public static final String PARAM_NAME_BLUE = "b";
	public static final String PARAM_NAME_CYAN = "c";
	public static final String PARAM_NAME_MAGENTA = "m";
	public static final String PARAM_NAME_YELLOW = "y";
	public static final String PARAM_NAME_BLACK = "k";
	public static final String PARAM_NAME_X = "cie-x";
	public static final String PARAM_NAME_Y = "cie-y";
	public static final String PARAM_NAME_Z = "cie-z";
	public static final String PARAM_NAME_PROFILE = "profile";
	public static final String PARAM_NAME_SOURCE_PROFILE = "sourceProfile";
	public static final String PARAM_NAME_CRUDE = "crude";

	private ColorType targetType;
	private ColorType sourceType;
	private Integer rValue;
	private Integer gValue;
	private Integer bValue;
	private Float cValue;
	private Float mValue;
	private Float yValue;
	private Float kValue;
	private Float cieXValue;
	private Float cieYValue;
	private Float cieZValue;
	private String targetProfile;
	private String sourceProfile;
	private boolean crudeConversion;
	private String cacheKey = null;

	public ColorParams(ColorType targetType, Map<String,String[]> params) throws ConversionException {
		this.targetType = targetType;
		readParams(params);
	}

	private void readParams(Map<String,String[]> params) throws ConversionException {
		rValue = readRgbParam(params, PARAM_NAME_RED);
		gValue = readRgbParam(params, PARAM_NAME_GREEN);
		bValue = readRgbParam(params, PARAM_NAME_BLUE);
		cValue = readCmykParam(params, PARAM_NAME_CYAN);
		mValue = readCmykParam(params, PARAM_NAME_MAGENTA);
		yValue = readCmykParam(params, PARAM_NAME_YELLOW);
		kValue = readCmykParam(params, PARAM_NAME_BLACK);
		cieXValue = readXyzParam(params, PARAM_NAME_X);
		cieYValue = readXyzParam(params, PARAM_NAME_X);
		cieZValue = readXyzParam(params, PARAM_NAME_X);
		if (rValue != null || gValue != null || bValue != null) {
			sourceType = ColorType.RGB;
			if (rValue == null || gValue == null || bValue == null) {
				throw new BadRequestException("Params '" + PARAM_NAME_RED + "', '" + PARAM_NAME_GREEN + "' and '" + PARAM_NAME_BLUE + "' must all be specified");
			}
			if (cValue != null || mValue != null || yValue != null || kValue != null || cieXValue != null || cieYValue != null || cieZValue != null) {
				throw new BadRequestException("Params '" + PARAM_NAME_RED + "', '" + PARAM_NAME_GREEN + "' and '" + PARAM_NAME_BLUE + "' cannot be mixed with CMYK or CIE-XYZ params");
			}
		} else if (cValue != null || mValue != null || yValue != null || kValue != null) {
			sourceType = ColorType.CMYK;
			if (cValue == null || mValue == null || yValue == null || kValue == null) {
				throw new BadRequestException("Params '" + PARAM_NAME_CYAN + "', '" + PARAM_NAME_MAGENTA + "', '" + PARAM_NAME_YELLOW + "' and '" + PARAM_NAME_BLACK + "' must all be specified");
			}
			if (cieXValue != null || cieYValue != null || cieZValue != null) {
				throw new BadRequestException("Params '" + PARAM_NAME_CYAN + "', '" + PARAM_NAME_MAGENTA + "', '" + PARAM_NAME_YELLOW + "' and '" + PARAM_NAME_BLACK + "' cannot be mixed with CIE-XYZ params");
			}
		} else if (cieXValue != null || cieYValue != null || cieZValue != null) {
			sourceType = ColorType.XYZ;
			if (cieXValue == null || cieYValue == null || cieZValue == null) {
				throw new BadRequestException("Params '" + PARAM_NAME_X + "', '" + PARAM_NAME_Y + "' and '" + PARAM_NAME_Z + "' must all be specified");
			}
		} else if (ColorType.RGB.equals(targetType)) {
			throw new BadRequestException("Params '" + PARAM_NAME_CYAN + "', '" + PARAM_NAME_MAGENTA + "', '" + PARAM_NAME_YELLOW + "' and '" + PARAM_NAME_BLACK + "' should be specified");
		} else {
			throw new BadRequestException("Params '" + PARAM_NAME_RED + "', '" + PARAM_NAME_GREEN + "' and '" + PARAM_NAME_BLUE + "' should be specified");
		}
		targetProfile = getFirstParam(params, PARAM_NAME_PROFILE);
		if (targetProfile != null && !targetProfile.endsWith(ICC_EXT)) {
			targetProfile += ICC_EXT;
		}
		sourceProfile = getFirstParam(params, PARAM_NAME_SOURCE_PROFILE);
		if (sourceProfile != null && !sourceProfile.endsWith(ICC_EXT)) {
			sourceProfile += ICC_EXT;
		}
		crudeConversion = params.containsKey(PARAM_NAME_CRUDE) && (getFirstParam(params, PARAM_NAME_CRUDE) == null || getFirstParam(params, PARAM_NAME_CRUDE).isEmpty() || "true".equals(getFirstParam(params, PARAM_NAME_CRUDE)));
		if (crudeConversion && (targetProfile != null || sourceProfile != null)) {
			throw new BadRequestException("Param '" + PARAM_NAME_CRUDE + "' cannot be used with params '" + PARAM_NAME_PROFILE + "' or '" + PARAM_NAME_SOURCE_PROFILE + "'");
		}
	}

	private static Integer readRgbParam(Map<String,String[]> params, String paramName) throws ConversionException {
		Integer result = null;
		String paramValue = getFirstParam(params, paramName);
		if (paramValue != null) {
			try {
				result = Integer.parseInt(paramValue);
			} catch (NumberFormatException nfe) {
				throw new BadRequestException("Param '" + paramName + "' must be a valid integer");
			}
			if (result < 0 || result > 255) {
				throw new BadRequestException("Param '" + paramName + "' must be in the range 0 to 255");
			}
		}
		return result;
	}

	private static Float readCmykParam(Map<String,String[]> params, String paramName) throws ConversionException {
		Float result = null;
		String paramValue = getFirstParam(params, paramName);
		boolean asPercentage = false;
		if (paramValue != null) {
			if (paramValue.endsWith("%")) {
				asPercentage = true;
				paramValue = paramValue.substring(0, paramValue.length() - 1);
			}
			try {
				result = Float.parseFloat(paramValue);
			} catch (NumberFormatException nfe) {
				throw new BadRequestException("Param '" + paramName + "' must be a valid numeric value");
			}
			if (asPercentage) {
				if (result < 0f || result > 100f) {
					throw new BadRequestException("Param '" + paramName + "' must be in the range 0% to 100%");
				}
				result = result / 100f;
			} else if (result < 0f || result > 1f) {
				throw new BadRequestException("Param '" + paramName + "' must be in the range 0 to 1");
			}
		}
		return result;
	}

	private static Float readXyzParam(Map<String,String[]> params, String paramName) throws ConversionException {
		Float result = null;
		String paramValue = getFirstParam(params, paramName);
		if (paramValue != null) {
			try {
				result = Float.parseFloat(paramValue);
			} catch (NumberFormatException nfe) {
				throw new BadRequestException("Param '" + paramName + "' must be a valid numeric value");
			}
			if (result < 0f || result > 1f) {
				throw new BadRequestException("Param '" + paramName + "' must be in the range 0 to 1");
			}
		}
		return result;
	}

	private static String getFirstParam(Map<String,String[]> params, String paramName) throws BadRequestException {
		String result = null;
		String[] values = params.get(paramName);
		if (values != null) {
			if (values.length != 1) {
				throw new BadRequestException("Param '" + paramName + "' can only be specified once");
			}
			result = values[0];
		}
		return result;
	}

	public ColorType getTargetType() {
		return targetType;
	}

	public ColorType getSourceType() {
		return sourceType;
	}

	public Integer getRedValue() {
		return rValue;
	}

	public Integer getGreenValue() {
		return gValue;
	}

	public Integer getBlueValue() {
		return bValue;
	}

	public float[] getRGB() {
		return new float[] {rValue.floatValue() / 255f, gValue.floatValue() / 255f, bValue.floatValue() / 255f};
	}

	public Float getCyanValue() {
		return cValue;
	}

	public Float getMagentaValue() {
		return mValue;
	}

	public Float getYellowValue() {
		return yValue;
	}

	public Float getBlackValue() {
		return kValue;
	}

	public float[] getCMYK() {
		return new float[] {cValue, mValue, yValue, kValue};
	}

	public Float getCieXValue() {
		return cieXValue;
	}

	public Float getCieYValue() {
		return cieYValue;
	}

	public Float getCieZValue() {
		return cieZValue;
	}

	public float[] getCieXYZ() {
		return new float[] {cieXValue, cieYValue, cieZValue};
	}

	public String getTargetProfile() {
		return targetProfile;
	}

	public String getSourceProfile() {
		return sourceProfile;
	}

	public boolean isCrudeConversion() {
		return crudeConversion;
	}

	public String getCacheKey() {
		if (cacheKey == null) {
			StringBuilder cacheKeyBuilder = (new StringBuilder(sourceType.name())).append(":");
			switch (sourceType) {
				case CMYK:
					cacheKeyBuilder.append(cValue).append(",").append(mValue).append(",").append(yValue).append(",").append(kValue);
					break;
				case RGB:
					cacheKeyBuilder.append(rValue).append(",").append(gValue).append(",").append(bValue);
					break;
			}
			if (crudeConversion) {
				cacheKeyBuilder.append(":!CRUDE!");
			} else {
				if (targetProfile != null) {
					cacheKeyBuilder.append(":T=").append(targetProfile);
				}
				if (sourceProfile != null) {
					cacheKeyBuilder.append(":S=").append(sourceProfile);
				}
			}
			cacheKey = cacheKeyBuilder.toString();
		}
		return cacheKey;
	}
}
