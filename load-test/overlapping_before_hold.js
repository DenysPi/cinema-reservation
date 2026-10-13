import http from 'k6/http';
import {Counter} from 'k6/metrics';


const SCREENING_ID = 1;
const FIRST_SEAT_ID = 1;
const SEAT_COUNT = 120;
const USER_iDS = [1,2];

const created = new Counter('bookings_created');
const conflicts = new Counter('bookings_conflicts');
const refused = new Counter('bookings_refused');
const errors = new Counter('bookings_error');
const seatsSoldOut = new Counter('bookings_sold_out');

export const options = { vus: 200, iterations: 1000};

export default function(){
    const groupSize = 1 + Math.floor(Math.random()*3);
    const start = FIRST_SEAT_ID + Math.floor(Math.random() * (SEAT_COUNT-groupSize+1))
    const seatIds = [];

    for(let i = 0; i<groupSize; i++){
        seatIds.push(start+i);
    }

    const userId = USER_iDS[Math.floor(Math.random()* USER_iDS.length)]
    
    const res = http.post(
        'http://localhost:8081/bookings',
        JSON.stringify({ userId: userId, screeningId: SCREENING_ID, seatIds: seatIds }),
        { headers: { 'Content-Type': 'application/json' } }
      );
    
    if (res.status === 201){
         created.add(1);
         seatsSoldOut.add(groupSize);
    }
    else if (res.status === 409) conflicts.add(1);
    else if (res.status === 0) refused.add(1)
    else errors.add(1);
}