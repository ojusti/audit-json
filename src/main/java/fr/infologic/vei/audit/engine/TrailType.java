package fr.infologic.vei.audit.engine;

import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailFind;


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

    PatchableTrailFind query(String key)
    {
        return db.find(type, key);
    }

    void save(TrailTrace trace)
    {
        db.save(trace);
    }

}
