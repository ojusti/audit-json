package fr.infologic.vei.audit;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.util.Objects;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailObject;
import fr.infologic.vei.audit.mongo.json.MongoJson;

public class AuditJsonObjectAssert extends ObjectAssert<TrailObject>
{
    public static AuditJsonObjectAssert assertThat(TrailObject actual)
    {
        return new AuditJsonObjectAssert(actual);
    }
    public static AuditJsonObjectListAssert assertThat(List<? extends TrailObject> actual)
    {
        return new AuditJsonObjectListAssert((List) actual);
    }
    
    protected AuditJsonObjectAssert(TrailObject actual)
    {
        super(actual);
    }

    public AuditJsonObjectAssert hasKey(String key)
    {
        Assertions.assertThat(actual.getKey()).isEqualTo(key);
        return this;
    }

    public AuditJsonObjectAssert hasType(String type)
    {
        Assertions.assertThat(actual.getType()).isEqualTo(type);
        return this;
    }

    public AuditJsonObjectAssert hasMetadata(Map<String, Object> metadata)
    {
        Assertions.assertThat(actual.getMetadata().entrySet()).isEqualTo(metadata.entrySet());
        return this;
    }
    
    public AuditJsonObjectAssert hasContent(String content)
    {
        Assertions.assertThat(actual.getContent()).isEqualTo(MongoJson.fromString(content));
        return this;
    }
    public AuditJsonObjectAssert hasContent(Content content)
    {
        Assertions.assertThat(actual.getContent()).isEqualTo(content);
        return this;
    }
    
    public AuditJsonObjectAssert isEqualTo(TrailObject expected)
    {
        return hasType(expected.getType()).hasKey(expected.getKey()).hasMetadata(expected.getMetadata()).hasContent(expected.getContent());
    }
    public static class AuditJsonObjectListAssert extends ListAssert<TrailObject>
    {
        protected AuditJsonObjectListAssert(List<TrailObject> actual)
        {
            super(actual);
            usingComparisonStrategy(new StandardComparisonStrategy()
            {
                @Override
                public boolean areEqual(Object actual, Object other)
                {
                    TrailObject a = (TrailObject) actual;
                    TrailObject o = (TrailObject) other;
                    return Objects.areEqual(a.getKey(), o.getKey()) && Objects.areEqual(a.getType(), o.getType())
                        && Objects.areEqual(a.getMetadata().keySet(), o.getMetadata().keySet()) 
                        && Objects.areEqual(a.getContent(), o.getContent());   
                }
            });
            
        }
        
        
    }
}


