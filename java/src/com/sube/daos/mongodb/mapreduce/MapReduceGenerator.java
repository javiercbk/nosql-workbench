/*******************************************************************************
 * Copyright 2012 Javier Ignacio Lecuona
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.sube.daos.mongodb.mapreduce;

import java.io.IOException;

import com.sube.utils.MongoQueryReader;

public class MapReduceGenerator {
	private MongoQueryReader queryReader;
	private String mapResource;
	private String reduceResource;

	public MapReduce generateMapReduce() throws IOException {
		MapReduce mapReduce = new MapReduce(queryReader.readQuery(mapResource)
				.toString(), queryReader.readQuery(reduceResource).toString());
		return mapReduce;
	}

	public void setQueryReader(MongoQueryReader queryReader) {
		this.queryReader = queryReader;
	}

	public void setMapResource(String mapResource) {
		this.mapResource = mapResource;
	}

	public void setReduceResource(String reduceResource) {
		this.reduceResource = reduceResource;
	}
}
