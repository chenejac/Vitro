/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.ABOX_UNION;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;

public class StandardModelSelector implements ModelSelector {
    public static final ModelSelector selector = new StandardModelSelector();
    private static final Log log = LogFactory.getLog(StandardModelSelector.class);

    @Override
    public OntModel getModel(HttpServletRequest request, ServletContext context) {
        return ModelAccess.on(context).getOntModel(ABOX_UNION);
    }

}
