package vttp2023.batch4.paf.assessment.services;

import java.io.Reader;
import java.io.StringReader;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	RestTemplate template = new RestTemplate();

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
		String frankfurter = "https://www.frankfurter.app/latest";
		String uri = UriComponentsBuilder
			.fromUriString(frankfurter)
			.queryParam("amount", amount)
			.queryParam("from", from)
			.queryParam("to", to)
			.toUriString();
		
		System.out.println(uri);
		
		ResponseEntity<String> responseEntity = template.getForEntity(uri, String.class);
		System.out.println(responseEntity.getBody());
		Reader reader = new StringReader(responseEntity.getBody());
		JsonReader jsonReader = Json.createReader(reader);
		JsonObject obj = jsonReader.readObject();
		if (obj.containsKey("message")){
			return -1000f;
		} else {
			JsonObject rates = obj.getJsonObject("rates");
			float converted = Float.parseFloat(rates.get("SGD").toString());
			return converted;
		}


		
	}
}
