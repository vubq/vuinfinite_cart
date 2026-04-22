import createClient from './client'
import { useCustomerAuthStore } from '@/stores/customerAuth'
import { useAppStore } from '@/stores/app'

const customerApi = createClient(
  'http://localhost:8080/api', 
  '/auth/customer/refresh', 
  () => useCustomerAuthStore(),
  () => useAppStore()
)

export default customerApi
