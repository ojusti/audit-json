package fr.infologic.vei.audit;

import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class AuditJsonDriverTest
{
    private AuditJsonDriver driver;
    
    @Before
    public void setUpDriver()
    {
        driver = driver();
    }
    
    @Test
    public void traceAndRetrieveADocument()
    {
        AuditJsonObject object = new TestAuditJsonObject("collection", "key").addMetadata("meta", "value");
        driver.trace(object);

        AuditJsonKey key = object;
        AuditJsonObject persisted = driver.find(key).last();
        AuditJsonObjectAssert.assertThat(persisted).hasKey(object.key)
                                                   .hasType(object.type)
                                                   .hasMetadata(object.metadata)
                                                   .hasContent(object.objectAsJson)
                                                   .isEqualTo(object);
    }
    
    @Test
    public void traceAPatchAndRetrieveAllVersions()
    {
        AuditJsonObject v1 = new TestAuditJsonObject("collection", "key").withContent("{a : \"b\"}");
        driver.trace(v1);
        
        AuditJsonObject v2 = new TestAuditJsonObject("collection", "key").withContent("{a : \"c\", b : \"d\"}");
        driver.trace(v2);

        AuditJsonKey key = v1;
        List<AuditJsonObject> persisted = driver.find(key).all();
        AuditJsonObjectAssert.assertThat(persisted).containsExactly(v1, v2);
    }
    
    @After
    public void tearDownDriver()
    {
        driver.drop();
    }
    
    @BeforeClass
    public static void isAlive()
    {
        Assume.assumeTrue(driver().isAlive());
    }
    private static AuditJsonDriver driver()
    {
        return AuditJsonDriverBuilder.db(AuditJsonDriverTest.class.getSimpleName()).build();
    }
}
