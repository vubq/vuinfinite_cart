<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

interface SelectOption {
  label: string
  value: string | number | null
}

interface Props {
  modelValue: string | number | null
  options: SelectOption[]
  label?: string
  placeholder?: string
  error?: string
  disabled?: boolean
  required?: boolean
  id?: string
  size?: 'sm' | 'md'
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  required: false,
  size: 'md',
  placeholder: 'Select an option'
})

const emit = defineEmits(['update:modelValue', 'blur', 'focus'])

const isOpen = ref(false)
const rootRef = ref<HTMLElement | null>(null)
const dropdownRef = ref<HTMLElement | null>(null)
const dropdownStyle = ref<Record<string, string>>({})
const dropdownPlacement = ref<'top' | 'bottom'>('bottom')

const selectedOption = computed(() =>
  props.options.find(option => option.value === props.modelValue) ?? null
)

const displayLabel = computed(() => selectedOption.value?.label ?? props.placeholder)

const updateDropdownPosition = () => {
  if (!rootRef.value) return

  const rect = rootRef.value.getBoundingClientRect()
  const viewportHeight = window.innerHeight
  const viewportWidth = window.innerWidth
  const estimatedHeight = Math.min(320, Math.max(56, props.options.length * 42 + (props.placeholder !== '' ? 42 : 0) + 12))
  const gap = 8
  const spaceBelow = viewportHeight - rect.bottom
  const spaceAbove = rect.top
  const shouldOpenUp = spaceBelow < estimatedHeight && spaceAbove > spaceBelow

  dropdownPlacement.value = shouldOpenUp ? 'top' : 'bottom'

  const width = rect.width
  const left = Math.min(rect.left, viewportWidth - width - 12)
  const top = shouldOpenUp
    ? Math.max(12, rect.top - Math.min(estimatedHeight, spaceAbove - 12) - gap)
    : Math.min(viewportHeight - 12, rect.bottom + gap)

  dropdownStyle.value = {
    position: 'fixed',
    left: `${Math.max(12, left)}px`,
    top: `${top}px`,
    width: `${width}px`,
    maxHeight: shouldOpenUp
      ? `${Math.max(120, spaceAbove - gap - 12)}px`
      : `${Math.max(120, spaceBelow - gap - 12)}px`,
  }
}

const open = async () => {
  if (props.disabled) return
  isOpen.value = true
  await nextTick()
  updateDropdownPosition()
}

const toggleOpen = () => {
  if (props.disabled) return
  if (isOpen.value) {
    close()
    return
  }

  open()
}

const close = () => {
  isOpen.value = false
}

const selectOption = (option: SelectOption) => {
  emit('update:modelValue', option.value)
  emit('blur')
  close()
}

const clearSelection = () => {
  emit('update:modelValue', null)
  emit('blur')
  close()
}

const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as Node
  const clickedTrigger = rootRef.value?.contains(target)
  const clickedDropdown = dropdownRef.value?.contains(target)

  if (!clickedTrigger && !clickedDropdown) {
    close()
  }
}

const handleEscape = (event: KeyboardEvent) => {
  if (event.key === 'Escape') {
    close()
  }
}

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside)
  document.addEventListener('keydown', handleEscape)
  window.addEventListener('resize', updateDropdownPosition)
  window.addEventListener('scroll', updateDropdownPosition, true)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleClickOutside)
  document.removeEventListener('keydown', handleEscape)
  window.removeEventListener('resize', updateDropdownPosition)
  window.removeEventListener('scroll', updateDropdownPosition, true)
})

watch(isOpen, async (openState) => {
  if (!openState) return
  await nextTick()
  updateDropdownPosition()
})
</script>

<template>
  <div ref="rootRef" class="w-full space-y-1.5">
    <label
      v-if="label"
      :for="id"
      class="block text-sm font-medium transition-colors"
      :class="[error ? 'text-red-600' : 'text-gray-700']"
    >
      {{ label }}
      <span v-if="required" class="ml-0.5 text-red-500">*</span>
    </label>

    <div class="relative">
      <button
        :id="id"
        type="button"
        :disabled="disabled"
        @click="toggleOpen"
        @focus="emit('focus', $event)"
        class="group flex w-full items-center rounded-xl border bg-white text-left transition-all focus:outline-none focus:ring-4 disabled:cursor-not-allowed disabled:bg-gray-50 disabled:text-gray-400"
        :class="[
          size === 'sm' ? 'h-9 pl-3 pr-2' : 'h-[46px] pl-4 pr-2.5',
          error
            ? 'border-red-300 text-red-900 focus:border-red-500 focus:ring-red-500/10'
            : 'border-gray-200 text-gray-900 hover:border-gray-300 focus:border-primary focus:ring-primary/10',
          isOpen && !error ? 'border-primary ring-4 ring-primary/10' : ''
        ]"
      >
        <span
          class="min-w-0 flex-1 truncate text-sm font-semibold"
          :class="selectedOption ? 'text-gray-900' : 'text-gray-400'"
        >
          {{ displayLabel }}
        </span>

        <span
          class="ml-2 flex h-5 w-5 shrink-0 items-center justify-center transition-colors"
          :class="error ? 'text-red-500' : isOpen ? 'text-emerald-600' : 'text-slate-400 group-hover:text-slate-600'"
        >
          <svg
            class="h-3.5 w-3.5 transition-transform duration-200"
            :class="isOpen ? 'rotate-180' : ''"
            viewBox="0 0 20 20"
            fill="currentColor"
            aria-hidden="true"
          >
            <path fill-rule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clip-rule="evenodd" />
          </svg>
        </span>
      </button>

    </div>

    <Teleport to="body">
      <Transition
        enter-active-class="transition duration-150 ease-out"
        :enter-from-class="dropdownPlacement === 'top' ? 'opacity-0 translate-y-1 scale-[0.98]' : 'opacity-0 -translate-y-1 scale-[0.98]'"
        enter-to-class="opacity-100 translate-y-0 scale-100"
        leave-active-class="transition duration-100 ease-in"
        leave-from-class="opacity-100 translate-y-0 scale-100"
        :leave-to-class="dropdownPlacement === 'top' ? 'opacity-0 translate-y-1 scale-[0.98]' : 'opacity-0 -translate-y-1 scale-[0.98]'"
      >
        <div
          v-if="isOpen"
          ref="dropdownRef"
          class="z-[120] overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-[0_22px_50px_-26px_rgba(15,23,42,0.45)]"
          :style="dropdownStyle"
        >
          <div class="max-h-[inherit] overflow-y-auto py-1.5">
            <button
              v-if="placeholder !== ''"
              type="button"
              @click="clearSelection"
              class="flex w-full items-center justify-between px-3 py-2.5 text-left text-sm font-semibold transition-colors hover:bg-slate-50"
              :class="!selectedOption ? 'text-emerald-700' : 'text-slate-500'"
            >
              <span class="truncate">{{ placeholder }}</span>
              <span
                v-if="!selectedOption"
                class="rounded-full bg-emerald-50 px-2 py-0.5 text-[10px] font-black uppercase tracking-[0.14em] text-emerald-700"
              >
                Active
              </span>
            </button>

            <button
              v-for="option in options"
              :key="`${option.value}`"
              type="button"
              @click="selectOption(option)"
              class="flex w-full items-center justify-between px-3 py-2.5 text-left text-sm font-semibold transition-colors hover:bg-slate-50"
              :class="selectedOption?.value === option.value ? 'bg-emerald-50/70 text-emerald-700' : 'text-slate-700'"
            >
              <span class="truncate">{{ option.label }}</span>
              <svg
                v-if="selectedOption?.value === option.value"
                class="h-4 w-4 shrink-0 text-emerald-600"
                viewBox="0 0 20 20"
                fill="currentColor"
                aria-hidden="true"
              >
                <path fill-rule="evenodd" d="M16.704 5.29a1 1 0 010 1.42l-7.2 7.2a1 1 0 01-1.414 0l-3-3a1 1 0 111.414-1.42l2.293 2.294 6.493-6.494a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
        </div>
      </Transition>
    </Teleport>

    <p v-if="error" class="mt-1 text-xs font-medium italic text-red-600">
      {{ error }}
    </p>
  </div>
</template>
