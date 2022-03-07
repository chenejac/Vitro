package edu.cornell.mannlib.vitro.webapp.dynapi;

import static edu.cornell.mannlib.vitro.webapp.dynapi.request.RequestPath.REST_BASE_PATH;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import edu.cornell.mannlib.vitro.webapp.dynapi.components.Action;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.OperationResult;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.ResourceAPI;
import edu.cornell.mannlib.vitro.webapp.dynapi.components.ResourceAPIKey;

@RunWith(MockitoJUnitRunner.class)
public class RESTEndpointTest {

	private final static String PATH_INFO = "/1/test";
	private final static String CONTEXT_PATH = REST_BASE_PATH + PATH_INFO;

	private Map<String, String[]> params;

	private ServletContext context;

	private MockedStatic<ResourceAPIPool> resourceAPIPoolStatic;

	private MockedStatic<ActionPool> actionPoolStatic;

	private RESTEndpoint restEndpoint;

	@Mock
	private ResourceAPIPool resourceAPIPool;

	@Mock
	private ActionPool actionPool;

	@Mock
	private ResourceAPI resourceAPI;

	@Spy
	private Action action;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Before
	public void beforeEach() {
		resourceAPIPoolStatic = mockStatic(ResourceAPIPool.class);
		actionPoolStatic = mockStatic(ActionPool.class);

		when(ResourceAPIPool.getInstance()).thenReturn(resourceAPIPool);
		when(resourceAPIPool.get(any(ResourceAPIKey.class))).thenReturn(resourceAPI);

		when(ActionPool.getInstance()).thenReturn(actionPool);
		when(actionPool.get(any(String.class))).thenReturn(action);

		when(request.getParameterMap()).thenReturn(params);
		when(request.getServletContext()).thenReturn(context);

		restEndpoint = new RESTEndpoint();
	}

	@After
	public void afterEach() {
		resourceAPIPoolStatic.close();
		actionPoolStatic.close();
	}

	@Test
	public void doPostTest() {
		prepareMocks("POST", "test");
		restEndpoint.doPost(request, response);
		verifyMocks(1, HttpServletResponse.SC_OK);
	}

	@Test
	public void doPostCustomRestActionTest() {
		OperationResult result = new OperationResult(HttpServletResponse.SC_OK);

		when(request.getMethod()).thenReturn("POST");
		when(request.getContextPath()).thenReturn(CONTEXT_PATH + "/dedupe");
		when(request.getPathInfo()).thenReturn(PATH_INFO + "/dedupe");

		when(action.run(any(OperationData.class))).thenReturn(result);

		when(resourceAPI.getCustomRestActionByName("dedupe")).thenReturn("dedupe");
		doNothing().when(resourceAPI).removeClient();

		restEndpoint.doPost(request, response);

		verify(resourceAPI, times(0)).getActionNameByMethod(any());
		verify(resourceAPI, times(1)).getCustomRestActionByName(any());
		verify(resourceAPI, times(1)).removeClient();
		verify(action, times(1)).run(any());
		verify(action, times(1)).removeClient();
		verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
	}

	@Test
	public void doPostTestNotFound() {
		when(request.getPathInfo()).thenReturn(EMPTY);
		restEndpoint.doPost(request, response);
		verifyMocks(0, HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void doGetTest() {
		prepareMocks("GET", "test");
		restEndpoint.doGet(request, response);
		verifyMocks(1, HttpServletResponse.SC_OK);
	}

	@Test
	public void doGetTestNotFound() {
		when(request.getPathInfo()).thenReturn(EMPTY);
		restEndpoint.doGet(request, response);
		verifyMocks(0, HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void doPutTest() {
		prepareMocks("PUT", "test");
		restEndpoint.doPut(request, response);
		verifyMocks(1, HttpServletResponse.SC_OK);
	}

	@Test
	public void doPutTestNotFound() {
		when(request.getPathInfo()).thenReturn(EMPTY);
		restEndpoint.doPut(request, response);
		verifyMocks(0, HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void doPatchTest() {
		prepareMocks("PATCH", "test");
		restEndpoint.doPatch(request, response);
		verifyMocks(1, HttpServletResponse.SC_OK);
	}

	@Test
	public void doPatchTestNotFound() {
		when(request.getPathInfo()).thenReturn(EMPTY);
		restEndpoint.doPatch(request, response);
		verifyMocks(0, HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void doDeleteTest() {
		prepareMocks("DELETE", "test");
		restEndpoint.doDelete(request, response);
		verifyMocks(1, HttpServletResponse.SC_OK);
	}

	@Test
	public void doDeleteTestNotFound() {
		when(request.getPathInfo()).thenReturn(EMPTY);
		restEndpoint.doDelete(request, response);
		verifyMocks(0, HttpServletResponse.SC_NOT_FOUND);
	}

	private void prepareMocks(String method, String actionName) {
		OperationResult result = new OperationResult(HttpServletResponse.SC_OK);

		when(request.getMethod()).thenReturn(method);
		when(request.getContextPath()).thenReturn(CONTEXT_PATH);
		when(request.getPathInfo()).thenReturn(PATH_INFO);

		when(action.run(any(OperationData.class))).thenReturn(result);

		when(resourceAPI.getActionNameByMethod(method)).thenReturn(actionName);
		doNothing().when(resourceAPI).removeClient();
	}

	private void verifyMocks(int count, int status) {
		verify(resourceAPI, times(count)).getActionNameByMethod(any());
		verify(resourceAPI, times(0)).getCustomRestActionByName(any());
		verify(resourceAPI, times(count)).removeClient();
		verify(action, times(count)).run(any());
		verify(action, times(count)).removeClient();
		verify(response, times(1)).setStatus(status);
	}

}
