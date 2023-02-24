/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Html2Text extends HTMLEditorKit.ParserCallback {
    private static final Log log = LogFactory.getLog(Html2Text.class.getName());
    StringBuffer s;

    public Html2Text() {
    }

    public void parse(Reader in) throws IOException {
        s = new StringBuffer();
        ParserDelegator delegator = new ParserDelegator();
        // the third parameter is TRUE to ignore charset directive
        delegator.parse(in, this, Boolean.TRUE);
    }

    public void parse(String in) throws IOException {
        Reader r = new StringReader(in);
        try {
            parse(r);
        } catch (IOException e) {
            log.error("could not strip html", e);
        } finally {
            r.close();
        }
    }

    public void handleText(char[] text, int pos) {
        s.append(text);
    }

    public String getText() {
        return s.toString();
    }

    public String stripHtml(String in) {
        try {
            parse(in);
        } catch (IOException e) {
            log.debug("could not strip html", e);
        }
        return getText();
    }
}
