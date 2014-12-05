package fr.infologic.vei.audit.api;

import java.util.Collections;
import java.util.Map;

public class AuditTrace implements TrailKey
{
    public String type;
    public String group;
    public String key;
    public String content;
    public Map<String, Object> metadata = Collections.emptyMap();
    
    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getGroup()
    {
        return group;
    }
    
    @Override
    public String getKey()
    {
        return key;
    }
    
    @Override
    public String toString()
    {
        return String.format("AuditTrace [type=%s, group=%s, key=%s, metadata=%s, content=%s]",
                             type, group, key, metadata, content);
    }
}
