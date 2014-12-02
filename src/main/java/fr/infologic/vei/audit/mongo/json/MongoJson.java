package fr.infologic.vei.audit.mongo.json;

import static fr.infologic.vei.audit.mongo.json.JsonMerge.merge;

import java.io.IOException;

import org.bson.BasicBSONObject;

import com.cedarsoftware.util.io.JsonWriter;
import com.mongodb.util.JSON;

import fr.infologic.vei.audit.api.AuditFind.Content;

public class MongoJson implements Content
{
    private BasicBSONObject object;
    public static MongoJson fromString(String objectAsJson)
    {
        return new MongoJson((BasicBSONObject) JSON.parse(objectAsJson));
    }
    public MongoJson(BasicBSONObject object)
    {
        this.object = object;
    }
    
    /**
     * Based on RFC-7396 + recursive algorithm for objects and arrays + array support (arrays' elements are merged one by one)
     * @see JsonMerge#merge(BasicBSONObject, BasicBSONObject)
     */
    @Override
    public MongoJson applyTo(Content original)
    {
        return applyTo(convert(original));
    }
    private MongoJson applyTo(MongoJson original)
    {
        if(object == null)
        {
            return this;
        }
        if(object.isEmpty())
        {
            return original;
        }
        BasicBSONObject originalContent = original.object == null ? new BasicBSONObject() : original.object;
        return new MongoJson(merge(originalContent, object));
    }
    private static MongoJson convert(Content content)
    {
        if(content == null || content instanceof MongoJson)
        {
            return (MongoJson) content;
        }
        return MongoJson.fromString(content.toString());
    }
    /**
     * Based on RFC-7396 + recursive algorithm for objects and arrays + array support (arrays' elements are merged one by one)
     * @see JsonDiff#diff(BasicBSONObject, BasicBSONObject)
     */
    @Override
    public MongoJson diff(Content original)
    {
        return diff(convert(original));
    }
    
    private MongoJson diff(MongoJson original)
    {
        if(object == null)
        {
            return this;
        }
        if(original.object == null || original.object.isEmpty())
        {
            return this;
        }
        BasicBSONObject patch = JsonDiff.diff(object, original.object);
        return new MongoJson(patch == null ? new BasicBSONObject() : patch);
    }
    
    @Override
    public String toString()
    {
        if(object == null)
        {
            return null;
        }
        try
        {
            return JsonWriter.formatJson(JSON.serialize(object));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) { return true; }
        if(obj == null) { return false; }
        if(!(obj instanceof MongoJson)) { return false; }
        MongoJson other = (MongoJson) obj;
        if(object == null)
        {
            if(other.object != null) { return false; }
        }
        else if(!object.equals(other.object)) { return false; }
        return true;
    }
    public BasicBSONObject getBSONObject()
    {
        return object;
    }
}
