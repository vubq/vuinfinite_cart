import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { i18nPublicApi } from '@/api/i18nApi'

export interface Language {
  id: number
  code: string
  name: string
  nativeName: string
  isDefault: boolean
  isActive: boolean
}

export const useI18nStore = defineStore('i18n', () => {
  const currentLocale = ref(localStorage.getItem('locale') || 'vi')
  const languages = ref<Language[]>([])
  
  // record <locale, record<namespace, record<key, value>>>
  const translations = ref<Record<string, Record<string, Record<string, string>>>>({})
  const loading = ref(false)

  const defaultLocale = computed(() => languages.value.find(l => l.isDefault)?.code || 'vi')

  async function fetchLanguages() {
    if (languages.value.length > 0) return
    try {
      const { data } = await i18nPublicApi.fetchLanguages()
      languages.value = data.data
    } catch (err) {
      console.error('Failed to fetch languages:', err)
    }
  }

  function setLocale(locale: string) {
    currentLocale.value = locale
    localStorage.setItem('locale', locale)
    // We don't clear everything, just ensure the new locale is loaded when needed
  }

  async function fetchNamespace(namespace: string, locale: string = currentLocale.value) {
    if (translations.value[locale]?.[namespace]) return
    
    loading.value = true
    try {
      const { data } = await i18nPublicApi.fetchTranslations(locale, namespace)
      if (!translations.value[locale]) translations.value[locale] = {}
      translations.value[locale][namespace] = data.data
    } catch (err) {
      console.error(`Failed to fetch translations for ${namespace} in ${locale}:`, err)
    } finally {
      loading.value = false
    }
  }

  function t(path: string, defaultValue?: string) {
    let namespace: string | null = null
    let key: string = path

    // Check if path has explicit namespace (e.g. "common:btn.save")
    if (path.includes(':')) {
      const [ns, ...rest] = path.split(':')
      namespace = ns
      key = rest.join(':')
    }

    const findInLocale = (locale: string) => {
      const localeData = translations.value[locale]
      if (!localeData) return null

      // 1. If namespace is explicit, look only there
      if (namespace) {
        return localeData[namespace]?.[key]
      }

      // 2. If no explicit namespace, check if first part of path is a namespace
      const parts = path.split('.')
      if (parts.length > 1) {
        const potentialNs = parts[0]
        const potentialKey = parts.slice(1).join('.')
        const val = localeData[potentialNs]?.[potentialKey]
        if (val) return val
      }

      // 3. Last resort: search through all loaded namespaces in this locale
      for (const ns in localeData) {
        if (localeData[ns][path]) return localeData[ns][path]
      }
      
      return null
    }

    // Try current locale
    const currentVal = findInLocale(currentLocale.value)
    if (currentVal) return currentVal

    // Try default locale
    if (currentLocale.value !== defaultLocale.value) {
      const defaultVal = findInLocale(defaultLocale.value)
      if (defaultVal) return defaultVal
    }

    return defaultValue || path
  }

  return {
    currentLocale,
    languages,
    defaultLocale,
    translations,
    loading,
    fetchLanguages,
    setLocale,
    fetchNamespace,
    t
  }
})
