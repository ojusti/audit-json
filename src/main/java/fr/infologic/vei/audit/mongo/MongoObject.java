package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailTrace;
import fr.infologic.vei.audit.mongo.json.MongoJson;

class MongoObject implements PatchableTrailTrace
{
    static final String VERSION = "version";
    static final String KEY = "key";
    static final String CONTENT = "content";
    static final String METADATA = "metadata";
    private final String type;
    private final DBObject object;
    
    private MongoObject(String type, DBObject object)
    {
        this.type = type;
        this.object = object;
    }
    
    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getKey()
    {
        return (String) object.get(KEY);
    }
    
    @Override
    public Map<String, Object> getMetadata()
    {
        return (Map<String, Object>) object.get(METADATA);
    }
    
    @Override
    public int getVersion()
    {
        return (int) object.get(VERSION);
    }
    
    @Override
    public MongoJson getContent()
    {
        return new MongoJson((BasicBSONObject) object.get(CONTENT));
    }
    
    static List<MongoObject> list(String type, DBCursor it)
    {
        List<MongoObject> result = new ArrayList<>();
        try
        {
            it.forEach(o -> result.add(new MongoObject(type, o)));
        }
        finally
        {
            it.close();
        }
        return result;
    }

    static MongoObject object(String type, DBObject object)
    {
        return object == null ? null : new MongoObject(type, object);
    }

    static DBObject toDBObject(TrailTrace object)
    {
        if(object instanceof MongoObject)
        {
            return ((MongoObject) object).object;
        }
        return start(KEY, object.getKey())
                .add(VERSION, object.getVersion())
                .add(CONTENT, convert(object.getContent()))
                .add(METADATA, start(object.getMetadata()).get())
                .get();
    }

    private static BSONObject convert(Content object)
    {
        if(object instanceof MongoJson)
        {
            return ((MongoJson) object).getBSONObject();
        }
        return (BSONObject) JSON.parse(object.toString());
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
            && Objects.equals(getKey(), o.getKey()) 
            && Objects.equals(getMetadata(), o.getMetadata()) 
            && Objects.equals(getContent(), o.getContent());   
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(getType(), getKey(), getMetadata(), getContent());
    }

    @Override
    public MongoObject diff(Content original)
    {
        BasicDBObject diff = new BasicDBObject(object.toMap());
        diff.put(CONTENT, getContent().diff(original).getBSONObject());
        return new MongoObject(type, diff);
    }


    @Override
    public MongoObject applyTo(Content original)
    {
        DBObject merge = new BasicDBObject(object.toMap());
        merge.put(CONTENT, getContent().applyTo(original).getBSONObject());
        return new MongoObject(type, merge);
    }
}
