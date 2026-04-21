import { useI18nStore } from '@/stores/i18n'
import { onMounted } from 'vue'

export function useI18n(namespace: string = 'common') {
  const i18nStore = useI18nStore()

  // Auto-fetch namespace on mount if not loaded
  onMounted(async () => {
    await i18nStore.fetchNamespace(namespace)
  })

  return {
    t: i18nStore.t,
    currentLocale: i18nStore.currentLocale,
    setLocale: i18nStore.setLocale,
    loading: i18nStore.loading
  }
}
