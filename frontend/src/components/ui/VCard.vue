<script setup lang="ts">
interface Props {
  title?: string
  subtitle?: string
  noPadding?: boolean
}

defineProps<Props>()
</script>

<template>
  <div class="bg-white border border-slate-200/60 rounded-[1.25rem] shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)] transition-all hover:shadow-[0_10px_40px_-10px_rgba(5,150,105,0.05)] flex flex-col group">
    <div v-if="title || $slots.header" class="px-7 py-6 border-b border-slate-100/50 flex items-center justify-between">
      <slot name="header">
        <div>
          <h3 class="text-[15px] font-bold text-slate-900 tracking-tight">{{ title }}</h3>
          <p v-if="subtitle" class="text-[13px] text-slate-500 mt-1.5">{{ subtitle }}</p>
        </div>
      </slot>
      <div v-if="$slots.headerAction">
        <slot name="headerAction"></slot>
      </div>
    </div>
    
    <div :class="[noPadding ? 'p-0' : 'p-7']" class="flex-grow">
      <slot></slot>
    </div>

    <div v-if="$slots.footer" class="px-7 py-5 bg-slate-50/50 rounded-b-[1.25rem] border-t border-slate-100/50 mt-auto">
      <slot name="footer"></slot>
    </div>
  </div>
</template>
