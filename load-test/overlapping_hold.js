import http from 'k6/http';
import {Counter} from 'k6/metrics';

http.setResponseCallback(http.expectedStatuses(200, 201, 204, 409));

const SCREENING_ID = 1;
const FIRST_SEAT_ID = 1;
const SEAT_COUNT = 120;
const BASE_URL = 'http://localhost:8081';

const created = new Counter('bookings_created');
const conflicts = new Counter('bookings_conflicts');
const errors = new Counter('bookings_error');
const seatsSoldOut = new Counter('bookings_sold_out');


export const options = {
  scenarios: {
    overlapping: {
      executor: 'per-vu-iterations',
      vus: 200,
      iterations: 1,
    },
  },
  thresholds: {
    bookings_error: ['count==0'],
    bookings_sold_out: [`count<=${SEAT_COUNT}`],
  },
};

function holdUrl(seatId, userId) {
  return `${BASE_URL}/screenings/${SCREENING_ID}/seats/${seatId}/hold?userId=${userId}`;
}

export default function(){

    const userId = __VU;

    const groupSize = 1 + Math.floor(Math.random()*3);
    const start = FIRST_SEAT_ID + Math.floor(Math.random() * (SEAT_COUNT-groupSize+1))
    const seatIds = [];

    for(let i = 0; i<groupSize; i++){
        seatIds.push(start+i);
    }


    const hold = [];

    for(const seatId of seatIds){
        const res = http.post(holdUrl(seatId,userId))

        if (res.status === 201){
            hold.push(seatId);
        }else if(res.status===409){
            console.log(`409 on seat ${seatId}, step ${hold.length + 1}/${groupSize}: ${res.body}`);
            for(const heldSeat of hold){
                http.del(holdUrl(heldSeat,userId))
            }
            conflicts.add(1);
            return;
        }else{
            errors.add(1);
            console.log(`hold failed: ${res.status} ${res.body}`);
            return;
        }
    }
    const booking = http.post(
        `${BASE_URL}/bookings`,
        JSON.stringify({ userId: userId, screeningId: SCREENING_ID, seatIds: seatIds }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    if (booking.status === 201) {
        created.add(1);
        seatsSoldOut.add(seatIds.length);
        
    } else {
        console.log(`booking failed: ${booking.status} ${booking.body}`);
        errors.add(1);
        
    }
}