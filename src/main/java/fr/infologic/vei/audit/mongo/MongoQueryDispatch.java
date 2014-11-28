package fr.infologic.vei.audit.mongo;

import com.mongodb.DB;

import fr.infologic.vei.audit.api.AuditQuery.TraceAllQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceFieldQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryDispatch;

class MongoQueryDispatch implements TraceQueryDispatch, TraceFieldQueryBuilder
{

    private DB db;
    private String field;

    MongoQueryDispatch(DB db)
    {
        this.db = db;
    }

    @Override
    public TraceFieldQueryBuilder forModificationsOf(String field)
    {
        this.field = field;
        return this;
    }
    
    @Override
    public TraceQueryBuilder inType(String requestedType)
    {
        return new MongoFieldModificationsQuery(db, requestedType, field);
    }

    @Override
    public TraceAllQueryBuilder forAllModifications()
    {
        return new MongoAllModificationsQuery(db);
    }
}
