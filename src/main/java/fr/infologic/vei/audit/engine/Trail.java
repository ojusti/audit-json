package fr.infologic.vei.audit.engine;

import fr.infologic.vei.audit.api.AuditFind.Content;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailFind;
import fr.infologic.vei.audit.engine.TrailEngine.TrailRecord;

public interface Trail
{
    TrailRecord setContent(Content json);
    PatchableTrailFind find();
}