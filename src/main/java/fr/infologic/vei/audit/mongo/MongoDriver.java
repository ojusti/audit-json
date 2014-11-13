package fr.infologic.vei.audit.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import fr.infologic.vei.audit.AuditJsonDBDriver;


public class MongoDriver implements AuditJsonDBDriver
{
    private final MongoClient mongo;
    private final DB db;
    public MongoDriver()
    {
        this(null, null, "audit");
    }
    
    public MongoDriver(String host, Integer port, String db)
    {
        try
        {
            this.mongo = new MongoClient(host, port == null ? ServerAddress.defaultPort() : port.intValue());
            this.db = mongo.getDB(db);
        }
        catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuditJsonType type(String type)
    {
        return new MongoType(db.getCollection(type));
    }
    
    @Override
    public void drop()
    {
        try
        {
            db.dropDatabase();
            close();
        }
        catch(MongoException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void close()
    {
        try
        {
            mongo.close();
        }
        catch(MongoException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAlive()
    {
        try
        {
            mongo.getAddress();
            return true;
        }
        catch(Throwable t)
        {
            return false;
        }
    }
    


//    private void addJson(String collection, String... json)
//    {
//        List<DBObject> objects = new ArrayList<>(json.length);
//        for(String content : json)
//        {
//            objects.add((DBObject) JSON.parse(content));
//        }
//        collection.insert(objects);
//    }
//    
//    private void addJson(String... json)
//    {
//        List<DBObject> objects = new ArrayList<>(json.length);
//        for(String content : json)
//        {
//            objects.add((DBObject) JSON.parse(content));
//        }
//        collection.insert(objects);
//    }
//    
//    public List valuesOf(String field)
//    {
//        return collection.distinct(nested(field));
//    }
//
//    public int count(String field, String value)
//    {
//        return (int) collection.count(new BasicDBObjectBuilder().add(nested(field), value).get());
//    }
//    public int count()
//    {
//        return (int) collection.count();
//    }
//
//    private static String nested(String field)
//    {
//        return "object." + field;
//    }
}
