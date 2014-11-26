package fr.infologic.vei.audit;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.util.Objects;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.mongo.json.MongoJson;

public class TrailTraceAssert extends ObjectAssert<TrailTrace>
{
    public static TrailTraceAssert assertThat(TrailTrace actual)
    {
        return new TrailTraceAssert(actual);
    }
    public static TrailTraceListAssert assertThat(List<? extends TrailTrace> actual)
    {
        return new TrailTraceListAssert((List) actual);
    }
    
    protected TrailTraceAssert(TrailTrace actual)
    {
        super(actual);
    }

    public TrailTraceAssert hasKey(String key)
    {
        Assertions.assertThat(actual.getKey()).isEqualTo(key);
        return this;
    }

    public TrailTraceAssert hasType(String type)
    {
        Assertions.assertThat(actual.getType()).isEqualTo(type);
        return this;
    }

    public TrailTraceAssert hasMetadata(Map<String, Object> metadata)
    {
        Assertions.assertThat(actual.getMetadata().entrySet()).isEqualTo(metadata.entrySet());
        return this;
    }
    
    public TrailTraceAssert hasContent(String content)
    {
        Assertions.assertThat(actual.getContent()).isEqualTo(MongoJson.fromString(content));
        return this;
    }
    public TrailTraceAssert hasContent(Content content)
    {
        Assertions.assertThat(actual.getContent()).isEqualTo(content);
        return this;
    }
    
    public TrailTraceAssert isEqualTo(TrailTrace expected)
    {
        return hasType(expected.getType()).hasKey(expected.getKey()).hasMetadata(expected.getMetadata()).hasContent(expected.getContent());
    }
    public static class TrailTraceListAssert extends ListAssert<TrailTrace>
    {
        protected TrailTraceListAssert(List<TrailTrace> actual)
        {
            super(actual);
            usingComparisonStrategy(new StandardComparisonStrategy()
            {
                @Override
                public boolean areEqual(Object actual, Object other)
                {
                    TrailTrace a = (TrailTrace) actual;
                    TrailTrace o = (TrailTrace) other;
                    return Objects.areEqual(a.getKey(), o.getKey()) && Objects.areEqual(a.getType(), o.getType())
                        && Objects.areEqual(a.getMetadata().entrySet(), o.getMetadata().entrySet()) 
                        && Objects.areEqual(a.getContent(), o.getContent());   
                }
            });
            
        }
        
        
    }
}


