package edu.cornell.mannlib.vitro.webapp.dynapi.components;

import edu.cornell.mannlib.vitro.webapp.utils.configuration.Property;

public class ObjectParameterType extends ParameterType {

    private Parameters internalElements = new Parameters();

    @Property(uri = "https://vivoweb.org/ontology/vitro-dynamic-api#hasInternalElement")
    public void addInternalElement(Parameter param) {
        internalElements.add(param);
    }

    public Parameters getInternalElements() {
        return internalElements;
    }

}
