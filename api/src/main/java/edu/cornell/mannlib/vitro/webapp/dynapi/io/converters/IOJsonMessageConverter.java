package edu.cornell.mannlib.vitro.webapp.dynapi.io.converters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.*;

public class IOJsonMessageConverter extends IOMessageConverter {

    private static final IOJsonMessageConverter INSTANCE = new IOJsonMessageConverter();

    public static IOJsonMessageConverter getInstance() {
        return INSTANCE;
    }

    private ObjectMapper mapper = new ObjectMapper();

    public ObjectData loadDataFromRequest(HttpServletRequest request) {
        Map<String, Data> ioDataMap = new HashMap<String, Data>();
        try {
            if (request.getReader() != null && request.getReader().lines() != null) {
                String requestData = request.getReader().lines().collect(Collectors.joining());
                JsonNode actualObj = mapper.readTree(requestData);
                Iterator<String> fieldNames = actualObj.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode value = actualObj.get(fieldName);
                    Data data = fromJson(value);
                    if (data != null) {
                        ioDataMap.put(fieldName, data);
                    }
                }
            }
        } catch (IOException ignored) {

        }

        return new ObjectData(ioDataMap);
    }

    public String exportDataToResponseBody(ObjectData data) {
        ObjectNode objectNode = mapper.createObjectNode();
        Map<String, Data> ioDataMap = data.getContainer();
        Iterator<String> fieldNames = ioDataMap.keySet().iterator();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode node = toJson(ioDataMap.get(fieldName));
            if (node != null) {
                objectNode.set(fieldName, node);
            }
        }
        return objectNode.toString();
    }

    public Data fromJson(JsonNode node) {
        Data retVal = null;
        switch (getDataType(node)) {
            case Data.IOObject:
                Map<String, Data> fields = new HashMap<String, Data>();
                Iterator<String> fieldNames = node.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode value = node.get(fieldName);
                    Data data = fromJson(value);
                    if (data != null) {
                        fields.put(fieldName, data);
                    }
                }
                retVal = new ObjectData(fields);
                break;
            case Data.IOArray:
                if (node instanceof ArrayNode) {
                    ArrayNode arrayNode = (ArrayNode) node;
                    List<Data> values = new ArrayList<Data>();
                    Iterator<JsonNode> itr = arrayNode.elements();
                    while (itr.hasNext()) {
                        JsonNode next = itr.next();
                        values.add(fromJson(next));
                    }
                    retVal = new ArrayData(values);
                }
                break;
            case Data.IOInteger:
                retVal = new IntegerData(node.asInt());
                break;
            case Data.IODecimal:
                retVal = new DecimalData(node.asDouble());
                break;
            case Data.IOBoolean:
                retVal = new BooleanData(node.asBoolean());
                break;
            case Data.IOString:
                retVal = new StringData(node.asText());
                break;
            case Data.IOAnyURI:
                retVal = new AnyURIData(node.asText());
                break;
        }
        return retVal;
    }

    public JsonNode toJson(Data data) {
        JsonNode retVal = null;
        switch (getDataType(data)) {
            case Data.IOObject:
                ObjectNode objectNode = mapper.createObjectNode();
                Map<String, Data> fields = ((ObjectData) data).getContainer();
                Iterator<String> fieldNames = fields.keySet().iterator();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode node = toJson(fields.get(fieldName));
                    if (node != null) {
                        objectNode.set(fieldName, node);
                    }
                }
                retVal = objectNode;
                break;
            case Data.IOArray:
                ArrayNode arrayNode = mapper.createArrayNode();
                List<Data> values = ((ArrayData) data).getContainer();
                for (Data value : values) {
                    JsonNode node = toJson(value);
                    arrayNode.add(node);
                }
                retVal = arrayNode;
                break;
            case Data.IOInteger:
                retVal = IntNode.valueOf(((IntegerData) data).getValue());
                break;
            case Data.IODecimal:
                retVal = DoubleNode.valueOf(((DecimalData) data).getValue());
                break;
            case Data.IOBoolean:
                retVal = BooleanNode.valueOf(((BooleanData) data).getValue());
                break;
            case Data.IOString:
            case Data.IOAnyURI:
                retVal = TextNode.valueOf(((StringData) data).getValue());
                break;
        }
        return retVal;
    }

    private int getDataType(JsonNode node) {
        if (node.isArray())
            return Data.IOArray;
        else if (node.isObject())
            return Data.IOObject;
        else if (node.isInt())
            return Data.IOInteger;
        else if (node.isDouble())
            return Data.IODecimal;
        else if (node.isBoolean())
            return Data.IOBoolean;
        else if (node.isTextual() && isURI(node.asText()))
            return Data.IOAnyURI;
        else if (node.isTextual())
            return Data.IOString;
        else
            return Data.IOUnknown;
    }

}
