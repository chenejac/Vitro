package edu.cornell.mannlib.vitro.webapp.dynapi.io;

import java.util.List;

public interface IOData {

	List<String> getAsString();

	IOData get(String fieldName);

	String toJsonString();
}
