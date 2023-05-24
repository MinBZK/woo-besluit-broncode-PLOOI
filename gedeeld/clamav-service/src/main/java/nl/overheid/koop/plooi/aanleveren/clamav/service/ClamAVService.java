package nl.overheid.koop.plooi.aanleveren.clamav.service;

import fi.solita.clamav.ClamAVClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.clamav.exceptions.ScanServiceException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClamAVService {
    private final ClamAVClient clamAVClient;

    public boolean isFileOk(final byte[] file) {
        pingClamAVDeamon();
        try {
            val reply = clamAVClient.scan(file);
            return ClamAVClient.isCleanReply(reply);
        } catch (IOException e) {
            throw new ScanServiceException("Could not scan the input");
        }
    }

    public void pingClamAVDeamon() {
        try {
            log.info("Ping to ClamAV Deamon");
            clamAVClient.ping();
        } catch (IOException e) {
            throw new ScanServiceException(e.getLocalizedMessage());
        }
    }
}
