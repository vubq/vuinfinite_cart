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
  const estimatedHeight = Math.min(320, Math.max(56, props.options.length * 44 + (props.placeholder !== '' ? 44 : 0) + 16))
  const gap = 6
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
  if (isOpen.value) { close(); return }
  open()
}

const close = () => { isOpen.value = false }

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
  if (!rootRef.value?.contains(target) && !dropdownRef.value?.contains(target)) {
    close()
  }
}

const handleEscape = (event: KeyboardEvent) => {
  if (event.key === 'Escape') close()
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

    <!-- Label -->
    <label
      v-if="label"
      :for="id"
      class="block text-[13px] font-bold transition-colors"
      :class="error ? 'text-red-500' : 'text-slate-700'"
    >
      {{ label }}
      <span v-if="required" class="ml-0.5 text-red-500">*</span>
    </label>

    <!-- Trigger Button -->
    <div class="relative">
      <button
        :id="id"
        type="button"
        :disabled="disabled"
        @click="toggleOpen"
        @focus="emit('focus', $event)"
        class="group flex w-full items-center rounded-xl border bg-white text-left transition-all focus:outline-none focus:ring-4 disabled:cursor-not-allowed disabled:bg-slate-50 disabled:text-slate-400"
        :class="[
          size === 'sm' ? 'h-9 pl-3 pr-2.5' : 'h-[46px] pl-4 pr-3',
          error
            ? 'border-red-300 text-red-900 focus:border-red-400 focus:ring-red-500/10'
            : isOpen
              ? 'border-emerald-400 ring-4 ring-emerald-500/10'
              : 'border-slate-200/80 hover:border-slate-300 focus:border-emerald-400 focus:ring-emerald-500/10'
        ]"
      >
        <!-- Display value -->
        <span
          class="min-w-0 flex-1 truncate text-[13px] font-semibold"
          :class="selectedOption ? 'text-slate-800' : 'text-slate-400'"
        >
          {{ displayLabel }}
        </span>

        <!-- Arrow -->
        <span
          class="ml-2 flex h-5 w-5 shrink-0 items-center justify-center rounded-lg transition-all"
          :class="isOpen ? 'text-emerald-600 bg-emerald-50' : 'text-slate-400 group-hover:text-slate-600'"
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

    <!-- Dropdown (Teleported to body) -->
    <Teleport to="body">
      <Transition
        enter-active-class="transition duration-150 ease-out"
        :enter-from-class="dropdownPlacement === 'top' ? 'opacity-0 translate-y-2 scale-[0.97]' : 'opacity-0 -translate-y-2 scale-[0.97]'"
        enter-to-class="opacity-100 translate-y-0 scale-100"
        leave-active-class="transition duration-100 ease-in"
        leave-from-class="opacity-100 translate-y-0 scale-100"
        :leave-to-class="dropdownPlacement === 'top' ? 'opacity-0 translate-y-2 scale-[0.97]' : 'opacity-0 -translate-y-2 scale-[0.97]'"
      >
        <div
          v-if="isOpen"
          ref="dropdownRef"
          class="z-[9990] overflow-hidden rounded-[1.1rem] border border-slate-200/80 bg-white shadow-[0_8px_40px_-8px_rgba(0,0,0,0.14)]"
          :style="dropdownStyle"
        >
          <div class="max-h-[inherit] overflow-y-auto py-1.5">

            <!-- Placeholder / Clear row -->
            <button
              v-if="placeholder !== ''"
              type="button"
              @click="clearSelection"
              class="flex w-full items-center gap-3 px-3 py-2.5 text-left transition-colors hover:bg-slate-50"
              :class="!selectedOption ? 'text-emerald-700' : 'text-slate-400'"
            >
              <span class="flex-1 truncate text-[13px] font-semibold">{{ placeholder }}</span>
              <span
                v-if="!selectedOption"
                class="shrink-0 rounded-full bg-emerald-50 px-2 py-0.5 text-[10px] font-black uppercase tracking-widest text-emerald-600"
              >
                Active
              </span>
            </button>

            <!-- Thin divider after placeholder -->
            <div v-if="placeholder !== '' && options.length > 0" class="mx-3 my-0.5 border-t border-slate-100" />

            <!-- Options -->
            <button
              v-for="option in options"
              :key="`${option.value}`"
              type="button"
              @click="selectOption(option)"
              class="flex w-full items-center gap-3 px-3 py-2.5 text-left transition-colors"
              :class="selectedOption?.value === option.value
                ? 'bg-emerald-50/60 text-emerald-700 hover:bg-emerald-50'
                : 'text-slate-700 hover:bg-slate-50'"
            >
              <!-- Icon dot for selected -->
              <span
                class="h-1.5 w-1.5 shrink-0 rounded-full transition-all"
                :class="selectedOption?.value === option.value ? 'bg-emerald-500' : 'bg-transparent'"
              />
              <span class="flex-1 truncate text-[13px] font-semibold">{{ option.label }}</span>
              <!-- Check icon -->
              <svg
                v-if="selectedOption?.value === option.value"
                class="h-3.5 w-3.5 shrink-0 text-emerald-500"
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

    <!-- Error message -->
    <p v-if="error" class="text-[11px] text-red-500 font-semibold italic">
      {{ error }}
    </p>

  </div>
</template>
