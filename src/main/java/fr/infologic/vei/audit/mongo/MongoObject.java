package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

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
    public String toString()
    {
        return String.format("MongoObject [type=%s, object=%s]", type, object);
    }

    static List<MongoObject> list(String type, DBCursor it)
    {
        try
        {
            return stream(it.spliterator(), false).map(o -> new MongoObject(type, o)).collect(toList());
        }
        finally
        {
            it.close();
        }
    }

    static MongoObject object(String type, DBObject object)
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
        return (BSONObject) JSON.parse(object.toString());
    }

}
