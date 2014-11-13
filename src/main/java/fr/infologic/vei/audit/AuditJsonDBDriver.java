package fr.infologic.vei.audit;

import java.util.Map;

import com.mongodb.DBObject;

public interface AuditJsonDBDriver extends DBAdmin
{
    AuditJsonType type(String type);
    interface AuditJsonType
    {
        AuditJsonRecord record(String key);
    }
    interface AuditJsonRecord
    {
        AuditJsonTrace content(DBObject content);
        AuditJsonRecordQuery query();
    }
    interface AuditJsonTrace
    {
        AuditJsonTrace metadata(Map<String, Object> metadata);
        void append();
    }
}
