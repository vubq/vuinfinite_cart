<script setup lang="ts">
import { useI18n } from '@/composables/useI18n'
import VButton from '@/components/ui/VButton.vue'

const { t } = useI18n(['customer.home', 'common'])
</script>

<template>
  <div class="px-10 py-24 flex flex-col items-center justify-center text-center space-y-8 animate-in fade-in zoom-in-95 duration-1000">
    <div class="inline-flex items-center gap-3 px-5 py-2 rounded-full bg-emerald-50 border border-emerald-100 text-[11px] font-black text-emerald-700 uppercase tracking-[0.2em]">
       {{ t('hero.badge') }}
    </div>
    <h1 class="text-7xl font-black text-slate-900 tracking-tighter leading-tight max-w-4xl">
      {{ t('hero.title_prefix') }}<span class="text-emerald-600">{{ t('hero.title_highlight') }}</span>{{ t('hero.title_suffix') }}
    </h1>
    <p class="text-slate-400 font-bold text-xl max-w-2xl leading-relaxed">
      {{ t('hero.subtitle') }}
    </p>
    <div class="flex gap-4 pt-4">
      <VButton variant="primary" size="lg">{{ t('hero.explore') }}</VButton>
      <VButton variant="outline" size="lg">{{ t('hero.view_story') }}</VButton>
    </div>
  </div>
</template>
