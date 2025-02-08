package com.postsuite.derailer.infrastructure.managers.railway.client.model;

import lombok.Data;

import java.util.List;

@Data
public class DeploymentResponse {
    private List<Edge> edges;

    @Data
    public static class Edge {
        private Node node;
    }

    @Data
    public static class Node {
        // Getters and Setters
        private String id;
        private String status;
        private String serviceId;
        private boolean canRollback;
        private String url;
        private String staticUrl;
        private boolean canRedeploy;
        private String environmentId;
        private boolean deploymentStopped;
    }

}
