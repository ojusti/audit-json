package fr.infologic.vei.audit.engine;

import java.util.ArrayList;
import java.util.List;

import fr.infologic.vei.audit.api.AuditDriver.Content;
import fr.infologic.vei.audit.api.AuditDriver.TrailQuery;
import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailQuery;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailTrace;

public class PatchedTrailQuery implements TrailQuery
{
    private final PatchableTrailQuery query;
    
    public PatchedTrailQuery(PatchableTrailQuery query)
    {
        this.query = query;
    }

    @Override
    public TrailTrace last()
    {
        return query.last();
    }

    @Override
    public List<? extends TrailTrace> all()
    {
        List<? extends PatchableTrailTrace> content = query.all();
        if(containsPatches(content))
        {
            return patch(content);
        }
        return content;
    }

    private static boolean containsPatches(List<? extends PatchableTrailTrace> content)
    {
        return content.size() >= 2;
    }
    
    private static List<? extends TrailTrace> patch(List<? extends PatchableTrailTrace> patches)
    {
        List<TrailTrace> result = new ArrayList<>(patches);
        Content base = lastOf(patches);
        for(int i = patches.size() - 2; i >= 0; i--)
        {
            PatchableTrailTrace patch = patches.get(i);
            PatchableTrailTrace patched = patch.applyTo(base);
            result.set(i, patched);
            base = patched.getContent();
        }
        return result;
    }

    private static Content lastOf(List<? extends PatchableTrailTrace> content)
    {
        return content.get(content.size() - 1).getContent();
    }
}
