import createClient from './client'
import { useAdminAuthStore } from '@/stores/adminAuth'

const adminApi = createClient(
  'http://localhost:8080/api', 
  '/auth/admin/refresh', 
  () => useAdminAuthStore()
)

export default adminApi
