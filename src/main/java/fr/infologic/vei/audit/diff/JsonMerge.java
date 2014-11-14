package fr.infologic.vei.audit.diff;

import java.util.Map;

import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

class JsonMerge
{
    /**
     * RFC-7396 + recursive patch for objects and arrays
     * @see JsonDiff#diff(BasicBSONObject, BasicBSONObject)
     * @throws NullPointerException is original or patch are {@code null}
     */
    static BasicBSONObject merge(BasicBSONObject original, BasicBSONObject patch)
    {
        if(patch.isEmpty())
        {
            return original;
        }
        BasicBSONObject target = new BasicBSONObject();
        for(Map.Entry<String, Object> field : patch.entrySet())
        {
            if(field.getValue() != null)
            {
                target.put(field.getKey(), merge(original.get(field.getKey()), field.getValue()));
            }
        }
        for(Map.Entry<String, Object> field : original.entrySet())
        {
            if(!patch.containsField(field.getKey()))
            {
                target.put(field.getKey(), field.getValue());
            }
        }
        return target;
    }
    
    private static BasicBSONList merge(BasicBSONList original, BasicBSONList patch)
    {
        BasicBSONList target = new BasicBSONList();
        for(int i = 0; i < patch.size(); i++)
        {
            if(i < original.size())
            {
                if(patch.get(i) == null)
                {
                    target.add(original.get(i));
                }
                else
                {
                    target.add(merge(original.get(i), patch.get(i)));
                }
            }
            else
            {
                target.add(patch.get(i));
            }
        }
        return target;
    }
    
    private static Object merge(Object original, Object patch)
    {
        if(patch instanceof BasicBSONObject)
        {
            if(!(original instanceof BasicBSONObject))
            {
                original = new BasicBSONObject();
            }
            return merge((BasicBSONObject) original, (BasicBSONObject) patch);
        }
        if(patch instanceof BasicBSONList)
        {
            if(!(original instanceof BasicBSONList))
            {
                original = new BasicBSONList();
            }
            return merge((BasicBSONList) original, (BasicBSONList) patch);
        }
        return patch;
    }
}