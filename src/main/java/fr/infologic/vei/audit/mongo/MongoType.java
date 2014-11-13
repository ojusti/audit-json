package fr.infologic.vei.audit.mongo;

import com.mongodb.DBCollection;

import fr.infologic.vei.audit.AuditJsonDBDriver.AuditJsonRecord;
import fr.infologic.vei.audit.AuditJsonDBDriver.AuditJsonType;


class MongoType implements AuditJsonType
{
    final DBCollection collection;
    MongoType(DBCollection collection)
    {
        this.collection = collection;
    }

    @Override
    public AuditJsonRecord record(String key)
    {
        return new MongoAuditRecord(collection, key);
    }

}
