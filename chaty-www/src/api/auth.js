import axios from "@/http";

export const getAuthData = () => {
    return axios.get("/api/auth/getAuthData")
}

export const loginWithSchool = (schoolId) => {
    return axios.get(`/api/auth/login/school?schoolId=${schoolId}`)
}

export const getSchools = () => {
    return axios.get("/api/auth/getSchools")
}