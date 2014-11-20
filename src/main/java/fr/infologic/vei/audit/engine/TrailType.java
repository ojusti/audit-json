package fr.infologic.vei.audit.engine;

import fr.infologic.vei.audit.api.AuditDriver.TrailObject;
import fr.infologic.vei.audit.api.AuditDriver.TrailQuery;
import fr.infologic.vei.audit.engine.TrailEngine.Trail;


public class TrailType
{
    private TrailEngine db;
    String type;

    public TrailType(TrailEngine db, String type)
    {
        this.db = db;
        this.type = type;
    }

    public Trail trail(String key)
    {
        return new DiffTrail(this, key);
    }

    TrailQuery query(String key)
    {
        return db.query(type, key);
    }

    void insert(TrailObject object)
    {
        db.save(object);
    }

}
