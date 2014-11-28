package fr.infologic.vei.audit.gateway;

import fr.infologic.vei.audit.api.AdminDB;
import fr.infologic.vei.audit.api.AuditFind;
import fr.infologic.vei.audit.api.AuditQuery;
import fr.infologic.vei.audit.api.AuditUpdate;


public interface AuditGateway extends AuditUpdate, AuditFind, AuditQuery
{
    AdminDB db();
}
