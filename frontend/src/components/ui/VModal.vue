<script setup lang="ts">
import { onMounted, onUnmounted, watch } from 'vue'

interface Props {
  modelValue: boolean
  title?: string
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl'
}

const props = withDefaults(defineProps<Props>(), {
  maxWidth: 'md'
})

const emit = defineEmits(['update:modelValue', 'close'])

function close() {
  emit('update:modelValue', false)
  emit('close')
}

// Handle escape key
const handleEscape = (e: KeyboardEvent) => {
  if (e.key === 'Escape' && props.modelValue) close()
}

onMounted(() => document.addEventListener('keydown', handleEscape))
onUnmounted(() => document.removeEventListener('keydown', handleEscape))

// Handle body scroll locking
watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="modelValue" class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6" style="margin-top: 0">
        <!-- Backdrop -->
        <div class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" @click="close"></div>
        
        <!-- Modal Panel -->
        <Transition
          enter-active-class="transition duration-300 ease-out"
          enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
          enter-to-class="opacity-100 translate-y-0 sm:scale-100"
          leave-active-class="transition duration-200 ease-in"
          leave-from-class="opacity-100 translate-y-0 sm:scale-100"
          leave-to-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
        >
          <div 
            v-if="modelValue" 
            class="relative w-full bg-white dark:bg-slate-900 rounded-2xl shadow-2xl border border-gray-100 dark:border-slate-800 overflow-hidden flex flex-col max-h-[90vh]"
            :class="[
              maxWidth === 'sm' && 'max-w-sm',
              maxWidth === 'md' && 'max-w-md',
              maxWidth === 'lg' && 'max-w-lg',
              maxWidth === 'xl' && 'max-w-xl',
              maxWidth === '2xl' && 'max-w-2xl',
              maxWidth === '3xl' && 'max-w-3xl',
              maxWidth === '4xl' && 'max-w-4xl'
            ]"
          >
            <!-- Header -->
            <div class="px-6 py-5 border-b border-gray-100 dark:border-slate-800 flex items-center justify-between sticky top-0 bg-white dark:bg-slate-900 z-10">
              <h3 class="text-xl font-bold text-gray-900 dark:text-white">
                <slot name="title">{{ title }}</slot>
              </h3>
              <button 
                @click="close"
                class="text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-slate-800"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
              </button>
            </div>
            
            <!-- Body -->
            <div class="px-6 py-6 overflow-y-auto custom-scrollbar">
              <slot></slot>
            </div>
            
            <!-- Footer -->
            <div v-if="$slots.footer" class="px-6 py-4 border-t border-gray-100 dark:border-slate-800 bg-gray-50 dark:bg-[#0b1120] flex justify-end gap-3 sticky bottom-0">
              <slot name="footer"></slot>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1; /* slate-300 */
  border-radius: 20px;
}
.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #334155; /* slate-700 */
}
</style>
