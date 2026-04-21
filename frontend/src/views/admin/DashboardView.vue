<script setup lang="ts">
import VCard from '@/components/ui/VCard.vue'
import VButton from '@/components/ui/VButton.vue'
import { useAdminAuthStore } from '@/stores/adminAuth'

const adminAuth = useAdminAuthStore()

const stats = [
  { label: 'Total Sales', value: '$12,840', change: '+12.5%', icon: 'sales' },
  { label: 'Active Orders', value: '156', change: '+3.2%', icon: 'orders' },
  { label: 'New Customers', value: '42', change: '-1.4%', icon: 'customers' },
  { label: 'Growth Rate', value: '24.8%', change: '+5.7%', icon: 'growth' }
]
</script>
<template>
  <div class="space-y-8 max-w-[1400px] mx-auto animate-in fade-in zoom-in-[0.98] duration-700 ease-out pb-20">
    <!-- Header Section -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-6">
      <div class="space-y-2.5">
        <div class="inline-flex items-center gap-2 px-3 py-1 bg-white border border-slate-200/60 rounded text-[10px] font-bold text-slate-500 uppercase tracking-widest shadow-sm">
          <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
          System Optimal
        </div>
        <h1 class="text-3xl md:text-4xl font-extrabold text-slate-900 tracking-tight">
          Welcome back, <span class="text-emerald-600">{{ adminAuth.user?.username || 'Commander' }}</span>
        </h1>
        <p class="text-slate-500 font-medium text-[15px] max-w-lg leading-relaxed">
          Your commerce ecosystem is currently performing at peak efficiency. Here's your intelligence briefing.
        </p>
      </div>
      <div class="flex gap-3">
        <VButton variant="secondary" size="md">
          Parameters
        </VButton>
        <VButton variant="primary" size="md">
          Export Report
        </VButton>
      </div>
    </div>

    <!-- Top KPI Row (4 equal cards) instead of asymmetric for better structure -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <VCard class="relative overflow-hidden border-slate-200/60">
        <div class="absolute -right-4 -top-4 w-24 h-24 bg-emerald-50 rounded-full blur-2xl opacity-60"></div>
        <div class="relative z-10">
          <h3 class="text-[12px] font-bold text-slate-500 uppercase tracking-widest mb-1">Gross Revenue</h3>
          <div class="flex items-center gap-3">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">$142,840</span>
            <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[11px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-100">
              +24%
            </span>
          </div>
        </div>
      </VCard>
      
      <VCard v-for="stat in stats.slice(1)" :key="stat.label" class="relative overflow-hidden border-slate-200/60">
        <div class="relative z-10">
          <h3 class="text-[12px] font-bold text-slate-500 uppercase tracking-widest mb-1">{{ stat.label }}</h3>
          <div class="flex items-center gap-3">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">{{ stat.value }}</span>
            <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[11px] font-bold"
                  :class="stat.change.startsWith('+') ? 'bg-emerald-50 text-emerald-700 border border-emerald-100' : 'bg-rose-50 text-rose-700 border border-rose-100'">
              {{ stat.change }}
            </span>
          </div>
        </div>
      </VCard>
    </div>

    <!-- Main Content Area -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      
      <!-- Primary Data View -->
      <VCard title="Recent Transactions" subtitle="Live payment capture from storefront" class="lg:col-span-2 border-slate-200/60">
        <template #headerAction>
          <button class="text-[12px] font-bold text-emerald-600 hover:text-emerald-700">View All →</button>
        </template>
        
        <div class="divide-y divide-slate-100/80 -mx-7 -mb-7">
          <div v-for="i in 5" :key="i" class="px-7 py-4 flex items-center justify-between hover:bg-slate-50/50 transition-colors group">
            <div class="flex items-center gap-4">
              <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 group-hover:bg-emerald-50 group-hover:text-emerald-600 transition-colors">
                 <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
              </div>
              <div>
                <p class="text-[14px] font-bold text-slate-900 group-hover:text-emerald-700 transition-colors">Order #{{ 8040 + i }}</p>
                <p class="text-[12px] text-slate-500 font-medium mt-0.5">Customer Identity Verified • {{ i }}m ago</p>
              </div>
            </div>
            <div class="text-right">
              <p class="text-[15px] font-bold text-slate-900">$1,240.00</p>
              <div class="flex justify-end mt-1">
                <span class="w-2 h-2 rounded-full bg-emerald-500 border border-white"></span>
              </div>
            </div>
          </div>
        </div>
      </VCard>

      <!-- Sidebar Modules -->
      <div class="space-y-6">
        
        <!-- Command Tools -->
        <VCard title="Quick Actions" class="border-slate-200/60">
          <div class="grid grid-cols-2 gap-3 mt-2">
             <button v-for="action in ['Products', 'Promotions', 'Settings', 'Analytics']" :key="action"
                class="flex flex-col items-center justify-center py-5 rounded-xl bg-white border border-slate-200/60 shadow-[0_2px_8px_-3px_rgba(0,0,0,0.02)] hover:border-emerald-200 hover:shadow-[0_8px_20px_-6px_rgba(5,150,105,0.08)] transition-all group/action">
                <div class="text-slate-400 group-hover/action:text-emerald-600 transition-colors mb-2">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
                </div>
                <span class="text-[11px] font-bold text-slate-600 uppercase tracking-widest group-hover/action:text-emerald-700">{{ action }}</span>
             </button>
          </div>
        </VCard>
        
        <!-- Attention module -->
        <div class="p-6 rounded-2xl bg-slate-900 relative overflow-hidden group/alert border border-slate-800">
          <div class="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/carbon-fibre.png')] opacity-[0.03]"></div>
          
          <div class="flex items-center gap-2 mb-3 relative z-10">
            <span class="w-2 h-2 rounded-full bg-rose-500 animate-pulse"></span>
            <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">System Alert</span>
          </div>
          
          <h4 class="text-lg font-semibold text-white mb-4 relative z-10 leading-snug">Inventory depletion detected in <span class="text-emerald-400">Warehouse B</span>.</h4>
          <button class="w-full py-2.5 bg-white text-slate-900 text-[13px] font-bold rounded-lg hover:bg-slate-100 transition-colors relative z-10">
            Manage Inventory
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
/* Scoped styles removed; component entirely relies on Tailwind utilities for cleaner architecture */
</style>
