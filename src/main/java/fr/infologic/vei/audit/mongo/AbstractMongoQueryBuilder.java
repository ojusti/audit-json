package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;

import com.mongodb.BasicDBObjectBuilder;

import fr.infologic.vei.audit.api.AuditQuery.TraceMetadataQueryBuilder;
import fr.infologic.vei.audit.api.AuditQuery.TraceQuery;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryBuilder;

abstract class AbstractMongoQueryBuilder implements TraceQueryBuilder
{
    protected final BasicDBObjectBuilder query = start();
    
    @Override
    public TraceQueryBuilder havingKeyEqualsTo(String requestedKey)
    {
        query.append(MongoObject.KEY, requestedKey);
        return this;
    }
    
    @Override
    public TraceMetadataQueryBuilder havingMetadata()
    {
        return new MongoMetadataQueryBuilder();
    }
    
    private class MongoMetadataQueryBuilder implements TraceMetadataQueryBuilder
    {
        private final MongoObjectBuilder content = new MongoObjectBuilder();

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
            content.addTo(AbstractMongoQueryBuilder.this.query);
            return AbstractMongoQueryBuilder.this.build();
        }
    }
}
