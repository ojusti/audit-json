package fr.infologic.vei.audit.gateway;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditTrace;
import fr.infologic.vei.audit.api.TrailKey;
import fr.infologic.vei.audit.engine.PatchedTrailQuery;
import fr.infologic.vei.audit.engine.TrailEngine;
import fr.infologic.vei.audit.engine.TrailEngine.Trail;


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
        trailFor(trace).setContent(engine.convertContent(trace.content))
                       .setMetadata(trace.metadata)
                       .save();
    }

    private Trail trailFor(TrailKey key)
    {
        return engine.type(key.getType()).trail(key.getKey());
    }

    @Override
    public TrailQuery find(TrailKey key)
    {
        return new PatchedTrailQuery(trailFor(key).query());
    }

    @Override
    public AdminDB db()
    {
        return db;
    }

    @Override
    public TraceQueryBuilder makeQuery()
    {
        return engine.makeQuery();
    }
}
