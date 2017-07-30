/*
 * Profiles.java
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
import com.adeptions.colors.api.endpoints.profiles.ProfilesEndpoint;
import com.adeptions.colors.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.awt.color.ICC_ColorSpace;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * Service for listing profiles and obtaining details of specific profile
 */
@Service
public class Profiles {
	private String colorProfilesResourcesPath;
	private String colorProfilesFilesPath;

	public Profiles(Configuration config) {
		colorProfilesResourcesPath = config.getColorProfilesResourcesPath();
		colorProfilesFilesPath = config.getColorProfilesFilesPath();
	}

	@Autowired
	private ColorSpacePool colorSpacePool;

	public List<Map<String,Object>> listProfiles() throws IOException {
		Set<String> seen = new HashSet<String>();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		seen.add(PooledColorSpaceFactory.PROFILE_NAME_SRGB);
		result.add(createShortEntry(PooledColorSpaceFactory.PROFILE_NAME_SRGB));
		String filename;
		if (colorProfilesResourcesPath != null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(loader);
			Resource[] resources = resourcePatternResolver.getResources(colorProfilesResourcesPath + "/*" + ColorParams.ICC_EXT);
			for (Resource resource: resources) {
				filename = resource.getFile().getName();
				seen.add(filename);
				result.add(createShortEntry(filename));
			}
		}
		if (colorProfilesFilesPath != null) {
			File colorProfilesDirectory = new File(colorProfilesFilesPath);
			File[] files = colorProfilesDirectory.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isFile() && file.getName().endsWith(ColorParams.ICC_EXT);
				}
			});
			for (File file: files) {
				filename = file.getName();
				if (!seen.contains(filename)) {
					seen.add(filename);
					result.add(createShortEntry(filename));
				}
			}
		}
		return result;
	}

	public Map<String,Object> getProfileInfo(String profileName) throws NotFoundException {
		Map<String,Object> result = null;
		try {
			ICC_ColorSpace colorSpace = colorSpacePool.get(profileName);
			result = createFullEntry(profileName, colorSpace);
		} catch (Exception e) {
			throw new NotFoundException("Cannot find profile '" + profileName + "'");
		}
		return result;
	}

	private Map<String,Object> createShortEntry(String name) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		result.put("name", name);
		result.put("$ref", "/" + ProfilesEndpoint.ENDPOINT_URI_PROFILES + "/" + name);
		return result;
	}

	private Map<String,Object> createFullEntry(String name, ICC_ColorSpace colorSpace) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		String actualName = name + (!name.endsWith(ColorParams.ICC_EXT) ? ColorParams.ICC_EXT : "");
		result.put("name", actualName);
		result.put("$ref", "/" + ProfilesEndpoint.ENDPOINT_URI_PROFILES + "/" + actualName);
		result.put("type", colorSpace.getType());
		result.put("majorVersion", colorSpace.getProfile().getMajorVersion());
		result.put("minorVersion", colorSpace.getProfile().getMinorVersion());
		return result;
	}
}
