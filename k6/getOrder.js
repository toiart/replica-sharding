import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  iterations: 100,
};

export default function () {
    let res = http.get('http://localhost:8080/orders/user1');
    sleep(0.1);
}

// k6 run getOrder.js