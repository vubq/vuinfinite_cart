import adminApi from './adminApi'
import axios from 'axios'

const PUBLIC_BASE_URL = 'http://localhost:8080/api/i18n'

export const i18nPublicApi = {
  fetchLanguages: () => axios.get(`${PUBLIC_BASE_URL}/languages`),
  fetchTranslations: (locale: string, namespace: string) => 
    axios.get(`${PUBLIC_BASE_URL}/${locale}/${namespace}`)
}

export const i18nAdminApi = {
  // Language Management
  fetchLanguages: () => adminApi.get('/admin/i18n/languages'),
  createLanguage: (data: any) => adminApi.post('/admin/i18n/languages', data),
  updateLanguage: (id: number, data: any) => adminApi.put(`/admin/i18n/languages/${id}`, data),
  setDefaultLanguage: (id: number) => adminApi.patch(`/admin/i18n/languages/${id}/default`),
  toggleLanguageActive: (id: number) => adminApi.patch(`/admin/i18n/languages/${id}/toggle-active`),

  // Translation Management
  fetchTranslations: (params: { namespace?: string; search?: string; page?: number; size?: number }) => 
    adminApi.get('/admin/i18n/translations', { params }),
  fetchNamespaces: () => adminApi.get('/admin/i18n/namespaces'),
  upsertTranslation: (data: any) => adminApi.put('/admin/i18n/translations', data),
  deleteTranslationKey: (namespace: string, key: string) => 
    adminApi.delete(`/admin/i18n/translations/${namespace}/${key}`)
}
