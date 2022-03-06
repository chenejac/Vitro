package edu.cornell.mannlib.vitro.webapp.dynapi.components;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.IOData;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.IOPrimitiveData;
import org.apache.commons.lang3.Range;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class OperationResult {
	
	private int responseCode;
	private static Range<Integer> errors = Range.between(400, 599);
	private Map<String, IOData> ioDataMap;

	public OperationResult(int responseCode) {
		this.responseCode = responseCode;
		ioDataMap = new HashMap<String, IOData>();
	}
	
	public boolean hasError() {
		if (errors.contains(responseCode) ) {
			return true;
		}
		return false;
	}

	public void prepareResponse(HttpServletResponse response) {
		response.setStatus(responseCode);
		if(responseCode >= 200 && responseCode < 300){
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			if (out != null){
				out.print(jsonResults());
				out.flush();
			}
		}
	}

	public void addResult(String key, JsonNode node){
		if (node.getNodeType().equals(JsonNodeType.STRING))
			ioDataMap.put(key, new IOPrimitiveData(node.textValue()));
	}

	private String jsonResults(){
		StringBuffer retVal = new StringBuffer();
		retVal.append("{\n");
		for (String key:ioDataMap.keySet()) {
			retVal.append("\"" + key + "\" : " + ioDataMap.get(key).toJsonString() + "\n");
		}
		retVal.append("}");
		return retVal.toString();
	}

	public static OperationResult notImplemented() {
		return new OperationResult(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	public static OperationResult notFound() {
		return new OperationResult(HttpServletResponse.SC_NOT_FOUND);
	}
	
}
