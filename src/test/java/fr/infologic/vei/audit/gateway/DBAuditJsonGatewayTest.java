package fr.infologic.vei.audit.gateway;

import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.infologic.vei.audit.AuditJsonObjectAssert;
import fr.infologic.vei.audit.TestAuditJsonObject;
import fr.infologic.vei.audit.api.TrailKey;
import fr.infologic.vei.audit.api.AuditDriver.TrailObject;
import fr.infologic.vei.audit.gateway.AuditGateway;
import fr.infologic.vei.audit.gateway.MongoAuditGatewayBuilder;


public class DBAuditJsonGatewayTest
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
        TestAuditJsonObject object = new TestAuditJsonObject("collection", "key").addMetadata("meta", "value");
        gateway.trace(object);

        TrailKey key = object;
        TrailObject persisted = gateway.find(key).last();
        AuditJsonObjectAssert.assertThat(persisted).hasKey(object.key)
                                                   .hasType(object.type)
                                                   .hasMetadata(object.metadata)
                                                   .hasContent(object.content)
                                                   .isEqualTo(object);
    }
    
    @Test
    public void traceAPatchAndRetrieveAllVersions()
    {
        TestAuditJsonObject v1 = new TestAuditJsonObject("collection", "key").withContent("{a : \"b\"}");
        gateway.trace(v1);
        
        TestAuditJsonObject v2 = new TestAuditJsonObject("collection", "key").withContent("{a : \"c\", b : \"d\"}");
        gateway.trace(v2);

        TrailKey key = v1;
        List<? extends TrailObject> persisted = gateway.find(key).all();
        AuditJsonObjectAssert.assertThat(persisted).containsExactly(v1, v2);
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
        return MongoAuditGatewayBuilder.db(DBAuditJsonGatewayTest.class.getSimpleName()).build();
    }
}
