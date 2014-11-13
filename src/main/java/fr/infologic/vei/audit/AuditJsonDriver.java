package fr.infologic.vei.audit;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.infologic.vei.audit.AuditJsonDBDriver.AuditJsonRecord;


public class AuditJsonDriver implements DBAdmin
{
    private final AuditJsonDBDriver db;

    public AuditJsonDriver(AuditJsonDBDriver db)
    {
        this.db = db;
    }

    public void trace(AuditJsonObject object) 
    {
        get(object).content((DBObject) JSON.parse(object.objectAsJson))
                   .metadata(object.metadata)
                   .append();
    }
    
    private AuditJsonRecord get(AuditJsonKey key)
    {
        return db.type(key.type).record(key.key);
    }

    public AuditJsonRecordQuery find(AuditJsonKey key)
    {
        return get(key).query();
    }

    @Override
    public void drop()
    {
        db.drop();
    }

    @Override
    public void close()
    {
        db.close();
    }
    
    @Override
    public boolean isAlive()
    {
        return db.isAlive();
    }
}
