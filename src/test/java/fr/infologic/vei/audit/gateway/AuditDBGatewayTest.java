package fr.infologic.vei.audit.gateway;

import static fr.infologic.vei.audit.TestAuditJsonObject.make;
import static java.util.function.Function.identity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.infologic.vei.audit.TestAuditJsonObject;
import fr.infologic.vei.audit.TrailTraceAssert;
import fr.infologic.vei.audit.api.AdminDB.AdminDBException;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.api.TrailKey;


public class AuditDBGatewayTest
{
    private AuditGateway gateway;
    
    @Before
    public void setUpGateway() throws AdminDBException
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
    public void searchADocumentUsingTimestamps()
    {
        TestAuditJsonObject v1;
        long timestamp = new Date().getTime();
        gateway.trace(v1 = make().addMetadata("date", new Timestamp(timestamp)));

        List<TrailTrace> traces = gateway.makeQuery().forAllModifications().ofAnyTypeInSet(null, null).havingMetadata().fieldLessThan("date", new Timestamp(timestamp + 1)).build().search();
        TrailTraceAssert.assertThat(traces).containsExactly(v1.withContent(null));
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
    public void countAndDelete()
    {
        gateway.trace(make().withContent("{a:1}"));
        gateway.trace(make().withContent("{a:2,b:3}"));
        TrailKey key = make();
        Assertions.assertThat(gateway.find(key).count()).isEqualTo(2);
        gateway.find(key).delete();
        Assertions.assertThat(gateway.find(key).count()).isEqualTo(0);
    }
    
    @Test
    public void traceAndQueryForADocument()
    {
        @SuppressWarnings("unused")
        TestAuditJsonObject v1, v2;
        gateway.trace(v1 = make().addMetadata("key1", "value1").addMetadata("key2", "0value"));
        gateway.trace(v2 = make().addMetadata("key1", "value1").addMetadata("key2", "value2").withContent("{a:3}"));
        
        List<TrailTrace> traces = gateway.makeQuery().forAllModifications().ofAnyTypeInSet(null, null).havingMetadata().fieldEqualsTo("key1", "value1").fieldGreaterThan("key2", "value").build().search();
        TrailTraceAssert.assertThat(traces).containsExactly(v2.withContent(null));
    }
    
    @Test
    public void searchDocumentsWithRegExp()
    {
        @SuppressWarnings("unused")
        TestAuditJsonObject v1, v2;
        gateway.trace(v1 = make().addMetadata("key1", "value1"));
        gateway.trace(v2 = new TestAuditJsonObject("type", "group", "0key").withContent("{a:3}"));
        
        List<TrailTrace> traces = gateway.makeQuery().forAllModifications().ofAnyTypeInSet(null, null).havingKeyMatches(Pattern.compile("^k")).build().search();
        TrailTraceAssert.assertThat(traces).containsExactly(v1.withContent(null));
    }
    
    @Test
    public void searchDocumentsWithTypeDependantCriteria()
    {
        TestAuditJsonObject v1, v2;
        gateway.trace(v1 = new TestAuditJsonObject("type", "type", "key1").addMetadata("key1", "type").withContent(null));
        gateway.trace(v2 = new TestAuditJsonObject("type1", "type1", "key1").addMetadata("key1", "type1").withContent(null));
        gateway.trace(make().addMetadata("key1", "type1"));
        
        List<TrailTrace> traces = gateway.makeQuery().forAllModifications().ofAnyTypeInSet(null, (Function) identity()).havingMetadata().build().search();
        TrailTraceAssert.assertThat(traces).containsExactly(v1, v2);
    }
    
    @After
    public void tearDownDriver() throws AdminDBException
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
