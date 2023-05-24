package nl.overheid.koop.plooi.plooiiamservice.domain;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.configuration.ApplicationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Component
class RsaKey {
    private final KeyStoreKeyFactory keyStoreKeyFactory;

    RsaKey(final ApplicationProperties properties) {
        keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        new ClassPathResource(properties.keyStore()), properties.keyPassword().toCharArray());
    }

    RSAKey generateKey() {
        val keyPair = keyStoreKeyFactory.getKeyPair("1");
        val publicKey = (RSAPublicKey) keyPair.getPublic();
        val privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }
}
