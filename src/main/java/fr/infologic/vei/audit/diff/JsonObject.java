package fr.infologic.vei.audit.diff;

import org.bson.BasicBSONObject;

import com.mongodb.util.JSON;

public class JsonObject
{
    private BasicBSONObject object;
    public static JsonObject fromString(String objectAsJson)
    {
        return new JsonObject((BasicBSONObject) JSON.parse(objectAsJson));
    }
    public JsonObject(BasicBSONObject object)
    {
        this.object = object;
    }
    
    /**
     * Based on RFC-7396 + recursive algorithm for objects and arrays + array support (arrays' elements are merged one by one)
     * @see JsonMerge#merge(BasicBSONObject, BasicBSONObject)
     */
    public JsonObject apply(JsonObject patch)
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
        return new JsonObject(JsonMerge.merge(original, patch.object));
    }
    
    /**
     * Based on RFC-7396 + recursive algorithm for objects and arrays + array support (arrays' elements are merged one by one)
     * @see JsonDiff#diff(BasicBSONObject, BasicBSONObject)
     */
    public JsonObject diff(JsonObject original)
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
        return new JsonObject(patch == null ? new BasicBSONObject() : patch);
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
        if(!(obj instanceof JsonObject)) { return false; }
        JsonObject other = (JsonObject) obj;
        if(object == null)
        {
            if(other.object != null) { return false; }
        }
        else if(!object.equals(other.object)) { return false; }
        return true;
    }
    
    

}
