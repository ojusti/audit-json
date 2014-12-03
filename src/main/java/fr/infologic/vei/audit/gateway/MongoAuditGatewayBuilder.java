package fr.infologic.vei.audit.gateway;

import java.util.Objects;

import fr.infologic.vei.audit.api.AdminDB.AdminDBException;
import fr.infologic.vei.audit.mongo.MongoDB;

public class MongoAuditGatewayBuilder
{
    private String db;
    private String host;
    private Integer port;

    public static MongoAuditGatewayBuilder db(String db)
    {
        return new MongoAuditGatewayBuilder(db);
    }
    
    private MongoAuditGatewayBuilder(String db)
    {
        this.db = Objects.requireNonNull(db, "Database name should not be null");
    }
    
    public MongoAuditGatewayBuilder host(String host)
    {
        this.host = host;
        return null;
    }

    public MongoAuditGatewayBuilder port(int port)
    {
        this.port = port;
        return null;
    }

    public AuditGateway build() throws AdminDBException
    {
        MongoDB mongoDB = new MongoDB(host, port, db);
        return new AuditDBGateway(mongoDB, mongoDB);
    }

}
