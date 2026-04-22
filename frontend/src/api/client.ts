import axios, { type AxiosInstance, type AxiosError } from 'axios'

const createClient = (baseURL: string, refreshURL: string, authStore: any, appStore: any): AxiosInstance => {
  const client = axios.create({
    baseURL,
    withCredentials: true,
    headers: {
      'Content-Type': 'application/json',
    },
  })

  // Request Interceptor
  client.interceptors.request.use((config) => {
    // Start Global Loading
    appStore().startLoading()

    const token = authStore().token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }, (error) => {
    appStore().stopLoading()
    return Promise.reject(error)
  })

  // Response Interceptor
  client.interceptors.response.use(
    (response) => {
      // Stop Global Loading
      appStore().stopLoading()
      return response
    },
    async (error: AxiosError) => {
      const originalRequest = error.config as any
      
      // If 401 and not already retrying
      if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/refresh')) {
        originalRequest._retry = true
        
        try {
          // Note: Refresh call itself will trigger interceptors if using the same client,
          // but here we use a clean axios instance to avoid recursion issues if needed.
          // However, we want the loading bar to persist during refresh.
          const response = await axios.post(
            `${baseURL}${refreshURL}`, 
            {}, 
            { withCredentials: true }
          )
          
          const newToken = response.data.data.accessToken
          authStore().setToken(newToken)
          
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          
          // Re-trigger the request (this will trigger interceptors again, 
          // so we must stop loading for the FAILED request now)
          appStore().stopLoading()
          return client(originalRequest)
        } catch (refreshError) {
          appStore().stopLoading()
          authStore().logout()
          return Promise.reject(refreshError)
        }
      }
      
      appStore().stopLoading()
      return Promise.reject(error)
    }
  )

  return client
}

export default createClient
