package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static fr.infologic.vei.audit.mongo.MongoObject.list;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.api.QueryDriver.TraceMetadataQueryBuilder;
import fr.infologic.vei.audit.api.QueryDriver.TraceQuery;
import fr.infologic.vei.audit.api.QueryDriver.TraceQueryBuilder;

class MongoGlobalQuery implements TraceQueryBuilder, TraceQuery
{
    private final DB db;
    private Set<String> requestedCollectionNames;
    private final BasicDBObjectBuilder query = start();
    MongoGlobalQuery(DB db)
    {
        this.db = db;
    }
    
    @Override
    public TraceQueryBuilder inTypes(Set<String> requestedTypes)
    {
        this.requestedCollectionNames = requestedTypes;
        return this;
    }
    
    @Override
    public TraceQueryBuilder keyEqualsTo(String requestedKey)
    {
        this.query.append(MongoObject.KEY, requestedKey);
        return this;
    }
    
    @Override
    public TraceMetadataQueryBuilder metadata()
    {
        return new MetadataBuilder();
    }
    
    private class MetadataBuilder implements TraceMetadataQueryBuilder
    {
        private final MongoObjectBuilder builder = new MongoObjectBuilder();

        @Override
        public TraceMetadataQueryBuilder fieldEqualsTo(String field, Object requestedValue)
        {
            builder.fieldEqualsTo(metadataField(field), requestedValue);
            return this;
        }
        @Override
        public TraceMetadataQueryBuilder fieldGreaterThan(String field, Object minValue)
        {
            builder.fieldGreaterThan(metadataField(field), minValue);
            return this;
        }
        @Override
        public TraceMetadataQueryBuilder fieldLessThan(String field, Object maxValue)
        {
            builder.fieldLessThan(metadataField(field), maxValue);
            return this;
        }
        private String metadataField(String field)
        {
            return MongoObject.METADATA + "." + field;
        }
        @Override
        public TraceQuery build()
        {
            builder.addTo(query);
            return MongoGlobalQuery.this.build();
        }
    }
    
    @Override
    public TraceQuery build()
    {
        return this;
    }

    @Override
    public List<TrailTrace> search()
    {
        return matchingCollections().map(this::search).flatMap(list -> list.stream()).collect(toList());
    }
    
    private Stream<DBCollection> matchingCollections()
    {
        Set<String> allCollectionNames = db.getCollectionNames();
        if(requestedCollectionNames != null && !requestedCollectionNames.isEmpty())
        {
            allCollectionNames.retainAll(requestedCollectionNames);
        }
        return stream(allCollectionNames.spliterator(), false).filter(name -> !isSystemCollection(name)).map(name -> db.getCollection(name));
    }

    private static boolean isSystemCollection(String name)
    {
        return name.startsWith("system.");
    }

    private List<MongoObject> search(DBCollection collection)
    {
        System.out.println(query.get());
        return list(collection.getName(), collection.find(query.get()));
    }
}
