/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.beans;

public interface PropertyInstanceIface {
    //needed for PropertyInstance
//object property statements
    public abstract String getPropertyURI();

    /******************* setters ************************/

    public abstract void setPropertyURI(String in);

    public abstract String getObjectEntURI();

    public abstract void setObjectEntURI(String in);

    public abstract String getSubjectEntURI();

    public abstract void setSubjectEntURI(String in);

    //  entities
    public abstract String getSubjectName();

    public abstract void setSubjectName(String in);

    public abstract String getObjectName();

    public abstract void setObjectName(String in);

    //needed for Any Property
//properties
    public abstract String getPropertyName();

    public abstract void setPropertyName(String in);

    public abstract String getDomainPublic();

    public abstract void setDomainPublic(String in);

    public abstract String getRangePublic();

    public abstract void setRangePublic(String in);

    //classs
    public abstract String getDomainClassName();

    public abstract void setDomainClassName(String in);

    public abstract String getRangeClassName();

    public abstract void setRangeClassName(String in);

    public abstract String getDomainQuickEditJsp();

    public abstract void setDomainQuickEditJsp(String in);

    public abstract String getRangeQuickEditJsp();

    public abstract void setRangeQuickEditJsp(String in);

    //classs2relations
    public abstract String getRangeClassURI();

    public abstract void setRangeClassURI(String in);

    public abstract String getDomainClassURI();

    public abstract void setDomainClassURI(String in);

    public abstract boolean getSubjectSide();

    public abstract void setSubjectSide(boolean in);

}
