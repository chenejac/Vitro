package edu.cornell.mannlib.vitro.webapp.dynapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameter;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IODataLoader {

	protected final static int IOUnknown 	= 0;
	protected final static int IOObject 	= 1;
	protected final static int IOArray 		= 2;
	protected final static int IOPrimitive 	= 3;

	public static IOData load(String fieldName, JsonNode value, Parameters parameters){
		IOData retVal = null;
		switch (getDataType(fieldName, value.getNodeType(), parameters)){
			case IOObject:  retVal = new IOObjectData(fieldName, value, parameters);
							break;
			case IOArray: 	if (value instanceof ArrayNode){
								ArrayNode arrayNode = (ArrayNode) value;
								Iterator<JsonNode> itr = arrayNode.elements();
								List<String> stringList = new ArrayList<String>();
								while (itr.hasNext()) {
									JsonNode next = itr.next();
									stringList.add(next.textValue());
								}
								retVal = new IOArrayData(stringList.toArray(new String[stringList.size()]));
							}
							break;
			case IOPrimitive: retVal = new IOPrimitiveData(value.textValue());
		}
		return retVal;
	}

	public static IOData load(String fieldName, String value, Parameters parameters){
		IOData retVal = null;
		switch (getDataType(fieldName, JsonNodeType.STRING, parameters)){
			case IOPrimitive: retVal = new IOPrimitiveData(value);
		}
		return retVal;
	}


	private static int getDataType(String fieldName, JsonNodeType nodeType, Parameters parameters){
		Parameter parameter = parameters.findParameter(fieldName);
		if (parameter != null) {
			String type = parameter.getRDFDataType().toString();
			if (nodeType.equals(JsonNodeType.ARRAY) && type.contains("array"))
				return IOArray;
			else if (nodeType.equals(JsonNodeType.OBJECT) && type.contains("object"))
				return IOObject;
			else if ((nodeType.equals(JsonNodeType.STRING) || nodeType.equals(JsonNodeType.NUMBER) || nodeType.equals(JsonNodeType.BOOLEAN)) && (type.contains("integer") || type.contains("decimal") || type.contains("boolean") || type.contains("string")))
				return IOPrimitive;
		}
		return IOUnknown;
	}



}
