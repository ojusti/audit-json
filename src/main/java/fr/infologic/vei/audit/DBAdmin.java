package fr.infologic.vei.audit;

public interface DBAdmin
{
    void drop();
    void close();
    boolean isAlive();
}