package nl.overheid.koop.plooi.repository.service;

import java.util.List;
import java.util.function.Consumer;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import nl.overheid.koop.plooi.dcn.process.service.VerwerkenClient;

/**
 * Wraps a {@link VerwerkenClient} into a {@link Consumer} so it can be used by
 * {@link nl.overheid.koop.plooi.repository.storage.ArchiefProcessor}
 */
public class VerwerkenClientWrapper implements Consumer<List<String>> {

    private final VerwerkenClient verwerkenClient;
    private final VerwerkingActies verwerkingActie;
    private final String procesId;

    public VerwerkenClientWrapper(VerwerkenClient client, VerwerkingActies actie, String procId) {
        this.verwerkenClient = client;
        this.verwerkingActie = actie;
        this.procesId = procId;
    }

    @Override
    public void accept(List<String> dcnIds) {
        this.verwerkenClient.process(this.verwerkingActie, this.procesId, dcnIds);
    }

    @Override
    public String toString() {
        return new StringBuilder().append(getClass().getSimpleName()).append(" for ").append(this.verwerkenClient).toString();
    }
}
