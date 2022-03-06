package edu.cornell.mannlib.vitro.webapp.dynapi.components;

import edu.cornell.mannlib.vitro.webapp.dynapi.io.OperationData;

public interface RunnableComponent extends Removable{
	
	OperationResult run(OperationData input);
	
}
