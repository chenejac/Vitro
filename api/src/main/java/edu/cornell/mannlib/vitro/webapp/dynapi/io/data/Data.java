package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;

public interface Data {

    String getType();

    boolean checkType(ParameterType parameterType);

}
