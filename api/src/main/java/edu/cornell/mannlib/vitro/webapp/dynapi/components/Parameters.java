package edu.cornell.mannlib.vitro.webapp.dynapi.components;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ArrayParameterType;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ObjectParameterType;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Parameters implements Removable {

    private Map<String, Parameter> params;

    public Parameters() {
        params = new HashMap<String, Parameter>();
    }

    public void add(Parameter param) {
        params.put(param.getName(), param);
    }

    public Set<String> getNames() {
        return params.keySet();
    }

    public Parameter get(String name) {
        if (!name.contains(".")) {
            return params.get(name);
        } else {
            String fieldNameFirstPart = name.substring(0, name.indexOf("."));
            String fieldNameSecondPart = name.substring(name.indexOf(".") + 1);
            Parameter parameter = params.get(fieldNameFirstPart);
            if (parameter.getType() instanceof ObjectParameterType) {
                return ((ObjectParameterType) parameter.getType()).getInternalElements().get(fieldNameSecondPart);
            } else if (parameter.getType() instanceof ArrayParameterType) {
                String fieldNameOtherPart = fieldNameSecondPart.substring(fieldNameSecondPart.indexOf(".") + 1);
                if (StringUtils.isEmpty(fieldNameOtherPart))
                    return parameter;
                else {
                    ParameterType internalArrayParameterType = ((ArrayParameterType) parameter.getType()).getElementsType();
                    if (internalArrayParameterType instanceof ObjectParameterType)
                        return ((ObjectParameterType) internalArrayParameterType).getInternalElements().get(fieldNameOtherPart);
                }
            }
        }
        return null;
    }

    @Override
    public void dereference() {
        for (String name : params.keySet()) {
            params.get(name).dereference();
        }
        params = null;
    }

}
