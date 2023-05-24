package nl.overheid.koop.plooi.registration.client;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import javax.json.Json;
import javax.json.JsonValue;
import nl.overheid.koop.plooi.model.data.util.JSONBinding;
import nl.overheid.koop.plooi.model.data.util.PlooiBinding;
import nl.overheid.koop.plooi.registration.model.Proces;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;

public class RegistrationClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final PlooiBinding<Proces> procesBinding = new JSONBinding<>(Proces.class);
	private final PlooiBinding<Verwerking> verwerkingBinding = new JSONBinding<>(Verwerking.class);
	private final ApiClient client;

	public RegistrationClient() {
		this(null);
	}

	public RegistrationClient(@Value("${registration.base.url}") String base) {
		this(HttpClient.newBuilder(), base);
	}

	public RegistrationClient(HttpClient.Builder builder, String base) {
		this.client = new ApiClient(builder, base);
	}

	public Proces createProces(String source, String triggerType, String trigger) {
        this.logger.info("Proces Posted for source {} of trigger type {} triggered by {} ", source, triggerType, trigger);
        var data = this.procesBinding.marshalToString(new Proces().sourceLabel(source).triggerType(triggerType).trigger(trigger));
        var requestBuilder = HttpRequest.newBuilder()
                .uri(this.client.getUri("/processen"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(data));
        return this.procesBinding.unmarshalFromStream(this.client.send("createProces", requestBuilder));
	}

	public Verwerking createProcesVerwerking(String id, Verwerking verwerking) {
		this.logger.debug("Verwerking Posted for proces Id : {} ", id);
        var data = this.verwerkingBinding.marshalToString(verwerking);
        var requestBuilder = HttpRequest.newBuilder()
				.uri(this.client.getUri("/processen/{id}/verwerkingen".replace("{id}", ApiClient.urlEncode(id))))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(data));
        return this.verwerkingBinding.unmarshalFromStream(this.client.send("createProcesVerwerking", requestBuilder));
	}

	public Proces getLastSuccessful(Pageable page, String source, String trigger) {
		this.logger.debug("getLastSuccessful for source: {} , trigger: {}", source, trigger);
        var requestBuilder = HttpRequest.newBuilder()
				.uri(this.client.getUri("/processen?page=" + page.getPageNumber() + "&size=" + page.getPageSize() + "&bron=" + source + "&trigger=" + trigger))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .GET();
		var request = this.client.send("getLastSuccessful", requestBuilder);
		var jsonReader = Json.createReader(request);
        var content = jsonReader.readObject()
				.get("content")
				.asJsonArray()
				.stream()
				.map(JsonValue::toString)
				.toList();
		return  content.isEmpty() ? null : this.procesBinding.unmarshalFromString(content.get(0));
	}
}