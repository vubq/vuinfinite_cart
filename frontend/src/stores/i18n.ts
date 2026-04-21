import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useI18nStore = defineStore('i18n', () => {
  const currentLocale = ref(localStorage.getItem('locale') || 'vi')
  // record <namespace, record<key, value>>
  const translations = ref<Record<string, Record<string, string>>>({})
  const loading = ref(false)

  function setLocale(locale: string) {
    currentLocale.value = locale
    localStorage.setItem('locale', locale)
    // Clear translations to force reload when switching languages
    translations.value = {}
  }

  async function fetchNamespace(namespace: string) {
    if (translations.value[namespace]) return
    
    loading.value = true
    try {
      // Use direct axios for public i18n endpoint
      const { data } = await axios.get(`http://localhost:8080/api/i18n/${currentLocale.value}/${namespace}`)
      translations.value[namespace] = data.data
    } catch (err) {
      console.error(`Failed to fetch translations for ${namespace}:`, err)
    } finally {
      loading.value = false
    }
  }

  function t(path: string, defaultValue?: string) {
    const [namespace, ...keyParts] = path.split('.')
    const key = keyParts.join('.')
    
    if (translations.value[namespace] && translations.value[namespace][key]) {
      return translations.value[namespace][key]
    }
    
    return defaultValue || path
  }

  return {
    currentLocale,
    translations,
    loading,
    setLocale,
    fetchNamespace,
    t
  }
})
