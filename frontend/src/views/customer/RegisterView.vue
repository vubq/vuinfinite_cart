<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCustomerAuthStore } from '@/stores/customerAuth'
import VInput from '@/components/ui/VInput.vue'
import VButton from '@/components/ui/VButton.vue'

const router = useRouter()
const auth = useCustomerAuthStore()

const form = ref({
  name: '',
  email: '',
  password: ''
})
const error = ref('')

async function onSubmit() {
  error.value = ''
  try {
    await auth.register(form.value)
    router.push('/')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Registration failed'
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-16rem)] flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900 tracking-tight">
          Create account
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          Already have an account?
          <router-link to="/login" class="font-medium text-primary hover:text-blue-500 transition-colors">
            Sign in here
          </router-link>
        </p>
      </div>
      
      <form class="mt-8 space-y-5" @submit.prevent="onSubmit">
        <div v-if="error" class="bg-red-50 text-red-600 p-3 rounded-lg text-sm border border-red-100">
          {{ error }}
        </div>

        <div class="space-y-4">
          <VInput
            v-model="form.name"
            label="Full Name"
            placeholder="John Doe"
            required
          />
          
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
            placeholder="At least 6 characters"
            required
          />
        </div>

        <div>
          <VButton 
            type="submit" 
            :loading="auth.loading"
            class="w-full"
          >
            Create Account
          </VButton>
        </div>

        <p class="text-xs text-center text-gray-500 mt-4 leading-relaxed">
          By creating an account, you agree to our 
          <a href="#" class="underline hover:text-primary">Terms of Service</a> and 
          <a href="#" class="underline hover:text-primary">Privacy Policy</a>.
        </p>
      </form>
    </div>
  </div>
</template>
