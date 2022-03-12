package edu.cornell.mannlib.vitro.webapp.dynapi.io.converters;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.*;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class IOMessageConverter {

    public abstract Data loadDataFromRequest(HttpServletRequest request);

    public abstract String exportDataToResponseBody(ObjectData data);

    protected int getDataType(Data data) {
        if (data instanceof ArrayData)
            return Data.IOArray;
        else if (data instanceof ObjectData)
            return Data.IOObject;
        else if (data instanceof IntegerData)
            return Data.IOInteger;
        else if (data instanceof DecimalData)
            return Data.IODecimal;
        else if (data instanceof BooleanData)
            return Data.IOBoolean;
        else if (data instanceof AnyURIData)
            return Data.IOAnyURI;
        else if (data instanceof StringData)
            return Data.IOString;
        else
            return Data.IOUnknown;
    }

    protected boolean isURI(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }


}
