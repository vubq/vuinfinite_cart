import { useI18nStore } from '@/stores/i18n'
import { onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'

export function useI18n(namespace: string | string[] = 'common') {
  const i18nStore = useI18nStore()
  const { currentLocale, languages, loading, defaultLocale } = storeToRefs(i18nStore)
  const namespaces = Array.isArray(namespace) ? namespace : [namespace]

  const loadAll = async () => {
    // Ensure languages are loaded to know the default locale
    await i18nStore.fetchLanguages()
    
    // Load requested namespaces for current and default locale
    for (const ns of namespaces) {
      await i18nStore.fetchNamespace(ns, currentLocale.value)
      if (currentLocale.value !== defaultLocale.value) {
        await i18nStore.fetchNamespace(ns, defaultLocale.value)
      }
    }
  }

  onMounted(loadAll)
  
  // Reload if locale changes
  watch(currentLocale, loadAll)

  return {
    t: i18nStore.t,
    currentLocale,
    languages,
    setLocale: i18nStore.setLocale,
    loading
  }
}
