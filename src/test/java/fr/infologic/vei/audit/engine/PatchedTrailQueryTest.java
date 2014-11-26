package fr.infologic.vei.audit.engine;

import static fr.infologic.vei.audit.TestAuditJsonObject.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailQuery;

@RunWith(Parameterized.class)
public class PatchedTrailQueryTest
{
    @Parameters
    public static Collection<Object[]> data() 
    {
       String[][][] data = {{{"{b:null,c:2}", "{a:2,c:null}"}, {"{a:1,b:2,c:3}"}, {"{a:2,c:2}", "{a:2,b:2}"}},
                            {{"{b:null}", "{a:2,c:null}"},     {"{a:1,b:2,c:3}"}, {"{a:2}", "{a:2,b:2}"}}
                         
       };
                               
       return Arrays.asList((Object[][])data);    
    }

    @Parameter(0)
    public String[] patches;
    @Parameter(1)
    public String[] current;
    @Parameter(2)
    public String[] patched;
    private PatchableTrailQuery patchTrail;

    @Before
    public void setUpOriginalContent()
    {
        patchTrail = mock(PatchableTrailQuery.class);
        when(patchTrail.allFromVersion(1)).thenReturn(trailOf(patches));
    }
    private List trailOf(String[] content)
    {
        List trail = Arrays.stream(content).map(json -> make().withContent(json)).collect(Collectors.toList());
        trail.add(make().withContent(current[0])); 
        return trail;
    }
    @Test
    public void given_more_than_one_trace_When_query_all_Then_patches_are_applied()
    {
        List patchedContent = new PatchedTrailQuery(patchTrail).all();
        Assertions.assertThat(patchedContent).isEqualTo(trailOf(patched));
    }
}
