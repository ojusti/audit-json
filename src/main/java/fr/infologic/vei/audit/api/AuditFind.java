package fr.infologic.vei.audit.api;

import java.util.List;
import java.util.Map;

public interface AuditFind
{
    TrailFind find(TrailKey key);
    
    public interface TrailFind
    {
        TrailTrace last();
        /** shortcut from allFromVersion(1) */
        List<? extends TrailTrace> all();
        List<? extends TrailTrace> allFromVersion(int minVersion);
        
        int count();
        void delete();
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
        String asString();
    }
}
