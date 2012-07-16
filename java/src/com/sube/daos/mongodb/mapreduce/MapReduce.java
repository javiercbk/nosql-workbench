package com.sube.daos.mongodb.mapreduce;

public class MapReduce {
	private String mapExpression;
	private String reduceExpression;
	
	public MapReduce(String mapExpression, String reduceExpression){
		this.mapExpression = mapExpression;
		this.reduceExpression = reduceExpression;
	}
	
	public String getMapExpression() {
		return mapExpression;
	}
	public void setMapExpression(String mapExpression) {
		this.mapExpression = mapExpression;
	}
	public String getReduceExpression() {
		return reduceExpression;
	}
	public void setReduceExpression(String reduceExpression) {
		this.reduceExpression = reduceExpression;
	}
}
