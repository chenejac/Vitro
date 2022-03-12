package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;
import org.apache.jena.datatypes.RDFDatatype;

public interface Data {

    int IOUnknown = 0;
    int IOObject = 1;
    int IOArray = 2;
    int IOInteger = 3;
    int IODecimal = 4;
    int IOBoolean = 5;
    int IOString = 6;
    int IOAnyURI = 7;

    String getType();

    boolean checkType(ParameterType parameterType);

}
