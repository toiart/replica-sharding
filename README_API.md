# API Usage Documentation

## 1. Create an Order
```bash
curl --location 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data '{
    "userId": "user1",
    "productId": "product1"
}'
```

## 2. Get Orders
```bash
curl --location 'http://localhost:8080/orders'
```

## 3. Create a Product
```bash
curl --location 'http://localhost:8080/products' \
--header 'Content-Type: application/json' \
--data '{
  "name": "Power bank",
  "price": 500
}'
```

## 4. Get Products Within Price Range
```bash
curl --location 'http://localhost:8080/products?minPrice=1000&maxPrice=1500'
```