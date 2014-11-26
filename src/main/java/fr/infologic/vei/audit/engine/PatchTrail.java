package fr.infologic.vei.audit.engine;

import java.util.Map;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailQuery;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.Trail;
import fr.infologic.vei.audit.engine.TrailEngine.TrailRecord;

class PatchTrail implements Trail, TrailTrace, TrailRecord
{
    private final TrailType type;
    private final String key;
    private PatchableTrailQuery query;
    private Content content;
    private Map<String, Object> metadata;
    private int version;

    PatchTrail(TrailType type, String key)
    {
        this.type = type;
        this.key = key;
    }

    @Override
    public TrailRecord setContent(Content content)
    {
        this.content = content;
        return this;
    }

    @Override
    public TrailRecord setMetadata(Map<String, Object> metadata)
    {
        this.metadata = metadata;
        return this;
    }

    @Override
    public void save()
    {
        PatchableTrailTrace lastTrace = query().last();
        if(lastTrace == null)
        {
            this.version = 1;
        }
        else
        {
            this.version = lastTrace.getVersion() + 1;
            type.save(lastTrace.diff(getContent()));
        }
        type.save(this);
    }

    @Override
    public PatchableTrailQuery query()
    {
        if(query == null)
        {
            query = type.query(key);
        }
        return query;
    }

    @Override
    public String getType()
    {
        return type.type;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    public int getVersion()
    {
        return version;
    }

    @Override
    public Map<String, Object> getMetadata()
    {
        return metadata;
    }

    @Override
    public Content getContent()
    {
        return content;
    }
}
