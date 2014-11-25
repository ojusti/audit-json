package fr.infologic.vei.audit.api;

import java.util.Collections;
import java.util.Map;

public class AuditTrace implements TrailKey
{
    public String type;
    public String key;
    public String content;
    public Map<String, Object> metadata = Collections.emptyMap();
    
    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getKey()
    {
        return key;
    }
    
    @Override
    public String toString()
    {
        return String.format("AuditTrace [type=%s, key=%s, metadata=%s, content=%s]",
                             type, key, metadata, content);
    }
}
