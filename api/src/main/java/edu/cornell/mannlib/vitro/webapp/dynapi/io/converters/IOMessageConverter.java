package edu.cornell.mannlib.vitro.webapp.dynapi.io.converters;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.Parameters;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.*;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class IOMessageConverter {

    public abstract Data loadDataFromRequest(HttpServletRequest request, Parameters parameters);

    public abstract String exportDataToResponseBody(ObjectData data);


}
