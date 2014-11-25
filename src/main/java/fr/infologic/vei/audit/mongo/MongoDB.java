package fr.infologic.vei.audit.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.api.QueryDriver.TraceQueryBuilder;
import fr.infologic.vei.audit.engine.TrailEngine;
import fr.infologic.vei.audit.engine.TrailType;
import fr.infologic.vei.audit.mongo.json.MongoJson;


public class MongoDB implements TrailEngine, AdminDB
{
    private final MongoClient mongo;
    private final DB db;
    public MongoDB()
    {
        this(null, null, "audit");
    }
    
    public MongoDB(String host, Integer port, String db)
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
    public TrailType type(String type)
    {
        return new TrailType(this, type);
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

    @Override
    public MongoQuery query(String type, String key)
    {
        return new MongoQuery(db.getCollection(type), key);
    }

    @Override
    public void save(TrailTrace trace)
    {
        db.getCollection(trace.getType()).save(MongoObject.toDBObject(trace));
    }

    @Override
    public MongoJson convertContent(String object)
    {
        return MongoJson.fromString(object);
    }

    @Override
    public TraceQueryBuilder makeQuery()
    {
        return new MongoGlobalQuery(db);
    }
   
}
