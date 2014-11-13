package fr.infologic.vei.audit;

import java.util.Objects;

import fr.infologic.vei.audit.mongo.MongoDriver;

public class AuditJsonDriverBuilder
{
    private String db;
    private String host;
    private Integer port;

    public static AuditJsonDriverBuilder db(String db)
    {
        return new AuditJsonDriverBuilder(db);
    }
    
    private AuditJsonDriverBuilder(String db)
    {
        this.db = Objects.requireNonNull(db, "Database name should not be null");
    }
    
    public AuditJsonDriverBuilder host(String host)
    {
        this.host = host;
        return null;
    }

    public AuditJsonDriverBuilder port(int port)
    {
        this.port = port;
        return null;
    }

    public AuditJsonDriver build()
    {
        return new AuditJsonDriver(new MongoDriver(host, port, db));
    }

}
