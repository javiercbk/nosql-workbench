package com.sube.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MongoQueryReader {
	
	public StringBuffer readQuery(String resourcePath) throws IOException{
		StringBuffer fileData = new StringBuffer(1000);
		InputStream inputStream = this.getClass().getResourceAsStream(resourcePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData;
	}
}
