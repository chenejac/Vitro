/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.Property;
import org.apache.jena.ontology.OntModel;

/**
 * Should we allow the user to add this ObjectPropertyStatement to this model?
 */
public class AddObjectPropertyStatement extends
    AbstractObjectPropertyStatementAction {
    public AddObjectPropertyStatement(OntModel ontModel, String uriOfSub,
                                      Property predicate, String uriOfObj) {
        super(ontModel, uriOfSub, predicate, uriOfObj);
    }

}
