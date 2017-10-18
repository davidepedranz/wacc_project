#!/bin/bash

# sources
# https://docs.mongodb.com/manual/tutorial/deploy-replica-set/
# https://medium.com/lucjuggery/mongodb-replica-set-on-swarm-mode-45d66bc9245

# exit if the script receives a SIGTERM signal
trap "exit 0" SIGTERM

# read the name of the service from the environment
if [[ -z "${SERVICE_NAME}" ]]; then
    echo -e "Please set the environment variable SERVICE_NAME with the name of the MongoDB service in Docker Swarm!\n"
    exit 1
fi
REPLICA_SET_NAME="rs0"

nslookup ${SERVICE_NAME}

# wait to have at least 3 instances of mongo
READY=false
while [ "$READY" = "false" ]; do
    sleep 5
    echo -e "\nNew attempt to setup the MongoDB cluster."

    # get all instances of mongo
    INSTANCES=($(nslookup ${SERVICE_NAME} | tail -n +4 | grep 'Address' | cut -d' ' -f2 | sort))
    echo -e "Got the following instances of the service '${SERVICE_NAME}':"
    printf '  --> %s\n' "${INSTANCES[@]}"

    # make sure 3 replicas available
    if [ "${#INSTANCES[@]}" -ge "3" ]; then
        READY=true

        # create replica set
        TARGET=${INSTANCES[0]}
        echo -e "\nTry to create the replica set using the instance with the smallest ip: ${TARGET}.\n"
        status=$(mongo --host ${TARGET} --quiet --eval 'rs.status().members.length')
        if [ $? -ne 0 ]; then
            echo "Replica set not yet configured. Create it..."
            MEMBERS=$(echo ${INSTANCES[*]} | sed "s/ /\n/g" | awk '{print "{ _id: " ++ln ", host: \"" $0 "\" }," }'  | tr '\n' ' ' | sed 's/, $/\n/')
            echo -e "${MEMBERS}\n"
            mongo --host "${TARGET}" --eval "rs.initiate({ _id: \"${REPLICA_SET_NAME}\", version: 1, members: [ ${MEMBERS} ] })";
        else
            echo "Replica set already created... SKIP"
        fi
    else
        echo "MongoDB is not yet ready (we need at least 3 instances for a serious replica set)... retry again in 5 seconds."
        sleep 5
    fi
done

echo -e "\nDONE! =D\n"
