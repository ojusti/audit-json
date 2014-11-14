package fr.infologic.vei.audit.diff;

import java.util.Map;
import java.util.Objects;

import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

class JsonDiff
{
    /**
     * @return {@code null} if modified == original and a patch otherwise.
     * Inside the patch: 
     *  fields which kept their value don't appear
     *  deleted fields appear with a {@code null} value
     *  new fields appear with their (new) value
     *  modified fields appear with a diff between the new value and the original one
     *   for scalars, the diff is equal to the new value
     *   for objects, the diff is recursive
     *   for arrays, the diff is recursive, the algorithm compares each position
     *    position which kept their value appear as {@code null}
     * @throws NullPointerException if modified or original are {@code null}
     */
    static BasicBSONObject diff(BasicBSONObject modified, BasicBSONObject original)
    {
        BasicBSONObject patch = new BasicBSONObject();
        for(Map.Entry<String, Object> field : modified.entrySet())
        {
            if(field.getValue() == null)
            {
                continue;
            }
            Object originalValue = original.get(field.getKey());
            if(originalValue == null)
            {
                patch.append(field.getKey(), field.getValue());//add
            }
            else
            {
                Object diff = diff(field.getValue(), originalValue);
                if(diff != null)
                {
                    patch.append(field.getKey(), diff);//update
                }
            }
        }
        for(Map.Entry<String, Object> field : original.entrySet())
        {
            if(field.getValue() != null && modified.get(field.getKey()) == null)//delete
            {
                patch.append(field.getKey(), null);
            }
        }
        return patch.isEmpty() ? null : patch;
    }
    private static Object diff(Object modified, Object original)
    {
        if(modified instanceof BasicBSONObject)
        {
            if(!(original instanceof BasicBSONObject))
            {
                return modified;
            }
            return diff((BasicBSONObject) modified, (BasicBSONObject) original);
        }
        if(modified instanceof BasicBSONList)
        {
            if(!(original instanceof BasicBSONList))
            {
                return modified;
            }
            return diff((BasicBSONList) modified, (BasicBSONList) original);
        }
        if(Objects.equals(modified, original))
        {
            return null;
        }
        return modified;
    }
    
    private static BasicBSONList diff(BasicBSONList modified, BasicBSONList original)
    {
        BasicBSONList patch = new BasicBSONList();
        int i = 0;
        boolean differenceFound = false;
        for(; i < modified.size(); i++)
        {
            Object diff = i < original.size() ? diff(modified.get(i), original.get(i)) : modified.get(i);
            if(diff != null)
            {
                differenceFound = true;
                patch.add(diff);
            }
            else
            {
                patch.add(null);
            }
        }
        differenceFound = differenceFound || i < original.size();
        return differenceFound ? patch : null;
    }
}
