/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebServlet(name = "SiteAdminController", urlPatterns = {"/siteAdmin", "/siteAdmin.jsp"})
public class SiteAdminController extends BaseSiteAdminController {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SiteAdminController.class);

}
