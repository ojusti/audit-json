package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;
import static fr.infologic.vei.audit.mongo.MongoAuditJsonObject.CONTENT;
import static fr.infologic.vei.audit.mongo.MongoAuditJsonObject.KEY;
import static fr.infologic.vei.audit.mongo.MongoAuditJsonObject.METADATA;
import static fr.infologic.vei.audit.mongo.MongoAuditJsonObject.VERSION;

import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fr.infologic.vei.audit.AuditJsonDBDriver.AuditJsonRecord;
import fr.infologic.vei.audit.AuditJsonDBDriver.AuditJsonTrace;

class MongoAuditRecord implements AuditJsonRecord, AuditJsonTrace
{
    private final DBCollection type;
    private final String key;
    private MongoAuditQuery query;
    private DBObject content;
    private DBObject metadata;

    MongoAuditRecord(DBCollection type, String key)
    {
        this.type = type;
        this.key = key;
    }

    @Override
    public AuditJsonTrace content(DBObject content)
    {
        this.content = content;
        return this;
    }

    @Override
    public AuditJsonTrace metadata(Map<String, Object> metadata)
    {
        this.metadata = start(metadata).get();
        return this;
    }

    @Override
    public void append()
    {
        DBObject last = query()._last();
        type.insert(start(KEY, key)
                    .add(VERSION, incVersion(last))
                    .add(METADATA, metadata)
                    .add(CONTENT, content)
                    .get());
    }

    int incVersion(DBObject last)
    {
        return (last == null ? 0 : (int) last.get(VERSION)) + 1;
    }
    
    @Override
    public MongoAuditQuery query()
    {
        if(query == null)
        {
            query = new MongoAuditQuery(type, key);
        }
        return query;
    }
}
