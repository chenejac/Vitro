/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import org.apache.jena.ontology.OntModel;

/**
 * Should we allow the user to add this DataPropertyStatement to this model?
 */
public class AddDataPropertyStatement extends
    AbstractDataPropertyStatementAction {

    public AddDataPropertyStatement(OntModel ontModel, String subjectUri,
                                    String predicateUri, String dataValue) {
        super(ontModel, subjectUri, predicateUri, dataValue);
    }

    public AddDataPropertyStatement(OntModel ontModel, DataPropertyStatement dps) {
        super(ontModel, dps);
    }

}
