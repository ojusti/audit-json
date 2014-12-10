package fr.infologic.vei.audit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.infologic.vei.audit.api.AuditFind.Content;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.AuditTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailTrace;
import fr.infologic.vei.audit.mongo.json.MongoJson;


public class TestAuditJsonObject extends AuditTrace implements PatchableTrailTrace
{
    private MongoJson json;
    public TestAuditJsonObject(String type, String group, String key)
    {
        this.type = type;
        this.group = group;
        this.key = key;
        withContent("{}");
        this.metadata = new HashMap<>();
    }
    public static TestAuditJsonObject make()
    {
        return new TestAuditJsonObject("type", null, "key");
    }
    public TestAuditJsonObject withContent(String content)
    {
        this.content = content;
        this.json = MongoJson.fromString(content);
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
    public MongoJson getContent()
    {
        return json;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other == null)
        {
            return false;
        }
        if(!(other instanceof TrailTrace))
        {
            return false;
        }
        TrailTrace o = (TrailTrace) other;
        return Objects.equals(getType(), o.getType())
            && Objects.equals(getGroup(), o.getGroup()) 
            && Objects.equals(getKey(), o.getKey()) 
            && Objects.equals(getMetadata(), o.getMetadata()) 
            && Objects.equals(getContent(), o.getContent());   
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(getType(), getGroup(), getKey(), getMetadata(), getContent());
    }
    
    @Override
    public TestAuditJsonObject diff(Content original)
    {
        TestAuditJsonObject diff = new TestAuditJsonObject(type, group, key);
        diff.metadata = metadata;
        diff.json = json.diff(original);
        diff.content = diff.json.asString();
        return diff;
    }
    
    @Override
    public TestAuditJsonObject applyTo(Content original)
    {
        TestAuditJsonObject merge = new TestAuditJsonObject(type, group, key);
        merge.metadata = metadata;
        merge.json = json.applyTo(original);
        merge.content = merge.json.asString();
        return merge;
    }
    @Override
    public String asString()
    {
        return toString();
    }
}
