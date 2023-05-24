package nl.overheid.koop.plooi.repository.relational;

import java.util.List;
import java.util.stream.Stream;
import javax.persistence.QueryHint;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends JpaRepository<Relation, String> {

    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.READ_ONLY, value = "true"),
            @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    Stream<Relation> getByFromId(String fromId);

    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.READ_ONLY, value = "true"),
            @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    Stream<Relation> getByFromIdAndRelationTypeIn(String dcnId, List<RelationType> types);

    @QueryHints({
            @QueryHint(name = org.hibernate.annotations.QueryHints.READ_ONLY, value = "true"),
            @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    Stream<Relation> getByToId(String toId);

    @Modifying
    void deleteByFromIdAndOrigin(String fromId, String origin);
}
