package com.quizapp.model;

import java.time.LocalDateTime;

/**
 * RMIServer entity representing a distributed RMI server
 */
public class RMIServer extends BaseEntity {
    private int serverId;
    private String serverName;
    private String hostAddress;
    private int port;
    private ServerStatus status;
    private int loadFactor; // 0-100 indicating server load
    private LocalDateTime lastHeartbeat;
    
    public enum ServerStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }
    
    // Constructors
    public RMIServer() {
        super();
        this.status = ServerStatus.ACTIVE;
        this.loadFactor = 0;
        this.lastHeartbeat = LocalDateTime.now();
    }
    
    public RMIServer(String serverName, String hostAddress, int port) {
        this();
        this.serverName = serverName;
        this.hostAddress = hostAddress;
        this.port = port;
    }
    
    // Getters and Setters
    public int getServerId() {
        return serverId;
    }
    
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    public String getHostAddress() {
        return hostAddress;
    }
    
    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public ServerStatus getStatus() {
        return status;
    }
    
    public void setStatus(ServerStatus status) {
        this.status = status;
    }
    
    public int getLoadFactor() {
        return loadFactor;
    }
    
    public void setLoadFactor(int loadFactor) {
        this.loadFactor = Math.min(Math.max(loadFactor, 0), 100);
    }
    
    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    // Business logic methods
    public String getConnectionString() {
        return "rmi://" + hostAddress + ":" + port + "/" + serverName;
    }
    
    public boolean isActive() {
        return status == ServerStatus.ACTIVE;
    }
    
    public boolean isAvailable() {
        return isActive() && loadFactor < 80; // Available if load < 80%
    }
    
    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
    }
    
    public boolean isHeartbeatExpired(int timeoutSeconds) {
        LocalDateTime timeoutTime = lastHeartbeat.plusSeconds(timeoutSeconds);
        return LocalDateTime.now().isAfter(timeoutTime);
    }
    
    public void incrementLoad() {
        this.loadFactor = Math.min(this.loadFactor + 10, 100);
    }
    
    public void decrementLoad() {
        this.loadFactor = Math.max(this.loadFactor - 10, 0);
    }
    
    public String getStatusColor() {
        switch (status) {
            case ACTIVE:
                return loadFactor < 50 ? "green" : loadFactor < 80 ? "yellow" : "orange";
            case INACTIVE:
                return "red";
            case MAINTENANCE:
                return "blue";
            default:
                return "gray";
        }
    }
    
    public String getStatusMessage() {
        if (!isActive()) {
            return status.toString();
        }
        
        if (loadFactor < 30) return "Idle";
        else if (loadFactor < 60) return "Moderate";
        else if (loadFactor < 80) return "Busy";
        else return "Overloaded";
    }
    
    // Validation methods
    public boolean isValid() {
        return serverName != null && !serverName.trim().isEmpty() &&
               hostAddress != null && !hostAddress.trim().isEmpty() &&
               port > 0 && port <= 65535;
    }
    
    @Override
    public String toString() {
        return "RMIServer{" +
                "serverId=" + serverId +
                ", serverName='" + serverName + '\'' +
                ", hostAddress='" + hostAddress + '\'' +
                ", port=" + port +
                ", status=" + status +
                ", loadFactor=" + loadFactor +
                ", lastHeartbeat=" + lastHeartbeat +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RMIServer rmiServer = (RMIServer) o;
        return serverId == rmiServer.serverId;
    }
    
    @Override
    public int hashCode() {
        return serverId;
    }
}