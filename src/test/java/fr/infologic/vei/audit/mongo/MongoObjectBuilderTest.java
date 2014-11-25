package fr.infologic.vei.audit.mongo;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;


public class MongoObjectBuilderTest
{
    private DBObject result;

    @Test
    public void oneElementQuery()
    {
        result = resultOf(new MongoObjectBuilder().fieldEqualsTo("field", "value"));
        assertSingleton(result.toMap(), "field", "value");
    }

    private static DBObject resultOf(MongoObjectBuilder builder)
    {
        return builder.addTo(new BasicDBObjectBuilder()).get();
    }

    @Test
    public void emptyQuery()
    {
        result = resultOf(new MongoObjectBuilder());
        Assertions.assertThat(result.toMap()).isEmpty();
    }

    @Test
    public void twoElementsQuery()
    {
        result = resultOf(new MongoObjectBuilder().fieldEqualsTo("field", "value").fieldEqualsTo("field", "value"));
        assertSingletonWithKey("$and");
        List<Map> values = (List<Map>) getValue("$and");
        Assertions.assertThat(values).hasSize(2);
        assertSingleton(values.get(0), "field", "value");
        assertSingleton(values.get(1), "field", "value");
    }

    @Test
    public void greaterThanQuery()
    {
        result = resultOf(new MongoObjectBuilder().fieldGreaterThan("field", "value"));
        assertSingletonWithKey("field");
        Map values = (Map) getValue("field");
        assertSingleton(values, "$gt", "value");
    }
    
    @Test
    public void lessThanQuery()
    {
        result = resultOf(new MongoObjectBuilder().fieldLessThan("field", "value"));
        assertSingletonWithKey("field");
        Map values = (Map) getValue("field");
        assertSingleton(values, "$lt", "value");
    }
    
    private Object getValue(Object key)
    {
        return result.toMap().get(key);
    }

    private void assertSingletonWithKey(String key)
    {
        Assertions.assertThat(result.toMap()).hasSize(1).containsKey(key);
    }
    
    private static void assertSingleton(Map result, String key, Object value)
    {
        Assertions.assertThat(result).hasSize(1).containsEntry(key, value);
    }
}
