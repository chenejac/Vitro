/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.searchindex.indexing;

import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Statement;

public class AdditionalURIsForDataProperties implements IndexingUriFinder {

    @Override
    public List<String> findAdditionalURIsToIndex(Statement stmt) {
        if (stmt != null && stmt.getObject().isLiteral() && stmt.getSubject().getURI() != null) {
            return Collections.singletonList(stmt.getSubject().getURI());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void startIndexing() { /* nothing to prepare */ }

    @Override
    public void endIndexing() { /* nothing to do */ }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
