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
        /** shortcut from allFromVersion(1) */
        List<? extends TrailTrace> all();
        List<? extends TrailTrace> allFromVersion(int minVersion);
    }
    public interface TrailTrace extends TrailTraceKey
    {
        Map<String, Object> getMetadata();
        Content getContent();
    }
    public interface Content
    {
        Content applyTo(Content original);
        Content diff(Content original);
    }
}
