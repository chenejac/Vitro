package edu.cornell.mannlib.vitro.webapp.dynapi.components.types;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameter;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.Property;
import org.apache.commons.lang3.math.NumberUtils;

public class ArrayParameterType extends ParameterType {

	private ParameterType elementsType = new PrimitiveParameterType();

	public ParameterType getElementsType() {
		return elementsType;
	}

	@Property(uri = "https://vivoweb.org/ontology/vitro-dynamic-api#hasElementsOfType", maxOccurs = 1)
	public void setElementsType(ParameterType elementsType) {
		this.elementsType = elementsType;
	}

	@Override
	public String computePrefix(String fieldName, String parameterName) {
		String retVal = "";
		String fieldNameFirstPart = fieldName.substring(0, fieldName.indexOf("."));
		String index = "notNumber";
		String fieldNameOtherPart = fieldName.substring(fieldName.indexOf(".") + 1);
		if (!(parameterName.equals(fieldNameFirstPart))) {
			retVal = parameterName;
			index = fieldNameFirstPart;
		} else {
			index = (fieldNameOtherPart.contains(".")) ? fieldNameOtherPart.substring(0, fieldNameOtherPart.indexOf(".")) : "notNumber";
		}
		if (NumberUtils.isDigits(index)) {
			ParameterType internalParameterType = this.getElementsType();
			if (fieldNameOtherPart.contains("."))
				fieldNameOtherPart = fieldNameOtherPart.substring(fieldName.indexOf(".") + 1);
			if (internalParameterType instanceof ObjectParameterType){
				ObjectParameterType objectType = (ObjectParameterType)internalParameterType;
				boolean exist = false;
				for (String internalFieldName : objectType.getInternalElements().getNames()) {
					Parameter internalParameter = objectType.getInternalElements().get(internalFieldName);
					String prefix = internalParameter.computePrefix(fieldNameOtherPart);
					if (prefix != null) {
						retVal += prefix;
						exist = true;
						break;
					}
				}
				if (!exist)
					retVal = null;
			}
			else
			if (! (internalParameterType instanceof PrimitiveParameterType))
				retVal = null;
		}
		else
			retVal = null;
		return (retVal!=null && retVal.length()>0)?retVal+=".":retVal;
	}
}
