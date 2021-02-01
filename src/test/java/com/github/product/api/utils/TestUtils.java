package com.github.product.api.utils;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.util.ResourceUtils;

public class TestUtils {
	
	public static String buildURL(int port, final String uri) {
		return "http://localhost:" + port + uri;
	}
	
	public static String buildURL(int port) {
		return "http://localhost:" + port;
	}

	public static String jsonFromFile(String path) throws IOException {
		return new String(Files.readAllBytes(ResourceUtils.getFile(path).toPath()));
	}

}
