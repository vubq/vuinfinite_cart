<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAdminAuthStore } from '@/stores/adminAuth'

const adminAuth = useAdminAuthStore()
const route = useRoute()

const sidebarLinks = [
  { name: 'Dashboard', path: '/admin', icon: 'M3 3h7v9H3V3zm11 0h7v5h-7V3zm0 9h7v9h-7v-9zM3 16h7v5H3v-5z', resource: null, action: null },
  { name: 'Inventory Ecosystem', path: '/admin/products', icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4', resource: 'products', action: 'read' },
  { name: 'Sales Feed', path: '/admin/orders', icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z', resource: 'orders', action: 'read' },
  { name: 'Client Relations', path: '/admin/customers', icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z', resource: 'customers', action: 'read' },
  { name: 'Media Explorer', path: '/admin/media', icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z', resource: 'admins', action: 'read' },
  { name: 'Team Governance', path: '/admin/system', icon: 'M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z', resource: 'admins', action: 'read' },
  { name: 'Access Roles', path: '/admin/system/roles', icon: 'M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4v-4l5.514-5.514A6 6 0 0115 7z', resource: 'admins', action: 'read', isSuperadminOnly: true }
]

const visibleLinks = computed(() => {
  return sidebarLinks.filter(link => {
    if (link.isSuperadminOnly && !adminAuth.user?.superadmin) return false
    if (!link.resource) return true
    return adminAuth.hasPermission(link.resource, link.action)
  })
})

const pageHeader = computed(() => {
  if (route.path.includes('/products')) return { main: 'Inventory Ecosystem', sub: 'Directory' }
  if (route.path.includes('/orders')) return { main: 'Sales Feed', sub: 'Live Tracking' }
  if (route.path.includes('/customers')) return { main: 'Client Relations', sub: 'Profiles' }
  if (route.path.includes('/media')) return { main: 'Media Explorer', sub: 'Management' }
  if (route.path.includes('/system')) return { main: 'Team Governance', sub: 'Internal Controls' }
  return { main: 'Core Network', sub: 'Intelligence' }
})
</script>
<template>
  <div class="flex h-screen bg-[#fcfcfc]">
    <!-- Sidebar -->
    <aside class="w-[280px] bg-slate-50/50 border-r border-slate-200/60 flex flex-col relative z-20">
      <div class="px-7 pt-8 pb-4">
        <router-link to="/admin" class="flex items-center gap-3 hover:opacity-90 transition-opacity">
          <!-- Elegant V Mark -->
          <div class="flex items-center justify-center w-9 h-9 bg-white rounded-xl shadow-sm border border-slate-200/60 text-emerald-600">
            <svg viewBox="0 0 24 24" fill="none" class="w-5 h-5">
              <path d="M5 6L12 18L19 6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 12L19 6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" opacity="0.3"/>
            </svg>
          </div>
          <h1 class="text-[20px] font-extrabold tracking-tight text-slate-900 leading-none mt-1">
            Vu<span class="text-emerald-600">Emporium</span>
          </h1>
        </router-link>
        
        <div class="mt-6 text-[10px] font-bold text-slate-400 uppercase tracking-widest pl-2">
          Intelligence Portal
        </div>
      </div>

      <nav class="flex-grow px-5 space-y-2 mt-2">
        <router-link 
          v-for="link in visibleLinks"
          :key="link.path"
          :to="link.path"
          exact-active-class="bg-white text-emerald-700 font-bold border border-slate-200/80 shadow-[0_2px_8px_-3px_rgba(0,0,0,0.05)] shadow-emerald-900/5 relative after:absolute after:left-0 after:top-1/2 after:-translate-y-1/2 after:w-1 after:h-6 after:bg-emerald-500 after:rounded-r" 
          class="flex items-center gap-3.5 px-4 py-3 text-[13px] font-semibold text-slate-500 rounded-[14px] transition-all border border-transparent hover:bg-slate-100 hover:text-slate-800"
        >
          <svg class="w-5 h-5 opacity-80" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.2" :d="link.icon" />
          </svg>
          {{ link.name }}
        </router-link>
      </nav>

      <div class="border-t border-slate-100 p-6 bg-white shrink-0 mt-auto">
        <div class="flex items-center gap-3 px-1">
          <router-link to="/admin/profile" class="relative group/avatar">
            <div class="w-10 h-10 rounded-xl bg-slate-900 flex items-center justify-center text-[11px] font-bold text-white tracking-widest shadow-sm overflow-hidden border-2 border-transparent group-hover/avatar:border-emerald-500 transition-all">
              <img v-if="adminAuth.user?.avatarUrl" :src="adminAuth.user.avatarUrl" class="w-full h-full object-cover" />
              <span v-else>{{ adminAuth.user?.username?.substring(0, 2).toUpperCase() || 'AD' }}</span>
            </div>
            <div class="absolute -bottom-1 -right-1 w-4 h-4 bg-emerald-500 rounded-lg border-2 border-white flex items-center justify-center text-[8px] text-white opacity-0 group-hover/avatar:opacity-100 transition-opacity">
              <svg xmlns="http://www.w3.org/2000/svg" width="8" height="8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
            </div>
          </router-link>
          <div class="flex-grow overflow-hidden">
            <router-link to="/admin/profile" class="text-[13px] font-bold text-slate-900 truncate block hover:text-emerald-600 transition-colors">{{ adminAuth.user?.fullName || adminAuth.user?.username }}</router-link>
            <button @click="adminAuth.logout" class="text-[11px] font-medium text-slate-500 hover:text-emerald-600 transition-colors">Sign Out securely</button>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content Area -->
    <main class="flex-grow flex flex-col overflow-hidden relative">
      <!-- Top Bar -->
      <header class="h-[72px] border-b border-slate-200/60 flex items-center justify-between px-10 bg-white/60 backdrop-blur-md sticky top-0 z-10 shrink-0">
        <div class="flex items-center gap-3 text-slate-400 font-bold text-[11px] uppercase tracking-[0.15em] transition-all">
          <span class="text-emerald-600">{{ pageHeader.main }}</span> 
          <span class="w-1 h-1 rounded-full bg-slate-300"></span>
          <span class="text-slate-800">{{ pageHeader.sub }}</span>
        </div>
        <div class="flex items-center gap-5 text-slate-500">
          <button class="p-2 hover:bg-slate-100 rounded-full transition-colors relative">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg>
            <span class="absolute top-[6px] right-[6px] w-[6px] h-[6px] bg-emerald-500 rounded-full border border-white"></span>
          </button>
          <div class="h-4 w-px bg-slate-200"></div>
          <VButton variant="primary" size="sm" class="!rounded-full px-5">Generate Report</VButton>
        </div>
      </header>

      <!-- Dashboard Body -->
      <div class="flex-grow overflow-y-auto px-6 py-8 md:px-12 md:py-10">
        <router-view />
      </div>
    </main>
  </div>
</template>
