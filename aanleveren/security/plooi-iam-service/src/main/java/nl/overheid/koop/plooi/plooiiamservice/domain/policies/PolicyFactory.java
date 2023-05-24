package nl.overheid.koop.plooi.plooiiamservice.domain.policies;

import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules.Policy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PolicyFactory {
    private final List<Policy> policies;

    public List<Policy> get(final String action) {
        return policies.stream()
                .filter(policy -> policy.match(action))
                .collect(Collectors.toList());
    }
}
