package fr.infologic.vei.audit.api;

import java.util.List;
import java.util.Set;

import fr.infologic.vei.audit.api.AuditDriver.TrailTrace;

public interface QueryDriver
{
    TraceQueryBuilder makeQuery();
    
    public interface TraceQueryBuilder
    {
        TraceQueryBuilder inTypes(Set<String> requestedTypes);
        TraceQueryBuilder keyEqualsTo(String requestedKey);
//        TraceQueryBuilder keyMatches(String regExp);
        TraceMetadataQueryBuilder metadata();
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
