package edu.cornell.mannlib.vitro.webapp.dynapi;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.DefaultResourceAPI;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.ResourceAPI;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.ResourceAPIKey;

public class ResourcePool extends VersionableAbstractPool<ResourceAPIKey, ResourceAPI, ResourcePool> {

	private static ResourcePool INSTANCE = new ResourcePool();

	public static ResourcePool getInstance() {
		return INSTANCE;
	}

	@Override
	public ResourcePool getPool() {
		return getInstance();
	}

	@Override
	public ResourceAPI getDefault() {
		return new DefaultResourceAPI();
	}

	@Override
	public Class<ResourceAPI> getType() {
		return ResourceAPI.class;
	}

}
