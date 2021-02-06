package com.github.product.api.controllers;

import static com.github.product.api.utils.TestUtils.buildURL;
import static com.github.product.api.utils.TestUtils.jsonFromFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.valid4j.matchers.jsonpath.JsonPathMatchers;

import com.github.product.api.ProductSpringBootApiApplication;


@SpringBootTest(classes = ProductSpringBootApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Product Controller Integration Tests")
@Sql("/sql/clean_up.sql")
@ActiveProfiles("test")
public class ProductControllerIT {
	
	private static final String ENDPOINT_DOCUMENT = "/{id}";

	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value("${spring.datasource.password}")
	private String dataBasePassword;
	
	private HttpHeaders headers;
	
	private Map<String, String> idDefaultParam;
	
	@BeforeEach
	public void setUp() {
		headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		idDefaultParam = new HashMap<String, String>();
		idDefaultParam.put("id", "1");
	}
	
	@Test
	@Sql("/sql/load_data.sql")
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must return a product page | HTTP 200")
	public void searchTest() throws Exception {
		
		final HttpEntity<String> entity = new HttpEntity<String>(headers);
		final ResponseEntity<String> response = restTemplate.exchange(buildURL(port), HttpMethod.GET, entity, String.class);

		assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	
	@Test
	@Sql("/sql/create.sql")
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must find a specific product | HTTP 200")
	public void findTest() throws Exception {
		
		final HttpEntity<String> entity = new HttpEntity<String>(headers);
		final ResponseEntity<String> response = restTemplate.exchange(buildURL(port, ENDPOINT_DOCUMENT), HttpMethod.GET, entity, String.class, idDefaultParam);

		assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		
		final String expected = jsonFromFile("classpath:json/response-resource.json");
		JSONAssert.assertEquals(expected, response.getBody(), JSONCompareMode.LENIENT);
	}
	
	
	@Test
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must create a product | HTTP 201")
	public void createTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final String json = jsonFromFile("classpath:json/post-valid-payload.json");
		final HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		final ResponseEntity<String> response = restTemplate.postForEntity(buildURL(port), entity, String.class);
		
		assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(response.getHeaders(), hasKey(HttpHeaders.LOCATION));
	}
	
	@Test
	@DisplayName("It must not create a product with an invalid payload | HTTP 400")
	public void createWithInvalidPayloadTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final String json = jsonFromFile("classpath:json/post-invalid-payload.json");
		final HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		final ResponseEntity<String> response = restTemplate.postForEntity(buildURL(port), entity, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	@Sql("/sql/create.sql")
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must not create a product with a repeated name | HTTP 400")
	public void createWithRepeatedNameTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final String json = jsonFromFile("classpath:json/post-valid-payload.json");
		final HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		final ResponseEntity<String> response = restTemplate.postForEntity(buildURL(port), entity, String.class);

		assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}
	
	@Test
	@Sql("/sql/create.sql")
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must update a specific product | HTTP 200")
	public void updateTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final JSONObject json = new JSONObject(jsonFromFile("classpath:json/update-valid-payload.json"));
		final HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);
		final ResponseEntity<String> response = restTemplate.exchange(buildURL(port, ENDPOINT_DOCUMENT), HttpMethod.PUT, entity, String.class, idDefaultParam);

		assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody(), JsonPathMatchers.hasJsonPath("$.id", is(1)));
		assertThat(response.getBody(), JsonPathMatchers.hasJsonPath("$.name", is(json.get("name"))));
	}
	
	@Test
	@Sql("/sql/load_data.sql")
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must not update a specific product with a repeated name | HTTP 400")
	public void updateWithRepeatedNameTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final JSONObject json = new JSONObject(jsonFromFile("classpath:json/update-valid-payload.json"));
		final HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);
		final ResponseEntity<String> response = restTemplate.exchange(buildURL(port, ENDPOINT_DOCUMENT), HttpMethod.PUT, entity, String.class, idDefaultParam);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
	}
	
	@Test
	@DisplayName("It must not update an inexistent product | HTTP 404")
	public void updateInexistentTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final JSONObject json = new JSONObject(jsonFromFile("classpath:json/update-valid-payload.json"));
		final HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);
		final ResponseEntity<String> response = restTemplate.exchange(buildURL(port, ENDPOINT_DOCUMENT), HttpMethod.PUT, entity, String.class, idDefaultParam);

		assertThat(response.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}
	
	@Test
	@Sql("/sql/create.sql")
	@Sql(scripts = "/sql/clean_up.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("It must delete a specific product | HTTP 204")
	public void excluirTest() throws Exception {

		headers.setContentType(MediaType.APPLICATION_JSON);

		final ResponseEntity<String> response = restTemplate.exchange(buildURL(port, ENDPOINT_DOCUMENT), HttpMethod.DELETE, HttpEntity.EMPTY, String.class, idDefaultParam);

		assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
	}
}
