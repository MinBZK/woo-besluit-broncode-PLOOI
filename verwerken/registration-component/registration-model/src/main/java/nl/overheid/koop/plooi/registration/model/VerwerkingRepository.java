package nl.overheid.koop.plooi.registration.model;

import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import javax.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

/**
 * The repository class for {@link nl.overheid.koop.plooi.registration.model.VerwerkingEntity}
 *
 * @see nl.overheid.koop.plooi.registration.model.VerwerkingEntity
 * @see ProcesEntity
 */
public interface VerwerkingRepository extends JpaRepository<VerwerkingEntity, Long> {

    /**
     * Returns the {@link nl.overheid.koop.plooi.registration.model.VerwerkingEntity} by id. NB This id field is NOT the
     * primary key.
     */
    Optional<VerwerkingEntity> findById(String id);

    /**
     * Returns the {@link nl.overheid.koop.plooi.registration.model.VerwerkingEntity} by dcnId.
     *
     * @param  dcnId    of VerwerkingEntity
     * @param  pageable for handling paginated calls
     * @return          {@link nl.overheid.koop.plooi.registration.model.VerwerkingEntity}
     */
    @Query("SELECT de "
            + "FROM VerwerkingEntity de "
            + "WHERE de.dcnId = :dcnId order by de.dcnSeq desc")
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.READ_ONLY, value = "true") })
    Page<VerwerkingEntity> getVerwerkingenByDcnId(@Param("dcnId") String dcnId, Pageable pageable);

    /**
     * Returns the {@link VerwerkingEntity internal id's of document events} for the latest proces per source.
     *
     * @param  sourceLabel sourceName of Verwerkingen
     * @return             a list of {@link VerwerkingEntity} dcnIds
     *
     * @see                VerwerkingEntity
     */
    @Query("SELECT de.dcnId "
            + "FROM VerwerkingEntity de, VerwerkingStatusEntity ds "
            + "WHERE   de.sourceLabel = :sourceLabel "
            + "    AND de.dcnId=ds.dcnId "
            + "    AND de.procesId=ds.lastProcesId "
            + "ORDER BY de.timeCreated ASC")
    List<String> getVerwerkingDcnIdsBySourceName(@Param("sourceLabel") String sourceLabel);

    /**
     * Returns the set of source name list those have a reference in {@link VerwerkingEntity document event table}
     *
     * @return List<String> distinct list of source names
     */
    @Query(nativeQuery = true, value = "WITH RECURSIVE cte AS ( "
            + "   (SELECT source_label FROM Verwerkingen ORDER BY source_label ASC LIMIT 1) "
            + "   UNION ALL "
            + "   SELECT ( "
            + "      SELECT source_label FROM Verwerkingen "
            + "      WHERE source_label > t.source_label "
            + "      ORDER BY source_label ASC LIMIT 1 "
            + "   )"
            + "   FROM cte t "
            + "   WHERE t.source_label IS NOT NULL "
            + ") "
            + "SELECT * FROM cte WHERE cte.source_label IS NOT NULL")
    List<String> getSourceNames();

    /**
     * Returns the {@link ExceptieEntity verwerking and regarding processing error} where the latest proces caused
     * processing error for any source.
     *
     * @param  sourceName source name of document event
     * @param  pageable   for handling paginated calls
     * @return            {@link ExceptieEntity} Document Event Status Response
     */
    @Query("SELECT pe "
            + " FROM VerwerkingEntity de "
            + "    JOIN ExceptieEntity pe ON pe.verwerkingId = de.id "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "ORDER BY de.timeCreated DESC")
    Page<ExceptieEntity> getLaatsteStatusVerwerkingen(Pageable pageable);

    /**
     * Returns the {@link ExceptieEntity document event and regarding processing error} where the latest proces caused
     * processing error per source.
     *
     * @param  sourceLabel source name of document event
     * @param  pageable    for handling paginated calls
     * @return             {@link ExceptieEntity} Document Event Status Response
     */
    @Query("SELECT pe "
            + "FROM VerwerkingEntity de "
            + "    JOIN ExceptieEntity pe ON pe.verwerkingId = de.id "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "WHERE de.sourceLabel = :sourceLabel "
            + "ORDER BY de.timeCreated DESC")
    Page<ExceptieEntity> getLaatsteStatusVerwerkingenBySource(@Param("sourceLabel") String sourceLabel, Pageable pageable);

    /**
     * Returns the {@link ExceptieEntity document event and regarding processing error} where the latest proces caused a
     * particular exception per source.
     *
     * @param  sourceName source name of document event
     * @param  exception  processing error exception
     * @param  pageable   for handling paginated calls
     * @return            ExceptieEntity Response
     */
    @Query("SELECT pe "
            + "FROM VerwerkingEntity de "
            + "    JOIN ExceptieEntity pe ON pe.verwerkingId = de.id "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "WHERE pe.exceptionClass = :exception "
            + "    AND de.sourceLabel = :sourceLabel "
            + "ORDER BY de.timeCreated DESC")
    Page<ExceptieEntity> getLaatsteStatusVerwerkingenBySourceAndExceptie(@Param("sourceLabel") String sourceLabel, @Param("exception") String exception,
            Pageable pageable);

    /**
     * Returns the {@link SeveritySummaryResponse severity count by diagnose parameters per source} where the latest proces
     * caused a particular {@link nl.overheid.koop.plooi.registration.model.Diagnose} severity.
     *
     * @param  sourceName source name of document event
     * @param  severity   severity of requested diagnoses
     * @param  pageable   for handling paginated calls
     * @return            {@link SeveritySummaryResponse} Severity summary response
     */
    @Query("SELECT new nl.overheid.koop.plooi.registration.model.SeveritySummaryResponse(diag.target, diag.sourceLabel, count(diag.id)) "
            + "FROM VerwerkingEntity de "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "    JOIN DiagnoseEntity diag ON diag.verwerkingId = de.id "
            + "WHERE de.sourceLabel = :sourceLabel "
            + "    AND diag.severity = :severity "
            + "GROUP BY diag.target, diag.sourceLabel")
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    Page<SeveritySummaryResponse> getLatestMappingErrorSummaryBySourceAndSeverity(@Param("sourceLabel") String sourceLabel,
            @Param("severity") Severity severity, Pageable page);

    /**
     * Returns the {@link VerwerkingEntity} where the latest proces caused a particular
     * {@link nl.overheid.koop.plooi.registration.model.Diagnose}. Via the query parameters, the required diagnoses can be
     * specified.
     *
     * @param  sourceLabel source name of document event
     * @param  target      target of requested diagnoses
     * @param  sourceLabel source label of request diagnoses
     * @param  severity    severity of requested diagnoses
     * @param  pageable    for handling paginated calls
     * @return             {@link VerwerkingEntity} Document event
     */
    @Query("SELECT  new nl.overheid.koop.plooi.registration.model.LastVerwerkingState( de.timeCreated, de.dcnId)  "
            + "FROM VerwerkingEntity de "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "    JOIN DiagnoseEntity diag ON diag.verwerkingId = de.id "
            + "WHERE de.sourceLabel = :sourceName "
            + "    AND diag.target = :target"
            + "    AND diag.sourceLabel = :sourceLabel "
            + "    AND diag.severity = :severity "
            + "GROUP BY de.dcnId, de.timeCreated "
            + "ORDER BY de.timeCreated DESC")
    Page<LastVerwerkingState> getVerwerkingenWithLatestDiagnoses(@Param("sourceName") String sourceName, @Param("target") String target,
            @Param("sourceLabel") String sourceLabel, @Param("severity") Severity severity, Pageable pageable);

    /**
     * Returns the count per source where the latest proces caused a particular
     * {@link nl.overheid.koop.plooi.registration.model.Diagnose} by severity.
     *
     * @param  pageable for handling paginated calls
     * @return          Count per source
     */
    @Query("SELECT de.sourceLabel, COUNT(de.sourceLabel) "
            + "FROM VerwerkingEntity de "
            + "    JOIN VerwerkingStatusEntity  ds ON ds.dcnId = de.dcnId and ds.lastProcesId = de.procesId "
            + "    JOIN DiagnoseEntity diag on diag.verwerkingId = de.id "
            + "WHERE diag.severity = :severity "
            + "GROUP BY de.sourceLabel")
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    List<Tuple> getLatestDiagnoseSeverityCountPerSourceBySeverity(@Param("severity") Severity severity);

    /**
     * Returns the count per source where the latest proces caused a processing error.
     *
     * @return Count per source
     */
    @Query("SELECT de.sourceLabel, COUNT(de.sourceLabel) "
            + "FROM VerwerkingEntity de "
            + "    JOIN VerwerkingStatusEntity ds ON (de.dcnId = ds.dcnId AND ds.lastProcesId = de.procesId) "
            + "WHERE de.severity = :severity "
            + "GROUP BY de.sourceLabel")
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    List<Tuple> getLatestEventSeverityCountPerSourceBySeverity(@Param("severity") Severity severity);

    @Query("SELECT de.procesId, COUNT(distinct de.dcnId) "
            + "FROM VerwerkingEntity de "
            + "WHERE de.procesId IN :procesIds "
            + "    AND de.dcnId IS NOT NULL "
            + "GROUP BY de.procesId")
    List<Tuple> getVerwerkingCountOnProcesIds(@Param("procesIds") List<String> procesIds);

    @Query("SELECT de.procesId, COUNT(de.id) "
            + "FROM VerwerkingEntity de "
            + "WHERE de.severity = :severity "
            + "    AND de.procesId IN :procesIds "
            + "    AND de.dcnId IS NOT NULL "
            + "GROUP BY de.procesId")
    List<Tuple> getVerwerkingErrorsOnProcesIds(@Param("procesIds") List<String> procesIds, @Param("severity") Severity severity);

    @Query("SELECT de.procesId, COUNT(*) "
            + "FROM VerwerkingEntity de "
            + "WHERE de.procesId IN :procesIds "
            + "    AND de.dcnId IS NULL "
            + "GROUP BY de.procesId")
    List<Tuple> getExceptiesOnProcessen(@Param("procesIds") List<String> procesIds);

    /**
     * Returns the {@link ProcesVerwerkingSeverityStats} by procesId. The statistics presents Verwerking related counts for
     * every severity level.
     *
     * @return {@link ProcesVerwerkingSeverityStats} Count per severity level
     */
    @Query("SELECT new nl.overheid.koop.plooi.registration.model.ProcesVerwerkingSeverityStats("
            + "     COUNT(de.id) AS total_count,"
            + "     COUNT(distinct de.dcnId) AS document_count,"
            + "     SUM(CASE WHEN de.severity='OK' THEN 1 ELSE 0 END) AS ok_count,"
            + "     SUM(CASE WHEN de.severity='INFO' THEN 1 ELSE 0 END) AS info_count,"
            + "     SUM(CASE WHEN de.severity='WARNING' THEN 1 ELSE 0 END) AS warning_count,"
            + "     SUM(CASE WHEN de.severity='ERROR' THEN 1 ELSE 0 END) AS error_count,"
            + "     SUM(CASE WHEN de.severity='EXCEPTION' THEN 1 ELSE 0 END) AS exception_count"
            + ") "
            + "FROM VerwerkingEntity de "
            + "WHERE de.procesId = :procesId "
            + "    AND de.dcnId IS NOT NULL")
    @QueryHints({ @QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true") })
    ProcesVerwerkingSeverityStats getProcesSeverityStatistieken(@Param("procesId") String procesId);

    @Query("SELECT de "
            + "FROM VerwerkingEntity de "
            + " WHERE de.procesId =:procesId AND de.dcnId is not null "
            + "ORDER BY de.dcnSeq DESC")
    Page<VerwerkingEntity> getVerwerkingByProcesId(@Param("procesId") String procesId, Pageable pageable);

    @Query("SELECT de "
            + "FROM VerwerkingEntity de "
            + "WHERE de.procesId =:procesId AND de.severity =:severity "
            + "AND de.dcnId is not null "
            + "ORDER BY de.dcnSeq DESC")
    Page<VerwerkingEntity> getVerwerkingByProcesIdAndSeverity(@Param("procesId") String procesId, @Param("severity") Severity severity, Pageable pageable);

    @Query("SELECT pe "
            + "FROM VerwerkingEntity de "
            + "LEFT JOIN ExceptieEntity pe ON pe.verwerkingId = de.id "
            + "WHERE de.procesId =:procesId "
            + "AND de.dcnId is null "
            + "ORDER BY de.dcnSeq DESC")
    Page<ExceptieEntity> getProcesExcepties(@Param("procesId") String procesId, Pageable pageable);
}
