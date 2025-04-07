# MongoDB Replica Sets on Docker

## Overview
A MongoDB replica set is a group of MongoDB servers that maintain the same dataset to provide high availability and fault tolerance. It consists of a primary node, which handles write operations, and multiple secondary nodes that replicate data from the primary. If the primary fails, an automatic election process promotes a secondary to primary, ensuring minimal downtime. Replica sets support read scaling by allowing reads from secondaries and enable disaster recovery by maintaining redundant copies of data.

## Prerequisites
- Docker installed on your system.
- Basic understanding of MongoDB replica sets.

## Steps to Set Up
This guide walks you through setting up a **MongoDB Replica Set** using Docker.

### 1. Pull a mongo image
```sh
docker pull mongo:8.0
```

### 2. Create a Docker Network
```sh
docker network create mongo-replica-net
```

### 3. Start MongoDB Containers
Run the following commands to start three MongoDB instances as replica set members:

```sh
docker run -d --name mongo1 --net mongo-replica-net -p 27017:27017 \
-v mongo1_data:/data/db \
mongo:8.0 --replSet rs0 --bind_ip_all --port 27017
```
```sh
docker run -d --name mongo2 --net mongo-replica-net -p 27018:27018 \
-v mongo2_data:/data/db \
mongo:8.0 --replSet rs0 --bind_ip_all --port 27018
```
```sh
docker run -d --name mongo3 --net mongo-replica-net -p 27019:27019 \
-v mongo3_data:/data/db \
mongo:8.0 --replSet rs0 --bind_ip_all --port 27019
```

### 4. Initial Replica Set
Connect to the primary MongoDB instance and initiate the replica set:
```sh
docker exec -it mongo1 mongosh --host mongo1 --port 27017
```
```sh
rs.initiate({
    _id: "rs0",
    members: [
        { _id: 0, host: "mongo1:27017" },
        { _id: 1, host: "mongo2:27018" },
        { _id: 2, host: "mongo3:27019" }
    ]
})
```
```sh
rs.status()
```

### 5. Check Secondary Replica Set
Verify secondary replication setup with:
```sh
rs.printSecondaryReplicationInfo()
```

### 6. Stop primary
Stopping the primary node which will triggers an automatic failover:
```sh
docker stop mongo1
```
```sh
docker exec -it mongo2 mongosh --host mongo2 --port 27018
```
```sh
rs.status()
```

### 7. Start mongo1 node
Start `mongo1`, which will initially start as a secondary node.
```sh
docker start mongo1
```

### 8. Setup hosts and connect to db client
```sh
docker network inspect mongo-replica-net
```
Add the following entry to /etc/hosts to enable communication with Docker containers:
```sh
192.168.x.x mongo1
192.168.x.x mongo2
192.168.x.x mongo3
```

### 9. Pull sample api
Pull the sample API to test reading from secondary nodes in a MongoDB replica set:
```sh
docker pull toiart/replicate-sharding-app:1.0.0
```
```sh
docker ps -a
```

### 10. Configure Spring Boot to Use the Replica Set
You can start application using Docker Compose with the following environment variables:
```sh
SPRING_DATA_MONGODB_URI="mongodb://mongo1:27017,mongo2:27018,mongo3:27019/product_db?replicaSet=rs0" docker-compose up -d
```
```sh
curl --location 'http://localhost:8080/orders/user1'
```

### 12. Monitor MongoDB Instances
Run the following commands to check the status of each MongoDB instance:

```sh
mongostat --host mongo1:27017 -o=insert,query,update,delete
```
```sh
mongostat --host mongo2:27018 -o=insert,query,update,delete
```
```sh
mongostat --host mongo3:27019 -o=insert,query,update,delete
```

### 13. Simulate read from the primary node
```sh
k6 run getOrder.js
```

### 14. Configure Spring Boot to Use the Replica Set
```sh
docker stop replicate-sharding-app
```

### 15. Configure Spring Boot to read from Secondary Nodes
```sh
SPRING_DATA_MONGODB_URI="mongodb://mongo1:27017,mongo2:27018,mongo3:27019/product_db?replicaSet=rs0&readPreference=secondaryPreferred" docker-compose up -d
```

### 16. Simulate read from the secondary node
```sh
k6 run getOrder.js
```