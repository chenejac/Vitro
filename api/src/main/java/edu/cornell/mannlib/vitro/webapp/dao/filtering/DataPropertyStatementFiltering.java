/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.dao.filtering;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

public class DataPropertyStatementFiltering implements DataPropertyStatement {
    final DataPropertyStatement innerStmt;
    final VitroFilters filters;

    public DataPropertyStatementFiltering(DataPropertyStatement stmt, VitroFilters filters) {
        this.innerStmt = stmt;
        this.filters = filters;
    }

    /***** methods that return wrapped objects *****/
    /*
    public String getIndividual() {
        return new IndividualFiltering(innerStmt.getIndividual(),filters);
    } */

    /* ******** */
    public String toString() {
        return innerStmt.toString();
    }

    public Individual getIndividual() {
        return innerStmt.getIndividual();
    }

    public void setIndividual(Individual individual) {
        innerStmt.setIndividual(individual);
    }

    public String getIndividualURI() {
        return innerStmt.getIndividualURI();
    }

    public void setIndividualURI(String individualURI) {
        innerStmt.setIndividualURI(individualURI);
    }

    public String getLanguage() {
        return innerStmt.getLanguage();
    }

    public void setLanguage(String language) {
        innerStmt.setLanguage(language);
    }

    public String getData() {
        return innerStmt.getData();
    }

    public void setData(String data) {
        innerStmt.setData(data);
    }

    public String getDatatypeURI() {
        return innerStmt.getDatatypeURI();
    }

    public void setDatatypeURI(String URI) {
        innerStmt.setDatatypeURI(URI);
    }

    public String getDatapropURI() {
        return innerStmt.getDatapropURI();
    }

    public void setDatapropURI(String datapropURI) {
        innerStmt.setDatapropURI(datapropURI);
    }

    public String getString() {
        return innerStmt.getString();
    }

}
