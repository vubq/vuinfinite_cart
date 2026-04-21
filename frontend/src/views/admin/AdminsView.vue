<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <!-- Header Section -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">Team Governance</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium italic">Manage administrative access and internal security controls.</p>
      </div>
      <VButton 
        v-if="adminAuth.user?.superadmin" 
        variant="primary" 
        size="md" 
        class="shadow-emerald-200/50 shadow-lg"
        @click="openInviteModal"
      >
        Invite Officer
      </VButton>
    </div>

    <!-- ... existing table ... -->
    <VCard class="overflow-hidden border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)]">
      <!-- ... loading and empty cases ... -->
      <div v-if="loading" class="p-20 flex justify-center items-center">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600"></div>
      </div>
      
      <div v-else-if="admins.length === 0" class="p-20 text-center">
        <svg class="w-12 h-12 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
        <h3 class="text-lg font-bold text-slate-800">No officers found</h3>
        <p class="text-[13px] text-slate-500 mt-1">Contact the root superadmin to register base personnel.</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="border-b border-slate-100">
              <th class="px-7 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-wider">Officer</th>
              <th class="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-wider">Access Level</th>
              <th class="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-wider">Status</th>
              <th class="px-7 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-wider text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-50">
            <tr v-for="admin in admins" :key="admin.id" class="hover:bg-slate-50/40 transition-colors group">
              <td class="px-7 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full bg-slate-900 flex items-center justify-center text-xs font-bold text-white tracking-widest shadow-sm ring-2 ring-white">
                    {{ admin.username?.substring(0,2).toUpperCase() }}
                  </div>
                  <div>
                    <div class="text-[13px] font-bold text-slate-900 leading-none mb-1">{{ admin.fullName }}</div>
                    <div class="text-[11px] text-slate-400 font-medium tracking-tight">{{ admin.email }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <span v-if="admin.superadmin" class="inline-flex items-center px-2 py-0.5 rounded-md text-[10px] font-extrabold bg-emerald-50 text-emerald-700 ring-1 ring-emerald-600/10 uppercase tracking-wider">
                  Superadmin
                </span>
                <span v-else class="inline-flex items-center px-2 py-0.5 rounded-md text-[10px] font-bold bg-slate-100 text-slate-600 uppercase tracking-wider">
                  Restricted Access
                </span>
                <div v-if="!admin.superadmin" @click="openPermissionModal(admin)" class="text-[10px] text-emerald-600 mt-1 pl-0.5 font-bold cursor-pointer hover:underline">
                    {{ admin.permissions?.length || 0 }} total permits • Manage
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-1.5">
                  <span class="w-1.5 h-1.5 rounded-full" :class="admin.status === 'ACTIVE' ? 'bg-emerald-500' : 'bg-slate-300'"></span>
                  <span class="text-[11px] font-bold uppercase tracking-wide" :class="admin.status === 'ACTIVE' ? 'text-emerald-600' : 'text-slate-400'">
                    {{ admin.status }}
                  </span>
                </div>
              </td>
              <td class="px-7 py-4 text-right">
                <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button 
                    v-if="canManage(admin)"
                    @click="openPermissionModal(admin)"
                    class="p-2 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-all"
                    title="Edit Permissions"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4" /></svg>
                  </button>
                  <button 
                    v-if="canManage(admin)"
                    @click="toggleStatus(admin)"
                    class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                    :title="admin.status === 'ACTIVE' ? 'Suspend Access' : 'Restore Access'"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636" /></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </VCard>

    <!-- Invite Admin Modal -->
    <VModal v-model="showInviteModal" title="Invite New Officer" maxWidth="lg">
        <form @submit.prevent="handleInvite" class="space-y-5">
            <div class="grid grid-cols-2 gap-4">
                <VInput v-model="inviteForm.username" label="Username" placeholder="e.g. jdoe" required />
                <VInput v-model="inviteForm.fullName" label="Full Name" placeholder="e.g. John Doe" required />
            </div>
            <VInput v-model="inviteForm.email" type="email" label="Email Address" placeholder="john@example.com" required />
            <VInput v-model="inviteForm.password" type="password" label="Temporary Password" placeholder="At least 6 chars" required />
            
            <div class="space-y-4 max-h-[300px] overflow-y-auto px-1 py-1 custom-scrollbar">
                <!-- Groups Section -->
                <section class="space-y-2">
                    <div class="flex items-center justify-between">
                        <h4 class="text-[11px] font-extrabold uppercase tracking-widest text-slate-400">Departmental Groups</h4>
                        <div class="relative w-32">
                            <input v-model="inviteGroupSearch" type="text" placeholder="Search..." 
                                   class="w-full pl-7 pr-2 py-1 text-[11px] border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500/10 focus:border-emerald-500 transition-all" />
                            <svg class="w-3 h-3 absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                        </div>
                    </div>
                    <div class="grid grid-cols-2 gap-2">
                        <div v-for="group in filteredInviteGroups" :key="group.id" 
                             @click="toggleInviteGroup(group.id)"
                             class="p-3 border rounded-xl cursor-pointer transition-all flex items-start gap-2"
                             :class="inviteForm.groupIds.has(group.id) ? 'bg-emerald-50 border-emerald-500 ring-1 ring-emerald-500/20' : 'bg-white border-slate-200 hover:border-slate-300'">
                            <div class="w-4 h-4 rounded border flex items-center justify-center mt-0.5 transition-colors"
                                 :class="inviteForm.groupIds.has(group.id) ? 'bg-emerald-500 border-emerald-500 text-white' : 'bg-white border-slate-300'">
                                <svg v-if="inviteForm.groupIds.has(group.id)" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
                            </div>
                            <div>
                                <div class="text-[12px] font-bold" :class="inviteForm.groupIds.has(group.id) ? 'text-emerald-900' : 'text-slate-800'">{{ group.name }}</div>
                            </div>
                        </div>
                        <div v-if="filteredInviteGroups.length === 0" class="col-span-2 py-4 text-center text-[11px] text-slate-400 italic">No groups matching your search</div>
                    </div>
                </section>

                <!-- Individual Permits Section -->
                <section class="space-y-2">
                    <div class="flex items-center justify-between">
                        <h4 class="text-[11px] font-extrabold uppercase tracking-widest text-slate-400">Special Individual Permits</h4>
                        <div class="relative w-32">
                            <input v-model="invitePermSearch" type="text" placeholder="Search..." 
                                   class="w-full pl-7 pr-2 py-1 text-[11px] border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500/10 focus:border-emerald-500 transition-all" />
                            <svg class="w-3 h-3 absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                        </div>
                    </div>
                    <div class="grid grid-cols-2 gap-2">
                        <div v-for="perm in filteredInvitePerms" :key="perm.id" 
                             @click="toggleInvitePerm(perm.id)"
                             class="flex items-start gap-2 p-3 border rounded-xl cursor-pointer transition-colors hover:bg-slate-50"
                             :class="inviteForm.individualPermissionIds.has(perm.id) ? 'bg-emerald-50 border-emerald-500 ring-1 ring-emerald-500/20' : 'bg-white border-slate-200'">
                            <div class="w-4 h-4 rounded border flex shrink-0 items-center justify-center mt-0.5 transition-colors"
                                 :class="inviteForm.individualPermissionIds.has(perm.id) ? 'bg-emerald-500 border-emerald-500 text-white' : 'bg-white border-slate-300'">
                                <svg v-if="inviteForm.individualPermissionIds.has(perm.id)" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
                            </div>
                            <div>
                                <div class="text-[12px] font-bold text-slate-700 leading-tight">{{ perm.description }}</div>
                                <div class="text-[10px] font-mono text-slate-400 mt-0.5">{{ perm.key }}</div>
                            </div>
                        </div>
                        <div v-if="filteredInvitePerms.length === 0" class="col-span-2 py-4 text-center text-[11px] text-slate-400 italic">No permits matching your search</div>
                    </div>
                </section>
            </div>
        </form>
        <template #footer>
            <VButton variant="secondary" @click="showInviteModal = false">Cancel</VButton>
            <VButton variant="primary" :loading="submitting" @click="handleInvite">Authorize Access</VButton>
        </template>
    </VModal>

    <!-- Permission Matrix Modal -->
    <VModal v-model="showPermissionModal" :title="`Permissions: ${selectedAdmin?.fullName}`" maxWidth="2xl">
        <div v-if="selectedAdmin" class="space-y-6">
            <!-- Groups Section -->
            <section class="space-y-3">
                <div class="flex items-center justify-between">
                    <h4 class="text-[12px] font-extrabold uppercase tracking-widest text-slate-400">Departmental Groups</h4>
                    <div class="relative w-40">
                        <input v-model="matrixGroupSearch" type="text" placeholder="Search groups..." 
                               class="w-full pl-8 pr-3 py-1.5 text-[12px] border border-slate-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/5 focus:border-emerald-500 transition-all" />
                        <svg class="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                    </div>
                </div>
                <div class="grid grid-cols-2 gap-3">
                    <div v-for="group in filteredMatrixGroups" :key="group.id" 
                         @click="toggleGroup(group.id)"
                         class="p-4 border rounded-2xl cursor-pointer transition-all flex items-start gap-3"
                         :class="matrix.groupIds.has(group.id) ? 'bg-emerald-50 border-emerald-500 ring-1 ring-emerald-500/20' : 'bg-white border-slate-200 hover:border-slate-300'">
                        <div class="w-5 h-5 rounded-md border flex items-center justify-center mt-0.5 transition-colors"
                             :class="matrix.groupIds.has(group.id) ? 'bg-emerald-500 border-emerald-500 text-white' : 'bg-white border-slate-300'">
                            <svg v-if="matrix.groupIds.has(group.id)" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
                        </div>
                        <div>
                            <div class="text-[13px] font-bold" :class="matrix.groupIds.has(group.id) ? 'text-emerald-900' : 'text-slate-800'">{{ group.name }}</div>
                            <div class="text-[11px] text-slate-500 mt-0.5">{{ group.description }}</div>
                        </div>
                    </div>
                    <div v-if="filteredMatrixGroups.length === 0" class="col-span-2 py-10 text-center bg-slate-50/50 rounded-2xl border border-dashed border-slate-200">
                        <p class="text-sm text-slate-400 font-medium italic">No departmental groups found</p>
                    </div>
                </div>
            </section>

            <!-- Individual Permits Section -->
            <section class="space-y-3">
                <div class="flex items-center justify-between">
                    <h4 class="text-[12px] font-extrabold uppercase tracking-widest text-slate-400">Special Individual Permits</h4>
                    <div class="relative w-40">
                        <input v-model="matrixPermSearch" type="text" placeholder="Search permits..." 
                               class="w-full pl-8 pr-3 py-1.5 text-[12px] border border-slate-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/5 focus:border-emerald-500 transition-all" />
                        <svg class="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                    </div>
                </div>
                <div class="bg-slate-50/50 border border-slate-200/60 rounded-2xl overflow-hidden max-h-[240px] overflow-y-auto custom-scrollbar">
                    <div v-for="perm in filteredMatrixPerms" :key="perm.id" 
                         @click="togglePerm(perm.id)"
                         class="flex items-center justify-between px-5 py-3 border-b last:border-0 border-slate-100 cursor-pointer hover:bg-white transition-colors">
                        <div class="flex items-center gap-3">
                            <div class="w-4 h-4 rounded border flex items-center justify-center transition-colors"
                                 :class="matrix.individualPermissionIds.has(perm.id) ? 'bg-emerald-500 border-emerald-500 text-white' : 'bg-white border-slate-300'">
                                <svg v-if="matrix.individualPermissionIds.has(perm.id)" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
                            </div>
                            <span class="text-[12px] font-bold text-slate-700 tracking-tight">{{ perm.description }}</span>
                        </div>
                        <span class="text-[10px] font-mono font-bold text-slate-400 bg-white px-2 py-0.5 rounded border border-slate-200 group-hover:text-emerald-500">{{ perm.key }}</span>
                    </div>
                    <div v-if="filteredMatrixPerms.length === 0" class="py-10 text-center">
                        <p class="text-sm text-slate-400 font-medium italic">No individual permits found</p>
                    </div>
                </div>
            </section>
        </div>
        <template #footer>
            <VButton variant="secondary" @click="showPermissionModal = false">Cancel</VButton>
            <VButton variant="primary" :loading="submitting" @click="handleUpdatePermissions">Secure Access</VButton>
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
import { useAdminAuthStore } from '@/stores/adminAuth'
import adminApi from '@/api/adminApi'

const adminAuth = useAdminAuthStore()
const admins = ref<any[]>([])
const loading = ref(true)
const submitting = ref(false)

// Modals state
const showInviteModal = ref(false)
const showPermissionModal = ref(false)
const selectedAdmin = ref<any>(null)

// Search states
const inviteGroupSearch = ref('')
const invitePermSearch = ref('')
const matrixGroupSearch = ref('')
const matrixPermSearch = ref('')

// Data for Matrix
const availableGroups = ref<any[]>([])
const availablePermissions = ref<any[]>([])

// Forms
const inviteForm = reactive({
    username: '',
    fullName: '',
    email: '',
    password: '',
    groupIds: new Set<number>(),
    individualPermissionIds: new Set<number>()
})

const matrix = reactive({
    groupIds: new Set<number>(),
    individualPermissionIds: new Set<number>()
})

const fetchAdmins = async () => {
  loading.value = true
  try {
    const { data } = await adminApi.get('/admin/system/admins')
    admins.value = data
  } catch (err) {
    console.error('Failed to fetch admins', err)
  } finally {
    loading.value = false
  }
}

const fetchPermissionData = async () => {
    try {
        const [groupsRes, permsRes] = await Promise.all([
            adminApi.get('/admin/system/permission-groups'),
            adminApi.get('/admin/system/permissions')
        ])
        availableGroups.value = groupsRes.data
        availablePermissions.value = permsRes.data
    } catch (err) {
        console.error('Failed to load permission catalog', err)
    }
}

const filteredInviteGroups = computed(() => {
    const s = inviteGroupSearch.value.toLowerCase()
    return availableGroups.value.filter(g => 
        g.name.toLowerCase().includes(s) || (g.description && g.description.toLowerCase().includes(s))
    )
})

const filteredInvitePerms = computed(() => {
    const s = invitePermSearch.value.toLowerCase()
    return availablePermissions.value.filter(p => 
        p.description.toLowerCase().includes(s) || p.key.toLowerCase().includes(s)
    )
})

const filteredMatrixGroups = computed(() => {
    const s = matrixGroupSearch.value.toLowerCase()
    return availableGroups.value.filter(g => 
        g.name.toLowerCase().includes(s) || (g.description && g.description.toLowerCase().includes(s))
    )
})

const filteredMatrixPerms = computed(() => {
    const s = matrixPermSearch.value.toLowerCase()
    return availablePermissions.value.filter(p => 
        p.description.toLowerCase().includes(s) || p.key.toLowerCase().includes(s)
    )
})

const openInviteModal = () => {
    inviteForm.username = ''
    inviteForm.fullName = ''
    inviteForm.email = ''
    inviteForm.password = ''
    inviteForm.groupIds.clear()
    inviteForm.individualPermissionIds.clear()
    inviteGroupSearch.value = ''
    invitePermSearch.value = ''
    showInviteModal.value = true
}

const toggleInviteGroup = (id: number) => {
    if (inviteForm.groupIds.has(id)) inviteForm.groupIds.delete(id)
    else inviteForm.groupIds.add(id)
}

const toggleInvitePerm = (id: number) => {
    if (inviteForm.individualPermissionIds.has(id)) inviteForm.individualPermissionIds.delete(id)
    else inviteForm.individualPermissionIds.add(id)
}

const handleInvite = async () => {
    if (!inviteForm.username || !inviteForm.email || !inviteForm.password) return
    submitting.value = true
    try {
        await adminApi.post('/admin/system/admins', {
            ...inviteForm,
            groupIds: Array.from(inviteForm.groupIds),
            individualPermissionIds: Array.from(inviteForm.individualPermissionIds)
        })
        showInviteModal.value = false
        await fetchAdmins()
    } catch (err) {
        console.error('Failed to invite officer', err)
    } finally {
        submitting.value = false
    }
}

const openPermissionModal = (admin: any) => {
    selectedAdmin.value = admin
    matrix.groupIds = new Set(admin.permissionGroupIds || []) // We need to ensure backend returns these IDs
    matrix.individualPermissionIds = new Set(admin.individualPermissionIds || [])
    matrixGroupSearch.value = ''
    matrixPermSearch.value = ''
    showPermissionModal.value = true
}

const toggleGroup = (id: number) => {
    if (matrix.groupIds.has(id)) matrix.groupIds.delete(id)
    else matrix.groupIds.add(id)
}

const togglePerm = (id: number) => {
    if (matrix.individualPermissionIds.has(id)) matrix.individualPermissionIds.delete(id)
    else matrix.individualPermissionIds.add(id)
}

const handleUpdatePermissions = async () => {
    if (!selectedAdmin.value) return
    submitting.value = true
    try {
        await adminApi.put(`/admin/system/admins/${selectedAdmin.value.id}/permissions`, {
            groupIds: Array.from(matrix.groupIds),
            individualPermissionIds: Array.from(matrix.individualPermissionIds)
        })
        showPermissionModal.value = false
        await fetchAdmins()
    } catch (err) {
        console.error('Failed to update permissions', err)
    } finally {
        submitting.value = false
    }
}

const canManage = (targetAdmin: any) => {
  if (!adminAuth.user?.superadmin) return false
  // Cannot manage other superadmins
  if (targetAdmin.superadmin && targetAdmin.id !== adminAuth.user.id) return false
  // Cannot deactivate self
  if (targetAdmin.id === adminAuth.user.id) return false
  return true
}

const toggleStatus = async (admin: any) => {
  const newStatus = admin.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  try {
    await adminApi.patch(`/admin/system/admins/${admin.id}/status?status=${newStatus}`)
    admin.status = newStatus
  } catch (err) {
    console.error('Failed to update admin status', err)
  }
}

onMounted(() => {
    fetchAdmins()
    fetchPermissionData()
})
</script>
