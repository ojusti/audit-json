package fr.infologic.vei.audit.api;

import java.util.List;
import java.util.Map;

public interface AuditDriver
{
    void trace(AuditTrace trace);
    TrailQuery find(TrailKey key);
    
    public interface TrailQuery
    {
        TrailTrace last();
        List<? extends TrailTrace> all();
    }
    public interface TrailTrace extends TrailKey
    {
        int getVersion();
        Map<String, Object> getMetadata();
        Content getContent();
    }
    public interface Content
    {
        Content applyTo(Content original);
        Content diff(Content original);
    }
}
