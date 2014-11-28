package fr.infologic.vei.audit.mongo;

import java.util.List;

import com.mongodb.DB;

import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.AuditQuery.TraceQuery;

class MongoFieldModificationsQuery extends AbstractMongoQueryBuilder implements TraceQuery
{
    private DB db;
    private String requestedType;
    private String field;
    
    MongoFieldModificationsQuery(DB db, String requestedType, String field)
    {
        this.db = db;
        this.requestedType = requestedType;
        this.field = field;
    }

    @Override
    public TraceQuery build()
    {
        return this;
    }


    @Override
    public List<TrailTrace> search()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
