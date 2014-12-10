package fr.infologic.vei.audit.api;

public interface AdminDB
{
    void drop() throws AdminDBException;
    void close() throws AdminDBException;
    int count();
    boolean isAlive();
    
    class AdminDBException extends Exception
    {
        public AdminDBException(Throwable cause)
        {
            super(cause);
        }
    }

}