<template>
  <div class="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:py-20 animate-in fade-in slide-in-from-bottom-4 duration-1000">
    <div class="mb-12">
      <h1 class="text-4xl font-serif text-slate-900 italic">Personal Portfolio</h1>
      <p class="text-slate-500 mt-2 font-medium tracking-tight">Curate your digital presence and account security.</p>
    </div>

    <div class="flex flex-col md:flex-row gap-12">
      <!-- Profile Sidebar -->
      <div class="w-full md:w-1/3 space-y-8">
        <div class="relative group cursor-pointer" @click="triggerAvatarUpload">
          <div class="aspect-square rounded-3xl overflow-hidden bg-slate-50 border border-slate-100 shadow-sm transition-all duration-500 group-hover:shadow-xl group-hover:-translate-y-1">
            <img v-if="profile.avatarUrl" :src="profile.avatarUrl" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center bg-slate-50 text-slate-300">
              <Icon icon="ph:user-circle-thin" class="w-24 h-24 stroke-[0.5]" />
            </div>
            
            <!-- Minimal Hover Overlay -->
            <div class="absolute inset-0 bg-slate-900/5 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center backdrop-blur-[1px]">
              <div class="bg-white/80 px-4 py-2 rounded-full text-[11px] font-bold text-slate-900 uppercase tracking-widest shadow-sm">
                Change Portrait
              </div>
            </div>
          </div>
          
          <input type="file" ref="avatarInput" class="hidden" accept="image/*" @change="handleAvatarChange" />

          <!-- Loading Indicator -->
          <div v-if="uploadingAvatar" class="absolute inset-0 bg-white/60 backdrop-blur-sm rounded-3xl flex items-center justify-center z-10">
            <div class="flex flex-col items-center">
              <div class="w-6 h-6 border-2 border-slate-900 border-t-transparent rounded-full animate-spin"></div>
              <span class="text-[10px] font-bold text-slate-900 mt-2 uppercase tracking-widest">Updating</span>
            </div>
          </div>
        </div>

        <div class="space-y-1">
          <h3 class="text-xl font-serif text-slate-900">{{ profile.name }}</h3>
          <p class="text-sm text-slate-400 font-medium">{{ profile.email }}</p>
        </div>

        <nav class="space-y-1">
          <button class="w-full text-left px-4 py-3 rounded-xl bg-slate-900 text-white text-sm font-bold tracking-tight shadow-lg shadow-slate-200 transition-all">
            Identity & Security
          </button>
          <button class="w-full text-left px-4 py-3 rounded-xl text-slate-500 text-sm font-bold tracking-tight hover:bg-slate-50 transition-all">
            Order Archive
          </button>
          <button class="w-full text-left px-4 py-3 rounded-xl text-slate-500 text-sm font-bold tracking-tight hover:bg-slate-50 transition-all">
            Saved Curations
          </button>
        </nav>
      </div>

      <!-- Main Forms -->
      <div class="flex-grow space-y-12">
        <!-- Details Section -->
        <section class="space-y-8">
          <div class="border-b border-slate-100 pb-4 flex items-center justify-between">
            <h2 class="text-sm font-black text-slate-900 uppercase tracking-[0.2em]">Contact Details</h2>
            <div v-if="updated" class="text-emerald-500 text-[10px] font-bold uppercase animate-pulse">Changes Saved</div>
          </div>

          <form @submit.prevent="handleUpdateInfo" class="grid grid-cols-1 gap-8">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div class="space-y-2">
                <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Preferred Name</label>
                <input v-model="form.name" type="text" class="w-full bg-white border-b border-slate-200 py-3 px-1 focus:outline-none focus:border-slate-900 transition-colors text-slate-800 font-medium" />
              </div>
              <div class="space-y-2">
                <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Phone Reference</label>
                <input v-model="form.phone" type="tel" class="w-full bg-white border-b border-slate-200 py-3 px-1 focus:outline-none focus:border-slate-900 transition-colors text-slate-800 font-medium" />
              </div>
            </div>
            <div class="space-y-2">
              <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Primary Email</label>
              <input v-model="form.email" type="email" class="w-full bg-white border-b border-slate-200 py-3 px-1 focus:outline-none focus:border-slate-900 transition-colors text-slate-800 font-medium" />
            </div>

            <div class="flex justify-start">
              <button type="submit" :disabled="updatingInfo" class="group flex items-center gap-3 text-slate-900 font-black text-xs uppercase tracking-widest">
                <span>Synchronize Identity</span>
                <div class="w-8 h-px bg-slate-900 transition-all group-hover:w-12"></div>
                <Icon v-if="updatingInfo" icon="ph:spinner-gap-bold" class="animate-spin w-4 h-4" />
              </button>
            </div>
          </form>
        </section>

        <!-- Security Section -->
        <section class="space-y-8">
          <div class="border-b border-slate-100 pb-4">
            <h2 class="text-sm font-black text-slate-900 uppercase tracking-[0.2em]">Security Protocol</h2>
          </div>

          <form @submit.prevent="handleUpdatePassword" class="space-y-10">
            <div class="space-y-2">
              <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Current Password</label>
              <input v-model="passForm.currentPassword" type="password" placeholder="••••••••" class="w-full bg-white border-b border-slate-200 py-3 px-1 focus:outline-none focus:border-slate-900 transition-colors text-slate-800 font-medium" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div class="space-y-2">
                <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">New Credential</label>
                <input v-model="passForm.newPassword" type="password" placeholder="••••••••" class="w-full bg-white border-b border-slate-200 py-3 px-1 focus:outline-none focus:border-slate-900 transition-colors text-slate-800 font-medium" />
              </div>
              <div class="space-y-2">
                <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Confirm Credential</label>
                <input v-model="passForm.confirmPassword" type="password" placeholder="••••••••" class="w-full bg-white border-b border-slate-200 py-3 px-1 focus:outline-none focus:border-slate-900 transition-colors text-slate-800 font-medium" />
              </div>
            </div>

            <div class="flex justify-start">
              <button type="submit" :disabled="updatingPass" class="group flex items-center gap-3 text-slate-900 font-black text-xs uppercase tracking-widest">
                <span>Update Credentials</span>
                <div class="w-8 h-px bg-slate-900 transition-all group-hover:w-12"></div>
                <Icon v-if="updatingPass" icon="ph:spinner-gap-bold" class="animate-spin w-4 h-4" />
              </button>
            </div>
          </form>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Icon } from '@iconify/vue'
import { useCustomerAuthStore } from '@/stores/customerAuth'
import customerApi from '@/api/customerApi'

const authStore = useCustomerAuthStore()
const profile = ref<any>({})
const uploadingAvatar = ref(false)
const updatingInfo = ref(false)
const updatingPass = ref(false)
const updated = ref(false)

const form = reactive({
  name: '',
  email: '',
  phone: ''
})

const passForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const fetchProfile = async () => {
  try {
    const { data } = await customerApi.get('/customer/profile')
    profile.value = data.data
    form.name = profile.value.name || ''
    form.email = profile.value.email || ''
    form.phone = profile.value.phone || ''
  } catch (err) {
    console.error('Failed to fetch profile', err)
  }
}

onMounted(fetchProfile)

const handleUpdateInfo = async () => {
  updatingInfo.value = true
  try {
    const { data } = await customerApi.put('/customer/profile', form)
    profile.value = data.data
    authStore.setUser({ ...authStore.user, name: data.data.name })
    updated.value = true
    setTimeout(() => updated.value = false, 3000)
  } catch (err) {
    console.error('Update failed', err)
  } finally {
    updatingInfo.value = false
  }
}

const handleUpdatePassword = async () => {
  if (passForm.newPassword !== passForm.confirmPassword) {
    alert('New passwords do not match')
    return
  }
  
  updatingPass.value = true
  try {
    await customerApi.put('/customer/profile', {
      currentPassword: passForm.currentPassword,
      newPassword: passForm.newPassword
    })
    passForm.currentPassword = ''
    passForm.newPassword = ''
    passForm.confirmPassword = ''
    alert('Credentials updated successfully')
  } catch (err: any) {
    alert(err.response?.data?.message || 'Update failed')
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
    const { data } = await customerApi.post('/customer/profile/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    profile.value.avatarUrl = data.data
    authStore.setUser({ ...authStore.user, avatarUrl: data.data })
  } catch (err) {
    console.error('Avatar upload failed', err)
  } finally {
    uploadingAvatar.value = false
    if (avatarInput.value) avatarInput.value.value = ''
  }
}
</script>
