package edu.cornell.mannlib.vitro.webapp.dynapi.io.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ObjectParameterType;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;

public class ObjectData extends ContainerData<Map<String, Data>> {

    public ObjectData() {
        super(new HashMap<>());
    }

    public ObjectData(Map<String, Data> container) {
        super(container);
    }

    @Override
    public Data getElement(String fieldName) {
        if (!fieldName.contains(".")) {
            return container.get(fieldName);
        } else {
            String fieldNameFirstPart = fieldName.substring(0, fieldName.indexOf("."));
            String fieldNameSecondPart = fieldName.substring(fieldName.indexOf(".") + 1);
            Data data = container.get(fieldNameFirstPart);
            if (data instanceof ContainerData) {
                return ((ContainerData<?>) data).getElement(fieldNameSecondPart);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean setElement(String fieldName, Data newData) {
        if (!fieldName.contains(".")) {
            this.container.put(fieldName, newData);
            return true;
        } else {
            String fieldNameFirstPart = fieldName.substring(0, fieldName.indexOf("."));
            Data internalData = container.get(fieldNameFirstPart);
            String fieldNameOtherPart = fieldName.substring(fieldName.indexOf(".") + 1);
            String fieldNameSecondPart = (fieldNameOtherPart.contains("."))
                    ? fieldNameOtherPart.substring(0, fieldNameOtherPart.indexOf("."))
                    : fieldNameOtherPart;
            if (NumberUtils.isDigits(fieldNameSecondPart)) {
                if (!(internalData instanceof ArrayData)) {
                    internalData = new ArrayData();
                    container.put(fieldNameFirstPart, internalData);
                }
            } else {
                if (!(internalData instanceof ObjectData)) {
                    internalData = new ObjectData();
                    container.put(fieldNameFirstPart, internalData);
                }
            }
            return ((ContainerData<?>) internalData).setElement(fieldNameOtherPart, newData);
        }
    }

    public ObjectData filter(Set<String> fieldNames) {
        ObjectData retVal = new ObjectData();
        for (String prefix : fieldNames) {
            Data data = this.getElement(prefix);
            if (data != null) {
                retVal.setElement(prefix, data);
            }
        }
        return retVal;
    }

    @Override
    public RDFDatatype getRDFDataType() {
        return new XSDDatatype("object");
    }

    @Override
    public boolean checkType(ParameterType parameterType) {
        boolean retVal = true;
        for (String name : ((ObjectParameterType)parameterType).getInternalElements().getNames()){
            Data element = container.get(name);
            ParameterType internalParameterType = ((ObjectParameterType)parameterType).getInternalElements().get(name).getType();
            if ( (element == null) || (! (element.checkType(internalParameterType)))) {
                retVal = false;
                break;
            }
        }
        return retVal;
    }

}
