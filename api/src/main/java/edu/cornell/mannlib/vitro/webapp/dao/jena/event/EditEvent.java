/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

public class EditEvent {

    private static final String EDIT_EVENT = VitroVocabulary.EDIT_EVENT;
    private static final String EDIT_EVENT_AGENT = VitroVocabulary.EDIT_EVENT_AGENT;
    protected List<String> eventTypeURIList;
    protected Map<String, List<RDFNode>> propertyMap;
    private Boolean begin;
    private String userURI;

    public EditEvent(String userURI, boolean begin) {
        this.begin = begin;
        this.userURI = userURI;
    }

    public Map<String, List<RDFNode>> getPropertyMap() {
        this.propertyMap = new HashMap<String, List<RDFNode>>();
        List<RDFNode> agentValueList = new LinkedList<RDFNode>();
        agentValueList.add(ResourceFactory.createResource(userURI));
        this.propertyMap.put(EDIT_EVENT_AGENT, agentValueList);
        return propertyMap;
    }

    public Boolean getBegin() {
        return this.begin;
    }

    public List<String> getEventTypeURIList() {
        return eventTypeURIList;
    }

}
