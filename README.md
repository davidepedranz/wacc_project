# Docker Management Dashboard

This repository contains the project of the course Web and Cloud Computing (winter 2017-18, University of Groningen) for group 4:

* Davide Pedranz (S3543757)
* Francesco Segala (S3521885)
* Yuying Andrew Chen (S3421902)

## Repository Structure

| Folder                                   | Content                                  |
| ---------------------------------------- | ---------------------------------------- |
| [architecture](architecture)             | The folder contains the description of the initial architecture of the project. |
| [frontend](frontend)                     | Implementation of the frontend using Angular 4 and Angular Material. |
| [backend](backend)                       | Implementation of the backend (and workers) using Play Framework and Scala. |
| [traefik](traefik)                       | Configuration for the Traefik reverse proxy (in particular, TLS/SSL settings). |
| [mongo-setup](mongo-setup)               | Script to setup the replica set for MongoDB at deployment time (+ Dockerfile to package it). |
| [docker-socket-proxy](docker-socket-proxy) | Dockerfile for the `socat` container used as a proxy for the Docker UNIX socket. |
| [gcp](gcp)                               | Contains a script to install and setup Docker on the virtual machines on Google Cloud Platform. |
| [machines](machines)                     | Vagrantfile and scripts to setup a local Docker Swarm cluster. This works, but was not used in the end, since we decided to deploy to Google Cloud Platform. |
| [report](report)                         | LaTeX source for the final report.       |

Other interesting files are:

* [wacc-gcp.yml](./wacc-gcp.yml) -> define the stack of the application and allows to deploy it with a single `docker stack deploy` command
* [Makefile](Makefile) -> define commands to compile and package all components of the application, push them to the Docker Registry and deploy the stack to Google Cloud Platform. 
* [report.pdf](./report.pdf) -> final report

## Setup

### Frontend

```bash
# install yarn
# https://yarnpkg.com/lang/en/docs/install/

# use yarn instead of npm for Angular
ng set --global packageManager=yarn
```

### Google Cloud Platform

In order to deploy to Google Cloud Platform, you need to correctly configure SSH. You can add the following snippet to the `~/.ssh/config` file:

```
Host wacc0
    HostName <ip-node-0>
    User ubuntu
    IdentityFile ~/.ssh/wacc

Host wacc1
    HostName <ip-node-1>
    User ubuntu 
    IdentityFile ~/.ssh/wacc

Host wacc2
    HostName <ip-node-2>
    User ubuntu
    IdentityFile ~/.ssh/wacc

Host wacc3
    HostName <ip-node-3> 
    User ubuntu
    IdentityFile ~/.ssh/wacc
```

After spawning the needed machines, installed Docker, configured Swarm, you need to add some labels to Docker Swarm nodes.
Login to `wacc0` using SSH. Then run the following commands.
```bash
docker node update --label-add node=node0 node0
docker node update --label-add node=node1 node1
docker node update --label-add node=node2 node2
docker node update --label-add node=node3 node3
```

The last step is to create a folder to store the SSL certificates for Traefik.
Login to `wacc0` using SSH. Then run the following commands:
```bash
sudo mkdir /opt/traefik
sudo touch /opt/traefik/acme.json
sudo chmod 600 /opt/traefik/acme.json
```
