<template>
  <div class="min-h-[calc(100vh-16rem)] flex items-center justify-center">
    <div class="text-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto"></div>
      <h2 class="mt-4 text-xl font-medium text-gray-900">Authenticating...</h2>
      <p class="mt-2 text-sm text-gray-500">Please wait while we log you in securely.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCustomerAuthStore } from '@/stores/customerAuth'
import customerApi from '@/api/customerApi'

const router = useRouter()
const route = useRoute()
const auth = useCustomerAuthStore()

onMounted(async () => {
  const token = route.query.token as string
  const error = route.query.error as string

  if (error) {
    router.push('/login?error=' + encodeURIComponent(error))
    return
  }

  if (token) {
    auth.setToken(token)
    try {
      // Fetch user profile to populate store
      const { data } = await customerApi.get('/customer/profile')
      auth.setUser(data.data)
      router.push('/')
    } catch (err) {
      console.error('Failed to fetch profile after OAuth login', err)
      auth.logout()
      router.push('/login?error=profile_fetch_failed')
    }
  } else {
    router.push('/login')
  }
})
</script>
