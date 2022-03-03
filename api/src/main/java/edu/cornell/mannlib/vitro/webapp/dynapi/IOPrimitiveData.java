package edu.cornell.mannlib.vitro.webapp.dynapi;

import com.fasterxml.jackson.databind.JsonNode;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameter;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameters;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.validators.Validator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IOPrimitiveData implements IOData {

	private String value;

	public IOPrimitiveData(String value) {
		this.value = value;
	}

	@Override
	public IOData get(String fieldName) {
		return null;
	}

	@Override
	public List<String> getAsString() {
		List<String> retVal = new ArrayList<String>();
		retVal.add(value);
		return retVal;
	}

	@Override
	public String toJsonString() {
		//TODO to decouple primitive types in hierarchy of types (integer, decimal, string, boolean)
		return "\"" + value + "\"";
	}
}
