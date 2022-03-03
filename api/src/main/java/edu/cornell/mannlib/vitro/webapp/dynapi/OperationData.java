package edu.cornell.mannlib.vitro.webapp.dynapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Action;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameter;
import edu.cornell.mannlib.vitro.webapp.dynapi.request.RequestPath;
import edu.cornell.mannlib.vitro.webapp.searchengine.elasticsearch.JsonTree;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class OperationData {

	private final Map<String, String[]> params;
	private Map<String, IOData> ioDataMap;
	private final ServletContext context;

	public OperationData(HttpServletRequest request, Action action) {
		params = request.getParameterMap();
		context = request.getServletContext();
		loadIODataMap(request, action);
	}

	private void loadIODataMap(HttpServletRequest request, Action action){
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
					IOData ioData = IODataLoader.load(fieldName, value, action.getRequiredParams());
					if (ioData != null)
						ioDataMap.put(fieldName, ioData);
				}
			}
		} catch (IOException e) {

		}

		if(ioDataMap.get("id") == null) {
			RequestPath requestPath = RequestPath.from(request);
			String resourceId = requestPath.getResourceId();
			if (resourceId != null) {
				IOData resourceIdData = IODataLoader.load("id", resourceId, action.getRequiredParams());
				if (resourceIdData != null)
					ioDataMap.put("id", resourceIdData);
			}
		}
	}

	public ServletContext getContext() {
		return context;
	}

	public boolean has(String paramName) {
		return ioDataMap.containsKey(paramName);
	}

	public String[] get(String paramName) {
		return params.get(paramName);
	}

}
