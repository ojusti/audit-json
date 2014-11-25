package fr.infologic.vei.audit.gateway;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditDriver;
import fr.infologic.vei.audit.api.QueryDriver;


public interface AuditGateway extends AuditDriver, QueryDriver
{
    AdminDB db();
}
