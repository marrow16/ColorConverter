/*
 * LRUCache.java
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
package com.adeptions.colors.domain.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class LRUCache<K,V> {
	final Map<K,V> MRUdata;
	final Map<K,V> LRUdata;

	public LRUCache(final int capacity) {
		LRUdata = new WeakHashMap<K, V>();
		MRUdata = new LinkedHashMap<K, V>(capacity+1, 1.0f, true) {
			protected boolean removeEldestEntry(Map.Entry<K,V> entry) {
				if (this.size() > capacity) {
					LRUdata.put(entry.getKey(), entry.getValue());
					return true;
				}
				return false;
			};
		};
	}

	public synchronized V get(K key) {
		V value = MRUdata.get(key);
		if (value!=null)
			return value;
		value = LRUdata.get(key);
		if (value!=null) {
			LRUdata.remove(key);
			MRUdata.put(key, value);
		}
		return value;
	}

	public synchronized void put(K key, V value) {
		LRUdata.remove(key);
		MRUdata.put(key, value);
	}
}
