package fr.infologic.vei.audit.api;

import java.util.Collections;
import java.util.Map;

import fr.infologic.vei.audit.api.AuditFind.Content;

public class AuditIngestTrace implements TrailKey
{
    public String type;
    public String group;
    public String key;
    public Content content;
    public int version;
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
        return String.format("AuditTrace [type=%s, group=%s, key=%s, version=%d, metadata=%s, content=%s]",
                             type, group, key, version, metadata, content);
    }
}
