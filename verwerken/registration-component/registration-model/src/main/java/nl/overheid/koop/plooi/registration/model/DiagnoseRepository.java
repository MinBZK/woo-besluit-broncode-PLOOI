package nl.overheid.koop.plooi.registration.model;

import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface DiagnoseRepository extends JpaRepository<DiagnoseEntity, Long> {

    @Query("SELECT  dia "
            + "FROM     DiagnoseEntity dia "
            + "WHERE    dia.verwerkingId = :verwerkingId "
            + "ORDER BY dia.id DESC")
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.READ_ONLY, value = "true") })
    Page<DiagnoseEntity> getDiagnosesByVerwerkingId(@Param("verwerkingId") String verwerkingId, Pageable pageable);
}
