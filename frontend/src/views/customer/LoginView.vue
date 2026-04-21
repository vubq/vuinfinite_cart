<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCustomerAuthStore } from '@/stores/customerAuth'
import VInput from '@/components/ui/VInput.vue'
import VButton from '@/components/ui/VButton.vue'
import { useI18n } from '@/composables/useI18n'

const router = useRouter()
const auth = useCustomerAuthStore()
const { t } = useI18n('auth')

const form = ref({
  email: '',
  password: ''
})
const error = ref('')

async function onSubmit() {
  error.value = ''
  try {
    await auth.login(form.value)
    router.push('/')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Login failed'
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-16rem)] flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900 tracking-tight">
          {{ t('login.title', 'Welcome back') }}
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          Or
          <router-link to="/register" class="font-medium text-primary hover:text-blue-500 transition-colors">
            create a new account
          </router-link>
        </p>
      </div>
      
      <form class="mt-8 space-y-6" @submit.prevent="onSubmit">
        <div v-if="error" class="bg-red-50 text-red-600 p-3 rounded-lg text-sm border border-red-100">
          {{ error }}
        </div>

        <div class="space-y-4">
          <VInput
            v-model="form.email"
            label="Email address"
            type="email"
            placeholder="name@example.com"
            required
          />
          
          <VInput
            v-model="form.password"
            label="Password"
            type="password"
            placeholder="••••••••"
            required
          />
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input id="remember-me" name="remember-me" type="checkbox" class="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded" />
            <label for="remember-me" class="ml-2 block text-sm text-gray-900">
              Remember me
            </label>
          </div>

          <div class="text-sm">
            <a href="#" class="font-medium text-primary hover:text-blue-500 transition-colors">
              Forgot your password?
            </a>
          </div>
        </div>

        <div>
          <VButton 
            type="submit" 
            :loading="auth.loading"
            class="w-full"
          >
            Sign in
          </VButton>
        </div>
      </form>
    </div>
  </div>
</template>
