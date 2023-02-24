/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.rdf.model.Model;

public interface ModelChangePreprocessor {

    public abstract void preprocess(Model retractionsModel, Model additionsModel,
                                    HttpServletRequest request);

}
