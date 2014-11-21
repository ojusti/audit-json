package fr.infologic.vei.audit.engine;

import static fr.infologic.vei.audit.TestAuditJsonObject.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import fr.infologic.vei.audit.TestAuditJsonObject;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailQuery;
import fr.infologic.vei.audit.mongo.json.MongoJson;


public class PatchTrailTest
{
    private TrailType type;
    @Before
    public void setUpTrailWithOneTrace()
    {
        type = mock(TrailType.class);
        PatchableTrailQuery query = mock(PatchableTrailQuery.class);
        when(type.query("key")).thenReturn(query);
        
        TestAuditJsonObject version1 = make().withContent("{a:1,b:2}");
        when(query.last()).thenReturn(version1).thenThrow(new RuntimeException());
    }
    @Test
    public void given_trail_not_empty_When_save_new_trace_Then_existing_trace_is_diffed()
    {
        PatchTrail version2 = new PatchTrail(type, "key");
        version2.setContent(MongoJson.fromString("{a:1,b:3}")).save();
        
        TrailTrace diff2_1 = make().withContent("{b:2}");
        verify(type).save(diff2_1);
        verify(type).save(version2);
    }
}
