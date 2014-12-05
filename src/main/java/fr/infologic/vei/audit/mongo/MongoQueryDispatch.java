package fr.infologic.vei.audit.mongo;

import java.util.Set;
import java.util.function.Function;

import com.mongodb.DB;

import fr.infologic.vei.audit.api.AuditQuery.TraceAllQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceFieldQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryDispatch;

class MongoQueryDispatch implements TraceQueryDispatch, TraceFieldQueryBuilder, TraceAllQueryBuilder
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
    public TraceQueryBuilder inType(String requestedType, String requestedGroup)
    {
        return new MongoFieldModificationsQuery(db, requestedType, requestedGroup, field);
    }

    @Override
    public TraceAllQueryBuilder forAllModifications()
    {
        return this;
    }
    
    @Override
    public TraceQueryBuilder ofAnyTypeInSet(Set<String> requestedTypes, Function<String, Object> typeDependantGroup)
    {
        return new MongoAllModificationsQuery(db, requestedTypes, typeDependantGroup);
    }
}
