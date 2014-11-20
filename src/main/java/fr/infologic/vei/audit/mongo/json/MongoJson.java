package fr.infologic.vei.audit.mongo.json;

import org.bson.BasicBSONObject;

import com.mongodb.util.JSON;

import fr.infologic.vei.audit.api.AuditDriver.Content;

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
    public MongoJson apply(Content patch)
    {
        return apply(convert(patch));
    }
    private MongoJson apply(MongoJson patch)
    {
        if(patch.object == null)
        {
            return patch;
        }
        if(patch.object.isEmpty())
        {
            return this;
        }
        BasicBSONObject original = object == null ? new BasicBSONObject() : object;
        return new MongoJson(JsonMerge.merge(original, patch.object));
    }
    private static MongoJson convert(Content patch)
    {
        if(patch == null || patch instanceof MongoJson)
        {
            return (MongoJson) patch;
        }
        return MongoJson.fromString(patch.toString());
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
        return JSON.serialize(object);
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
