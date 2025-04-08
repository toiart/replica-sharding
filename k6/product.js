import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '10s', target: 0 }
  ]
};

export default function () {
  const randomProductName = `Product-${Math.random().toString(36).substring(7)}`;
  const randomPrice = (Math.random() < 0.3 ? (Math.random() * 100 + 1) : (Math.random() * 1399 + 101)).toFixed(2);

  let res = http.post('http://localhost:8080/products', JSON.stringify({
    name: randomProductName,
    price: randomPrice
  }), { headers: { 'Content-Type': 'application/json' } });

  check(res, { 'status is 200': (r) => r.status === 200 });
}


// k6 run product.js