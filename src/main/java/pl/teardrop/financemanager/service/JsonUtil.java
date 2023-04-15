package pl.teardrop.financemanager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import pl.teardrop.financemanager.model.Category;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonUtil {

	public List<Category> loadDefaultCategories() {
		return load("json/categories_pl.json", new TypeReference<List<Category>>() {

		});
	}

	private <T> List<T> load(String path, TypeReference<List<T>> typeRef) {
		try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
			if (stream == null) {
				throw new RuntimeException("Resource for path " + path + " not found");
			}

			final String jsonString = IOUtils.toString(stream, StandardCharsets.UTF_8);

			final ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, typeRef);

		} catch (IOException e) {
			throw new RuntimeException("Cannot load resourse: " + path);
		}

	}

}
