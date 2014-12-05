package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static fr.infologic.vei.audit.mongo.MongoObject.CONTENT;
import static fr.infologic.vei.audit.mongo.MongoObject._ID;
import static fr.infologic.vei.audit.mongo.MongoObject.toList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.AuditQuery.TraceQuery;

class MongoAllModificationsQuery extends AbstractMongoQueryBuilder implements TraceQuery
{
    protected final DB db;
    private Set<String> requestedCollectionNames;
    MongoAllModificationsQuery(DB db, Set<String> requestedTypes, Function<String, String> typeDependantGroup)
    {
        super(typeDependantGroup);
        this.db = db;
        requestedCollectionNames = requestedTypes;
    }
    
    @Override
    public TraceQuery build()
    {
        return this;
    }

    @Override
    public List<TrailTrace> search()
    {
        return matchingCollections().map(this::traces).flatMap(List::stream).collect(toList());
    }
    
    private Stream<DBCollection> matchingCollections()
    {
        Set<String> allCollectionNames = db.getCollectionNames();
        if(requestedCollectionNames != null && !requestedCollectionNames.isEmpty())
        {
            allCollectionNames.retainAll(requestedCollectionNames);
        }
        return allCollectionNames.stream().filter(MongoAllModificationsQuery::isUserCollection).map(db::getCollection);
    }

    private static boolean isUserCollection(String name)
    {
        return !name.startsWith("system.");
    }

    private List<MongoObject> traces(DBCollection collection)
    {
        String type = collection.getName();
        return toList(type, collection.find(makeQueryForType(type), traceProjection()));
    }

    protected DBObject traceProjection()
    {
        return start(_ID, false).add(CONTENT, false).get();
    }
}
