package fr.infologic.vei.audit.mongo.json;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MongoJsonTest
{
    @Parameters
    public static Collection<Object[]> data() 
    {
       String[][] data = {/* ORIGINAL(n)   PATCH                  RESULT(n-1)
                          -------------------------------------------------------------------*/
                         {"{a:2}",         "{a:3}",               "{a:3}"},
                         {"{a:2}",         "{b:3}",               "{a:2,b:3}"},
                         {"{a:2}",         "{a:null}",            "{}"},
                         {"{a:2}",         "{}",                  "{a:2}"},//no change
                         {"{a:2}",         null,                  null},
                         {null,            "{a:2}",               "{a:2}"},
                         {"{a:2,b:3}",     "{a:null}",            "{b:3}"},
                         {"{a:[2]}",       "{a:3}",               "{a:3}"},
                         {"{a:3}",         "{a:[2]}",             "{a:[2]}"},
                         {"{a:{b:3,c:5}}", "{a:{b:4,c:null}}",    "{a:{b:4}}"},
                         {"{a:{b:3,c:5,d:6}}", "{a:{c:4,d:null}}", "{a:{b:3,c:4}}"},
                         {"{a:[{b:3}]}",   "{a:[1]}",             "{a:[1]}"},
                         {"{a:[1,2]}",     "{a:[3,4]}",           "{a:[3,4]}"},
                         {"{a:[{b:2,c:3},{b:4,c:5}]}", "{a:[null,{b:3}]}", "{a:[{b:2,c:3},{b:3,c:5}]}"},
                         {"{e:{}}",        "{a:1}",               "{e:{},a:1}"},
                         {"{}",            "{a:{b:{}}}",          "{a:{b:{}}}"},
                         {"{a:[1,2,3]}",   "{a:[2,3]}",           "{a:[2,3]}"},
                         {"{a:[1,{},3]}",  "{a:[2,null]}",        "{a:[2,{}]}"},
                         {"{a:[1,2,3]}",   "{a:[{},null]}",       "{a:[{},2]}"},
                         {"{a:[1,2,3]}",   "{a:[]}",              "{a:[]}"},
                         
                         {"{b:2}",         "{a:3}",               "{a:3,b:2}"},
       };
                               
       return Arrays.asList((Object[][]) data);    
    }

    private MongoJson original, patch, result;

    public MongoJsonTest(String original, String patch, String result) 
    {
        this.original = MongoJson.fromString(original);
        this.patch = MongoJson.fromString(patch);
        this.result = MongoJson.fromString(result);
    }
    
    @Test
    public void testComputePatch()
    {
        MongoJson computedPatch = result.diff(original);
        Assertions.assertThat(computedPatch).describedAs("%s - %s = %s", result, original, patch).isEqualTo(patch);
        assertThatKeysAreSortedLexicographically(computedPatch.getBSONObject());
    }
    
    private void assertThatKeysAreSortedLexicographically(Map<String, Object> mapOfMaps)
    {
        if(mapOfMaps == null)
        {
            return;
        }
        String previousKey = "";
        for(Map.Entry<String, Object> entry : mapOfMaps.entrySet())
        { 
            String actualKey = entry.getKey();
            Assertions.assertThat(previousKey.compareTo(actualKey)).describedAs("%s < %s", previousKey, actualKey).isLessThan(0);
            previousKey = actualKey;
            Object value = entry.getValue();
            if(value instanceof Map)
            {
                assertThatKeysAreSortedLexicographically((Map) value);
            }
        }
    }

    @Test
    public void testMergePatch()
    {
        MongoJson computedResult = patch.applyTo(original);
        Assertions.assertThat(computedResult).describedAs("%s + %s = %s", original, patch, result).isEqualTo(result);
        assertThatKeysAreSortedLexicographically(computedResult.getBSONObject());
    }
}
