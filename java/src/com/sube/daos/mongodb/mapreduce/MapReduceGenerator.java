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
