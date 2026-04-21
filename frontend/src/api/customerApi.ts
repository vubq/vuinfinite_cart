import createClient from './client'
import { useCustomerAuthStore } from '@/stores/customerAuth'

const customerApi = createClient(
  'http://localhost:8080/api', 
  '/auth/customer/refresh', 
  () => useCustomerAuthStore()
)

export default customerApi
