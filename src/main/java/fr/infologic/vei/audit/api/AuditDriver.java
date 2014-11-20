package fr.infologic.vei.audit.api;

import java.util.List;
import java.util.Map;

public interface AuditDriver
{
    void trace(AuditTrace trace);
    TrailQuery find(TrailKey key);
    
    public interface TrailQuery
    {
        TrailObject last();
        List<? extends TrailObject> all();
    }
    public interface TrailObject extends TrailKey
    {
        int getVersion();
        Map<String, Object> getMetadata();
        Content getContent();
    }
    public interface Content
    {
        Content apply(Content patch);
        Content diff(Content original);
    }
}
