package fr.infologic.vei.audit;

import java.util.HashMap;
import java.util.Map;

import fr.infologic.vei.audit.api.AuditTrace;
import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailObject;
import fr.infologic.vei.audit.mongo.json.MongoJson;


public class TestAuditJsonObject extends AuditTrace implements TrailObject
{
    public TestAuditJsonObject(String type, String key)
    {
        this.type = type;
        this.key = key;
        this.content = "{}";
        this.metadata = new HashMap<>();
    }
    public TestAuditJsonObject withContent(String content)
    {
        this.content = content;
        return this;
    }
    public TestAuditJsonObject addMetadata(String key, Object value)
    {
        this.metadata.put(key, value);
        return this;
    }
    @Override
    public int getVersion()
    {
        return 0;
    }
    @Override
    public Map<String, Object> getMetadata()
    {
        return metadata;
    }
    @Override
    public Content getContent()
    {
        return MongoJson.fromString(content);
    }
}
