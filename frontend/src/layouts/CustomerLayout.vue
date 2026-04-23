<script setup lang="ts">
import { useCustomerAuthStore } from '@/stores/customerAuth'

const customerAuth = useCustomerAuthStore()
</script>

<template>
  <div class="min-h-screen flex flex-col bg-white">
    <!-- Navigation Header -->
    <header class="sticky top-0 z-50 bg-white/70 backdrop-blur-xl border-b border-slate-100">
      <nav class="max-w-7xl mx-auto px-6 md:px-10 h-20 flex items-center justify-between">
        <div class="flex items-center gap-12">
          <router-link to="/" class="text-3xl font-black text-emerald-600 tracking-tighter flex items-center gap-2">
            Vu<span class="text-slate-800">Emporium</span>
          </router-link>
          <div class="hidden md:flex items-center gap-8 text-[13px] font-bold text-slate-500 uppercase tracking-widest">
            <router-link to="/" class="hover:text-emerald-600 transition-colors">Collection</router-link>
            <router-link to="/" class="hover:text-emerald-600 transition-colors">Catalog</router-link>
            <router-link to="/" class="hover:text-emerald-600 transition-colors">Stories</router-link>
          </div>
        </div>

        <div class="flex items-center gap-6">
          <button class="p-2 text-slate-400 hover:text-emerald-600 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
          </button>
          
          <template v-if="customerAuth.isAuthenticated">
            <div class="flex items-center gap-6">
              <router-link to="/profile" class="flex flex-col items-end group">
                <span class="text-sm font-black text-slate-800 group-hover:text-emerald-600 transition-colors">{{ customerAuth.user?.name }}</span>
                <button @click.prevent.stop="customerAuth.logout()" class="text-[10px] font-bold text-slate-400 hover:text-rose-500 uppercase tracking-widest transition-colors">Logout</button>
              </router-link>
              <router-link to="/profile" class="w-10 h-10 rounded-2xl bg-slate-50 flex items-center justify-center text-emerald-700 font-black overflow-hidden border border-slate-100 hover:border-emerald-200 transition-all">
                <img v-if="customerAuth.user?.avatarUrl" :src="customerAuth.user.avatarUrl" class="w-full h-full object-cover" />
                <span v-else>{{ customerAuth.user?.name?.substring(0, 1) }}</span>
              </router-link>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="text-sm font-bold text-slate-600 hover:text-emerald-600 transition-colors">Login</router-link>
            <VButton variant="primary" size="sm">
              Join Now
            </VButton>
          </template>
        </div>
      </nav>
    </header>

    <!-- Page Content -->
    <main class="flex-grow">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-slate-50 border-t border-slate-100 py-16 px-10 mt-auto">
      <div class="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-12 text-center md:text-left">
        <div class="space-y-4">
          <h2 class="text-2xl font-black text-emerald-600 tracking-tighter">Vu<span class="text-slate-800">Emporium</span></h2>
          <p class="text-slate-400 text-sm font-medium max-w-xs mx-auto md:mx-0 leading-relaxed">
            Crafting a premium e-commerce intelligence platform for the modern era. Designed for elegance.
          </p>
        </div>
        <div class="flex flex-col gap-3 text-slate-500 text-sm font-bold uppercase tracking-widest">
           <p class="text-slate-800 mb-2">Connect</p>
           <a href="#" class="hover:text-emerald-600 transition-colors">Instagram</a>
           <a href="#" class="hover:text-emerald-600 transition-colors">X / Twitter</a>
           <a href="#" class="hover:text-emerald-600 transition-colors">LinkedIn</a>
        </div>
        <div class="flex flex-col gap-3 text-slate-500 text-sm font-bold uppercase tracking-widest font-bold">
           <p class="text-slate-800 mb-2">Support</p>
           <a href="#" class="hover:text-emerald-600 transition-colors">Contact Intelligence</a>
           <a href="#" class="hover:text-emerald-600 transition-colors">Privacy Charter</a>
        </div>
      </div>
      <div class="max-w-7xl mx-auto pt-12 border-t border-slate-200/60 mt-12 flex justify-between items-center text-[10px] font-bold text-slate-400 uppercase tracking-[0.2em]">
          <span>&copy; 2026 VuEmporium Intel.</span>
          <span>Built for the Modern Web</span>
      </div>
    </footer>
  </div>
</template>
