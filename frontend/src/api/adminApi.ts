import createClient from './client'
import { useAdminAuthStore } from '@/stores/adminAuth'
import { useAppStore } from '@/stores/app'

const adminApi = createClient(
  'http://localhost:8080/api', 
  '/auth/admin/refresh', 
  () => useAdminAuthStore(),
  () => useAppStore()
)

export default adminApi
