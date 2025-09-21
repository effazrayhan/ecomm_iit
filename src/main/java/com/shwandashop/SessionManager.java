package com.shwandashop;

/**
 * SessionManager class to manage current user session information
 * This allows controllers to access user role and other session data
 */
public class SessionManager {
    private static SessionManager instance;
    private String currentUserEid;
    private String currentUserRole;
    private String currentUserName;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(String eid, String role, String name) {
        this.currentUserEid = eid;
        this.currentUserRole = role;
        this.currentUserName = name;
    }

    public String getCurrentUserEid() {
        return currentUserEid;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public boolean isAdmin() {
        return "admin".equals(currentUserRole);
    }

    public boolean isEmployee() {
        return "employee".equals(currentUserRole);
    }

    public void clearSession() {
        this.currentUserEid = null;
        this.currentUserRole = null;
        this.currentUserName = null;
    }
}
