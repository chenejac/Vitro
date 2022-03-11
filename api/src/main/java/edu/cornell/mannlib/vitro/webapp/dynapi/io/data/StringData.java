package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;


public class StringData extends PrimitiveData<String> {

    public StringData(String value) {
        super(value);
    }

    @Override
    public RDFDatatype getRDFDataType() {
        return (isAnyURI()) ? new XSDDatatype("anyURI") : new XSDDatatype("string");
    }

    private boolean isAnyURI(){
        return false;
    }

}
