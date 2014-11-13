package fr.infologic.vei.audit;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.util.Objects;

import com.mongodb.util.JSON;

public class AuditJsonObjectAssert extends ObjectAssert<AuditJsonObject>
{
    public static AuditJsonObjectAssert assertThat(AuditJsonObject actual)
    {
        return new AuditJsonObjectAssert(actual);
    }
    public static AuditJsonObjectListAssert assertThat(List<AuditJsonObject> actual)
    {
        return new AuditJsonObjectListAssert(actual);
    }
    
    protected AuditJsonObjectAssert(AuditJsonObject actual)
    {
        super(actual);
    }

    public AuditJsonObjectAssert hasKey(String key)
    {
        Assertions.assertThat(actual.key).isEqualTo(key);
        return this;
    }

    public AuditJsonObjectAssert hasType(String type)
    {
        Assertions.assertThat(actual.type).isEqualTo(type);
        return this;
    }

    public AuditJsonObjectAssert hasMetadata(Map<String, Object> metadata)
    {
        Assertions.assertThat(actual.metadata.entrySet()).isEqualTo(metadata.entrySet());
        return this;
    }
    
    public AuditJsonObjectAssert hasContent(String content)
    {
        Assertions.assertThat(JSON.parse(actual.objectAsJson)).isEqualTo(JSON.parse(content));
        return this;
    }
    
    public AuditJsonObjectAssert isEqualTo(AuditJsonObject expected)
    {
        return hasType(expected.type).hasKey(expected.key).hasMetadata(expected.metadata).hasContent(expected.objectAsJson);
    }
    public static class AuditJsonObjectListAssert extends ListAssert<AuditJsonObject>
    {
        protected AuditJsonObjectListAssert(List<AuditJsonObject> actual)
        {
            super(actual);
            usingComparisonStrategy(new StandardComparisonStrategy()
            {
                @Override
                public boolean areEqual(Object actual, Object other)
                {
                    AuditJsonObject a = (AuditJsonObject) actual;
                    AuditJsonObject o = (AuditJsonObject) other;
                    return Objects.areEqual(a.key, o.key) && Objects.areEqual(a.type, o.type)
                        && Objects.areEqual(a.metadata.keySet(), o.metadata.keySet()) 
                        && Objects.areEqual(JSON.parse(a.objectAsJson), JSON.parse(o.objectAsJson));   
                }
            });
            
        }
        
        
    }
}


