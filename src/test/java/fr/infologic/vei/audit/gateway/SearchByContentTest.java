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
import fr.infologic.vei.audit.api.AdminDB.AdminDBException;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;

public class SearchByContentTest
{
    private TestAuditJsonObject v1, v2, v4;
    
    @Before
    public void setUp() throws AdminDBException
    {
        setUpGateway();
        gateway.trace(v1 = make().addMetadata("version", 1).withContent("{a:1}"));
        gateway.trace(v2 = make().addMetadata("version", 2).withContent("{}"));
        gateway.trace(/*v3 =*/make().addMetadata("version", 3).withContent("{}"));
        gateway.trace(v4 = make().addMetadata("version", 4).withContent("{a:1}"));
    }
    @Test
    public void searchADocumentByContent()
    {
        List<TrailTrace> traces = gateway.makeQuery().forModificationsOf("a").inType("type", null).build().search();
        
        TrailTraceAssert.assertThat(traces).hasSize(3).containsExactly(v1, v2, v4);
    }
    
    @Test
    public void searchADocumentByContentReturnsEmpty()
    {
        List<TrailTrace> traces = gateway.makeQuery().forModificationsOf("a").inType("type", "group").havingMetadata().fieldEqualsTo("version", 3).build().search();
        
        TrailTraceAssert.assertThat(traces).isEmpty();
    }
    
    @Test
    public void searchAMissingDocument()
    {
        List<TrailTrace> traces = gateway.makeQuery().forModificationsOf("a").inType("type", "group").havingKeyEqualsTo("another").build().search();
        
        TrailTraceAssert.assertThat(traces).isEmpty();
    }
    
    @Test
    public void searchFindsTwoDocuments()
    {
        TestAuditJsonObject another;
        gateway.trace(another = new TestAuditJsonObject("type", null, "another").addMetadata("version", "another").withContent("{a:1, b:2}"));

        List<TrailTrace> traces = gateway.makeQuery().forModificationsOf("a").inType("type", null).build().search();
        
        TrailTraceAssert.assertThat(traces).hasSize(4).containsOnly(v1, v2, v4, another.withContent("{a:1}"));
    }
    
    @Test
    public void searchFindsADocumentWithGroup()
    {
        TestAuditJsonObject another;
        gateway.trace(another = new TestAuditJsonObject("type", "group", "another").addMetadata("version", "another").withContent("{a:1, b:2}"));

        List<TrailTrace> traces = gateway.makeQuery().forModificationsOf("a").inType("type", "group").build().search();
        
        TrailTraceAssert.assertThat(traces).hasSize(1).containsExactly(another.withContent("{a:1}"));
    }

    private AuditGateway gateway;
    
    private void setUpGateway() throws AdminDBException
    {
        gateway = gateway();
    }
    @After
    public void tearDownGateway() throws AdminDBException
    {
        gateway.db().drop();
    }
    
    @BeforeClass
    public static void isAlive() throws AdminDBException
    {
        Assume.assumeTrue(gateway().db().isAlive());
    }
    private static AuditGateway gateway() throws AdminDBException
    {
        return MongoAuditGatewayBuilder.db(AuditDBGatewayTest.class.getSimpleName()).build();
    }
}
