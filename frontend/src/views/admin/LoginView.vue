<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminAuthStore } from '@/stores/adminAuth'
import VInput from '@/components/ui/VInput.vue'
import VButton from '@/components/ui/VButton.vue'

const router = useRouter()
const auth = useAdminAuthStore()

const form = ref({
  username: '',
  password: ''
})
const error = ref('')

onMounted(() => {
  if (auth.isAuthenticated) {
    router.push('/admin')
  }
})

async function onSubmit() {
  error.value = ''
  try {
    await auth.login(form.value)
    router.push('/admin')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Admin login failed'
  }
}
</script>

<template>
  <div class="fixed inset-0 min-h-screen bg-white flex items-center justify-center p-6 z-[9999] overflow-y-auto">
    <!-- Abstract Background Element -->
    <div class="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
        <div class="absolute -top-24 -left-24 w-96 h-96 bg-emerald-50 rounded-full blur-3xl opacity-50"></div>
        <div class="absolute bottom-0 right-0 w-[500px] h-[500px] bg-emerald-50 rounded-full blur-[100px] opacity-30"></div>
    </div>

    <div class="max-w-md w-full relative z-10">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-black text-emerald-600 tracking-tighter flex items-center justify-center gap-2 mb-3 animate-in fade-in slide-in-from-top duration-700">
           Vu<span class="text-slate-800">Emporium</span>
        </h1>
        <div class="inline-flex items-center gap-2 px-3 py-1 bg-slate-900 text-white rounded-full text-[10px] font-bold uppercase tracking-widest animate-in fade-in duration-1000 delay-300">
          Intelligence Portal
        </div>
      </div>

      <div class="bg-white/70 backdrop-blur-xl border border-slate-100 p-10 rounded-[2.5rem] shadow-2xl shadow-emerald-900/5 animate-in fade-in zoom-in-95 duration-700 delay-200">
        <form @submit.prevent="onSubmit" class="space-y-8">
          <div v-if="error" class="bg-rose-50 text-rose-600 p-4 rounded-2xl text-xs font-bold border border-rose-100 flex items-center gap-3">
            <span class="w-1.5 h-1.5 rounded-full bg-rose-600 animate-pulse"></span>
            {{ error }}
          </div>

          <div class="space-y-6">
            <VInput
              v-model="form.username"
              label="Administrative Identity"
              placeholder="e.g. master.intel"
              id="admin-user"
              class="modern-input"
            />
            
            <VInput
              v-model="form.password"
              label="Access Cipher"
              type="password"
              placeholder="••••••••"
              id="admin-pass"
              class="modern-input"
            />
          </div>

          <VButton 
            type="submit" 
            :loading="auth.loading"
            variant="primary"
            class="w-full h-14 text-sm font-black uppercase tracking-[0.15em]"
          >
            Authenticate Access
          </VButton>
        </form>

        <div class="mt-10 text-center pt-8 border-t border-slate-50">
          <p class="text-slate-400 text-[10px] font-bold uppercase tracking-[0.1em] leading-relaxed">
            Authorized access only. System activity is monitored and managed remotely by Intel.
          </p>
        </div>
      </div>
      
      <div class="mt-10 text-center animate-in fade-in duration-1000 delay-500">
        <router-link to="/" class="text-slate-400 hover:text-emerald-600 text-xs font-bold uppercase tracking-widest transition-colors flex items-center justify-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path></svg>
          Back to Storefront
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.modern-input label) {
  color: #64748b; /* slate-500 */
  font-weight: 800;
  text-transform: uppercase;
  font-size: 10px;
  letter-spacing: 0.1em;
  margin-bottom: 0.5rem;
}
:deep(.modern-input input) {
  height: 3.5rem;
  border-radius: 1rem;
  border-color: #f1f5f9; /* slate-100 */
  background-color: #f8fafc; /* slate-50 */
  padding-left: 1.25rem;
  font-weight: 600;
}
:deep(.modern-input input:focus) {
  border-color: #10b981; /* emerald-500 */
  background-color: white;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.05);
}
</style>
