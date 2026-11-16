import http from 'k6/http';
import {Counter} from 'k6/metrics';
import { BASE_URL, loginUsers, authHeaders} from './auth.js';


http.setResponseCallback(http.expectedStatuses(201, 409));

const holdsWon = new Counter('holds_won');
const created = new Counter('bookings_creates');
const conflicts = new Counter('bookings_conflicts');
const errors = new Counter('bookings_error');


const SCREENING_ID = 1;
const SEAT_ID = 4;
const USERS = 200

export const options = {
  scenarios: {
    same_seat: {
      executor: 'per-vu-iterations',
      vus: USERS,
      iterations: 1,
    },
  },
  thresholds: {
    bookings_creates: ['count==1'], 
    bookings_error: ['count==0'],  
  },
};


export function setup() {
    const tokens = loginUsers(USERS) 
    
    return {tokens };
}

function holdUrl(seatId) {
  return `${BASE_URL}/screenings/${SCREENING_ID}/seats/${seatId}/hold`;
}


export default function (data) {

    const params = authHeaders(data.tokens[__VU - 1]); 

    const hold = http.post(holdUrl(SEAT_ID), null, params);

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
        JSON.stringify({ screeningId: SCREENING_ID, seatIds: [SEAT_ID] }),
        
        params
    );

    if (booking.status === 201) created.add(1);

    else errors.add(1);
}