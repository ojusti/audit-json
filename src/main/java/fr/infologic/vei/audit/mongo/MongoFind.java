package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static fr.infologic.vei.audit.mongo.MongoObject.GROUP;
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
    private final DBObject key;

    MongoFind(DBCollection collection, String group, String key)
    {
        this.collection = collection;
        this.key = start(KEY, key).add(GROUP, group).get();
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
    
    @Override
    public int count()
    {
        return trail().count();
    }
    
    @Override
    public void delete()
    {
        collection.remove(key);
        
    }

    private DBObject lastTrace()
    {
        return trail().sort(desc()).one();
    }

    private DBCursor trail()
    {
        return collection.find(key);
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
