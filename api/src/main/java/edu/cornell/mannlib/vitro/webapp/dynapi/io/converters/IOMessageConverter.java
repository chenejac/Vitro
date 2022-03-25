package edu.cornell.mannlib.vitro.webapp.dynapi.io.converters;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.*;

public abstract class IOMessageConverter {

    public abstract Data loadDataFromRequest(HttpServletRequest request);

    public abstract String exportDataToResponseBody(ObjectData data);


}
