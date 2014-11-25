package fr.infologic.vei.audit.api;

public interface AuditQueryDriver
{
    AuditTraceCollection search(AuditTraceQuery query);
    
    public interface AuditTraceQuery
    {

    }

    public interface AuditTraceCollection
    {

    }
}
