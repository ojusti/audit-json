package fr.infologic.vei.audit.mongo;

import static fr.infologic.vei.audit.mongo.MongoObject.KEY;
import static fr.infologic.vei.audit.mongo.MongoObject.VERSION;
import static fr.infologic.vei.audit.mongo.MongoObject.toList;
import static fr.infologic.vei.audit.mongo.MongoObject.toObject;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailFind;

class MongoFind implements PatchableTrailFind
{
    private final DBCollection collection;
    private final String key;

    MongoFind(DBCollection collection, String key)
    {
        this.collection = collection;
        this.key = key;
    }

    @Override
    public MongoObject last()
    {
        return toObject(collection.getName(), lastTrace());
    }
    
    @Override
    public List<MongoObject> all()
    {
        return toList(collection.getName(), trail().sort(asc()));
    }
    
    @Override
    public List<MongoObject> allFromVersion(int minVersion)
    {
        return toList(collection.getName(), trail().skip(minVersion - 1).sort(asc()));
    }

    private DBObject lastTrace()
    {
        return trail().sort(desc()).one();
    }

    private DBCursor trail()
    {
        return collection.find(eq(KEY, key));
    }

    private static DBObject desc()
    {
        return eq(VERSION, -1);
    } 
    private static DBObject asc()
    {
        return eq(VERSION, 1);
    } 
    private static DBObject eq(String key, Object value)
    {
        return new BasicDBObject(key, value);
    }
}
