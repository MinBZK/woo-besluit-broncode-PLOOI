package nl.overheid.koop.plooi.registration.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.QueryHint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.lang.Nullable;

public interface ProcesRepository extends JpaRepository<ProcesEntity, String>, JpaSpecificationExecutor<ProcesEntity> {

    @Override
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    Page<ProcesEntity> findAll(@Nullable Specification<ProcesEntity> spec, Pageable pageable);

    static Specification<ProcesEntity> querySpecification(List<String> ids, String sourceLabel, String triggerType, OffsetDateTime since, OffsetDateTime till,
            boolean errorsOnly) {
        return new Specification<>() {
            private static final long serialVersionUID = -2385742857784229528L;

            @Override
            public Predicate toPredicate(Root<ProcesEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                var predicates = new ArrayList<Predicate>();
                if (ids != null && !ids.isEmpty()) {
                    predicates.add(root.get("id").in(ids));
                }
                if (StringUtils.isNotBlank(sourceLabel)) {
                    predicates.add(cb.equal(root.get("sourceLabel"), sourceLabel));
                }
                if (StringUtils.isNotBlank(triggerType)) {
                    predicates.add(cb.equal(root.get("triggerType"), triggerType));
                }
                if (since != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("timeCreated"), since));
                }
                if (till != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("timeCreated"), till));
                }
                if (errorsOnly) {
                    var subQuery = query.subquery(VerwerkingEntity.class);
                    var verwerking = subQuery.from(VerwerkingEntity.class);
                    predicates.add(cb.exists(subQuery.select(verwerking)
                            .where(cb.equal(verwerking.get("procesId"), root.get("id")),
                                    cb.or(cb.equal(verwerking.get("severity"), Severity.ERROR), cb.equal(verwerking.get("severity"), Severity.EXCEPTION)))));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
