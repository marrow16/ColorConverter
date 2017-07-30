/*
 * ColorSpacePool.java
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
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.springframework.stereotype.Service;

import java.awt.color.ICC_ColorSpace;

/**
 * Pool for the ICC_ColorSpace objects
 * It's really only the ICC_Profile that isn't thread safe and requires pooling
 * but also the performance of conversions by the actual ICC_ColorSpace is improved by
 * keeping them and re-using them (the first conversion requires the profile to be processed)
 */
@Service
public class ColorSpacePool {
	private GenericKeyedObjectPool<String,ICC_ColorSpace> pool;

	public ColorSpacePool(Configuration config,
						  PooledColorSpaceFactory pooledColorSpaceFactory) {
		pool = new GenericKeyedObjectPool<String,ICC_ColorSpace>(pooledColorSpaceFactory, config.getColorSpacePoolConfig());
	}

	public ICC_ColorSpace get(String profile) throws Exception {
		return pool.borrowObject(profile);
	}

	public void release(String profile, ICC_ColorSpace colorSpace) {
		pool.returnObject(profile, colorSpace);
	}
}
