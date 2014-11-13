package fr.infologic.vei.audit;

import java.util.Collections;
import java.util.Map;

public class AuditJsonObject extends AuditJsonKey
{
    @Override
    public String toString()
    {
        return String.format("AuditJsonObject [type=%s, key=%s, metadata=%s, objectAsJson=%s]",
                             type, key, metadata, objectAsJson);
    }
    public Map<String, Object> metadata = Collections.emptyMap();
    public String objectAsJson;
}
