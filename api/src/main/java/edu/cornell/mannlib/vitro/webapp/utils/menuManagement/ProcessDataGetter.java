/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils.menuManagement;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface ProcessDataGetter {
    public void populateTemplate(HttpServletRequest req, Map<String, Object> pageData,
                                 Map<String, Object> templateData);

    public Model processSubmission(VitroRequest vreq, Resource dataGetterResource);


}
