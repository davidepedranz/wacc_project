export class Service {
    Name = "";
    TaskTemplate : TaskTemplate = new TaskTemplate();
    Mode : Mode = new Mode();
    EndpointSpec : EndpointSpec = new EndpointSpec();
}

export class TaskTemplate {
    ContainerSpec : ContainerSpec = new ContainerSpec();
}

export class ContainerSpec {
    Image = "";
}

export class Mode {
    Replicated : Replicated = new Replicated();
}

export class Replicated {
    Replicas = 0;
}

export class EndpointSpec {
  Ports : Ports[] = new Array( new Ports())
}

export class Ports {
  Protocol : String = "";
  PublishedPort:number = 0;
  TargetPort :number = 0
}
