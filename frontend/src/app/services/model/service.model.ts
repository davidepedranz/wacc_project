export class Service {
    Name = "";
    taskTemplate : TaskTemplate;
    mode : Mode;
    endpointSpec : EndpointSpec;
}

export class TaskTemplate {
    containerSpec : ContainerSpec;
}

export class ContainerSpec {
    Image = "";
}

export class Mode {
    replicated : Replicated;
}

export class Replicated {
    Replicas = 0;
}

export class EndpointSpec {
    ports : Ports[];
}

export class Ports {
    protcol = "";
    PublishedPort = 0;
    TargetPort = 0;
    
}