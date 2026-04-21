<script setup lang="ts">
interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
}

withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
  type: 'button'
})
</script>

<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    class="relative inline-flex items-center justify-center font-extrabold transition-all active:scale-[0.96] disabled:opacity-50 disabled:pointer-events-none rounded-2xl tracking-tight"
    :class="[
      // Variants
      variant === 'primary' && 'bg-emerald-600 text-white hover:bg-emerald-700 shadow-xl shadow-emerald-600/15 hover:shadow-emerald-600/30',
      variant === 'secondary' && 'bg-slate-100 text-slate-700 hover:bg-slate-200',
      variant === 'outline' && 'bg-transparent border-2 border-emerald-600/20 text-emerald-700 hover:bg-emerald-50 hover:border-emerald-600',
      variant === 'ghost' && 'bg-transparent text-emerald-700/80 hover:bg-emerald-50 hover:text-emerald-800',
      variant === 'danger' && 'bg-rose-50 text-rose-600 hover:bg-rose-100 border border-rose-100 hover:border-rose-200',
      
      // Sizes
      size === 'sm' && 'px-5 py-2.5 text-xs',
      size === 'md' && 'px-7 py-3 text-sm',
      size === 'lg' && 'px-11 py-4.5 text-base',
    ]"
  >
    <!-- Loading Spinner -->
    <span v-if="loading" class="absolute inset-0 flex items-center justify-center">
      <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
    </span>

    <!-- Content -->
    <span :class="{ 'opacity-0': loading }" class="flex items-center gap-2">
      <slot name="prefix"></slot>
      <slot></slot>
      <slot name="suffix"></slot>
    </span>
  </button>
</template>
