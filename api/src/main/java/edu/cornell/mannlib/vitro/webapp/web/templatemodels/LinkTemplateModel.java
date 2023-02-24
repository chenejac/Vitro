/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import org.apache.commons.lang3.StringEscapeUtils;

public class LinkTemplateModel extends BaseTemplateModel {

    private String url;
    private String text;

    public LinkTemplateModel() {
    }

    public LinkTemplateModel(String text, String path) {
        setText(text);
        setUrl(path);
    }

    public LinkTemplateModel(String text, String path, String... params) {
        setText(text);
        setUrl(path, params);
    }

    public LinkTemplateModel(String text, String path, ParamMap params) {
        setText(text);
        setUrl(path, params);
    }

    protected void setUrl(String path, String... params) {
        url = UrlBuilder.getUrl(path, params);
    }

    protected void setUrl(String path, ParamMap params) {
        url = UrlBuilder.getUrl(path, params);
    }

    public String getUrl() {
        return cleanTextForDisplay(url);
    }

    protected void setUrl(String path) {
        url = UrlBuilder.getUrl(path);
    }

    /* Template properties */

    public String getText() {
        return cleanTextForDisplay(text);
    }

    protected void setText(String text) {
        this.text = StringEscapeUtils.ESCAPE_HTML4.translate(text);
    }
}
