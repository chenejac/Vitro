package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;

public class BooleanData extends PrimitiveData<Boolean> {

    public BooleanData(Boolean value) {
        super(value);
    }

    @Override
    public RDFDatatype getRDFDataType() {
        return new XSDDatatype("boolean");
    }
}
