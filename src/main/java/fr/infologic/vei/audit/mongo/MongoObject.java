package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.infologic.vei.audit.api.AuditFind.Content;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailTrace;
import fr.infologic.vei.audit.mongo.json.MongoJson;

class MongoObject implements PatchableTrailTrace
{
    static final String _ID = "_id";
    static final String VERSION = "version";
    static final String KEY = "key";
    static final String GROUP = "group";
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
    public String getGroup()
    {
        return (String) object.get(GROUP);
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
    public String toString()
    {
        return String.format("MongoObject [type=%s, object=%s]", type, object);
    }

    static List<MongoObject> toList(String type, Cursor it)
    {
        try
        {
            return stream(it).map(o -> new MongoObject(type, o)).collect(Collectors.toList());
        }
        finally
        {
            it.close();
        }
    }

    private static Stream<DBObject> stream(Cursor it)
    {
        return StreamSupport.stream(spliteratorUnknownSize(it, 0), false);
    }

    static MongoObject toObject(String type, DBObject object)
    {
        return object == null ? null : new MongoObject(type, object);
    }

    static DBObject toDBObject(TrailTrace trace)
    {
        if(trace instanceof MongoObject)
        {
            return ((MongoObject) trace).object;
        }
        return start(KEY, trace.getKey())
                .add(GROUP, trace.getGroup())
                .add(VERSION, trace.getVersion())
                .add(CONTENT, convert(trace.getContent()))
                .add(METADATA, start(trace.getMetadata()).get())
                .get();
    }

    private static BSONObject convert(Content object)
    {
        if(object instanceof MongoJson)
        {
            return ((MongoJson) object).getBSONObject();
        }
        return (BSONObject) JSON.parse(object.asString());
    }
    
    @Override
    public String asString()
    {
        return String.format("AuditTrace [type=%s, group=%s, key=%s, version=%d, metadata=%s, content=%s]",
                      type, getGroup(), getKey(), getMetadata(), getVersion(), getContent().asString());
    }

}
