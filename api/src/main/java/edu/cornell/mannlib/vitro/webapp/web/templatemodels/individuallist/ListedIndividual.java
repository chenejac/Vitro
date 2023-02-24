/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ListedIndividual extends BaseListedIndividual {

    private static final Log log = LogFactory.getLog(ListedIndividual.class);

    ListedIndividual(Individual individual, VitroRequest vreq) {
        super(individual, vreq);
    }
}
