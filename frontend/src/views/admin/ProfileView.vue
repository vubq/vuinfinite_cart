<template>
  <div class="max-w-5xl mx-auto space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <!-- Header -->
    <div class="flex items-end justify-between px-2">
      <div>
        <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">{{ t('header.title') }}</h1>
        <p class="text-[13px] text-slate-500 mt-1.5 font-medium italic">{{ t('header.subtitle') }}</p>
      </div>
      <div class="text-[10px] font-bold text-slate-300 uppercase tracking-widest mb-1">
        {{ t('header.security_level') }}{{ authStore.user?.superadmin ? t('role.superadmin') : t('role.standard') }}
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Left Column: Avatar Management -->
      <div class="lg:col-span-1 space-y-6">
        <div class="bg-white rounded-[2rem] border border-slate-200/60 p-8 shadow-[0_8px_30px_rgb(0,0,0,0.04)] backdrop-blur-sm relative overflow-hidden group">
          <div class="absolute top-0 left-0 w-full h-1.5 bg-gradient-to-r from-emerald-400 to-emerald-600"></div>
          
          <div class="flex flex-col items-center">
            <div class="relative group/avatar cursor-pointer" @click="triggerAvatarUpload">
              <div class="w-40 h-40 rounded-[2.5rem] border-4 border-white shadow-xl overflow-hidden bg-slate-100 transition-transform duration-500 group-hover/avatar:scale-[1.02]">
                <img v-if="profile.avatarUrl" :src="profile.avatarUrl" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full flex items-center justify-center bg-emerald-50 text-emerald-600 text-4xl font-black italic">
                  {{ profile.username?.substring(0, 2).toUpperCase() }}
                </div>
              </div>
              
              <!-- Hover Overlay -->
              <div class="absolute inset-0 rounded-[2.5rem] bg-emerald-900/40 opacity-0 group-hover/avatar:opacity-100 flex items-center justify-center transition-all duration-300 backdrop-blur-[2px]">
                <div class="bg-white/20 p-3 rounded-2xl border border-white/30 text-white">
                  <Icon icon="ph:camera-bold" class="w-7 h-7" />
                </div>
              </div>

              <div v-if="uploadingAvatar" class="absolute inset-0 rounded-[2.5rem] bg-white/80 flex flex-col items-center justify-center">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600"></div>
                <p class="text-[10px] font-bold text-emerald-600 mt-2 uppercase">{{ t('avatar.uploading') }}</p>
              </div>
            </div>

            <input type="file" ref="avatarInput" class="hidden" accept="image/*" @change="handleAvatarChange" />

            <div class="mt-6 text-center">
              <h3 class="text-lg font-black text-slate-900 tracking-tight">{{ profile.fullName || profile.username }}</h3>
              <p class="text-xs font-bold text-slate-400 mt-0.5 tracking-wide">{{ profile.email }}</p>
            </div>

            <div class="w-full h-px bg-slate-100 my-6"></div>

            <div class="w-full space-y-3">
              <div class="flex items-center justify-between text-xs px-1">
                <span class="font-bold text-slate-400">{{ t('label.username') }}</span>
                <span class="font-black text-slate-700 italic">@{{ profile.username }}</span>
              </div>
              <div class="flex items-center justify-between text-xs px-1">
                <span class="font-bold text-slate-400">{{ t('label.account_id') }}</span>
                <span class="font-black text-slate-700 italic">#{{ profile.id }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Security Badge -->
        <div class="bg-slate-900 rounded-[1.5rem] p-6 text-white overflow-hidden relative">
          <div class="absolute -right-4 -bottom-4 opacity-10 rotate-12">
            <Icon icon="ph:shield-check-fill" class="w-24 h-24" />
          </div>
          <div class="relative flex items-center gap-4">
            <div class="w-10 h-10 rounded-xl bg-emerald-500/20 flex items-center justify-center border border-emerald-500/30">
              <Icon icon="ph:fingerprint-bold" class="w-6 h-6 text-emerald-400" />
            </div>
            <div>
              <p class="text-[10px] font-bold text-emerald-400 uppercase tracking-widest">{{ t('badge.protected_identity') }}</p>
              <p class="text-xs font-medium text-slate-300 mt-0.5">{{ t('badge.encrypted') }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column: Forms -->
      <div class="lg:col-span-2 space-y-8">
        <!-- Personal Information -->
        <div class="bg-white rounded-[2rem] border border-slate-200/60 p-8 shadow-[0_8px_30px_rgb(0,0,0,0.04)] backdrop-blur-sm relative overflow-hidden">
          <div class="flex items-center gap-3 mb-8">
            <div class="w-10 h-10 rounded-xl bg-slate-50 flex items-center justify-center border border-slate-100">
              <Icon icon="ph:user-bold" class="w-5 h-5 text-slate-600" />
            </div>
            <h2 class="text-xl font-extrabold text-slate-900 tracking-tight">{{ t('section.identity_details') }}</h2>
          </div>

          <form @submit.prevent="handleUpdateInfo" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <VInput v-model="form.fullName" :label="t('label.full_name')" :placeholder="t('placeholder.full_name')" icon="ph:user" required />
              <VInput v-model="form.email" :label="t('label.email_address')" type="email" :placeholder="t('placeholder.email_address')" icon="ph:envelope" required />
            </div>

            <div class="flex justify-end pt-2">
              <VButton type="submit" variant="primary" :loading="updatingInfo" class="!px-8 shadow-lg shadow-emerald-200/50">
                {{ t('btn.save_changes') }}
              </VButton>
            </div>
          </form>
        </div>

        <!-- Security / Password -->
        <div class="bg-white rounded-[2rem] border border-slate-200/60 p-8 shadow-[0_8px_30px_rgb(0,0,0,0.04)] backdrop-blur-sm relative overflow-hidden">
          <div class="flex items-center gap-3 mb-8">
            <div class="w-10 h-10 rounded-xl bg-slate-50 flex items-center justify-center border border-slate-100">
              <Icon icon="ph:lock-key-bold" class="w-5 h-5 text-slate-600" />
            </div>
            <h2 class="text-xl font-extrabold text-slate-900 tracking-tight">{{ t('section.security_credentials') }}</h2>
          </div>

          <form @submit.prevent="handleUpdatePassword" class="space-y-6">
            <div class="grid grid-cols-1 gap-6">
              <VInput v-model="passForm.currentPassword" :label="t('label.current_access_password')" type="password" placeholder="••••••••" icon="ph:key-bold" required />
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <VInput v-model="passForm.newPassword" :label="t('label.new_password')" type="password" placeholder="••••••••" icon="ph:lock-simple-bold" required />
                <VInput v-model="passForm.confirmPassword" :label="t('label.confirm_new_password')" type="password" placeholder="••••••••" icon="ph:lock-simple-bold" required />
              </div>
            </div>

            <div class="flex justify-end pt-2">
              <VButton type="submit" variant="secondary" :loading="updatingPass" class="!px-8 !border-slate-200 !text-slate-700 hover:!bg-slate-50">
                {{ t('btn.update_security') }}
              </VButton>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Icon } from '@iconify/vue'
import { useAdminAuthStore } from '@/stores/adminAuth'
import adminApi from '@/api/adminApi'
import VInput from '@/components/ui/VInput.vue'
import VButton from '@/components/ui/VButton.vue'
import { useI18n } from '@/composables/useI18n'

const authStore = useAdminAuthStore()
const { t } = useI18n(['admin.profile', 'common'])
const profile = ref<any>({})
const uploadingAvatar = ref(false)
const updatingInfo = ref(false)
const updatingPass = ref(false)

const form = reactive({
  fullName: '',
  email: ''
})

const passForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const fetchProfile = async () => {
  try {
    const { data } = await adminApi.get('/admin/profile')
    profile.value = data.data
    form.fullName = profile.value.fullName || ''
    form.email = profile.value.email || ''
  } catch (err) {
    console.error('Failed to fetch profile', err)
  }
}

onMounted(fetchProfile)

const handleUpdateInfo = async () => {
  updatingInfo.value = true
  try {
    const { data } = await adminApi.put('/admin/profile', {
      fullName: form.fullName,
      email: form.email
    })
    profile.value = data.data
    // Update auth store user if needed
    authStore.setUser({ ...authStore.user, fullName: data.data.fullName })
  } catch (err) {
    console.error('Update failed', err)
  } finally {
    updatingInfo.value = false
  }
}

const handleUpdatePassword = async () => {
  if (passForm.newPassword !== passForm.confirmPassword) {
    alert(t('alert.password_mismatch'))
    return
  }
  
  updatingPass.value = true
  try {
    await adminApi.put('/admin/profile', {
      currentPassword: passForm.currentPassword,
      newPassword: passForm.newPassword
    })
    passForm.currentPassword = ''
    passForm.newPassword = ''
    passForm.confirmPassword = ''
    alert(t('alert.password_updated'))
  } catch (err: any) {
    alert(err.response?.data?.message || t('alert.password_update_failed'))
  } finally {
    updatingPass.value = false
  }
}

const avatarInput = ref<HTMLInputElement | null>(null)
const triggerAvatarUpload = () => avatarInput.value?.click()

const handleAvatarChange = async (event: any) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  uploadingAvatar.value = true
  try {
    const { data } = await adminApi.post('/admin/profile/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    profile.value.avatarUrl = data.data
    // Update store
    authStore.setUser({ ...authStore.user, avatarUrl: data.data })
  } catch (err) {
    console.error('Avatar upload failed', err)
  } finally {
    uploadingAvatar.value = false
    if (avatarInput.value) avatarInput.value.value = ''
  }
}
</script>

<style scoped>
.shadow-emerald-200\/50 {
  --tw-shadow-color: rgba(16, 185, 129, 0.5);
  --tw-shadow: 0 10px 15px -3px var(--tw-shadow-color), 0 4px 6px -4px var(--tw-shadow-color);
}
</style>
