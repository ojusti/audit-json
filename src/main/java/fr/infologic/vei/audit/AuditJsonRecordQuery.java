package fr.infologic.vei.audit;

import java.util.List;

public interface AuditJsonRecordQuery
{

    AuditJsonObject last();

    List<AuditJsonObject> all();

}
