<script setup lang="ts">
interface Props {
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'gray'
  size?: 'sm' | 'md'
}

withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md'
})
</script>

<template>
  <span 
    class="inline-flex items-center justify-center font-semibold rounded-lg tracking-wide uppercase"
    :class="[
      size === 'sm' ? 'px-2 py-0.5 text-[10px]' : 'px-3 py-1 text-xs',
      variant === 'primary' && 'bg-blue-100 text-blue-800 dark:bg-blue-500/10 dark:text-blue-400',
      variant === 'secondary' && 'bg-slate-100 text-slate-800 dark:bg-slate-800 dark:text-slate-300',
      variant === 'success' && 'bg-green-100 text-green-800 dark:bg-green-500/10 dark:text-green-400',
      variant === 'danger' && 'bg-red-100 text-red-800 dark:bg-red-500/10 dark:text-red-400',
      variant === 'warning' && 'bg-yellow-100 text-yellow-800 dark:bg-yellow-500/10 dark:text-yellow-500',
      variant === 'info' && 'bg-indigo-100 text-indigo-800 dark:bg-indigo-500/10 dark:text-indigo-400',
      variant === 'gray' && 'bg-gray-100 text-gray-800 dark:bg-slate-500/10 dark:text-gray-400',
    ]"
  >
    <slot></slot>
  </span>
</template>
