/*
 * Configuration.java
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
package com.adeptions.colors.domain.components;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Reads environment settings at startup
 */
@Component
public class Configuration {
	private static final String PTY_CONVERTED_COLOR_CACHE = "converted.color.cache";
	private static final String PTY_CONVERTED_COLOR_CACHE_SIZE = "converted.color.cache.size";
	private static final String PTY_COLORSPACE_POOL_MAX_PER_KEY  = "colorspace.pool.max.per.key";
	private static final String PTY_COLORSPACE_POOL_MAX_TOTAL = "colorspace.pool.max.total";
	private static final String PTY_COLORSPACE_POOL_MIN_IDLE_PER_KEY = "colorspace.pool.min.idle.per.key";
	private static final String PTY_COLORSPACE_POOL_MAX_IDLE_PER_KEY = "colorspace.pool.max.idle.per.key";
	private static final String PTY_COLORSPACE_POOL_MAX_WAIT = "colorspace.pool.max.wait";
	private static final String PTY_COLORSPACE_POOL_MIN_EVICTABLE_IDLE_TIME = "colorspace.pool.min.evictable.idle.time";
	private static final String PTY_COLORSPACE_POOL_SOFT_MIN_EVICTABLE_IDLE_TIME = "colorspace.pool.soft.min.evictable.idle.time";
	private static final String PTY_COLORSPACE_POOL_TIME_BETWEEN_EVICTION_RUNS = "colorspace.pool.time.between.eviction.runs";
	private static final String PTY_COLORSPACE_POOL_BLOCK_WHEN_EXHAUSTED = "colorspace.pool.block.when.exhausted";
	private static final String PTY_COLORPROFILES_RESOURCES_PATH = "colorprofiles.resources.path";
	private static final String PTY_COLORPROFILES_DEFAULT_CMYK = "colorprofiles.default.cmyk";
	private static final String PTY_COLORPROFILES_FILES_PATH = "colorprofiles.files.path";

	private static final int DEFAULT_CONVERTED_COLOR_CACHE_SIZE = 1024;

	// converted color cache settings...
	private boolean useConvertedColorCache = false;
	private int convertedColorCacheSize = DEFAULT_CONVERTED_COLOR_CACHE_SIZE;
	// colorspace pool settings...
	private Integer colorSpacePoolMaxPerKey;
	private Integer colorSpacePoolMaxTotal;
	private Integer colorSpacePoolMinIdlePerKey;
	private Integer colorSpacePoolMaxIdlePerKey;
	private Long colorSpacePoolMaxWait;
	private Long colorSpacePoolMinEvictableIdleTime;
	private Long colorSpacePoolSoftMinEvictableIdleTime;
	private Long colorSpacePoolTimeBetweenEvictionRuns;
	private Boolean colorSpacePoolBlockWhenExhausted;
	// color profile settings...
	private String colorProfilesResourcesPath;
	private String colorProfilesDefaultCmykProfile;
	private String colorProfilesFilesPath;

	public Configuration(Environment environment) {
		useConvertedColorCache = getBoolSetting(environment, PTY_CONVERTED_COLOR_CACHE, true);
		convertedColorCacheSize = getIntSetting(environment, PTY_CONVERTED_COLOR_CACHE_SIZE, DEFAULT_CONVERTED_COLOR_CACHE_SIZE);
		colorSpacePoolMaxPerKey = getIntegerSetting(environment, PTY_COLORSPACE_POOL_MAX_PER_KEY);
		colorSpacePoolMaxTotal = getIntegerSetting(environment, PTY_COLORSPACE_POOL_MAX_TOTAL);
		colorSpacePoolMinIdlePerKey = getIntegerSetting(environment, PTY_COLORSPACE_POOL_MIN_IDLE_PER_KEY);
		colorSpacePoolMaxIdlePerKey = getIntegerSetting(environment, PTY_COLORSPACE_POOL_MAX_IDLE_PER_KEY);
		colorSpacePoolMaxWait = getLongSetting(environment, PTY_COLORSPACE_POOL_MAX_WAIT);
		colorSpacePoolMinEvictableIdleTime = getLongSetting(environment, PTY_COLORSPACE_POOL_MIN_EVICTABLE_IDLE_TIME);
		colorSpacePoolSoftMinEvictableIdleTime = getLongSetting(environment, PTY_COLORSPACE_POOL_SOFT_MIN_EVICTABLE_IDLE_TIME);
		colorSpacePoolTimeBetweenEvictionRuns = getLongSetting(environment, PTY_COLORSPACE_POOL_TIME_BETWEEN_EVICTION_RUNS);
		colorSpacePoolBlockWhenExhausted = getBooleanSetting(environment, PTY_COLORSPACE_POOL_BLOCK_WHEN_EXHAUSTED);
		colorProfilesResourcesPath = getStringSetting(environment, PTY_COLORPROFILES_RESOURCES_PATH);
		colorProfilesDefaultCmykProfile = getStringSetting(environment, PTY_COLORPROFILES_DEFAULT_CMYK);
		colorProfilesFilesPath = getStringSetting(environment, PTY_COLORPROFILES_FILES_PATH);
	}

	public boolean isUseConvertedColorCache() {
		return useConvertedColorCache;
	}

	public int getConvertedColorCacheSize() {
		return convertedColorCacheSize;
	}

	public GenericKeyedObjectPoolConfig getColorSpacePoolConfig() {
		GenericKeyedObjectPoolConfig result = new GenericKeyedObjectPoolConfig();
		if (colorSpacePoolMaxTotal != null) {
			result.setMaxTotal(colorSpacePoolMaxTotal);
		}
		if (colorSpacePoolMaxPerKey != null) {
			result.setMaxTotalPerKey(colorSpacePoolMaxPerKey);
		}
		if (colorSpacePoolMaxIdlePerKey != null) {
			result.setMaxIdlePerKey(colorSpacePoolMaxIdlePerKey);
		}
		if (colorSpacePoolMinIdlePerKey != null) {
			result.setMinIdlePerKey(colorSpacePoolMinIdlePerKey);
		}
		if (colorSpacePoolMaxWait != null) {
			result.setMaxWaitMillis(colorSpacePoolMaxWait);
		}
		if (colorSpacePoolMinEvictableIdleTime != null) {
			result.setMinEvictableIdleTimeMillis(colorSpacePoolMinEvictableIdleTime);
		}
		if (colorSpacePoolSoftMinEvictableIdleTime != null) {
			result.setSoftMinEvictableIdleTimeMillis(colorSpacePoolSoftMinEvictableIdleTime);
		}
		if (colorSpacePoolTimeBetweenEvictionRuns != null) {
			result.setTimeBetweenEvictionRunsMillis(colorSpacePoolTimeBetweenEvictionRuns);
		}
		if (colorSpacePoolBlockWhenExhausted != null) {
			result.setBlockWhenExhausted(colorSpacePoolBlockWhenExhausted);
		}
		return result;
	}

	public String getColorProfilesResourcesPath() {
		return colorProfilesResourcesPath;
	}

	public String getColorProfilesDefaultCmykProfile() {
		return colorProfilesDefaultCmykProfile;
	}

	public String getColorProfilesFilesPath() {
		return colorProfilesFilesPath;
	}

	private static int getIntSetting(Environment environment, String settingName, int defaultValue) {
		Integer result = getIntegerSetting(environment, settingName);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	private static Integer getIntegerSetting(Environment environment, String settingName) {
		Integer result = null;
		String ptyValue = environment.getProperty(settingName);
		if (ptyValue != null && !ptyValue.isEmpty()) {
			try {
				result = Integer.parseInt(ptyValue);
			} catch (NumberFormatException nfe) {
				// swallow
			}
		}
		return result;
	}

	private static Long getLongSetting(Environment environment, String settingName) {
		Long result = null;
		String ptyValue = environment.getProperty(settingName);
		if (ptyValue != null && !ptyValue.isEmpty()) {
			try {
				result = Long.parseLong(ptyValue);
			} catch (NumberFormatException nfe) {
				// swallow
			}
		}
		return result;
	}

	private static boolean getBoolSetting(Environment environment, String settingName, boolean defaultValue) {
		Boolean result = getBooleanSetting(environment, settingName);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	private static Boolean getBooleanSetting(Environment environment, String settingName) {
		Boolean result = null;
		String ptyValue = environment.getProperty(settingName);
		if (ptyValue != null && !ptyValue.isEmpty()) {
			result = "true".equals(ptyValue);
		}
		return result;
	}

	private static String getStringSetting(Environment environment, String settingName) {
		String result = environment.getProperty(settingName);
		if (result != null && result.isEmpty()) {
			result = null;
		}
		return result;
	}
}
