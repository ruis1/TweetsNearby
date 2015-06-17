package com.android.tweetsnearby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class ResToString {
	
	public static String getString(InputStream ins){
	BufferedReader reader = null;
    final StringBuilder sb = new StringBuilder();
    try {
        try {
            reader = new BufferedReader(new InputStreamReader(ins));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
        } finally {
            reader.close();
        }
    } catch (IOException e) {
        Log.v("breakpoint", "failed to open and read stream");
    }
    return sb.toString(); 
	
}
}
