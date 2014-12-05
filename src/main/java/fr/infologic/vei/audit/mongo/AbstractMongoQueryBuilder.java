package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static fr.infologic.vei.audit.mongo.MongoObjectBuilder.and;

import java.util.function.Function;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import fr.infologic.vei.audit.api.AuditQuery.TraceMetadataQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceQuery;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryBuilder;

abstract class AbstractMongoQueryBuilder implements TraceQueryBuilder
{
    private final BasicDBObjectBuilder query = start();
    private final Function<String, DBObject> queryGenerator;
    protected AbstractMongoQueryBuilder(Function<String, String> typeDependantFunction)
    {
        this.queryGenerator = new QueryWithDependantCriteria(MongoObject.GROUP, typeDependantFunction);
    }
    
    protected DBObject makeQueryForType(String type)
    {
        return queryGenerator.apply(type);
    }
    
    @Override
    public TraceQueryBuilder havingKeyEqualsTo(String requestedKey)
    {
        query.append(MongoObject.KEY, requestedKey);
        return this;
    }
    
    @Override
    public TraceQueryBuilder havingKeyMatches(Pattern regExp)
    {
        query.append(MongoObject.KEY, regExp);
        return this;
    }
    
    protected void addToQuery(MongoObjectBuilder queryFragment)
    {
        queryFragment.addTo(query);
    }
    
    @Override
    public TraceMetadataQueryBuilder havingMetadata()
    {
        return new MongoMetadataQueryBuilder();
    }
    
    private class MongoMetadataQueryBuilder implements TraceMetadataQueryBuilder
    {
        private final MongoObjectBuilder content = and();

        @Override
        public TraceMetadataQueryBuilder fieldEqualsTo(String field, Object requestedValue)
        {
            content.fieldEqualsTo(metadataField(field), requestedValue);
            return this;
        }
        @Override
        public TraceMetadataQueryBuilder fieldGreaterThan(String field, Object minValue)
        {
            content.fieldGreaterThan(metadataField(field), minValue);
            return this;
        }
        @Override
        public TraceMetadataQueryBuilder fieldLessThan(String field, Object maxValue)
        {
            content.fieldLessThan(metadataField(field), maxValue);
            return this;
        }
        
        private String metadataField(String field)
        {
            return MongoObject.METADATA + "." + field;
        }
        @Override
        public TraceQuery build()
        {
            addToQuery(content);
            return AbstractMongoQueryBuilder.this.build();
        }
    }
    
    private class QueryWithDependantCriteria implements Function<String, DBObject>
    {
        final String field;
        final Function<String, String> function;

        QueryWithDependantCriteria(String field, Function<String, String> function)
        {
            this.field = field;
            this.function = function;
        }

        @Override
        public DBObject apply(String type)
        {
            return start(query.get().toMap()).add(field, function == null ? null : function.apply(type)).get();
        }
        
    }
}
