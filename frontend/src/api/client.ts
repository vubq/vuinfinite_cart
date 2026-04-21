import axios, { type AxiosInstance, type AxiosError } from 'axios'

const createClient = (baseURL: string, refreshURL: string, authStore: any): AxiosInstance => {
  const client = axios.create({
    baseURL,
    withCredentials: true,
    headers: {
      'Content-Type': 'application/json',
    },
  })

  // Request Interceptor: Attach Access Token
  client.interceptors.request.use((config) => {
    const token = authStore().token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  // Response Interceptor: Handle Token Refresh
  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as any
      
      // If 401 and not already retrying
      if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/refresh')) {
        originalRequest._retry = true
        
        try {
          // Attempt call refresh endpoint (uses HttpOnly cookie)
          const response = await axios.post(
            `${baseURL}${refreshURL}`, 
            {}, 
            { withCredentials: true }
          )
          
          const newToken = response.data.data.accessToken
          authStore().setToken(newToken)
          
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return client(originalRequest)
        } catch (refreshError) {
          authStore().logout()
          return Promise.reject(refreshError)
        }
      }
      
      return Promise.reject(error)
    }
  )

  return client
}

export default createClient
