import http from 'k6/http';
import {Counter} from 'k6/metrics';

http.setResponseCallback(http.expectedStatuses(201, 409));

const holdsWon = new Counter('holds_won');
const created = new Counter('bookings_creates');
const conflicts = new Counter('bookings_conflicts');
const errors = new Counter('bookings_error');

const BASE_URL = 'http://localhost:8081';
const SCREENING_ID = 1;
const SEAT_ID = 4;
export const options = {
  scenarios: {
    same_seat: {
      executor: 'per-vu-iterations',
      vus: 200,
      iterations: 1,
    },
  },
  thresholds: {
    bookings_creates: ['count==1'], 
    bookings_error: ['count==0'],  
  },
};

export default function () {

    const userId = __VU;

    const hold = http.post(
        `${BASE_URL}/screenings/${SCREENING_ID}/seats/${SEAT_ID}/hold?userId=${userId}`,
        
        { headers: { 'Content-Type': 'application/json' } }
    );

    if (hold.status === 409) {
        conflicts.add(1);
        return;               
    }
    if (hold.status !== 201) {
        console.log(`hold failed: ${hold.status} ${hold.body}`);
        errors.add(1);
        return;
    }
    holdsWon.add(1);

    const booking = http.post(
        `${BASE_URL}/bookings`,
        JSON.stringify({ userId: userId, screeningId: SCREENING_ID, seatIds: [SEAT_ID] }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    if (booking.status === 201) created.add(1);

    else errors.add(1);
}