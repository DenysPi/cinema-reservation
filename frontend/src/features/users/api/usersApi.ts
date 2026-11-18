import { http } from "../../../shared/api/httpClient"
import type { User } from "../types"


export const usersApi = {
    me: () => http<User>("/me"),
}