/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test of dump directives
 *
 * @author rjy7
 */
public class DumpTestController extends FreemarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(TestController.class);
    private static final String TEMPLATE_DEFAULT = "test.ftl";

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        body.put("title", "Freemarker Test");

        body.put("dog", "Rover");
        body.put("int", 7);
        body.put("bool", false);
        body.put("now", new Date());

        java.sql.Date date = new java.sql.Date(1302297332043L);
        body.put("date", date);

        Time time = new Time(1302297332043L);
        body.put("time", time);

        Timestamp ts = new Timestamp(1302297332043L);
        body.put("timestamp", ts);

        // List of strings
        List<String> fruit = new ArrayList<String>();
        fruit.add("apples");
        fruit.add("bananas");
        fruit.add("peaches");
        body.put("fruit", fruit);

        // Mixed list
        List<Object> mixedList = new ArrayList<Object>();

        String myString = "apples";
        mixedList.add(myString);

        int myInt = 4;
        mixedList.add(myInt);

        boolean myBool = true;
        mixedList.add(myBool);

        List<String> myList = new ArrayList<String>();
        myList.add("dog");
        myList.add("cat");
        myList.add("elephant");
        mixedList.add(myList);
        body.put("mixedList", mixedList);

        // Collection (non-indexable)
        Set<Integer> odds = new HashSet<Integer>();
        for (int i = 0; i <= 10; i++) {
            if (i % 2 == 1) {
                odds.add(i);
            }
        }
        body.put("oddNums", new SimpleCollection(odds));

        // String-string map
        Map<String, String> myMap = new HashMap<String, String>();
        myMap.put("Albany", "New York");
        myMap.put("St. Paul", "Minnesota");
        myMap.put("Austin", "Texas");
        myMap.put("Sacramento", "California");
        myMap.put("Richmond", "Virginia");
        body.put("capitals", myMap);


        // Mixed map
        Map<String, Object> mixedMap = new HashMap<String, Object>();

        mixedMap.put("myString", myString);
        mixedMap.put("myBoolean", myBool);
        mixedMap.put("myNumber", myInt);
        Date myDate = new Date();
        mixedMap.put("myDate", myDate);
        mixedMap.put("myList", myList);
        mixedMap.put("capitals", myMap);
        body.put("mixedMap", mixedMap);

        // Java object
        Employee jdoe = getEmployee();
        body.put("employeeLimited", jdoe);
        try {
            body.put("employeeInvisible", wrap(jdoe, BeansWrapper.EXPOSE_NOTHING));
            body.put("employeeFull", wrap(jdoe, BeansWrapper.EXPOSE_SAFE));
        } catch (TemplateModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
    }

    @Override
    protected String getTitle(String siteName, VitroRequest vreq) {
        return "Test";
    }

    private Employee getEmployee() {

        Calendar c = Calendar.getInstance();
        c.set(1982, Calendar.MAY, 5);
        c = DateUtils.truncate(c, Calendar.DATE);
        Employee jdoe = new Employee("John", "Doe", 34523, c.getTime());
        jdoe.setFavoriteColors("blue", "green");
        jdoe.setSalary(65000);

        c.clear();
        c.set(1975, Calendar.OCTOBER, 25);
        c = DateUtils.truncate(c, Calendar.DATE);
        Employee jsmith = new Employee("Jane", "Smith", 78234, c.getTime());
        jsmith.setFavoriteColors("red", "orange");

        jdoe.setSupervisor(jsmith);

        return jdoe;
    }

    public static class Employee {

        private static int count = 0;

        private String firstName;
        private String lastName;
        private String nickname;
        private Date birthdate;
        private boolean married;
        private int id;
        private String middleName;
        private List<String> favoriteColors;
        private Employee supervisor;
        private float salary;

        Employee(String firstName, String lastName, int id, Date birthdate) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = null; // test a null value
            this.birthdate = birthdate;
            this.married = true;
            this.id = id;
            this.nickname = "";
            this.favoriteColors = new ArrayList<String>();
            count++;
        }

        public static int getEmployeeCount() {
            return count;
        }

        float getSalary() {
            return salary;
        }

        void setSalary(float salary) {
            this.salary = salary;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }

        public String getName(String which) {
            return "first".equals(which) ? firstName : lastName;
        }

        public String getMiddleName() {
            return middleName;
        }

        /*  Public accessor methods for templates */

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Date getBirthdate() {
            return birthdate;
        }

        public int getId() {
            return id;
        }

        public boolean isMarried() {
            return married;
        }

        @Deprecated
        public int getFormerId() {
            return id % 10000;
        }

        public Employee getSupervisor() {
            return supervisor;
        }

        protected void setSupervisor(Employee supervisor) {
            this.supervisor = supervisor;
        }

        public List<String> getFavoriteColors() {
            return favoriteColors;
        }

        public void setFavoriteColors(String... colors) {
            Collections.addAll(favoriteColors, colors);
        }
    }
}

