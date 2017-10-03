#!/bin/bash
set -eux

# script paramaters
ip=$1
first_node_ip=$2

# check it this is the first node
if [ "$ip" == "$first_node_ip" ]; then

    # first node: init the swarm
    docker swarm init \
        --data-path-addr $ip \
        --listen-addr "$ip:2377" \
        --advertise-addr "$ip:2377"

    # save the swarm join tokens into the shared folder
    mkdir -p /vagrant/shared
    docker swarm join-token manager -q >/vagrant/shared/docker-swarm-join-token-manager.txt
else
    # new node: join the swarm as a manager
    docker swarm join \
        --token $(cat /vagrant/shared/docker-swarm-join-token-manager.txt) \
        "$first_node_ip:2377"
fi
