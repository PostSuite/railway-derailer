package com.postsuite.derailer.infrastructure.managers.railway.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeploymentList {

    private String projectId;
    private String environmentId;
    private String serviceId;

}
