<script setup lang="ts">
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
</script>

<template>
  <Transition
    enter-active-class="transition-opacity duration-300"
    enter-from-class="opacity-0"
    leave-active-class="transition-opacity duration-700"
    leave-to-class="opacity-0"
  >
    <div 
      v-if="appStore.isLoading"
      class="fixed top-0 left-0 right-0 z-[9999] h-[2px] pointer-events-none"
    >
      <!-- Main Progress Bar -->
      <div 
        class="h-full bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.5)] animate-progress-indeterminate"
      ></div>
      
      <!-- Ghost Shimmer Effect -->
      <div 
        class="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent animate-shimmer"
      ></div>
    </div>
  </Transition>
</template>

<style scoped>
@keyframes progress-indeterminate {
  0% { width: 0%; left: 0; }
  50% { width: 30%; left: 35%; }
  100% { width: 0%; left: 100%; }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.animate-progress-indeterminate {
  position: absolute;
  animation: progress-indeterminate 2.5s cubic-bezier(0.4, 0, 0.2, 1) infinite;
}

.animate-shimmer {
  animation: shimmer 1.5s infinite;
}
</style>
