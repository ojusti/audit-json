package fr.infologic.vei.audit.engine;

import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailFind;


public class TrailType
{
    private final TrailEngine db;
    final String type;
    final String group;

    public TrailType(TrailEngine db, String type, String group)
    {
        this.db = db;
        this.type = type;
        this.group = group;
    }

    public Trail trail(String key)
    {
        return new PatchTrail(this, key);
    }

    PatchableTrailFind query(String key)
    {
        return db.find(type, group, key);
    }

    void save(TrailTrace trace)
    {
        db.save(trace);
    }

}
