<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">{{ t('header.title') }}</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium">{{ t('header.subtitle') }}</p>
      </div>
      <VButton variant="primary" size="md">{{ t('btn.add') }}</VButton>
    </div>
    
    <VCard class="min-h-[500px] flex items-center justify-center border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)]">
      <div class="text-center">
        <svg class="w-12 h-12 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path></svg>
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

const { t } = useI18n(['admin.products'])
</script>
