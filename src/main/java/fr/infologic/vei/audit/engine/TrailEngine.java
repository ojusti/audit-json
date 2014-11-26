package fr.infologic.vei.audit.engine;

import java.util.List;
import java.util.Map;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailQuery;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.api.QueryDriver.TraceQueryBuilder;

public interface TrailEngine
{
    TrailType type(String type);
    
    interface Trail
    {
        TrailRecord setContent(Content json);
        PatchableTrailQuery query();
    }
    interface TrailRecord
    {
        TrailRecord setMetadata(Map<String, Object> metadata);
        void save();
    }
    public interface PatchableTrailQuery extends TrailQuery
    {
        @Override
        PatchableTrailTrace last();
        @Override
        List<? extends PatchableTrailTrace> all();
        @Override
        List<? extends PatchableTrailTrace> allFromVersion(int minVersion);
    }
    public interface PatchableTrailTrace extends TrailTrace, Content
    {
        @Override
        PatchableTrailTrace diff(Content original);
        @Override
        PatchableTrailTrace applyTo(Content original);
    }
    PatchableTrailQuery query(String type, String key);
    void save(TrailTrace trace);
    Content convertContent(String object);
    TraceQueryBuilder makeQuery();
}
