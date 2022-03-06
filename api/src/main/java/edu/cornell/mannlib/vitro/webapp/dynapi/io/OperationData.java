package edu.cornell.mannlib.vitro.webapp.dynapi.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import edu.cornell.mannlib.vitro.webapp.dynapi.request.RequestPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class OperationData {

	private final Map<String, String[]> params;
	private final ServletContext context;
	private Map<String, IOData> ioDataMap;

	public OperationData(HttpServletRequest request) {
		params = request.getParameterMap();
		context = request.getServletContext();
		loadIODataMap(request);
	}

	private void loadIODataMap(HttpServletRequest request){
		ioDataMap = new HashMap<String, IOData>();
		try {
			if(request.getReader() != null && request.getReader().lines()!=null){
				String requestData = request.getReader().lines().collect(Collectors.joining());
				ObjectMapper mapper = new ObjectMapper();
				JsonNode actualObj = mapper.readTree(requestData);
				Iterator<String> fieldNames = actualObj.fieldNames();
				while (fieldNames.hasNext()) {
					String fieldName = fieldNames.next();
					JsonNode value = actualObj.get(fieldName);
					IOData ioData = IODataLoader.load(value);
					if (ioData != null)
						ioDataMap.put(fieldName, ioData);
				}
			}
		} catch (IOException ignored) {

		}

		if(ioDataMap.get("id") == null) {
			RequestPath requestPath = RequestPath.from(request);
			String resourceId = requestPath.getResourceId();
			if (resourceId != null) {
				IOData resourceIdData = IODataLoader.load(resourceId);
				if (resourceIdData != null)
					ioDataMap.put("id", resourceIdData);
			}
		}
	}

	public ServletContext getContext() {
		return context;
	}

	private IOData getIOData(String paramName){
		if (!paramName.contains(".")){
			return ioDataMap.get(paramName);
		} else {
			String fieldNameFirstPart = paramName.substring(0, paramName.indexOf("."));
			String fieldNameSecondPart = paramName.substring(paramName.indexOf(".")+1);
			IOData ioData = ioDataMap.get(fieldNameFirstPart);
			if (ioData != null)
				return ioData.get(fieldNameSecondPart);
			else
				return null;
		}
	}

	public boolean has(String paramName) {
		return (getIOData(paramName)!=null);
	}

	public String[] get(String paramName) {
		String[] retVal = new String[0];
		IOData ioData= getIOData(paramName);
		if (ioData != null){
			List<String> listString = ioData.getAsString();
			retVal = (listString != null)?listString.toArray(new String[0]):retVal;
		}
		return retVal;
	}

	public void add(String key, JsonNode node){
		if (node.getNodeType().equals(JsonNodeType.STRING))
			ioDataMap.put(key, new IOPrimitiveData(node.textValue()));
	}

}
