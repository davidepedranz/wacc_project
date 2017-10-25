export class Service {
    name = "";
    taskTemplate : TaskTemplate;
    mode : Mode;
    endpointSpec : EndpointSpec;
}

export class TaskTemplate {
    containerSpec : ContainerSpec;
}

export class ContainerSpec {
    image = "";
}

export class Mode {
    replicated : Replicated;
}

export class Replicated {
    replicas = 0;
}

export class EndpointSpec {
    ports : Ports[];
}

export class Ports {
    protcol = "";
    publishedPort = 0;
    targetPort = 0;
    
}