package com.postsuite.derailer.infrastructure.managers.railway.client.model;

import lombok.Data;

import java.util.List;

@Data
public class ProjectResponse {
    public Services services;

    @Data
    public static class Services {
        private List<Edge> edges;
    }

    @Data
    public static class Edge {
        private Node node;
    }

    @Data
    public static class Node {
        private String id;
        private String name;
        private String icon;
        private String templateServiceId;
        private String createdAt;
        private String projectId;
        private List<String> featureFlags;
        private String templateThreadSlug;
    }
}