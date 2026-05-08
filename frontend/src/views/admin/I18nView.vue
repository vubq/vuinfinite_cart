<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue'
import { useI18n } from '@/composables/useI18n'
import { i18nAdminApi } from '@/api/i18nApi'
import VCard from '@/components/ui/VCard.vue'
import VButton from '@/components/ui/VButton.vue'
import VInput from '@/components/ui/VInput.vue'
import VSelect from '@/components/ui/VSelect.vue'
import VPagination from '@/components/ui/VPagination.vue'
import VConfirmDialog from '@/components/ui/VConfirmDialog.vue'
import VModal from '@/components/ui/VModal.vue'

const { t } = useI18n(['admin.i18n', 'common'])
const activeTab = ref('translations')

// --- Languages Tab ---
const languages = ref<any[]>([])
const loadingLangs = ref(false)

const fetchLanguages = async () => {
  loadingLangs.value = true
  try {
    const { data } = await i18nAdminApi.fetchLanguages()
    languages.value = data.data
  } catch (err) {
    console.error('Failed to fetch languages', err)
  } finally {
    loadingLangs.value = false
  }
}

// --- Translations Tab ---
const translations = ref<any[]>([])
const namespaces = ref<string[]>([])
const selectedNamespace = ref('common')
const searchKey = ref('')
const loadingTrans = ref(false)
const page = ref(1)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(20)

// Add Language Modal
const showAddLocaleModal = ref(false)
const submitting = ref(false)
const newLocale = reactive({
  name: '',
  nativeName: '',
  code: '',
  isActive: true,
  isDefault: false
})

const openAddLocaleModal = () => {
  showAddLocaleModal.value = true
}

const submitNewLocale = async () => {
  if (!newLocale.name || !newLocale.code || !newLocale.nativeName) return
  submitting.value = true
  try {
    await i18nAdminApi.createLanguage(newLocale)
    showAddLocaleModal.value = false
    // Reset form
    Object.assign(newLocale, {
      name: '',
      nativeName: '',
      code: '',
      isActive: true,
      isDefault: false
    })
    await fetchLanguages()
  } catch (err) {
    console.error('Failed to create language', err)
  } finally {
    submitting.value = false
  }
}

const fetchNamespaces = async () => {
  try {
    const { data } = await i18nAdminApi.fetchNamespaces()
    namespaces.value = data.data
    if (namespaces.value.length > 0 && !selectedNamespace.value) {
      selectedNamespace.value = namespaces.value[0]
    }
  } catch (err) {
    console.error('Failed to fetch namespaces', err)
  }
}

const fetchTranslations = async () => {
  loadingTrans.value = true
  try {
    const params: any = {
      namespace: selectedNamespace.value,
      search: searchKey.value,
      page: page.value - 1, // backend is 0-indexed
      size: pageSize.value
    }
    const { data } = await i18nAdminApi.fetchTranslations(params)
    translations.value = data.data.items
    totalElements.value = data.data.totalElements
    totalPages.value = data.data.totalPages
  } catch (err) {
    console.error('Failed to fetch translations', err)
  } finally {
    loadingTrans.value = false
  }
}

// --- Actions ---
const showAddModal = ref(false)
const newTranslation = ref({
  namespace: '',
  key: '',
  locale: '',
  value: ''
})

const saveTranslation = async (trans: any) => {
  try {
    await i18nAdminApi.upsertTranslation(trans)
    // Optional: Toast success
  } catch (err) {
    console.error('Failed to save translation', err)
  }
}

const handlePageChange = (newPage: number) => {
  page.value = newPage
  fetchTranslations()
}

const handlePageSizeChange = () => {
  page.value = 1 // Reset to first page when changing page size
  fetchTranslations()
}

onMounted(() => {
  fetchLanguages()
  fetchNamespaces().then(fetchTranslations)
})

const groupedTranslations = computed(() => translations.value || [])
</script>

<template>
  <div class="space-y-6 animate-in fade-in duration-500">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">{{ t('header.title') }}</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium">{{ t('header.subtitle') }}</p>
      </div>
      <div class="flex gap-2 p-1 bg-slate-100/80 rounded-2xl border border-slate-200/50 backdrop-blur-sm">
        <button 
          @click="activeTab = 'translations'"
          class="px-5 py-2 text-[11px] font-extrabold uppercase tracking-widest rounded-xl transition-all"
          :class="activeTab === 'translations' ? 'bg-white text-emerald-600 shadow-sm ring-1 ring-slate-200/50' : 'text-slate-500 hover:text-slate-700'"
        >
          {{ t('tab.dictionary') }}
        </button>
        <button 
          @click="activeTab = 'languages'"
          class="px-5 py-2 text-[11px] font-extrabold uppercase tracking-widest rounded-xl transition-all"
          :class="activeTab === 'languages' ? 'bg-white text-emerald-600 shadow-sm ring-1 ring-slate-200/50' : 'text-slate-500 hover:text-slate-700'"
        >
          {{ t('tab.locales') }}
        </button>
      </div>
    </div>
    <!-- Filters and Search -->
    <div v-if="activeTab === 'translations'" class="flex items-center gap-4 bg-white/50 p-2 rounded-2xl border border-slate-200/50 backdrop-blur-sm">
        <div class="relative flex-1">
            <input 
                v-model="searchKey" 
                type="text" 
                :placeholder="t('search.ph_translations')" 
                class="w-full pl-10 pr-4 py-2 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/5 focus:border-emerald-500 transition-all placeholder:text-slate-400"
                @keyup.enter="fetchTranslations"
            />
            <svg class="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
        </div>
        <div class="w-52">
            <VSelect
                v-model="selectedNamespace"
                :options="namespaces.map(ns => ({ label: ns, value: ns }))"
                :placeholder="t('filter.namespace_ph')"
                size="sm"
                @update:modelValue="fetchTranslations"
            />
        </div>
    </div>

    <template v-if="activeTab === 'translations'">
      <VCard class="overflow-hidden border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.02)]">
        <div class="overflow-x-auto">
          <table class="w-full text-left border-collapse">
            <thead>
              <tr class="border-b border-slate-100">
                <th class="px-7 py-4 text-xs font-extrabold text-slate-500 uppercase tracking-[0.16em] w-1/3">{{ t('table.col_key') }}</th>
                <th v-for="lang in languages" :key="lang.code" class="px-6 py-4 text-xs font-extrabold text-slate-500 uppercase tracking-[0.16em]">
                  {{ lang.nativeName }}
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-50">
              <tr v-for="group in groupedTranslations" :key="group.key" class="hover:bg-slate-50/40 transition-colors group">
                <td class="px-7 py-4">
                  <div class="text-[12px] font-mono font-bold text-slate-600 bg-slate-100/80 px-2 py-1 rounded-lg inline-block border border-slate-200/50">
                    {{ group.key }}
                  </div>
                </td>
                <td v-for="lang in languages" :key="lang.code" class="px-6 py-4">
                  <textarea 
                    v-if="group.values"
                    v-model="group.values[lang.code]"
                    class="w-full bg-white border border-slate-200 rounded-xl p-3 text-[13px] font-medium text-slate-700 focus:ring-4 focus:ring-emerald-500/5 focus:border-emerald-500 transition-all resize-none min-h-[44px]"
                    rows="1"
                    @blur="saveTranslation({ namespace: group.namespace, key: group.key, locale: lang.code, value: group.values[lang.code] })"
                  ></textarea>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div>
          <VPagination 
            v-model:currentPage="page"
            v-model:pageSize="pageSize"
            :total-elements="totalElements" 
            :total-pages="totalPages"
            :page-size-options="[10, 20, 50, 100]"
            @update:currentPage="fetchTranslations"
            @update:pageSize="handlePageSizeChange"
          />
        </div>
      </VCard>
    </template>

    <template v-else>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <VCard v-for="lang in languages" :key="lang.id" class="border-slate-200/60 shadow-sm relative group">
          <div class="flex items-start justify-between">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-2xl bg-emerald-50 flex items-center justify-center text-emerald-600 font-black text-lg border border-emerald-100">
                {{ lang.code.toUpperCase() }}
              </div>
              <div>
                <h3 class="text-base font-extrabold text-slate-900 leading-tight">{{ lang.nativeName }}</h3>
                <p class="text-[12px] text-slate-500 font-medium">{{ lang.name }}</p>
              </div>
            </div>
            <div v-if="lang.default" class="px-2 py-0.5 bg-emerald-500 text-white text-[9px] font-black uppercase tracking-widest rounded-lg shadow-sm shadow-emerald-900/20">
              {{ t('card.default') }}
            </div>
          </div>

          <div class="mt-6 flex items-center justify-between pt-4 border-t border-slate-50">
            <div class="flex gap-3">
              <span class="inline-flex items-center gap-1.5 text-[11px] font-bold uppercase tracking-wider" :class="lang.active ? 'text-emerald-600' : 'text-slate-400'">
                <span class="w-1.5 h-1.5 rounded-full" :class="lang.active ? 'bg-emerald-500' : 'bg-slate-300'"></span>
                {{ lang.active ? t('card.status_active') : t('card.status_inactive') }}
              </span>
            </div>
            <div class="flex gap-2">
               <VButton v-if="!lang.default" variant="secondary" size="sm" class="!px-3 !py-1.5 !text-[10px]" @click="i18nAdminApi.setDefaultLanguage(lang.id).then(fetchLanguages)">{{ t('btn.make_default') }}</VButton>
               <VButton v-if="!lang.default" variant="secondary" size="sm" class="!px-3 !py-1.5 !text-[10px]" @click="i18nAdminApi.toggleLanguageActive(lang.id).then(fetchLanguages)">{{ t('btn.toggle') }}</VButton>
            </div>
          </div>
        </VCard>
        
        <!-- Add Language Card -->
        <button 
          @click="openAddLocaleModal"
          class="border-2 border-dashed border-slate-200 rounded-2xl p-8 flex flex-col items-center justify-center gap-3 hover:border-emerald-300 hover:bg-emerald-50/30 transition-all group"
        >
          <div class="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 group-hover:bg-emerald-100 group-hover:text-emerald-600 transition-all">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4"></path></svg>
          </div>
          <span class="text-[13px] font-bold text-slate-500 group-hover:text-emerald-700 transition-colors">{{ t('card.add_locale') }}</span>
        </button>
      </div>
    </template>

    <!-- Add Locale Modal -->
    <VModal 
      v-model="showAddLocaleModal" 
      :title="t('modal.title')" 
      max-width="md"
    >
      <div class="space-y-5">
        <div>
          <label class="block text-[11px] font-extrabold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">{{ t('modal.label_code') }}</label>
          <VInput v-model="newLocale.code" :placeholder="t('modal.ph_code')" />
        </div>
        <div>
          <label class="block text-[11px] font-extrabold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">{{ t('modal.label_native') }}</label>
          <VInput v-model="newLocale.nativeName" :placeholder="t('modal.ph_native')" />
        </div>
        <div>
          <label class="block text-[11px] font-extrabold text-slate-400 uppercase tracking-widest mb-1.5 ml-1">{{ t('modal.label_global') }}</label>
          <VInput v-model="newLocale.name" :placeholder="t('modal.ph_global')" />
        </div>
        
        <div class="flex items-center gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-100">
            <div class="flex-1">
                <div class="text-[13px] font-bold text-slate-800">{{ t('modal.set_default') }}</div>
                <div class="text-[11px] text-slate-500">{{ t('modal.set_default_desc') }}</div>
            </div>
            <input type="checkbox" v-model="newLocale.isDefault" class="w-5 h-5 rounded border-slate-300 text-emerald-600 focus:ring-emerald-500" />
        </div>

        <div class="flex items-center gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-100">
            <div class="flex-1">
                <div class="text-[13px] font-bold text-slate-800">{{ t('modal.status') }}</div>
                <div class="text-[11px] text-slate-500">{{ t('modal.status_desc') }}</div>
            </div>
            <input type="checkbox" v-model="newLocale.isActive" class="w-5 h-5 rounded border-slate-300 text-emerald-600 focus:ring-emerald-500" />
        </div>

        <div class="flex gap-3 pt-2">
          <VButton variant="secondary" class="flex-1" @click="showAddLocaleModal = false">{{ t('common:btn.cancel') }}</VButton>
          <VButton variant="primary" class="flex-1" :loading="submitting" @click="submitNewLocale">{{ t('modal.btn_create') }}</VButton>
        </div>
      </div>
    </VModal>
  </div>
</template>
