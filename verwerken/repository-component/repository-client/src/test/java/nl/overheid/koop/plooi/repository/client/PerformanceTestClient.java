package nl.overheid.koop.plooi.repository.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.Titelcollectie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTestClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ApiClient apiClient = new ApiClient();
    private final AanleverenClient aanleverenClient = new AanleverenClient(this.apiClient);
    private final PublicatieClient publicatieClient = new PublicatieClient(this.apiClient);
    private final DocumentenClient documentenClient = new DocumentenClient(this.apiClient);
    private final Random content = new Random();

    public static void main(String[] args) throws Exception {
        new PerformanceTestClient().bigDelivery(args.length == 0 ? 60 : Integer.valueOf(args[0]));
        new PerformanceTestClient().manyDeliveries();
    }

    public void bigDelivery(int duration) throws ClientException, IOException {
        try {
            StopWatch timer = new StopWatch();
            timer.start();
            this.logger.debug("Recieved: {}", this.aanleverenClient
                    .createRequest(
                            new Versie().oorzaak(OorzaakEnum.AANLEVERING),
                            "_plooi",
                            "big")
                    .addPart("big", () -> new InputStream() {
                        private int cnt;

                        @Override
                        public int read() throws IOException {
                            if (timer.getTime() < duration * 1_000) {
                                this.cnt++;
                                return PerformanceTestClient.this.content.nextInt(2 ^ Byte.SIZE);
                            } else {
                                PerformanceTestClient.this.logger.debug("Sent {} kb after {}ms", this.cnt / 1024, timer.getTime());
                                return -1;
                            }
                        }
                    })
                    .post());
            this.logger.debug("Done after {}", timer.getTime());
        } catch (ClientException e) {
            this.logger.error("ClientException", e);
        }
    }

    /**
     * Useful to find leaks, like unclosed files:
     * <ul>
     * <li>get the process id with <code>ps -fC java</code>;
     * <li>get the file handles with <code>lsof -p NNN | grep plooi-repos</code>
     * </ul>
     */
    private void manyDeliveries() throws ClientException, IOException {
        for (int i = 1; i < 100; i++) {
            var plooi = this.aanleverenClient
                    .createRequest(new Versie().oorzaak(OorzaakEnum.AANLEVERING), "_plooi", String.valueOf(i))
                    .addPart("metadata", "{ meta ... }")
                    .addPart("document", "DOC")
                    .post();
            if (plooi.isPresent()) {
                var dcnId = plooi.get().getPlooiIntern().getDcnId();
                this.publicatieClient.getManifest(dcnId);
                this.publicatieClient.postPlooi(dcnId,
                        plooi.get().document(new Document().titelcollectie(new Titelcollectie().officieleTitel("Test document"))));
                this.publicatieClient.postText(dcnId, "{}");
                this.documentenClient.getManifest(dcnId);
                this.documentenClient.getLatestVersion(dcnId);
                this.documentenClient.getPlooi(dcnId);
                this.documentenClient.getRelations(dcnId);
                this.documentenClient.getOwms(dcnId);
                this.documentenClient.getText(dcnId);
                this.documentenClient.getFileContent(dcnId, "metadata").close();
                this.publicatieClient.delete(dcnId, OorzaakEnum.INTREKKING, "reden");
            }
        }
    }
}
