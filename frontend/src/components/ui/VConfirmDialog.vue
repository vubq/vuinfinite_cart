<script setup lang="ts">
import { computed } from 'vue'
import VButton from './VButton.vue'
import VModal from './VModal.vue'

interface Props {
  modelValue: boolean
  title: string
  message: string
  confirmText?: string
  cancelText?: string
  variant?: 'info' | 'warning' | 'danger' | 'success'
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  confirmText: 'Confirm',
  cancelText: 'Cancel',
  variant: 'warning',
  loading: false
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const confirmVariant = computed(() => props.variant === 'danger' ? 'danger' : 'primary')
const iconClasses = computed(() => {
  switch (props.variant) {
    case 'danger':
      return 'bg-rose-50 text-rose-600 ring-1 ring-rose-100'
    case 'success':
      return 'bg-emerald-50 text-emerald-600 ring-1 ring-emerald-100'
    case 'info':
      return 'bg-sky-50 text-sky-600 ring-1 ring-sky-100'
    default:
      return 'bg-amber-50 text-amber-600 ring-1 ring-amber-100'
  }
})

const close = () => {
  emit('update:modelValue', false)
}

const cancel = () => {
  emit('cancel')
  close()
}
</script>

<template>
  <VModal
    :model-value="modelValue"
    maxWidth="md"
    @update:model-value="emit('update:modelValue', $event)"
    @close="emit('cancel')"
  >
    <template #title>{{ title }}</template>

    <div class="flex gap-4">
      <div class="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl" :class="iconClasses">
        <svg v-if="variant === 'danger'" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v4m0 4h.01M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0Z" />
        </svg>
        <svg v-else-if="variant === 'success'" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M20 6 9 17l-5-5" />
        </svg>
        <svg v-else-if="variant === 'info'" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 16v-4m0-4h.01M22 12A10 10 0 1 1 2 12a10 10 0 0 1 20 0Z" />
        </svg>
        <svg v-else class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v4m0 4h.01M12 3l9 4.5v6c0 5-3.5 7.5-9 8.5-5.5-1-9-3.5-9-8.5v-6L12 3Z" />
        </svg>
      </div>

      <p class="pt-1 text-[14px] leading-6 text-slate-600">
        {{ message }}
      </p>
    </div>

    <template #footer>
      <VButton variant="secondary" @click="cancel">
        {{ cancelText }}
      </VButton>
      <VButton :variant="confirmVariant" :loading="loading" @click="emit('confirm')">
        {{ confirmText }}
      </VButton>
    </template>
  </VModal>
</template>
