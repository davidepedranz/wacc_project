#!/bin/bash

# sources
# https://docs.mongodb.com/manual/tutorial/deploy-replica-set/
# https://medium.com/lucjuggery/mongodb-replica-set-on-swarm-mode-45d66bc9245

# exit if the script receives a SIGTERM signal
trap "exit 0" SIGTERM

# read the name of the service from the environment
if [[ -z "${MONGO_NAMES}" ]]; then
    echo -e "Please set the environment variable MONGO_NAMES with the names of the MongoDB services in Docker Swarm!\n"
    exit 1
fi
REPLICA_SET_NAME="rs0"

# wait to have at least 3 instances of mongo
echo -e "\nNew attempt to setup the MongoDB cluster."

# get all instances of mongo
INSTANCES=(${MONGO_NAMES})
echo -e "Got the following instances for MongoDB:"
printf '  --> %s\n' "${INSTANCES[@]}"
echo ""

# make sure the replicas available
for INSTANCE in ${INSTANCES[@]}; do
    echo "Testing instance ${INSTANCE}..."
    mongo --host $INSTANCE --eval 'db' > /dev/null
    if [ $? -ne 0 ]; then
        echo "Instance ${INSTANCE} is not available."
        exit 1
    fi
done

# create replica set
TARGET=${INSTANCES[0]}
echo -e "\nTry to create the replica set using the first instance: ${TARGET}.\n"
status=$(mongo --host ${TARGET} --quiet --eval 'rs.status().members.length')
if [ $? -ne 0 ]; then
    echo "Replica set not yet configured. Create it..."
    MEMBERS=$(echo ${INSTANCES[*]} | sed "s/ /\n/g" | awk '{print "{ _id: " ++ln ", host: \"" $0 "\" }," }'  | tr '\n' ' ' | sed 's/, $/\n/')
    echo -e "${MEMBERS}\n"
    mongo --host "${TARGET}" --eval "rs.initiate({ _id: \"${REPLICA_SET_NAME}\", version: 1, members: [ ${MEMBERS} ] })";
else
    echo "Replica set already created... SKIP"
fi

echo -e "\nDONE! =D\n"
