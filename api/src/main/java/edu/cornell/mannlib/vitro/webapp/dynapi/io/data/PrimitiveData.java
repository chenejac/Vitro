package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;
import org.apache.jena.datatypes.RDFDatatype;

import java.util.ArrayList;
import java.util.List;

public abstract class PrimitiveData<T> implements Data {

    protected T value;

    protected PrimitiveData(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean checkType(ParameterType parameterType) {
        return parameterType.getRDFDataType().toString().equals(getRDFDataType().toString());
    }
}
