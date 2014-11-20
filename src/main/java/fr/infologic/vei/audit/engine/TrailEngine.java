package fr.infologic.vei.audit.engine;

import java.util.Map;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailObject;
import fr.infologic.vei.audit.api.AuditDriver.TrailQuery;

public interface TrailEngine
{
    TrailType type(String type);
    
    interface Trail
    {
        TrailRecord setContent(Content json);
        TrailQuery query();
    }
    interface TrailRecord
    {
        TrailRecord setMetadata(Map<String, Object> metadata);
        void save();
    }
    TrailQuery query(String type, String key);
    void save(TrailObject object);
    Content convertContent(String object);
}
