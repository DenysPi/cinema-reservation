import http from 'k6/http';
import {Counter} from 'k6/metrics';

http.setResponseCallback(http.expectedStatuses(201, 409));

const created = new Counter('bookings_creates');
const conflicts = new Counter('bookings_conflicts');
const errors = new Counter('bookings_error');


export const options = { vus: 200, iterations: 200};

export default function () {
  const res = http.post(
    'http://localhost:8081/bookings',
    JSON.stringify({ userId: 1, screeningId: 1, seatIds: [5] }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (res.status === 201) created.add(1);
  else if (res.status === 409) conflicts.add(1);
  else errors.add(1);
}