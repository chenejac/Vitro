package edu.cornell.mannlib.vitro.webapp.dynapi.components;

import edu.cornell.mannlib.vitro.webapp.utils.configuration.Property;

public class RPC implements Removable{

	private String name;
	private String minVersion;
	private String maxVersion;
	private HTTPMethod httpMethod;

	public HTTPMethod getHttpMethod() {
		return httpMethod;
	}


	@Property(uri = "https://vivoweb.org/ontology/vitro-dynamic-api#name", minOccurs = 1, maxOccurs = 1)
	public void setName(String name) {
		this.name = name;
	}
	
	@Property(uri = "https://vivoweb.org/ontology/vitro-dynamic-api#minAPIVersion", minOccurs = 0, maxOccurs = 1)
	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}
	
	@Property(uri = "https://vivoweb.org/ontology/vitro-dynamic-api#maxAPIVersion", minOccurs = 0, maxOccurs = 1)
	public void setMaxVersion(String maxVersion) {
		this.maxVersion  = maxVersion;
	}

	public String getName() {
		return name;
	}
	
	@Property(uri = "https://vivoweb.org/ontology/vitro-dynamic-api#hasDefaultMethod")
	public void setHttpMethod(HTTPMethod httpMethod) {
		this.httpMethod  = httpMethod;
	}

	public String getMinVersion() {
		return minVersion;
	}

	public String getMaxVersion() {
		return maxVersion;
	}
	
	@Override
	public void dereference() {
		// TODO Auto-generated method stub
	}
}
