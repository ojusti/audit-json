package fr.infologic.vei.audit.api;

public interface AdminDB
{
    void drop();
    void close();
    boolean isAlive();
}