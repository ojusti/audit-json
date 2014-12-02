package fr.infologic.vei.audit.gateway;

import static fr.infologic.vei.audit.TestAuditJsonObject.make;

import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.infologic.vei.audit.TestAuditJsonObject;
import fr.infologic.vei.audit.TrailTraceAssert;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.TrailKey;


public class AuditDBGatewayTest
{
    private AuditGateway gateway;
    
    @Before
    public void setUpGateway()
    {
        gateway = gateway();
    }
    
    @Test
    public void traceAndFindADocument()
    {
        TestAuditJsonObject v1;
        gateway.trace(v1 = make().addMetadata("key", "value"));

        TrailKey key = v1;
        TrailTrace trace = gateway.find(key).last();
        TrailTraceAssert.assertThat(trace).hasKey(v1.key)
                                          .hasType(v1.type)
                                          .hasMetadata(v1.metadata)
                                          .hasContent(v1.content)
                                          .isEqualTo(v1);
    }
    
    @Test
    public void traceAPatchAndFindAllVersions()
    {
        TestAuditJsonObject v1, v2;
        gateway.trace(v1 = make().withContent("{a:1}"));
        gateway.trace(v2 = make().withContent("{a:2,b:3}"));

        TrailKey key = v1;
        List<? extends TrailTrace> persisted = gateway.find(key).all();
        TrailTraceAssert.assertThat(persisted).containsExactly(v1, v2);
    }
    
    @Test
    public void traceAndQueryForADocument()
    {
        @SuppressWarnings("unused")
        TestAuditJsonObject v1, v2;
        gateway.trace(v1 = make().addMetadata("key1", "value1").addMetadata("key2", "0value"));
        gateway.trace(v2 = make().addMetadata("key1", "value1").addMetadata("key2", "value2").withContent("{a:3}"));
        
        List<TrailTrace> traces = gateway.makeQuery().forAllModifications().havingMetadata().fieldEqualsTo("key1", "value1").fieldGreaterThan("key2", "value").build().search();
        TrailTraceAssert.assertThat(traces).containsExactly(v2.withContent(null));
    }
    
    @After
    public void tearDownDriver()
    {
        gateway.db().drop();
    }
    
    @BeforeClass
    public static void isAlive()
    {
        Assume.assumeTrue(gateway().db().isAlive());
    }
    private static AuditGateway gateway()
    {
        return MongoAuditGatewayBuilder.db(AuditDBGatewayTest.class.getSimpleName()).build();
    }
}
