package edu.cornell.mannlib.vitro.webapp.dynapi;

import com.fasterxml.jackson.databind.JsonNode;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameter;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameters;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.validators.Validator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class IOPrimitiveData extends IOData {

	private String value;

	public IOPrimitiveData(String value) {
		this.value = value;
	}

	@Override
	public String[] getAsString() {
		return new String[]{value};
	}
}
