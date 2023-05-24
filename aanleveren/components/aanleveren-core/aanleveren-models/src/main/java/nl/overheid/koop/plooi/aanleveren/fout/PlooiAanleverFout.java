package nl.overheid.koop.plooi.aanleveren.fout;

import lombok.Builder;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Builder
public record PlooiAanleverFout(String type, String title, Integer status, String detail, String instance,
                                List<String> messages) {

    public PlooiAanleverFout {
        requireNonNull(type);
        requireNonNull(title);
        requireNonNull(status);
        requireNonNull(detail);
        requireNonNull(instance);
    }

}
