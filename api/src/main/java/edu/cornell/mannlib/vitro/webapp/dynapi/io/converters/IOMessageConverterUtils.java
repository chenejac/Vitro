package edu.cornell.mannlib.vitro.webapp.dynapi.io.converters;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class IOMessageConverterUtils {

    public static boolean isURI(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static PrimitiveData getPrimitiveDataFromString(String text){
        PrimitiveData retVal = null;
        if (text != null){
            if (NumberUtils.isDigits(text))
                retVal = new IntegerData(Integer.parseInt(text));
            else if (NumberUtils.isParsable(text))
                retVal = new DecimalData(Double.parseDouble(text));
            else if (IOMessageConverterUtils.isURI(text))
                retVal = new AnyURIData(text);
            else if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false"))
                retVal = new BooleanData(Boolean.valueOf(text));
            else
                retVal = new StringData(text);
        }

        return retVal;
    }

    public static PrimitiveData getPrimitiveDataFromString(String text, ParameterType type){
        PrimitiveData retVal = null;
        if (text != null){
            if (type == null)
                retVal = getPrimitiveDataFromString(text);
            else if (NumberUtils.isDigits(text) && new IntegerData(Integer.parseInt(text)).checkType(type))
                retVal = new IntegerData(Integer.parseInt(text));
            else if (NumberUtils.isParsable(text) && new DecimalData(Double.parseDouble(text)).checkType(type))
                retVal = new DecimalData(Double.parseDouble(text));
            else if (IOMessageConverterUtils.isURI(text) && new AnyURIData(text).checkType(type))
                retVal = new AnyURIData(text);
            else if ((text.equalsIgnoreCase("true") || text.equalsIgnoreCase("false")) && new BooleanData(Boolean.valueOf(text)).checkType(type))
                retVal = new BooleanData(Boolean.valueOf(text));
            else if (new StringData(text).checkType(type))
                retVal = new StringData(text);
        }

        return retVal;
    }

}
