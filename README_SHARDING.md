# MongoDB Sharding with Replica Sets on Docker

## Overview
This guide sets up a MongoDB sharded cluster with multiple shards and replica sets using Docker. It includes a Config Server, 4 Shards, and a Mongos Router, with some shards configured as replica sets for high availability.

## Prerequisites
- Docker installed on your system.
- Basic understanding of MongoDB sharding and replica sets.

## Steps to Set Up
This guide walks you through setting up a **MongoDB Sharding with Replica Set** using Docker.

### 1. Create a Docker Network
```sh
docker network create mongo-shard-net
```

### 2. Run the Config Server
```sh
docker run -d --name config-server --network mongo-shard-net -p 27017:27017 \
  --add-host=host.docker.internal:host-gateway \
  -v config-server:/data/configdb \
  mongo:8.0 mongod --configsvr --replSet config-repl-set --port 27017 --bind_ip_all
```

### 3. Configure the Config Replica Set
```sh
docker exec -it config-server mongosh --port 27017
```
```sh
rs.initiate({
  _id: "config-repl-set",
  configsvr: true,
  members: [
    { _id: 0, host: "host.docker.internal:27017" }
  ]
});
```
```sh
rs.status()
```

### 4. Run Shard 1 (Replica Set with 3 Nodes)
```sh
docker run -d --name shard1-1 --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27018:27018 -v shard1-1:/data/db mongo:8.0 \
  mongod --shardsvr --replSet shard1-repl-set --port 27018 --bind_ip_all
```
```sh
docker run -d --name shard1-2 --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27019:27019 -v shard1-2:/data/db mongo:8.0 \
  mongod --shardsvr --replSet shard1-repl-set --port 27019 --bind_ip_all
```
```sh
docker run -d --name shard1-3 --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27020:27020 -v shard1-3:/data/db mongo:8.0 \
  mongod --shardsvr --replSet shard1-repl-set --port 27020 --bind_ip_all
```

#### Configure Shard 1 Replica Set
```sh
docker exec -it shard1-1 mongosh --port 27018
```
```sh
rs.initiate({
  _id: "shard1-repl-set",
  members: [
    { _id: 0, host: "host.docker.internal:27018", priority: 1 },
    { _id: 1, host: "host.docker.internal:27019", priority: 0.5 },
    { _id: 2, host: "host.docker.internal:27020", priority: 0.5 }
  ]
});
```

### 5. Run Shard 2
```sh
docker run -d --name shard2 --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27021:27021 -v shard2:/data/db mongo:8.0 \
  mongod --shardsvr --replSet shard2-repl-set --port 27021 --bind_ip_all
```

#### Configure Shard 2
```sh
docker exec -it shard2 mongosh --port 27021
```
```sh
rs.initiate({
  _id: "shard2-repl-set",
  members: [
    { _id: 0, host: "host.docker.internal:27021" }
  ]
});
```

### 6. Run Shard 3
```sh
docker run -d --name shard3 --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27022:27022 -v shard3:/data/db mongo:8.0 \
  mongod --shardsvr --replSet shard3-repl-set --port 27022 --bind_ip_all
```

#### Configure Shard 3
```sh
docker exec -it shard3 mongosh --port 27022
```
```sh
rs.initiate({
  _id: "shard3-repl-set",
  members: [
    { _id: 0, host: "host.docker.internal:27022" }
  ]
});
```

### 7. Run Shard 4
```sh
docker run -d --name shard4 --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27023:27023 -v shard4:/data/db mongo:8.0 \
  mongod --shardsvr --replSet shard4-repl-set --port 27023 --bind_ip_all
```

#### Configure Shard 4
```sh
docker exec -it shard4 mongosh --port 27023
```
```sh
rs.initiate({
  _id: "shard4-repl-set",
  members: [
    { _id: 0, host: "host.docker.internal:27023" }
  ]
});
```

### 8. Run Mongos Router
```sh
docker run -d --name mongos --network mongo-shard-net \
  --add-host=host.docker.internal:host-gateway \
  -p 27024:27024 mongo:8.0 \
  mongos --configdb config-repl-set/config-server:27017 --port 27024 --bind_ip_all
```

### 9. Add Shards to the Cluster
```sh
docker exec -it mongos mongosh --port 27024
```
```sh
sh.addShard("shard1-repl-set/host.docker.internal:27018,host.docker.internal:27019,host.docker.internal:27020")
```
```sh
sh.addShard("shard2-repl-set/host.docker.internal:27021")
```
```sh
sh.addShard("shard3-repl-set/host.docker.internal:27022")
```
```sh
sh.addShard("shard4-repl-set/host.docker.internal:27023")
```
```sh
sh.status()
```

## Verification
- Run `rs.status()` in each shard to ensure replica sets are working.
- Run `sh.status()` in `mongos` to verify the sharding setup.

# MongoDB Hash Sharding Setup

This guide explains how to set up hash-based sharding in MongoDB for the `orders` collection using `userId` as the shard key.

### 1. Create the `product_db` Database, `orders` Collection and Index
Create the `products` collection and an index on the `price` field.
- Create product_db database
- Create orders collection
- Create userId index

### 2. Enable Sharding for Database
Access the `mongos` router:
```sh
docker exec -it mongos mongosh --port 27024
```
Enable sharding for `product_db`:
```sh
sh.enableSharding("product_db")
```
Enable sharding on the `orders` collection using a hashed index:
```sh
sh.shardCollection("product_db.orders", { userId: "hashed" });
```

### 3. Verify Chunk Distribution
Switch to the `product_db` database:
```sh
use product_db
```
Check shard distribution:
```sh
db.orders.getShardDistribution()
```

### 4. Run Spring Boot Application with MongoDB
To connect your Spring Boot application to MongoDB, set the connection string:
```sh
SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27024/product_db" docker-compose up -d
```

### 5. Simulate request across the sharded collection
```sh
k6 run order.js
```

# MongoDB Range Sharding Setup

### 1. Create the `products` Collection and Index
Create the `products` collection and an index on the `price` field.
- Create products collection
- Create price index

### 2. Shard the `products` Collection Using Range-based Sharding
Enable sharding for the `products` collection on the `price` field.
```sh
sh.shardCollection("product_db.products", { price: 1 })
```

### 3. Define Range Splits for Shards
Set up range-based sharding by defining split points.
```sh
sh.splitAt("product_db.products", { price: 100 })
sh.splitAt("product_db.products", { price: 500 })
sh.splitAt("product_db.products", { price: 1000 })
```

### 4. Move Chunks to Specific Shards
Distribute chunks to different shards based on price ranges.
```sh
sh.moveChunk("product_db.products", { price: 50 }, "shard1-repl-set")
```
```sh
sh.moveChunk("product_db.products", { price: 300 }, "shard2-repl-set")
```
```sh
sh.moveChunk("product_db.products", { price: 700 }, "shard3-repl-set")
```
```sh
sh.moveChunk("product_db.products", { price: 1500 }, "shard4-repl-set")
```

#### Shard Distribution
- **shard1** → `price < 100`
- **shard2** → `100 ≤ price ≤ 500`
- **shard3** → `500 ≤ price ≤ 1000`
- **shard4** → `price > 1000`

### 5. Verify Shard Distribution
Check chunk distribution across shards.
```sh
db.products.getShardDistribution()
```

### 6. Simulate request across the sharded collection
```sh
k6 run product.js
```
