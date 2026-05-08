<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from '@/composables/useI18n'

const { languages, currentLocale, setLocale } = useI18n()
const isOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

const toggleDropdown = () => {
  isOpen.value = !isOpen.value
}

const closeDropdown = (e: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    isOpen.value = false
  }
}

const selectLanguage = (code: string) => {
  setLocale(code)
  isOpen.value = false
  // Optional: Full page reload to ensure everything is clean, 
  // but Pinia store + watch in useI18n should handle it.
}

onMounted(() => {
  window.addEventListener('click', closeDropdown)
})

onUnmounted(() => {
  window.removeEventListener('click', closeDropdown)
})

const currentLang = ref<any>(null)
// Sync currentLang when languages load or locale changes
import { watchEffect } from 'vue'
watchEffect(() => {
  if (languages.value && Array.isArray(languages.value)) {
    currentLang.value = languages.value.find(l => l.code === currentLocale.value)
  }
})
</script>

<template>
  <div class="relative" ref="dropdownRef">
    <button 
      @click="toggleDropdown"
      class="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-slate-100 transition-colors border border-transparent hover:border-slate-200"
    >
      <span class="text-[11px] font-bold text-slate-500 uppercase tracking-widest">
        {{ currentLang?.code || currentLocale }}
      </span>
      <svg class="w-3 h-3 text-slate-400 transition-transform duration-200" :class="{ 'rotate-180': isOpen }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 9l-7 7-7-7" />
      </svg>
    </button>

    <transition
      enter-active-class="transition duration-100 ease-out"
      enter-from-class="transform scale-95 opacity-0"
      enter-to-class="transform scale-100 opacity-100"
      leave-active-class="transition duration-75 ease-in"
      leave-from-class="transform scale-100 opacity-100"
      leave-to-class="transform scale-95 opacity-0"
    >
      <div v-if="isOpen" class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-xl border border-slate-200 py-2 z-[100]">
        <div class="px-4 py-2 mb-1 border-b border-slate-50">
          <span class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Select Language</span>
        </div>
        <button
          v-for="lang in languages"
          :key="lang.code"
          @click="selectLanguage(lang.code)"
          class="w-full text-left px-4 py-2.5 text-[13px] font-semibold transition-colors flex items-center justify-between"
          :class="currentLocale === lang.code ? 'text-emerald-600 bg-emerald-50/50' : 'text-slate-600 hover:bg-slate-50'"
        >
          <span>{{ lang.nativeName }}</span>
          <span class="text-[10px] text-slate-400 uppercase font-bold">{{ lang.code }}</span>
        </button>
      </div>
    </transition>
  </div>
</template>
