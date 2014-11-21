package fr.infologic.vei.audit.engine;

import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailQuery;
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
        return new PatchTrail(this, key);
    }

    PatchableTrailQuery query(String key)
    {
        return db.query(type, key);
    }

    void save(TrailTrace trace)
    {
        db.save(trace);
    }

}
