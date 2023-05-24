package nl.overheid.koop.plooi.model.data.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public final class DcnIdentifierUtil {

    public static final String PLOOI_API_SRC = "plooi-api";
    private static final Pattern PLOOI_API_ID_PTTRN = Pattern.compile("^[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    private DcnIdentifierUtil() {
        // Cannot instantiate util class
    }

    /** Generates a DCN identifier from a source label and a (series of) external identifiers. */
    public static String generateDcnId(String source, String... externalIds) {
        if (externalIds.length == 0) {
            throw new IllegalArgumentException("At least 1 externalIds is required for " + source);
        } else if (PLOOI_API_SRC.equals(source) && PLOOI_API_ID_PTTRN.matcher(externalIds[0]).matches()) {
            return source + "-" + externalIds[0];
        } else if (PLOOI_API_SRC.equals(source)) {
            throw new IllegalArgumentException("Illegal API identifier format " + externalIds[0]);
        } else {
            var digest = DigestUtils.updateDigest(DigestUtils.getSha1Digest(), source.toLowerCase());
            Arrays.stream(externalIds)
                    .map(id -> StringUtils.defaultIfBlank(id, null))
                    .forEach(id -> DigestUtils.updateDigest(digest, Objects.requireNonNull(id, "Got a null externalId. Go fix your mapping.")));
            return source + "-" + Hex.encodeHexString(digest.digest());
        }
    }

    public static String toDcn(String id) {
        return StringUtils.isNotBlank(id) && PLOOI_API_ID_PTTRN.matcher(id).matches() ? PLOOI_API_SRC + "-" + id : id;
    }

    private static final Pattern DCN_ID_PATTERN = Pattern.compile("([-\\w]+)-([-\\p{XDigit}]{36,40}+)");
    private static final int SOURCE_POS = 1;
    private static final int HASH_POS = 2;

    /**
     * Get the source from a DCN identifier, stripping hash and suffix.
     *
     * @param  dcnId                    The DCN identifier
     * @return                          The source
     * @throws IllegalArgumentException if the input identifier does not match the expected pattern
     */
    public static String extractSource(String dcnId) throws IllegalArgumentException {
        return doExtract(dcnId, SOURCE_POS);
    }

    /**
     * Get the hash from a DCN identifier, stripping prefix and suffix.
     *
     * @param  dcnId                    The DCN identifier
     * @return                          The hash
     * @throws IllegalArgumentException if the input identifier does not match the expected pattern
     */
    public static String extractHash(String dcnId) throws IllegalArgumentException {
        return doExtract(dcnId, HASH_POS);
    }

    private static String doExtract(String dcnId, int pos) throws IllegalArgumentException {
        if (StringUtils.isBlank(dcnId)) {
            return null;
        } else {
            var m = DCN_ID_PATTERN.matcher(dcnId);
            if (m.matches()) {
                return m.group(pos).toLowerCase();
            } else {
                throw new IllegalArgumentException("Illegal DCN identifier format " + dcnId);
            }
        }
    }
}
