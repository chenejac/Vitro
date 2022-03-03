package edu.cornell.mannlib.vitro.webapp.dynapi;

import com.fasterxml.jackson.databind.JsonNode;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Action;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameters;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class IOObjectData extends IOData {

	private final Map<String, IOData> fields;

	public IOObjectData(String fieldNamePrefix, JsonNode node, Parameters requiredParameters) {
		fields = new HashMap<String, IOData>();
		Iterator<String> fieldNames = node.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode value = node.get(fieldName);
			IOData ioData = IODataLoader.load((fieldNamePrefix!=null)?fieldNamePrefix + "." + fieldName : fieldName, value, requiredParameters);
			if (ioData != null)
				fields.put(fieldName, ioData);
		}
	}

	public boolean has(String fieldName) {
		String[] fieldNames = fieldName.split("\\.");
		IOData ioData = this;
		boolean notFound = false;
		for (String name: fieldNames) {
			if ((ioData instanceof IOObjectData) && ((IOObjectData)ioData).has(name)) {
					ioData = ((IOObjectData)ioData).get(name);
			} else {
				notFound = true;
				break;
			}
		}
		return !notFound;
	}

	public IOData get(String fieldName) {
		return fields.get(fieldName);
	}

	public String[] getAsString(String fieldName){
		return fields.get(fieldName).getAsString();
	}

	@Override
	public String[] getAsString() {
		return new String[0];
	}
}
