package fr.infologic.vei.audit.api;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import fr.infologic.vei.audit.api.AuditFind.TrailTrace;

public interface AuditQuery
{
    TraceQueryDispatch makeQuery();
    
    public interface TraceQueryDispatch
    {
        TraceFieldQueryBuilder forModificationsOf(String field);
        TraceAllQueryBuilder forAllModifications();
    }
    
    public interface TraceFieldQueryBuilder
    {
        TraceQueryBuilder inType(String requestedType, String group);
    }
    
    public interface TraceAllQueryBuilder
    {
        TraceQueryBuilder ofAnyTypeInSet(Set<String> requestedTypes, Function<String, String> typeDependantGroup);
    }
    public interface TraceQueryBuilder
    {
        TraceQueryBuilder havingKeyEqualsTo(String requestedKey);
        TraceQueryBuilder havingKeyMatches(Pattern regExp);
        TraceMetadataQueryBuilder havingMetadata();
        TraceQuery build();
    }
    
    public interface TraceMetadataQueryBuilder
    {
        public TraceMetadataQueryBuilder fieldEqualsTo(String field, Object requestedValue);
        public TraceMetadataQueryBuilder fieldGreaterThan(String field, Object minValue);
        public TraceMetadataQueryBuilder fieldLessThan(String field, Object maxValue);
        public TraceQuery build();
    }
    
    public interface TraceQuery
    {
        List<TrailTrace> search();
    }

}
