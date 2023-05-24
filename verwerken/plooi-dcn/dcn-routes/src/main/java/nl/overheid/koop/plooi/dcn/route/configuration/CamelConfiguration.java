package nl.overheid.koop.plooi.dcn.route.configuration;

import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CamelConfiguration {

    @Bean
    IdempotentRepository infiniteIdempotentRepository() {
        return MemoryIdempotentRepository.memoryIdempotentRepository(100_000_000);
    }
}
