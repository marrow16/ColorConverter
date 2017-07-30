/*
 * PooledColorSpaceFactory.java
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

import com.adeptions.colors.exceptions.BadRequestException;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.stereotype.Component;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * PooledColorSpaceFactory
 *
 * Loads color space (ICC_ColorSpace) from resources or file
 */
@Component
public class PooledColorSpaceFactory implements KeyedPooledObjectFactory<String,ICC_ColorSpace> {
	public static final String PROFILE_NAME_SRGB = "sRFB.icc";

	private String colorProfilesResourcesPath;
	private String colorProfilesFilesPath;

	public PooledColorSpaceFactory(Configuration config) throws IOException {
		colorProfilesResourcesPath = config.getColorProfilesResourcesPath();
		colorProfilesFilesPath = config.getColorProfilesFilesPath();
	}

	private ICC_ColorSpace loadColorspace(String filepath) throws IOException, BadRequestException {
		// make sure filepath can't escape our directories...
		if (filepath.contains("/") || filepath.contains("\\")) {
			throw new BadRequestException("Color profile '" + filepath + "' contains invalid characters");
		}
		if (PROFILE_NAME_SRGB.equals(filepath)) {
			ICC_Profile profile = ICC_Profile.getInstance(ICC_ColorSpace.CS_sRGB);
			return new ICC_ColorSpace(profile);
		}
		ICC_ColorSpace result = tryLoadingFromResource(filepath);
		if (result == null) {
			result = tryLoadingFromFile(filepath);
			if (result == null) {
				throw new BadRequestException("Cannot find profile '" + filepath + "'");
			}
		}
		return result;
	}

	private ICC_ColorSpace tryLoadingFromResource(String filepath) {
		ICC_ColorSpace result = null;
		if (colorProfilesResourcesPath != null) {
			InputStream inputStream = null;
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				inputStream = loader.getResourceAsStream(colorProfilesResourcesPath + "/" + filepath);
				if (inputStream != null) {
					ICC_Profile profile = ICC_Profile.getInstance(inputStream);
					result = new ICC_ColorSpace(profile);
				}
			} catch (IOException e) {
				// swallow exception to return null - and let caller throw bad request exception
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// fail on close quietly
					}
				}
			}
		}
		return result;
	}

	private ICC_ColorSpace tryLoadingFromFile(String filepath)  {
		ICC_ColorSpace result = null;
		if (colorProfilesFilesPath != null) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(colorProfilesFilesPath + "/" + filepath);
				ICC_Profile profile = ICC_Profile.getInstance(inputStream);
				result = new ICC_ColorSpace(profile);
			} catch (IOException e) {
				// swallow exception to return null - and let caller throw bad request exception
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// fail on close quietly
					}
				}
			}
		}
		return result;
	}

	private PooledObject<ICC_ColorSpace> wrap(ICC_ColorSpace colorSpace) {
		return new DefaultPooledObject<ICC_ColorSpace>(colorSpace);
	}

	@Override
	public PooledObject<ICC_ColorSpace> makeObject(String profile) throws Exception {
		return wrap(loadColorspace(profile));
	}

	@Override
	public boolean validateObject(String profile, PooledObject<ICC_ColorSpace> pooledObject) {
		return pooledObject != null && pooledObject.getObject() != null && (pooledObject.getObject() instanceof ICC_ColorSpace);
	}

	@Override
	public void destroyObject(String profile, PooledObject<ICC_ColorSpace> pooledObject) throws Exception {
		// no destruction of resource required
	}

	@Override
	public void activateObject(String profile, PooledObject<ICC_ColorSpace> pooledObject) throws Exception {
		// no activation of resource required
	}

	@Override
	public void passivateObject(String profile, PooledObject<ICC_ColorSpace> pooledObject) throws Exception {
		// no passivation of resource required
	}
}
