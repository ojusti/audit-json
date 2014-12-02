package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;


class MongoObjectBuilder
{
    private final  List<DBObject> fields = new ArrayList<>();
    private final String operator;
    private MongoObjectBuilder(String operator)
    {
        this.operator = operator;
    }
    static MongoObjectBuilder or()
    {
        return new MongoObjectBuilder("$or");
    }
    static MongoObjectBuilder and()
    {
        return new MongoObjectBuilder("$and");
    }
    MongoObjectBuilder fieldEqualsTo(String field, Object requestedValue)
    {
        return add(start(field, requestedValue));
    }
    MongoObjectBuilder fieldGreaterThan(String field, Object minValue)
    {
        return add(start().push(field).add("$gt", minValue));
    }
    MongoObjectBuilder fieldLessThan(String field, Object maxValue)
    {
        return add(start().push(field).add("$lt", maxValue));
    }
    MongoObjectBuilder fieldIn(String field, List values)
    {
        return add(start().push(field).add("$in", values));
    }
    MongoObjectBuilder add(BasicDBObjectBuilder field)
    {
        return add(field.get());
    }
    MongoObjectBuilder add(DBObject field)
    {
        fields.add(field);
        return this;
    }
    MongoObjectBuilder addAll(MongoObjectBuilder other)
    {
        if(!operator.equals(other.operator))
        {
            throw new IllegalArgumentException(String.format("Builder's operator should be %s not %s", operator, other.operator));
        }
        fields.addAll(other.fields);
        return this;
    }
    BasicDBObjectBuilder addTo(BasicDBObjectBuilder builder)
    {
        switch(fields.size())
        {
            case 0: return builder;
            case 1:
                Map.Entry<String, Object> singleton = (Map.Entry<String, Object>) fields.get(0).toMap().entrySet().iterator().next();
                return builder.add(singleton.getKey(), singleton.getValue());
            default: return builder.add(operator, fields);
        }
    }
    public boolean isEmpty()
    {
        return fields.isEmpty();
    }
}
