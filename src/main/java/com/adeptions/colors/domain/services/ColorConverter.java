/*
 * ColorConverter.java
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
package com.adeptions.colors.domain.services;

import com.adeptions.colors.domain.components.Configuration;
import com.adeptions.colors.domain.components.PooledColorSpaceFactory;
import com.adeptions.colors.dtos.ColorParams;
import com.adeptions.colors.dtos.ConversionResult;
import com.adeptions.colors.exceptions.BadRequestException;
import com.adeptions.colors.exceptions.ConversionException;
import com.adeptions.colors.enums.ColorType;
import com.adeptions.colors.domain.utils.LRUCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.color.ICC_ColorSpace;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts colors
 */
@Service
public class ColorConverter {
	protected LRUCache<String,ConversionResult> convertedColorCache;
	protected boolean useConvertedColorCache = false;
	protected String defaultCmykProfile;

	@Autowired
	ColorSpacePool colorSpacePool;

	ColorConverter(Configuration config) {
		useConvertedColorCache = config.isUseConvertedColorCache();
		if (useConvertedColorCache) {
			convertedColorCache = new LRUCache<String,ConversionResult>(config.getConvertedColorCacheSize());
		}
		defaultCmykProfile = config.getColorProfilesDefaultCmykProfile();
	}

	public ConversionResult convert(ColorParams params) throws Exception {
		ConversionResult result = resultFromCache(params);
		if (result == null) {
			Map<String,ICC_ColorSpace> colorSpacesUsed = new HashMap<String,ICC_ColorSpace>();
			try {
				switch (params.getTargetType()) {
					case CMYK:
						result = convertToCmyk(params, colorSpacesUsed);
						break;
					case RGB:
						result = convertToRgb(params, colorSpacesUsed);
						break;
					case XYZ:
						result = convertToXyz(params, colorSpacesUsed);
						break;
					default:
						throw new ConversionException("Non-implemented target color type '" + params.getTargetType().name() + "'");
				}
			} finally {
				for (Map.Entry<String,ICC_ColorSpace> entry : colorSpacesUsed.entrySet()) {
					colorSpacePool.release(entry.getKey(), entry.getValue());
				}
			}
		}
		cacheResult(params, result);
		return result;
	}

	private ConversionResult convertToCmyk(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		ConversionResult result = null;
		switch (params.getSourceType()) {
			case CMYK:
				result = Cmyk2Cmyk(params, colorSpacesUsed);
				break;
			case RGB:
				result = Rgb2Cmyk(params, colorSpacesUsed);
				break;
			case XYZ:
				result = Xyz2Cmyk(params, colorSpacesUsed);
				break;
			default:
				throw new ConversionException("Non-implemented source color type '" + params.getSourceType().name() + "'");
		}
		return result;
	}

	private ConversionResult convertToRgb(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		ConversionResult result = null;
		switch (params.getSourceType()) {
			case CMYK:
				result = Cmyk2Rgb(params, colorSpacesUsed);
				break;
			case RGB:
				result = Rgb2Rgb(params, colorSpacesUsed);
				break;
			case XYZ:
				result = Xyz2Rgb(params, colorSpacesUsed);
				break;
			default:
				throw new ConversionException("Non-implemented source color type '" + params.getSourceType().name() + "'");
		}
		return result;
	}

	private ConversionResult convertToXyz(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		ConversionResult result = null;
		switch (params.getSourceType()) {
			case CMYK:
				result = Cmyk2Xyz(params, colorSpacesUsed);
				break;
			case RGB:
				result = Rgb2Xyz(params, colorSpacesUsed);
				break;
			case XYZ:
				// no conversion...
				result = ConversionResult.createXyzResult(params.getCieXValue(), params.getCieYValue(), params.getCieZValue());
				break;
			default:
				throw new ConversionException("Non-implemented source color type '" + params.getSourceType().name() + "'");
		}
		return result;
	}

	private ConversionResult Rgb2Cmyk(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		if (params.isCrudeConversion()) {
			return Rgb2CmykCrude(params);
		}
		String sourceProfile = params.getSourceProfile();
		String targetProfile = params.getTargetProfile();
		if (targetProfile == null) {
			targetProfile = defaultCmykProfile;
		}
		ICC_ColorSpace targetColorSpace = colorSpacePool.get(targetProfile);
		colorSpacesUsed.put(targetProfile, targetColorSpace);
		checkCorrectColorSpace(targetProfile, ColorType.CMYK, targetColorSpace);
		float[] cmyk = null;
		float[] rgb = params.getRGB();
		if (sourceProfile == null) {
			cmyk = targetColorSpace.fromRGB(rgb);
		} else {
			ICC_ColorSpace sourceColorSpace = colorSpacePool.get(sourceProfile);
			colorSpacesUsed.put(sourceProfile, sourceColorSpace);
			checkCorrectColorSpace(sourceProfile, ColorType.RGB, sourceColorSpace);
			float[] ciexyz = sourceColorSpace.toCIEXYZ(rgb);
			cmyk = targetColorSpace.fromCIEXYZ(ciexyz);
		}
		return ConversionResult.createCmykResult(cmyk);
	}

	private ConversionResult Rgb2CmykCrude(ColorParams params) throws Exception {
		float c = (255f - (float)params.getRedValue()) / 255f;
		float m = (255f - (float)params.getGreenValue()) / 255f;
		float y = (255f - (float)params.getBlueValue()) / 255f;
		float k = Math.min(c, Math.min(m, y));
		if (k == 1.0) {
			return ConversionResult.createCmykResult(0f, 0f, 0f, 1f);
		}
		return ConversionResult.createCmykResult((c-k)/(1-k), (m-k)/(1-k), (y-k)/(1-k), k);
	}

	private ConversionResult Xyz2Cmyk(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		String targetProfile = params.getTargetProfile();
		if (targetProfile == null) {
			targetProfile = defaultCmykProfile;
		}
		ICC_ColorSpace targetColorSpace = colorSpacePool.get(targetProfile);
		colorSpacesUsed.put(targetProfile, targetColorSpace);
		checkCorrectColorSpace(targetProfile, ColorType.CMYK, targetColorSpace);
		float[] cmyk = targetColorSpace.fromCIEXYZ(params.getCieXYZ());
		return ConversionResult.createCmykResult(cmyk);
	}

	private ConversionResult Cmyk2Cmyk(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		String sourceProfile = params.getSourceProfile();
		String targetProfile = params.getTargetProfile();
		if (sourceProfile == null || targetProfile == null || sourceProfile.equals(targetProfile)) {
			// no actual conversion...
			return ConversionResult.createCmykResult(params.getCyanValue(), params.getMagentaValue(), params.getYellowValue(), params.getBlackValue());
		}
		// convert CMYK in specified color space to CMYK in target color space...
		ICC_ColorSpace sourceColorSpace = colorSpacePool.get(sourceProfile);
		colorSpacesUsed.put(sourceProfile, sourceColorSpace);
		checkCorrectColorSpace(sourceProfile, ColorType.CMYK, sourceColorSpace);
		ICC_ColorSpace targetColorSpace = colorSpacePool.get(targetProfile);
		colorSpacesUsed.put(targetProfile, targetColorSpace);
		checkCorrectColorSpace(targetProfile, ColorType.CMYK, targetColorSpace);
		float[] ciexyz = sourceColorSpace.toCIEXYZ(params.getCMYK());
		float[] cmyk = targetColorSpace.fromCIEXYZ(ciexyz);
		return ConversionResult.createCmykResult(cmyk);
	}

	private ConversionResult Cmyk2Rgb(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		if (params.isCrudeConversion()) {
			return Cmyk2RgbCrude(params);
		}
		String sourceProfile = params.getSourceProfile();
		String targetProfile = params.getTargetProfile();
		if (sourceProfile == null) {
			sourceProfile = defaultCmykProfile;
		}
		ICC_ColorSpace sourceColorSpace = colorSpacePool.get(sourceProfile);
		colorSpacesUsed.put(sourceProfile, sourceColorSpace);
		checkCorrectColorSpace(sourceProfile, ColorType.CMYK, sourceColorSpace);
		float[] cmyk = params.getCMYK();
		float[] rgb;
		if (targetProfile == null) {
			// no target RGB profile...
			rgb = sourceColorSpace.toRGB(cmyk);
		} else {
			// use specified target RGB profile...
			ICC_ColorSpace targetColorSpace = colorSpacePool.get(targetProfile);
			colorSpacesUsed.put(targetProfile, targetColorSpace);
			checkCorrectColorSpace(targetProfile, ColorType.RGB, targetColorSpace);
			float[] ciexyz = sourceColorSpace.toCIEXYZ(cmyk);
			rgb = targetColorSpace.fromCIEXYZ(ciexyz);
		}
		return ConversionResult.createRgbResult(rgb);
	}

	private ConversionResult Cmyk2RgbCrude(ColorParams params) {
		float red = (1 - params.getCyanValue()) * (1 - params.getBlackValue()) * 255f;
		float green = (1 - params.getMagentaValue()) * (1 - params.getBlackValue()) * 255f;
		float blue = (1 - params.getYellowValue()) * (1 - params.getBlackValue()) * 255f;
		return ConversionResult.createRgbResult((int)red, (int)green, (int)blue);
	}

	private ConversionResult Rgb2Rgb(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		String sourceProfile = params.getSourceProfile();
		String targetProfile = params.getTargetProfile();
		ICC_ColorSpace sourceColorSpace;
		float[] rgb = null;
		if ((sourceProfile == null && targetProfile == null) || (sourceProfile != null && sourceProfile.equals(targetProfile))) {
			// no actual conversion...
			return ConversionResult.createRgbResult(params.getRedValue(), params.getGreenValue(), params.getBlueValue());
		} else if (targetProfile == null) {
			// convert RGB in specified color space to sRFB...
			sourceColorSpace = colorSpacePool.get(sourceProfile);
			colorSpacesUsed.put(sourceProfile, sourceColorSpace);
			checkCorrectColorSpace(sourceProfile, ColorType.RGB, sourceColorSpace);
			rgb = sourceColorSpace.toRGB(params.getRGB());
		} else {
			// convert RGB in specified color space to RGB in target color space...
			sourceColorSpace = colorSpacePool.get(sourceProfile);
			colorSpacesUsed.put(sourceProfile, sourceColorSpace);
			checkCorrectColorSpace(sourceProfile, ColorType.RGB, sourceColorSpace);
			ICC_ColorSpace targetColorSpace = colorSpacePool.get(targetProfile);
			colorSpacesUsed.put(targetProfile, targetColorSpace);
			checkCorrectColorSpace(targetProfile, ColorType.RGB, targetColorSpace);
			float[] ciexyz = sourceColorSpace.toCIEXYZ(params.getRGB());
			rgb = targetColorSpace.fromCIEXYZ(ciexyz);
		}
		return ConversionResult.createRgbResult(rgb);
	}

	private ConversionResult Xyz2Rgb(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		String targetProfile = params.getTargetProfile();
		if (targetProfile == null) {
			targetProfile = PooledColorSpaceFactory.PROFILE_NAME_SRGB;
		}
		ICC_ColorSpace targetColorSpace = colorSpacePool.get(targetProfile);
		colorSpacesUsed.put(targetProfile, targetColorSpace);
		checkCorrectColorSpace(targetProfile, ColorType.RGB, targetColorSpace);
		float[] rgb = targetColorSpace.fromCIEXYZ(params.getCieXYZ());
		return ConversionResult.createRgbResult(rgb);
	}

	private ConversionResult Cmyk2Xyz(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		String sourceProfile = params.getSourceProfile();
		if (sourceProfile == null) {
			sourceProfile = defaultCmykProfile;
		}
		ICC_ColorSpace sourceColorSpace = colorSpacePool.get(sourceProfile);
		colorSpacesUsed.put(sourceProfile, sourceColorSpace);
		checkCorrectColorSpace(sourceProfile, ColorType.CMYK, sourceColorSpace);
		float[] xyz = sourceColorSpace.toCIEXYZ(params.getCMYK());
		return ConversionResult.createXyzResult(xyz);
	}

	private ConversionResult Rgb2Xyz(ColorParams params, Map<String,ICC_ColorSpace> colorSpacesUsed) throws Exception {
		String sourceProfile = params.getSourceProfile();
		if (sourceProfile == null) {
			sourceProfile = PooledColorSpaceFactory.PROFILE_NAME_SRGB;
		}
		ICC_ColorSpace sourceColorSpace = colorSpacePool.get(sourceProfile);
		colorSpacesUsed.put(sourceProfile, sourceColorSpace);
		checkCorrectColorSpace(sourceProfile, ColorType.RGB, sourceColorSpace);
		float[] xyz = sourceColorSpace.toCIEXYZ(params.getRGB());
		return ConversionResult.createXyzResult(xyz);
	}

	private ConversionResult resultFromCache(ColorParams params) {
		ConversionResult result = null;
		if (useConvertedColorCache) {
			result = convertedColorCache.get(params.getTargetType().name() + "<" + params.getCacheKey());
		}
		return result;
	}

	private void cacheResult(ColorParams params, ConversionResult converted) {
		if (useConvertedColorCache) {
			convertedColorCache.put(params.getTargetType().name() + "<" + params.getCacheKey(), converted);
		}
	}

	private void checkCorrectColorSpace(String profile, ColorType requiredType, ICC_ColorSpace colorSpace) throws BadRequestException {
		switch (requiredType) {
			case CMYK:
				if (colorSpace.getType() != ICC_ColorSpace.TYPE_CMYK) {
					throw new BadRequestException("Profile '" + profile + "' is not a CMYK profile");
				}
				break;
			case RGB:
				if (colorSpace.getType() != ICC_ColorSpace.TYPE_RGB) {
					throw new BadRequestException("Profile '" + profile + "' is not an RGB profile");
				}
				break;
		}
	}

}
