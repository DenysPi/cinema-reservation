import http from 'k6/http';


export const BASE_URL = "http://localhost:8081";

export function login(email, password){
    const res = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({email,password}),
        { headers: { 'Content-Type': 'application/json' } }
    )
    if (res.status !== 200) {
        throw new Error(`login failed for ${email}: ${res.status} ${res.body}`);
    }
    return res.json("accessToken");
    
}

export function loginUsers(number){
    const tokens = []

    for(let i=1; i <= number; i++){
        tokens.push(login(`user${i}@gmail.com`, "password_1234"))
    }
    return tokens;
}


export function authHeaders(token) {
  return {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
  };
}