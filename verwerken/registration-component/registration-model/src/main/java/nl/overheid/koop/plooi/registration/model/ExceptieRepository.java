package nl.overheid.koop.plooi.registration.model;

import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ExceptieRepository extends JpaRepository<ExceptieEntity, Long> {

    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.READ_ONLY, value = "true") })
    Optional<ExceptieEntity> getFirstByVerwerkingId(@Param("verwerkingId") String verwerkingId);

    @Query("SELECT DISTINCT (pe.exceptionClass) "
            + "FROM ExceptieEntity pe "
            + "    JOIN VerwerkingEntity de ON pe.verwerkingId = de.id "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "WHERE de.sourceLabel = :sourceLabel")
    List<String> getExceptiesByBron(@Param("sourceLabel") String sourceLabel);
}
