import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import customerApi from '@/api/customerApi'

export const useCustomerAuthStore = defineStore('customerAuth', () => {
  const token = ref<string | null>(localStorage.getItem('customer_token'))
  
  let initialUser = null
  try {
    initialUser = JSON.parse(localStorage.getItem('customer_user') || 'null')
    if (initialUser === '[object Object]') initialUser = null
  } catch (e) {
    localStorage.removeItem('customer_user')
  }
  const user = ref<any>(initialUser)
  const loading = ref(false)

  // Auto-sync persistence guaranteeing valid JSON parsing
  watch(token, (val) => val ? localStorage.setItem('customer_token', val) : localStorage.removeItem('customer_token'))
  watch(user, (val) => val ? localStorage.setItem('customer_user', JSON.stringify(val)) : localStorage.removeItem('customer_user'), { deep: true })

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
      const { data } = await customerApi.post('/auth/customer/login', credentials)
      token.value = data.data.accessToken
      user.value = {
        id: data.data.customerId,
        email: data.data.email,
        name: data.data.name,
        avatarUrl: data.data.avatarUrl
      }
      return data
    } finally {
      loading.value = false
    }
  }

  async function register(payload: any) {
    loading.value = true
    try {
      const { data } = await customerApi.post('/auth/customer/register', payload)
      // Register returns LoginResult on backend
      token.value = data.data.tokenResponse.accessToken
      user.value = {
        id: data.data.tokenResponse.customerId,
        email: data.data.tokenResponse.email,
        name: data.data.tokenResponse.name,
        avatarUrl: data.data.tokenResponse.avatarUrl
      }
      return data
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      if (token.value) {
        await customerApi.post('/auth/customer/logout')
      }
    } catch (e) {
      // Ignore
    } finally {
      token.value = null
      user.value = null
      import('@/router').then(m => m.default.push('/login'))
    }
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    setToken,
    setUser,
    login,
    register,
    logout
  }
})
