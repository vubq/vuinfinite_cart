<script setup lang="ts">
interface Props {
  modelValue: string | number
  label?: string
  placeholder?: string
  type?: string
  error?: string
  disabled?: boolean
  required?: boolean
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  required: false
})

const emit = defineEmits(['update:modelValue', 'blur', 'focus'])

function onInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>

<template>
  <div class="w-full space-y-1.5">
    <label 
      v-if="label" 
      :for="id" 
      class="block text-sm font-medium transition-colors"
      :class="[error ? 'text-red-600' : 'text-gray-700']"
    >
      {{ label }}
      <span v-if="required" class="text-red-500 ml-0.5">*</span>
    </label>
    
    <div class="relative group">
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        @input="onInput"
        @blur="emit('blur', $event)"
        @focus="emit('focus', $event)"
        class="block w-full px-4 py-2.5 bg-white border rounded-xl text-sm transition-all focus:outline-none focus:ring-4 disabled:bg-gray-50 disabled:text-gray-400"
        :class="[
          error 
            ? 'border-red-300 text-red-900 focus:border-red-500 focus:ring-red-500/10 placeholder-red-300' 
            : 'border-gray-200 text-gray-900 focus:border-primary focus:ring-primary/10 placeholder-gray-400'
        ]"
      />
      
      <!-- Error Icon -->
      <div v-if="error" class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
        <svg class="h-5 w-5 text-red-500" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
      </div>
    </div>

    <!-- Error Message -->
    <p v-if="error" class="text-xs text-red-600 mt-1 font-medium italic">
      {{ error }}
    </p>
  </div>
</template>
