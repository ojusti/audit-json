package fr.infologic.vei.audit.mongo;

import static fr.infologic.vei.audit.mongo.MongoObject.KEY;
import static fr.infologic.vei.audit.mongo.MongoObject.VERSION;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailQuery;

class MongoQuery implements PatchableTrailQuery
{
    private final DBCollection collection;
    private final String key;

    MongoQuery(DBCollection collection, String key)
    {
        this.collection = collection;
        this.key = key;
    }

    @Override
    public MongoObject last()
    {
        return MongoObject.object(collection.getName(), _last());
    }
    
    @Override
    public List<MongoObject> all()
    {
        return MongoObject.list(collection.getName(), trace().sort(asc(VERSION)));
    }

    DBObject _last()
    {
        return trace().sort(desc(VERSION)).one();
    }

    DBCursor trace()
    {
        return collection.find(eq(KEY, key));
    }

    static DBObject desc(String key)
    {
        return eq(key, -1);
    } 
    static DBObject asc(String key)
    {
        return eq(key, 1);
    } 
    static DBObject eq(String key, Object value)
    {
        return new BasicDBObject(key, value);
    }
}
