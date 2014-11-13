package fr.infologic.vei.audit;

import java.util.HashMap;


public class TestAuditJsonObject extends AuditJsonObject
{
    public TestAuditJsonObject(String type, String key)
    {
        this.type = type;
        this.key = key;
        this.objectAsJson = "{}";
        this.metadata = new HashMap<>();
    }
    TestAuditJsonObject withContent(String content)
    {
        this.objectAsJson = content;
        return this;
    }
    TestAuditJsonObject addMetadata(String key, Object value)
    {
        this.metadata.put(key, value);
        return this;
    }
}
