package fr.infologic.vei.audit.mongo;

import static com.mongodb.BasicDBObjectBuilder.start;

import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;


class MongoObjectBuilder
{
    private BasicDBList fields = new BasicDBList();
    MongoObjectBuilder fieldEqualsTo(String field, Object requestedValue)
    {
        add(start(field, requestedValue));
        return this;
    }
    MongoObjectBuilder fieldGreaterThan(String field, Object minValue)
    {
        add(start().push(field).add("$gt", minValue));
        return this;
    }
    MongoObjectBuilder fieldLessThan(String field, Object maxValue)
    {
        add(start().push(field).add("$lt", maxValue));
        return this;
    }
    void add(BasicDBObjectBuilder field)
    {
        fields.add(field.get());
    }
    BasicDBObjectBuilder addTo(BasicDBObjectBuilder builder)
    {
        switch(fields.size())
        {
            case 0: return builder;
            case 1:
                Map.Entry<String, Object> singleton = (Map.Entry<String, Object>) ((DBObject) fields.get(0)).toMap().entrySet().iterator().next();
                return builder.add(singleton.getKey(), singleton.getValue());
            default: return builder.add("$and", fields);
        }
    }
}
