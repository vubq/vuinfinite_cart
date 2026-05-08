<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">{{ t('header.title') }}</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium">{{ t('header.subtitle') }}</p>
      </div>
      <VButton variant="secondary" size="md">{{ t('btn.export') }}</VButton>
    </div>
    
    <VCard class="min-h-[500px] flex items-center justify-center border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)]">
      <div class="text-center">
        <svg class="w-12 h-12 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path></svg>
        <h3 class="text-lg font-bold text-slate-800">{{ t('empty.title') }}</h3>
        <p class="text-[13px] text-slate-500 mt-1 max-w-sm mx-auto">{{ t('empty.subtitle') }}</p>
      </div>
    </VCard>
  </div>
</template>

<script setup lang="ts">
import VCard from '@/components/ui/VCard.vue'
import VButton from '@/components/ui/VButton.vue'
import { useI18n } from '@/composables/useI18n'

const { t } = useI18n(['admin.orders'])
</script>
