package nl.overheid.koop.plooi.dcn.component.types;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.util.PlooiFileUtil;

public final class PlooiFile {

    private static final List<PlooiFile> EMP_CHILDREN = Collections.emptyList();

    private final Bestand file;
    private Envelope collectedIn;
    private List<PlooiFile> children;
    private byte[] content;
    private Supplier<InputStream> contentSupplier;

    public PlooiFile(String bestandsnaam, String label) {
        this();
        this.file.setBestandsnaam(bestandsnaam);
        this.file.setLabel(label);
    }

    public PlooiFile() {
        this(new Bestand());
    }

    public PlooiFile(PlooiFile otherFile) {
        this(otherFile.getFile());
        collectedIn(otherFile.getCollectedIn());
    }

    public PlooiFile(Bestand other) {
        this.file = other;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        return this == o || (o instanceof PlooiFile otherFile && Objects.equals(this.file, otherFile.file));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public PlooiFile postPopulate() {
        PlooiFileUtil.populate(this.file);
        return this;
    }

    public boolean hasContent() {
        return this.content != null || this.contentSupplier != null;
    }

    public byte[] getContent() {
        return this.content;
    }

    public Supplier<InputStream> getContentSupplier() {
        return this.contentSupplier;
    }

    public PlooiFile content(byte[] bytes) {
        this.content = bytes;
        return this;
    }

    public PlooiFile contentSupplier(Supplier<InputStream> supplier) {
        this.contentSupplier = supplier;
        return this;
    }

    public List<PlooiFile> getChildren() {
        return this.children == null ? EMP_CHILDREN : this.children;
    }

    public PlooiFile newChild() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        var child = new PlooiFile();
        this.children.add(child);
        return child;
    }

    public Bestand getFile() {
        return this.file;
    }

    PlooiFile collectedIn(Envelope plooiDoc) {
        this.collectedIn = plooiDoc;
        return this;
    }

    public Envelope getCollectedIn() {
        return this.collectedIn;
    }

    @Override
    public String toString() {
        var str = new StringBuilder(getClass().getSimpleName()).append(" ");
        if (this.file.getBestandsnaam() != null) {
            str.append(this.file.getBestandsnaam());
        } else if (this.file.getUrl() != null) {
            str.append(this.file.getUrl());
        }
        if (this.file.getId() != null) {
            str.append(" (").append(this.file.getId()).append(")");
        }
        if (this.children != null && !this.children.isEmpty()) {
            str.append(" with ").append(this.children.size()).append(" children");
        }
        return str.toString();
    }
}
