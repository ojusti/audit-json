package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static fr.infologic.vei.audit.mongo.MongoObject.KEY;
import static fr.infologic.vei.audit.mongo.MongoObject.VERSION;
import static fr.infologic.vei.audit.mongo.MongoObject._ID;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBObject;

import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.AuditQuery.TraceQueryBuilder;

class MongoFieldModificationsQuery extends MongoAllModificationsQuery
{
    private String requestedType;
    private String requestedKey;
    private String field;
    private static final AggregationOptions USE_CURSOR = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build();
    private static final String FIELD_IS_NULL = "isNull";
    
    MongoFieldModificationsQuery(DB db, String requestedType, String field)
    {
        super(db);
        ofAnyTypeInSet(singleton(requestedType));
        this.requestedType = requestedType;
        this.field = field;
    }
    
    @Override
    public TraceQueryBuilder havingKeyEqualsTo(String requestedKey)
    {
        this.requestedKey = requestedKey;
        return this;
    }

    @Override
    public List<TrailTrace> search()
    {
        MongoObjectBuilder completeKeyQuery = buildKeyQuery(db.getCollection(requestedType).aggregate(asList(matchTracesWithFieldInContent(), projectKeyVersionAndFieldIsNull(), sortByKeyAndVersion()), USE_CURSOR));
        if(completeKeyQuery.isEmpty())
        {
            return Collections.emptyList();
        }
        completeKeyQuery.addTo(query);
        return super.search();
    }
    
    private static MongoObjectBuilder buildKeyQuery(Cursor it)
    {
        try
        {
            return stream(it).map(VersionedPatchWithField::new)
                      .collect(groupingBy(VersionedPatchWithField::getKey)).entrySet().stream()
                      .map(VersionList::computeVersions)
                      .map(VersionList::buildQuery)
                      .collect(MongoObjectBuilder::or, MongoObjectBuilder::add, MongoObjectBuilder::addAll);
        }
        finally
        {
            it.close();
        }
    }
    
    private static class VersionedPatchWithField
    {
        String key;
        int version;
        boolean fieldIsNull;
        
        VersionedPatchWithField(DBObject o)
        {
            key = (String) o.get(MongoObject.KEY);
            version = ((Integer) o.get(MongoObject.VERSION)).intValue();
            fieldIsNull = ((Boolean) o.get(FIELD_IS_NULL)).booleanValue();
        }

        String getKey()
        {
            return key;
        }
    }
    
    private static class VersionList extends ArrayList<Integer>
    {
        static VersionList computeVersions(Map.Entry<String, List<VersionedPatchWithField>> trail)
        {
            VersionList versions = new VersionList(trail.getKey());
            
            Iterator<VersionedPatchWithField> it = trail.getValue().iterator();
            VersionedPatchWithField p = it.next();
            versions.addFirstPatch(p);
            for(; it.hasNext(); )
            {
                versions.addPatch(it.next());
            }
            versions.removeLast();
            return versions;
        }
        
        private void addFirstPatch(VersionedPatchWithField patch)
        {
            if(!patch.fieldIsNull)
            {
                add(1);
            }
            addPatch(patch);
        }
        
        private void addPatch(VersionedPatchWithField patch)
        {
            add(patch.version + 1);
        }
        
        private void removeLast()
        {
            remove(size() - 1);
        }
        
        private void add(int version)
        {
            add(Integer.valueOf(version));
        }
        
        DBObject buildQuery()
        {
            return new BasicDBObject("$and", Arrays.asList(new BasicDBObject(KEY, key), 
                                                           start().push(VERSION).add("$in", this).get()));
        }
        
        private final String key;
        private VersionList(String key)
        {
            this.key = key;
        }
    }
    
    private DBObject matchTracesWithFieldInContent()
    {
        BasicDBObjectBuilder match = start().push("$match");
        if(requestedKey != null)
        {
            match.add(MongoObject.KEY, requestedKey);
        }
        return match.push(field()).add("$exists", true).get();
    }
    private DBObject projectKeyVersionAndFieldIsNull()
    {
        return start().push("$project").add(_ID, false).add(KEY, true).add(VERSION, true).push(FIELD_IS_NULL).add("$not", "$" + field()).get();
    }
    
    private static DBObject sortByKeyAndVersion()
    {
        return start().push("$sort").add(KEY, 1).add(VERSION, 1).get();
    }

    private String field()
    {
        return MongoObject.CONTENT + "." + field;
    }
    
    private static Stream<DBObject> stream(Cursor it)
    {
        return StreamSupport.stream(spliteratorUnknownSize(it, 0), false);
    }

    
}
