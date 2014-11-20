package fr.infologic.vei.audit.gateway;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditDriver;


public interface AuditGateway extends AuditDriver
{
    AdminDB db();
}
