package edu.cornell.mannlib.vitro.webapp.dynapi;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.Action;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.DefaultAction;
import edu.cornell.mannlib.vitro.webapp.dynapi.validator.ModelValidator;
import edu.cornell.mannlib.vitro.webapp.dynapi.validator.SHACLActionBeanValidator;
import edu.cornell.mannlib.vitro.webapp.dynapi.validator.SHACLBeanValidator;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ConfigurationBeanLoader;
import org.apache.jena.rdf.model.Model;

import javax.servlet.ServletContext;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.FULL_UNION;
import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.TBOX_ASSERTIONS;

public class ActionPool extends AbstractPool<String, Action, ActionPool> {

    private static ActionPool INSTANCE = new ActionPool();

    public static ActionPool getInstance() {
        return INSTANCE;
    }

    @Override
    public ActionPool getPool() {
        return getInstance();
    }

    @Override
    public Action getDefault() {
        return new DefaultAction();
    }

    @Override
    public Class<Action> getType() {
        return Action.class;
    }

    @Override
    public ModelValidator getValidator(Model data, Model scheme) {
        return new SHACLActionBeanValidator(data, scheme);
    }
}
