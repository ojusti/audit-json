package fr.infologic.vei.audit.gateway;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditDriver;
import fr.infologic.vei.audit.api.AuditQueryDriver;


public interface AuditGateway extends AuditDriver, AuditQueryDriver
{
    AdminDB db();
}
