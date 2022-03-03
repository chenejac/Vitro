package edu.cornell.mannlib.vitro.webapp.dynapi;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class IOArrayData extends IOData {

	private String[] values;

	public IOArrayData(String[] values) {
		this.values = values;
	}

	@Override
	public String[] getAsString() {
		return values;
	}
}
