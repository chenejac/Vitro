/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vedit.validator.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import edu.cornell.mannlib.vedit.validator.ValidationObject;
import edu.cornell.mannlib.vedit.validator.Validator;

public class EnumValuesValidator implements Validator {

    private HashSet legalValues = new HashSet();

    public EnumValuesValidator(String[] legalValues) {
        Collections.addAll(this.legalValues, legalValues);
    }

    public ValidationObject validate(Object obj) {
        ValidationObject vo = new ValidationObject();
        if (legalValues.contains((String) obj)) {
            vo.setValid(true);
        } else {
            vo.setValid(false);
            if (legalValues.size() < 7) {
                StringBuilder msgString = new StringBuilder("Please enter one of ");
                Iterator valuesIt = legalValues.iterator();
                while (valuesIt.hasNext()) {
                    String legalValue = (String) valuesIt.next();
                    msgString.append("'").append(legalValue).append("'");
                    if (valuesIt.hasNext()) {
                        msgString.append(", ");
                    } else {
                        msgString.append(".");
                    }
                }
                vo.setMessage(msgString.toString());
            } else {
                vo.setMessage("Please enter a legal value.");
            }
        }
        vo.setValidatedObject(obj);
        return vo;
    }
}
