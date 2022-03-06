package edu.cornell.mannlib.vitro.webapp.dynapi.io;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

public class IOObjectData implements IOData {

	private final Map<String, IOData> fields;

	public IOObjectData(JsonNode node) {
		fields = new HashMap<String, IOData>();
		Iterator<String> fieldNames = node.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode value = node.get(fieldName);
			IOData ioData = IODataLoader.load(value);
			if (ioData != null)
				fields.put(fieldName, ioData);
		}
	}

	public IOData get(String fieldName) {
		if (!fieldName.contains(".")){
			return fields.get(fieldName);
		} else {
			String fieldNameFirstPart = fieldName.substring(0, fieldName.indexOf("."));
			String fieldNameSecondPart = fieldName.substring(fieldName.indexOf(".")+1);
			IOData ioData = fields.get(fieldNameFirstPart);
			if (ioData != null)
				return ioData.get(fieldNameSecondPart);
			else
				return null;
		}
	}

	@Override
	public List<String> getAsString() {
		List<String> retVal = new ArrayList<String>();
		for (IOData item: fields.values()) {
			retVal.addAll(item.getAsString());
		}
		return retVal;
	}

	@Override
	public String toJsonString() {
		//TODO
		return null;
	}
}
