package fr.infologic.vei.audit.api;


public interface AuditUpdate
{
    void trace(AuditTrace trace);
    void ingest(AuditIngestTrace patch);
}
