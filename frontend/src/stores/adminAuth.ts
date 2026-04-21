import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import adminApi from '@/api/adminApi'
import router from '@/router'

export const useAdminAuthStore = defineStore('adminAuth', () => {
  const token = ref<string | null>(localStorage.getItem('admin_token'))
  
  let initialUser = null
  try {
    initialUser = JSON.parse(localStorage.getItem('admin_user') || 'null')
    if (initialUser === '[object Object]') initialUser = null
  } catch (e) {
    localStorage.removeItem('admin_user')
  }
  const user = ref<any>(initialUser)
  const loading = ref(false)

  // Auto-sync persistence guaranteeing valid JSON parsing
  watch(token, (val) => val ? localStorage.setItem('admin_token', val) : localStorage.removeItem('admin_token'))
  watch(user, (val) => val ? localStorage.setItem('admin_user', JSON.stringify(val)) : localStorage.removeItem('admin_user'), { deep: true })

  const isAuthenticated = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
  }

  function setUser(userData: any) {
    user.value = userData
  }

  async function login(credentials: any) {
    loading.value = true
    try {
      const { data } = await adminApi.post('/auth/admin/login', credentials)
      token.value = data.data.accessToken
      user.value = {
        id: data.data.adminId,
        username: data.data.username,
        fullName: data.data.fullName,
        superadmin: data.data.superadmin,
        permissions: data.data.permissions
      }
      return data
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      if (token.value) {
        await adminApi.post('/auth/admin/logout')
      }
    } catch (e) {
      // Ignore network errors on logout
    } finally {
      token.value = null
      user.value = null
      router.push('/admin/login')
    }
  }

  function hasPermission(resource: string, action: string): boolean {
    if (!user.value) return false
    if (user.value.superadmin) return true
    const permissions = user.value.permissions || []
    return permissions.includes(`${resource}:${action}`)
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    setToken,
    setUser,
    login,
    logout,
    hasPermission
  }
})
