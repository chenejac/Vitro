package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;

public class DecimalData extends PrimitiveData<Double> {

    public DecimalData(Double value) {
        super(value);
    }

    @Override
    public RDFDatatype getRDFDataType() {
        return new XSDDatatype("decimal");
    }

}
