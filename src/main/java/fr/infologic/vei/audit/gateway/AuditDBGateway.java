package fr.infologic.vei.audit.gateway;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditTrace;
import fr.infologic.vei.audit.api.TrailKey;
import fr.infologic.vei.audit.engine.PatchedTrailFind;
import fr.infologic.vei.audit.engine.Trail;
import fr.infologic.vei.audit.engine.TrailEngine;


class AuditDBGateway implements AuditGateway
{
    private final TrailEngine engine;
    private final AdminDB db;

    AuditDBGateway(TrailEngine engine, AdminDB db)
    {
        this.engine = engine;
        this.db = db;
    }

    @Override
    public void trace(AuditTrace trace) 
    {
        trailFor(trace).setContent(engine.toContent(trace.content))
                       .setMetadata(trace.metadata)
                       .save();
    }

    private Trail trailFor(TrailKey key)
    {
        return engine.type(key.getType(), key.getGroup()).trail(key.getKey());
    }

    @Override
    public TrailFind find(TrailKey key)
    {
        return new PatchedTrailFind(trailFor(key).find());
    }

    @Override
    public AdminDB db()
    {
        return db;
    }

    @Override
    public TraceQueryDispatch makeQuery()
    {
        return engine.makeQuery();
    }
}
