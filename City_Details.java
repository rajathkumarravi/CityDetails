import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.json.*;

public class City_Details {

	// Fires the URL and fetches the result as a String
	private static String readUrl(String myURL) throws Exception {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
		} 
 
		return sb.toString();
	}
	
	// Convert the String into a JSON Array
	public static JSONArray getJSonFromString(String result) {
		JSONArray json_arr = new JSONArray(result);
		return json_arr;
	}
	
	// Write the Headers into the CSV File
	public static void write_csv_header(FileWriter out_csv) throws IOException {
		out_csv.append("ID");
        out_csv.append(",");
        out_csv.append("Type");
        out_csv.append(",");
        out_csv.append("Name");
        out_csv.append(",");
        out_csv.append("Latitude");
        out_csv.append(",");
        out_csv.append("Longitude");
        out_csv.append("\n");
	}
	
	// Write the required contents of JSON into the CSV File
	public static int write_csv_contents (JSONArray json_arr, FileWriter out_csv) throws IOException {
		String []keys = {"_id", "type", "name", "geo_position"};
	    String []sub_keys = {"latitude", "longitude"};
	    
	    if (json_arr.length() == 0)
	    {
	    	return 0;
	    }
	        
        for (int i=0 ; i< json_arr.length() ; i++) {
        	JSONObject json = (JSONObject)json_arr.get(i);
        	
        	for (int j=0 ; j< keys.length ; j++) {
        		if (j != keys.length-1) {
        			String val = (json.get(keys[j])).toString();
        			out_csv.append(val);
        			out_csv.append(",");
        		}
        		else {
        			JSONObject json_1 = (JSONObject)(json.get(keys[j]));
        			String val = json_1.get(sub_keys[0]).toString();
        			out_csv.append(val);
        			out_csv.append(",");
        			val = json_1.get(sub_keys[1]).toString();
        			out_csv.append(val);
        			out_csv.append("\n");
        		}
        	}
        }
        return 1;
	}
	
	// Converts JSON Array into a CSV File
	public static int writeJsonToCsv(JSONArray json_arr) throws IOException {
		FileWriter out_csv = null;
        out_csv = new FileWriter("City_Details.csv");
        write_csv_header(out_csv);
        int ret_val = write_csv_contents(json_arr, out_csv);
        out_csv.close();
        return ret_val;
	}

	// Main Function
    public static void main(String args[]) throws Exception {
	    String url = "http://api.goeuro.com/api/v2/position/suggest/en/" + args[0];
	    String output = readUrl(url);
	    JSONArray json_arr = getJSonFromString(output);
	    int ret_val = writeJsonToCsv(json_arr);   
	    
	    if (ret_val == 0) {
	    	System.out.println("City '" + args[0] + "' not found!!");
	    }
	    else {
	    	System.out.println("City '" + args[0] + "' found!!");
	    }
	    
    }
}

