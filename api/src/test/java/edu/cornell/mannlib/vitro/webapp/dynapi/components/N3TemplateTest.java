package edu.cornell.mannlib.vitro.webapp.dynapi.components;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.cornell.mannlib.vitro.webapp.dynapi.OperationData;
import edu.cornell.mannlib.vitro.webapp.dynapi.ServletContextTest;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.ParameterType;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.types.PrimitiveParameterType;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.ArrayData;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.Data;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.ObjectData;
import edu.cornell.mannlib.vitro.webapp.dynapi.io.data.StringData;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.impl.ContextModelAccessImpl;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ConfigurationBeanLoaderException;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;

import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import stubs.javax.servlet.http.HttpServletRequestStub;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class N3TemplateTest extends ServletContextTest {

    private final static String TEST_DATA_PATH="src/test/resources/rdf/abox/filegraph/dynamic-api-individuals-n3template-test.n3";
    private final static String TEST_N3TEMPLATE_URI="https://vivoweb.org/ontology/vitro-dynamic-api/N3Template/testN3Template";

    private static MockedStatic<ModelAccess> modelAccess;

    static ParameterType anyURI;
    static ParameterType stringType;
    static ParameterType booleanType;

    @Mock
    OperationData input;

    @Mock
    ContextModelAccessImpl contextModelAccess;

    @Mock
    ServletContext servletContext;

    @Mock
    ModelComponent modelComponent;

    OntModel writeModel;

    N3Template n3Template;

    @BeforeClass
    public static void setupStaticObjects(){
        modelAccess = mockStatic(ModelAccess.class);

        anyURI = new PrimitiveParameterType();
        anyURI.setName("anyURI");
        stringType = new PrimitiveParameterType();
        stringType.setName("string");
        booleanType = new PrimitiveParameterType();
        booleanType.setName("boolean");
    }

    @AfterClass
    public static void after() {
        modelAccess.close();
    }

    @Before
    public void setupTemplate(){
        when(ModelAccess.on(any(ServletContext.class))).thenReturn(contextModelAccess);
        writeModel = new OntModelImpl(OntModelSpec.OWL_DL_MEM);
        when(contextModelAccess.getOntModel(any(String.class))).thenReturn(writeModel);
        this.n3Template = new N3Template();
    }


    @Test
    public void testLoadingAndPropertiesSetup() throws IOException, ConfigurationBeanLoaderException {
        loadDefaultModel();
        loadModels(TEST_DATA_PATH.split("\\.")[1],TEST_DATA_PATH);

        N3Template n3Template = loader.loadInstance(TEST_N3TEMPLATE_URI, N3Template.class);
        assertNotNull(n3Template);
        assertEquals(0, n3Template.getProvidedParams().size());
        assertEquals(3, n3Template.getRequiredParams().size());
        assertEquals("?testSubject <http://has> ?testObject. ?testSubject2 <http://has> ?testObject", n3Template.getN3TextAdditions());
        assertEquals("?testSubject <http://has> ?testObject", n3Template.getN3TextRetractions());
    }

    @Test
    public void testNotAllN3VariablesSubstitutedWithValues(){
        n3Template.setN3TextRetractions("?uri1 <http:has> ?literal1");
        n3Template.setTemplateModel(modelComponent);

        Parameter param1 = new Parameter();
        param1.setParamType(anyURI);
        param1.setName("uri1");

        n3Template.addRequiredParameter(param1);

        when(input.has(any(String.class))).thenReturn(true);
        when(input.get("uri1")).thenReturn(new String[]{"http://testSubject"});

        assertTrue(n3Template.run(input).hasError());
    }

    @Test
    public void testInsertMultipleUris() {
        when(modelComponent.getName()).thenReturn("test");
        n3Template.setN3TextAdditions("?uri1 <http:has> ?uri2");
        n3Template.setTemplateModel(modelComponent);

        Parameter param1 = new Parameter();
        param1.setParamType(anyURI);
        param1.setName("uri1");
        Parameter param2 = new Parameter();
        param2.setParamType(anyURI);
        param2.setName("uri2");

        n3Template.addRequiredParameter(param1);
        n3Template.addRequiredParameter(param2);

        when(input.has(any(String.class))).thenReturn(true);
        when(input.getContext()).thenReturn(servletContext);
        when(input.get("uri1")).thenReturn(new String[]{"http://testSubject"});
        when(input.get("uri2")).thenReturn(new String[]{"http://testObject"});

        assertFalse(n3Template.run(input).hasError());
        assertNotNull(writeModel.getResource("http://testSubject"));
        assertTrue(writeModel.listObjectsOfProperty(new PropertyImpl("http:has")).next().isResource());
        assertEquals("http://testSubject",
                writeModel.listResourcesWithProperty(new PropertyImpl("http:has")).nextResource().getURI());
    }

    @Test
    public void testInsertOneUriOneLiteral(){
        when(modelComponent.getName()).thenReturn("test");
        n3Template.setN3TextAdditions("?uri1 <http:has> ?literal1");
        n3Template.setTemplateModel(modelComponent);

        Parameter param1 = new Parameter();
        param1.setParamType(anyURI);
        param1.setName("uri1");
        Parameter param2 = new Parameter();
        param2.setParamType(stringType);
        param2.setName("literal1");

        n3Template.addRequiredParameter(param1);
        n3Template.addRequiredParameter(param2);

        when(input.has(any(String.class))).thenReturn(true);
        when(input.getContext()).thenReturn(servletContext);
        when(input.get("uri1")).thenReturn(new String[]{"http://testSubject"});
        when(input.get("literal1")).thenReturn(new String[]{"testLiteral"});

        assertFalse(n3Template.run(input).hasError());
        assertNotNull(writeModel.getResource("http://testSubject"));
        assertTrue(writeModel.listObjectsOfProperty(new PropertyImpl("http:has")).next().isLiteral());
        assertEquals("http://testSubject",
                writeModel.listResourcesWithProperty(new PropertyImpl("http:has")).nextResource().getURI());
    }

    @Test
    public void testMultipleStatements(){
        when(modelComponent.getName()).thenReturn("test");
        n3Template.setN3TextAdditions("?uri1 <http://has> ?literal1 .\n?uri1 <http://was> ?literal2");
        n3Template.setTemplateModel(modelComponent);

        Parameter param1 = new Parameter();
        param1.setParamType(anyURI);
        param1.setName("uri1");
        Parameter param2 = new Parameter();
        param2.setParamType(stringType);
        param2.setName("literal1");
        Parameter param3 = new Parameter();
        param3.setParamType(booleanType);
        param3.setName("literal2");

        n3Template.addRequiredParameter(param1);
        n3Template.addRequiredParameter(param2);
        n3Template.addRequiredParameter(param3);

        when(input.has(any(String.class))).thenReturn(true);
        when(input.getContext()).thenReturn(servletContext);
        when(input.get("uri1")).thenReturn(new String[]{"http://testSubject"});
        when(input.get("literal1")).thenReturn(new String[]{"testLiteral"});
        when(input.get("literal2")).thenReturn(new String[]{"true"});

        assertFalse(n3Template.run(input).hasError());
        assertNotNull(writeModel.getResource("http://testSubject"));
        assertEquals(2,writeModel.listObjects().toList().size());
        assertEquals(1,writeModel.listSubjects().toList().size());
        assertEquals(2, writeModel.listStatements().toList().size());
        assertTrue(writeModel.containsLiteral(
                new ResourceImpl("http://testSubject"), new PropertyImpl("http://was"),true));
    }

    @Test
    public void testRetractionsWorkWhenModelIsEmpty() {
        when(modelComponent.getName()).thenReturn("test");
        n3Template.setN3TextRetractions("<http://testSubject> <http:has> <http://testObject>");
        n3Template.setTemplateModel(modelComponent);

        when(input.getContext()).thenReturn(servletContext);

        assertFalse(n3Template.run(input).hasError());
        assertTrue(writeModel.getGraph().isEmpty());
    }

    @Test
    public void removingATriplet() {
        when(modelComponent.getName()).thenReturn("test");
        n3Template.setN3TextRetractions("<http://testSubject> <http:has> <http://testObject>");
        n3Template.setTemplateModel(modelComponent);

        when(input.getContext()).thenReturn(servletContext);

        writeModel.add(new StatementImpl(
                new ResourceImpl("http://testSubject"),
                new PropertyImpl("http:has"),
                new ResourceImpl("http://testObject"))
        );
        assertEquals(1,writeModel.getGraph().size());
        assertFalse(n3Template.run(input).hasError());
        assertEquals(0,writeModel.getGraph().size());
    }


    @Test
    public void loadAndExecuteN3operationWithAdditionAndRetraction() throws IOException, ConfigurationBeanLoaderException {
        loadDefaultModel();
        loadModels(TEST_DATA_PATH.split("\\.")[1],TEST_DATA_PATH);

        N3Template n3Template = loader.loadInstance(TEST_N3TEMPLATE_URI, N3Template.class);

        when(input.has("testSubject")).thenReturn(true);
        when(input.has("testSubject2")).thenReturn(true);
        when(input.has("testObject")).thenReturn(true);
        when(input.get("testSubject")).thenReturn(new String[]{"http://Joe"});
        when(input.get("testSubject2")).thenReturn(new String[]{"http://Bob"});
        when(input.get("testObject")).thenReturn(new String[]{"Bike"});
        when(input.getContext()).thenReturn(servletContext);

        assertFalse(n3Template.run(input).hasError());
        assertEquals(1,writeModel.getGraph().size());
        assertTrue(writeModel.getGraph().contains(
                NodeFactory.createURI("http://Bob"),
                NodeFactory.createURI("http://has"),
                NodeFactory.createLiteral("Bike"))
        );
    }

    @Test
    public void loadAndExecuteN3operationMultipleTimes() throws IOException, ConfigurationBeanLoaderException {
        loadDefaultModel();
        loadModels(TEST_DATA_PATH.split("\\.")[1],TEST_DATA_PATH);

        N3Template n3Template = loader.loadInstance(TEST_N3TEMPLATE_URI, N3Template.class);

        when(input.has("testSubject")).thenReturn(true);
        when(input.has("testSubject2")).thenReturn(true);
        when(input.has("testObject")).thenReturn(true);
        when(input.get("testSubject")).thenReturn(new String[]{"http://Joe"});
        when(input.get("testSubject2")).thenReturn(new String[]{"http://Bob"});
        when(input.get("testObject")).thenReturn(new String[]{"Bike"});
        when(input.getContext()).thenReturn(servletContext);

        assertFalse(n3Template.run(input).hasError());
        assertEquals(1,writeModel.getGraph().size());
        assertTrue(writeModel.getGraph().contains(
                NodeFactory.createURI("http://Bob"),
                NodeFactory.createURI("http://has"),
                NodeFactory.createLiteral("Bike"))
        );

        when(input.get("testSubject")).thenReturn(new String[]{"http://Bob"});
        when(input.get("testSubject2")).thenReturn(new String[]{"http://Mike"});
        when(input.get("testObject")).thenReturn(new String[]{"Bike"});

        assertFalse(n3Template.run(input).hasError());
        assertEquals(1,writeModel.getGraph().size());
        assertTrue(writeModel.getGraph().contains(
                NodeFactory.createURI("http://Mike"),
                NodeFactory.createURI("http://has"),
                NodeFactory.createLiteral("Bike"))
        );

    }

    @Test
    public void testActionWithObjectsAsParameters() throws IOException, ConfigurationBeanLoaderException {
        String testDataPath = "src/test/resources/rdf/abox/filegraph/dynamic-api-individuals-sparql-n3-complex-action.n3";

        loadDefaultModel();
        loadModels(testDataPath.split("\\.")[1],testDataPath);

        String n3_uri="https://vivoweb.org/ontology/vitro-dynamic-api/N3Template/deleteDynamicActionN3Template";
        N3Template n3Template = loader.loadInstance(n3_uri, N3Template.class);
        assertNotNull(n3Template);
        assertEquals(1, n3Template.getRequiredParams().size());

        when(input.has("actionForDeleting")).thenReturn(true);
        when(input.get("?actionForDeleting.0.uri")).thenReturn(new String[]{"http://Joe"});
        when(input.get("?actionForDeleting.0.label")).thenReturn(new String[]{"http://Bob"});

        assertFalse(n3Template.run(input).hasError());
    }
}
