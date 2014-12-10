package fr.infologic.vei.audit.engine;

import java.util.List;
import java.util.Map;

import fr.infologic.vei.audit.api.AuditFind.Content;
import fr.infologic.vei.audit.api.AuditFind.TrailFind;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.AuditQuery;

public interface TrailEngine extends AuditQuery
{
    TrailType type(String type, String group);
    void save(TrailTrace trace);
    Content toContent(String object);
    
    PatchableTrailFind find(String type, String group, String key);
    
    interface TrailRecord
    {
        TrailRecord setMetadata(Map<String, Object> metadata);
        void save();
        void ingest(int version);
    }
    interface PatchableTrailFind extends TrailFind
    {
        @Override
        PatchableTrailTrace last();
        @Override
        List<? extends PatchableTrailTrace> all();
        @Override
        List<? extends PatchableTrailTrace> allFromVersion(int minVersion);
    }
    interface PatchableTrailTrace extends TrailTrace, Content
    {
        @Override
        PatchableTrailTrace diff(Content original);
        @Override
        PatchableTrailTrace applyTo(Content original);
    }
    
}
