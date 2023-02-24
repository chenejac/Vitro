/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.Property;
import org.apache.jena.ontology.OntModel;

/**
 * Should we allow the user to edit this ObjectPropertyStatement in this model?
 */
public class EditObjectPropertyStatement extends
    AbstractObjectPropertyStatementAction {
    public EditObjectPropertyStatement(OntModel ontModel, String subjectUri,
                                       Property keywordPred, String objectUri) {
        super(ontModel, subjectUri, keywordPred, objectUri);
    }
}
