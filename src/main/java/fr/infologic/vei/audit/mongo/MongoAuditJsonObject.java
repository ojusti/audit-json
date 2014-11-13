package fr.infologic.vei.audit.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.infologic.vei.audit.AuditJsonObject;

class MongoAuditJsonObject extends AuditJsonObject
{
    static final String VERSION = "version";
    static final String KEY = "key";
    static final String CONTENT = "content";
    static final String METADATA = "metadata";
    
    private MongoAuditJsonObject(String type, DBObject object)
    {
        this.type = type;
        this.key = (String) object.get(KEY);
        this.metadata = (Map<String, Object>) object.get(METADATA);
        this.objectAsJson = JSON.serialize(object.get(CONTENT));
    }

    static List<AuditJsonObject> list(String type, DBCursor it)
    {
        List<AuditJsonObject> result = new ArrayList<>();
        try
        {
            it.forEach(o -> result.add(new MongoAuditJsonObject(type, o)));
        }
        finally
        {
            it.close();
        }
        return result;
    }

    static AuditJsonObject object(String type, DBObject object)
    {
        return object == null ? null : new MongoAuditJsonObject(type, object);
    }
}
