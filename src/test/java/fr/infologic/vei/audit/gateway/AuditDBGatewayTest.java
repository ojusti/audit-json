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
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
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
    public void traceAndRetrieveADocument()
    {
        TestAuditJsonObject object = make().addMetadata("key", "value");
        gateway.trace(object);

        TrailKey key = object;
        TrailTrace persisted = gateway.find(key).last();
        TrailTraceAssert.assertThat(persisted).hasKey(object.key)
                                                   .hasType(object.type)
                                                   .hasMetadata(object.metadata)
                                                   .hasContent(object.content)
                                                   .isEqualTo(object);
    }
    
    @Test
    public void traceAPatchAndRetrieveAllVersions()
    {
        TestAuditJsonObject v1 = make().withContent("{a:1}");
        gateway.trace(v1);
        
        TestAuditJsonObject v2 = make().withContent("{a:2,b:3}");
        gateway.trace(v2);

        TrailKey key = v1;
        List<? extends TrailTrace> persisted = gateway.find(key).all();
        TrailTraceAssert.assertThat(persisted).containsExactly(v1, v2);
    }
    
    @Test
    public void traceAndSearchForADocument()
    {
        gateway.trace(make().addMetadata("key1", "value1").addMetadata("key2", "0value"));
        
        TestAuditJsonObject object = make().addMetadata("key1", "value1").addMetadata("key2", "value2").withContent("{a:3}");
        gateway.trace(object);
        
        object.withContent(null);
        
        List<TrailTrace> result = gateway.makeQuery().metadata().fieldEqualsTo("key1", "value1").fieldGreaterThan("key2", "value").build().search();
        TrailTraceAssert.assertThat(result).containsExactly(object);
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
