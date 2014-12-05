package fr.infologic.vei.audit.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditFind.Content;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine;
import fr.infologic.vei.audit.engine.TrailType;
import fr.infologic.vei.audit.mongo.json.MongoJson;


public class MongoDB implements TrailEngine, AdminDB
{
    private final MongoClient mongo;
    private final DB db;
    public MongoDB() throws AdminDBException
    {
        this(null, null, "audit");
    }
    
    public MongoDB(String host, Integer port, String db) throws AdminDBException
    {
        try
        {
            this.mongo = new MongoClient(host, port == null ? ServerAddress.defaultPort() : port.intValue());
            this.db = mongo.getDB(db);
        }
        catch (UnknownHostException e)
        {
            throw new AdminDBException(e);
        }
    }

    @Override
    public TrailType type(String type, String group)
    {
        return new TrailType(this, type, group);
    }
    
    @Override
    public void save(TrailTrace trace)
    {
        db.getCollection(trace.getType()).save(MongoObject.toDBObject(trace));
    }

    @Override
    public Content toContent(String object)
    {
        return MongoJson.fromString(object);
    }
    
    @Override
    public PatchableTrailFind find(String type, String group, String key)
    {
        return new MongoFind(db.getCollection(type), group, key);
    }
    
    @Override
    public TraceQueryDispatch makeQuery()
    {
        return new MongoQueryDispatch(db);
    }
    
    @Override
    public void drop() throws AdminDBException
    {
        try
        {
            db.dropDatabase();
            close();
        }
        catch(MongoException e)
        {
            throw new AdminDBException(e);
        }
    }
    
    @Override
    public void close() throws AdminDBException
    {
        try
        {
            mongo.close();
        }
        catch(MongoException e)
        {
            throw new AdminDBException(e);
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
   
}
