<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <!-- Header Section -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">Access Roles</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium italic">Define reusable departmental permission groups.</p>
      </div>
      <VButton 
        variant="primary" 
        size="md" 
        class="shadow-emerald-200/50 shadow-lg"
        @click="openCreateModal"
      >
        <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4" /></svg>
        New Role
      </VButton>
    </div>

    <!-- Roles Grid -->
    <div v-if="loading" class="p-20 flex justify-center items-center">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600"></div>
    </div>
    
    <div v-else-if="groups.length === 0" class="p-20 text-center bg-white rounded-2xl border border-slate-200/60">
      <h3 class="text-lg font-bold text-slate-800">No roles defined</h3>
      <p class="text-[13px] text-slate-500 mt-1">Create your first departmental group to start assigning permissions in bulk.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <div v-for="group in groups" :key="group.id" 
           class="group bg-white p-6 rounded-2xl border border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)] hover:border-emerald-500/30 hover:shadow-xl hover:shadow-emerald-900/5 transition-all relative overflow-hidden">
        
        <!-- Decoration -->
        <div class="absolute -right-6 -top-6 w-24 h-24 bg-slate-50 rounded-full group-hover:bg-emerald-50/50 transition-colors"></div>

        <div class="relative">
            <h3 class="text-lg font-extrabold text-slate-900 tracking-tight">{{ group.name }}</h3>
            <p class="text-[12px] text-slate-500 mt-1 min-h-[32px] leading-relaxed">{{ group.description || 'No description provided.' }}</p>
            
            <div class="mt-5 flex items-center justify-between">
                <div class="flex items-center gap-1.5">
                    <span class="px-2 py-0.5 rounded-md bg-slate-100 text-[10px] font-bold text-slate-600 uppercase tracking-wider">
                        {{ group.permissions?.length || 0 }} Permits
                    </span>
                </div>
                <div class="flex items-center gap-1">
                    <button @click="openEditModal(group)" class="p-2 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-all">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                    </button>
                    <button @click="confirmDelete(group)" class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                    </button>
                </div>
            </div>
        </div>
      </div>
    </div>

    <!-- Role Modal -->
    <VModal v-model="showModal" :title="isEditing ? 'Update Role' : 'Define New Role'" maxWidth="2xl">
        <form @submit.prevent="handleSubmit" class="space-y-6">
            <div class="space-y-4">
                <VInput v-model="form.name" label="Role Name" placeholder="e.g. Content Manager" required />
                <div class="space-y-1.5">
                    <label class="block text-sm font-medium text-gray-700">Description</label>
                    <textarea v-model="form.description" rows="2" 
                              class="block w-full px-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all placeholder-gray-400"
                              placeholder="Describe the scope of this role..."></textarea>
                </div>
            </div>

            <div class="space-y-3">
                <div class="flex items-center justify-between">
                    <h4 class="text-[12px] font-extrabold uppercase tracking-widest text-slate-400">Granted Permissions</h4>
                    <div class="relative w-40">
                        <input v-model="permSearch" type="text" placeholder="Search permits..." 
                               class="w-full pl-8 pr-3 py-1.5 text-[12px] border border-slate-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/5 focus:border-emerald-500 transition-all" />
                        <svg class="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                    </div>
                </div>
                <div class="grid grid-cols-2 gap-3 max-h-[300px] overflow-y-auto px-1 py-1 custom-scrollbar">
                    <div v-for="perm in filteredPermissions" :key="perm.id" 
                         @click="togglePerm(perm.id)"
                         class="p-3 border rounded-xl cursor-pointer transition-all flex items-start gap-2.5 hover:bg-slate-50"
                         :class="form.permissionIds.has(perm.id) ? 'bg-emerald-50 border-emerald-500/50 ring-1 ring-emerald-500/10' : 'bg-white border-slate-200'">
                        <div class="w-4 h-4 rounded border flex items-center justify-center mt-0.5"
                             :class="form.permissionIds.has(perm.id) ? 'bg-emerald-500 border-emerald-500 text-white' : 'bg-white border-slate-300'">
                            <svg v-if="form.permissionIds.has(perm.id)" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
                        </div>
                        <div>
                            <div class="text-[12px] font-bold text-slate-800 leading-tight">{{ perm.description }}</div>
                            <div class="text-[10px] font-mono text-slate-400 mt-0.5">{{ perm.key }}</div>
                        </div>
                    </div>
                    <div v-if="filteredPermissions.length === 0" class="col-span-2 py-10 text-center bg-slate-50/50 rounded-2xl border border-dashed border-slate-200">
                        <p class="text-sm text-slate-400 font-medium italic">No matching permissions found</p>
                    </div>
                </div>
            </div>
        </form>
        <template #footer>
            <VButton variant="secondary" @click="showModal = false">Cancel</VButton>
            <VButton variant="primary" :loading="submitting" @click="handleSubmit">
                {{ isEditing ? 'Save Changes' : 'Confirm & Create' }}
            </VButton>
        </template>
    </VModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import VCard from '@/components/ui/VCard.vue'
import VButton from '@/components/ui/VButton.vue'
import VModal from '@/components/ui/VModal.vue'
import VInput from '@/components/ui/VInput.vue'
import adminApi from '@/api/adminApi'

const groups = ref<any[]>([])
const availablePermissions = ref<any[]>([])
const loading = ref(true)
const submitting = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const selectedId = ref<number | null>(null)
const permSearch = ref('')

const form = reactive({
    name: '',
    description: '',
    permissionIds: new Set<number>()
})

const fetchGroups = async () => {
  loading.value = true
  try {
    const { data } = await adminApi.get('/admin/system/permission-groups')
    groups.value = data
  } catch (err) {
    console.error('Failed to fetch groups', err)
  } finally {
    loading.value = false
  }
}

const fetchPermissions = async () => {
    try {
        const { data } = await adminApi.get('/admin/system/permissions')
        availablePermissions.value = data
    } catch (err) {
        console.error('Failed to fetch permission catalog', err)
    }
}

const filteredPermissions = computed(() => {
    const s = permSearch.value.toLowerCase()
    return availablePermissions.value.filter(p => 
        p.description.toLowerCase().includes(s) || p.key.toLowerCase().includes(s)
    )
})

const openCreateModal = () => {
    isEditing.value = false
    selectedId.value = null
    form.name = ''
    form.description = ''
    form.permissionIds.clear()
    permSearch.value = ''
    showModal.value = true
}

const openEditModal = (group: any) => {
    isEditing.value = true
    selectedId.value = group.id
    form.name = group.name
    form.description = group.description
    form.permissionIds = new Set(group.permissions?.map((p: any) => p.id) || [])
    permSearch.value = ''
    showModal.value = true
}

const togglePerm = (id: number) => {
    if (form.permissionIds.has(id)) form.permissionIds.delete(id)
    else form.permissionIds.add(id)
}

const handleSubmit = async () => {
    if (!form.name) return
    submitting.value = true
    const payload = {
        name: form.name,
        description: form.description,
        permissionIds: Array.from(form.permissionIds)
    }
    
    try {
        if (isEditing.value && selectedId.value) {
            await adminApi.put(`/admin/system/permission-groups/${selectedId.value}`, payload)
        } else {
            await adminApi.post('/admin/system/permission-groups', payload)
        }
        showModal.value = false
        await fetchGroups()
    } catch (err) {
        console.error('Failed to save role', err)
    } finally {
        submitting.value = false
    }
}

const confirmDelete = async (group: any) => {
    if (!confirm(`Are you sure you want to delete "${group.name}"? All assigned admins will lose these permissions immediately.`)) return
    
    try {
        await adminApi.delete(`/admin/system/permission-groups/${group.id}`)
        await fetchGroups()
    } catch (err) {
        console.error('Failed to delete role', err)
    }
}

onMounted(() => {
    fetchGroups()
    fetchPermissions()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}
</style>
