<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">Client Relations (CRM)</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium">Monitor customer growth and manage account security.</p>
      </div>
      <div class="flex gap-3">
        <VButton variant="secondary" size="md">Export Leads</VButton>
      </div>
    </div>
    
    <VCard class="overflow-hidden border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)]">
      <div v-if="loading" class="p-20 flex justify-center items-center">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600"></div>
      </div>
      
      <div v-else-if="customers.length === 0" class="p-20 text-center">
        <svg class="w-12 h-12 text-slate-200 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
        <h3 class="text-lg font-bold text-slate-800">No customers registered</h3>
        <p class="text-[13px] text-slate-500 mt-1">Data will populate automatically as clients register on the storefront.</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/50 border-b border-slate-200/60">
              <th class="px-6 py-4 text-[11px] font-bold text-slate-400 uppercase tracking-wider">Client Identity</th>
              <th class="px-6 py-4 text-[11px] font-bold text-slate-400 uppercase tracking-wider">Contact</th>
              <th class="px-6 py-4 text-[11px] font-bold text-slate-400 uppercase tracking-wider">Status</th>
              <th class="px-6 py-4 text-[11px] font-bold text-slate-400 uppercase tracking-wider text-right">Protection</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="customer in customers" :key="customer.id" class="hover:bg-slate-50/40 transition-colors group">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-2xl bg-emerald-50 border border-emerald-100 flex items-center justify-center overflow-hidden">
                    <img v-if="customer.avatarUrl" :src="customer.avatarUrl" class="w-full h-full object-cover">
                    <span v-else class="text-emerald-700 font-bold text-xs">{{ customer.name?.substring(0,2).toUpperCase() }}</span>
                  </div>
                  <div>
                    <div class="text-[13px] font-bold text-slate-900 leading-none mb-1">{{ customer.name }}</div>
                    <div class="text-[11px] text-slate-400 font-medium italic">Joined {{ formatDate(customer.createdAt) }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="text-[12px] font-semibold text-slate-700 leading-none mb-1">{{ customer.email }}</div>
                <div class="text-[11px] text-slate-400 font-medium tracking-tight">{{ customer.phone || 'No phone recorded' }}</div>
              </td>
              <td class="px-6 py-4">
                <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider" 
                      :class="statusClass(customer.status)">
                  {{ customer.status }}
                </span>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-all">
                  <VButton 
                    v-if="customer.status !== 'BANNED'"
                    variant="secondary" 
                    size="sm"
                    class="!py-1.5 !px-3 !text-[11px] !bg-red-50 !text-red-600 !border-red-100 hover:!bg-red-100"
                    @click="updateStatus(customer, 'BANNED')"
                  >
                    Restrict Account
                  </VButton>
                  <VButton 
                    v-else
                    variant="secondary" 
                    size="sm"
                    class="!py-1.5 !px-3 !text-[11px] !bg-emerald-50 !text-emerald-600 !border-emerald-100 hover:!bg-emerald-100"
                    @click="updateStatus(customer, 'ACTIVE')"
                  >
                    Restore Access
                  </VButton>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </VCard>

    <VConfirmDialog
      v-model="showConfirmDialog"
      :title="confirmState.title"
      :message="confirmState.message"
      :confirm-text="confirmState.confirmText"
      :variant="confirmState.variant"
      :loading="submitting"
      @confirm="runConfirmedAction"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import VCard from '@/components/ui/VCard.vue'
import VButton from '@/components/ui/VButton.vue'
import VConfirmDialog from '@/components/ui/VConfirmDialog.vue'
import adminApi from '@/api/adminApi'

const customers = ref<any[]>([])
const loading = ref(true)
const submitting = ref(false)
const showConfirmDialog = ref(false)
const confirmState = ref({
  title: '',
  message: '',
  confirmText: 'Confirm',
  variant: 'warning' as 'info' | 'warning' | 'danger' | 'success',
  action: null as null | (() => Promise<void>)
})

const fetchCustomers = async () => {
  loading.value = true
  try {
    const { data } = await adminApi.get('/admin/crm/customers')
    customers.value = data
  } catch (err) {
    console.error('Failed to load CRM data', err)
  } finally {
    loading.value = false
  }
}

const updateStatus = async (customer: any, newStatus: string) => {
  confirmState.value = {
    title: newStatus === 'BANNED' ? 'Restrict Customer Account' : 'Restore Customer Account',
    message: newStatus === 'BANNED'
      ? `Restrict "${customer.name}" now? Their refresh access will be revoked immediately.`
      : `Restore "${customer.name}" so they can access their account again?`,
    confirmText: newStatus === 'BANNED' ? 'Restrict Account' : 'Restore Access',
    variant: newStatus === 'BANNED' ? 'danger' : 'success',
    action: async () => {
      submitting.value = true
      try {
        await adminApi.patch(`/admin/crm/customers/${customer.id}/status?status=${newStatus}`)
        customer.status = newStatus
        showConfirmDialog.value = false
      } catch (err) {
        console.error('Security action failed', err)
      } finally {
        submitting.value = false
      }
    }
  }
  showConfirmDialog.value = true
}

const runConfirmedAction = async () => {
  await confirmState.value.action?.()
}

const statusClass = (status: string) => {
  switch (status) {
    case 'ACTIVE': return 'bg-emerald-50 text-emerald-700'
    case 'BANNED': return 'bg-red-50 text-red-700'
    default: return 'bg-slate-100 text-slate-600'
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return 'Unknown'
  return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', year: 'numeric' })
}

onMounted(fetchCustomers)
</script>
