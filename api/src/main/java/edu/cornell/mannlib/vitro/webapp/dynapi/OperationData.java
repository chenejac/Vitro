package edu.cornell.mannlib.vitro.webapp.dynapi;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameters;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.converters.IOJsonMessageConverter;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.converters.IOParametersMessageConverter;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.Data;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.ObjectData;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.StringData;
import edu.cornell.mannlib.vitro.webapp.dynapi.request.ApiRequestPath;

public class OperationData {

    public final static String RESOURCE_ID = "resource_id";

    private final Map<String, String[]> params;
    private final ServletContext context;
    private ObjectData data;

    public OperationData(HttpServletRequest request, Parameters parameters) {
        params = request.getParameterMap();
        context = request.getServletContext();
        // if (ContentType.APPLICATION_JSON.toString().equalsIgnoreCase(request.getContentType()))
        //   data = IOJsonMessageConverter.getInstance().loadDataFromRequest(request);
        // else
        //   data = IOParametersMessageConverter.getInstance().loadDataFromRequest(request);
        data = IOJsonMessageConverter.getInstance().loadDataFromRequest(request, parameters);
        if ((data == null) || (data.getContainer().size() == 0)) {
            data = IOParametersMessageConverter.getInstance().loadDataFromRequest(request, parameters);
        }
        addRequestPathParameters(request);
    }

    private void addRequestPathParameters(HttpServletRequest request) {
        if (data == null) {
            data = new ObjectData();
        }
        Map<String, Data> ioDataMap = data.getContainer();
        if (ioDataMap.get(OperationData.RESOURCE_ID) == null) {
            ApiRequestPath requestPath = ApiRequestPath.from(request);
            String resourceId = requestPath.getResourceId();
            if (resourceId != null) {
                ioDataMap.put(OperationData.RESOURCE_ID, new StringData(resourceId));
            }
        }
    }

    public ServletContext getContext() {
        return context;
    }

    public ObjectData getRootData() {
        return data;
    }

    public Data getData(String paramName) {
        return data.getElement(paramName);
    }

    public boolean has(String paramName) {
        return getData(paramName) != null;
    }

    public String get(String paramName) {
        String retVal = null;
        Data internalData = getData(paramName);
        if (internalData != null) {
            retVal = internalData.toString();
        }
        return retVal;
    }

    public void add(String key, Data newData) {
        data.setElement(key, newData);
    }

}
