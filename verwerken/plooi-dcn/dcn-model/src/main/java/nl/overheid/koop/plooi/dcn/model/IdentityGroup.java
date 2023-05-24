package nl.overheid.koop.plooi.dcn.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "IDENTITYGROUPS")
public class IdentityGroup {

    @Id
    private String id;
    private String hash;

    protected IdentityGroup() {
        super();
    }

    public IdentityGroup(String hash) {
        super();
        this.id = Hex.encodeHexString(DigestUtils.updateDigest(DigestUtils.getSha1Digest(), hash).digest());
        this.hash = hash;
    }

    public String getId() {
        return this.id;
    }

    public String getHash() {
        return this.hash;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("hash", this.hash)
                .toString();
    }
}
