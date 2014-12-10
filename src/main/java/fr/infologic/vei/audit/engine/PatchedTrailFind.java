package fr.infologic.vei.audit.engine;

import java.util.ArrayList;
import java.util.List;

import fr.infologic.vei.audit.api.AuditFind.Content;
import fr.infologic.vei.audit.api.AuditFind.TrailFind;
import fr.infologic.vei.audit.api.AuditFind.TrailTrace;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailFind;
import fr.infologic.vei.audit.engine.TrailEngine.PatchableTrailTrace;

public class PatchedTrailFind implements TrailFind
{
    private final PatchableTrailFind find;
    
    public PatchedTrailFind(PatchableTrailFind find)
    {
        this.find = find;
    }

    @Override
    public TrailTrace last()
    {
        return find.last();
    }

    @Override
    public List<? extends TrailTrace> all()
    {
        return allFromVersion(1);
    }
    
    @Override
    public List<? extends TrailTrace> allFromVersion(int minVersion)
    {
        List<? extends PatchableTrailTrace> content = find.allFromVersion(minVersion);
        if(containsPatches(content))
        {
            return patch(content);
        }
        return content;
    }
    
    @Override
    public int count()
    {
        return find.count();
    }
    
    @Override
    public void delete()
    {
        find.delete();
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
