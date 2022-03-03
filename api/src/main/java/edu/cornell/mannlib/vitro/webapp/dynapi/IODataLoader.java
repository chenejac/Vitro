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

	public static IOData load(JsonNode value){
		IOData retVal = null;
		switch (getDataType(value.getNodeType())){
			case IOObject:  retVal = new IOObjectData(value);
							break;
			case IOArray: 	if (value instanceof ArrayNode){
								ArrayNode arrayNode = (ArrayNode) value;
								Iterator<JsonNode> itr = arrayNode.elements();
								retVal = new IOArrayData(arrayNode);
							}
							break;
			case IOPrimitive: retVal = new IOPrimitiveData(value.textValue());
		}
		return retVal;
	}

	public static IOData load(String value){
		IOData retVal = null;
		switch (getDataType(JsonNodeType.STRING)){
			case IOPrimitive: retVal = new IOPrimitiveData(value);
		}
		return retVal;
	}


	private static int getDataType(JsonNodeType nodeType){
		if (nodeType.equals(JsonNodeType.ARRAY))
			return IOArray;
		else if (nodeType.equals(JsonNodeType.OBJECT))
			return IOObject;
		else if (nodeType.equals(JsonNodeType.STRING) || nodeType.equals(JsonNodeType.NUMBER) || nodeType.equals(JsonNodeType.BOOLEAN))
			return IOPrimitive;
		else
			return IOUnknown;
	}



}
