package edu.cornell.mannlib.vitro.webapp.dynapi.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IOArrayData implements IOData {

	private final List<IOData> values;

	public IOArrayData(ArrayNode arrayNode) {
		values = new ArrayList<IOData>();
		Iterator<JsonNode> itr = arrayNode.elements();
		while (itr.hasNext()) {
			JsonNode next = itr.next();
			values.add(IODataLoader.load(next));
		}
	}

	@Override
	public IOData get(String fieldName) {
		if (!fieldName.contains(".")){
			try{
				int index = Integer.parseInt(fieldName);
				return values.get(index);
			}
			catch (NumberFormatException ex){
				return null;
			}
		} else {
			String fieldNameFirstPart = fieldName.substring(0, fieldName.indexOf("."));
			String fieldNameSecondPart = fieldName.substring(fieldName.indexOf(".")+1);
			IOData ioData = null;
			try{
				int index = Integer.parseInt(fieldNameFirstPart);
				ioData = values.get(index);
			}
			catch (NumberFormatException ignored){
			}
			if (ioData != null)
				return ioData.get(fieldNameSecondPart);
			else
				return null;
		}
	}

	@Override
	public List<String> getAsString() {
		List<String> retVal = new ArrayList<String>();
		for (IOData item:values) {
			retVal.addAll(item.getAsString());
		}
		return retVal;
	}

	@Override
	public String toJsonString() {
		//TODO
		return null;
	}
}
