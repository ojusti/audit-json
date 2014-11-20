package fr.infologic.vei.audit.engine;

import java.util.Map;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailObject;
import fr.infologic.vei.audit.api.AuditDriver.TrailQuery;
import fr.infologic.vei.audit.engine.TrailEngine.Trail;
import fr.infologic.vei.audit.engine.TrailEngine.TrailRecord;

class DiffTrail implements Trail, TrailRecord, TrailObject
{
    private final TrailType type;
    private final String key;
    private TrailQuery query;
    private Content content;
    private Map<String, Object> metadata;

    DiffTrail(TrailType type, String key)
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
        type.insert(this);
    }

    int incVersion(TrailObject last)
    {
        return last == null ? 0 : last.getVersion() + 1;
    }
    
    @Override
    public TrailQuery query()
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
        return incVersion(query().last());
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
