<script setup lang="ts" generic="T extends Record<string, any>">
interface Column {
  key: string
  label: string
  align?: 'left' | 'center' | 'right'
}

interface Props {
  columns: Column[]
  data: T[]
  loading?: boolean
  emptyText?: string
}

withDefaults(defineProps<Props>(), {
  loading: false,
  emptyText: 'No records found'
})
</script>

<template>
  <div class="w-full">
    <div class="overflow-x-auto rounded-tl-xl rounded-tr-xl border border-gray-100 dark:border-slate-800 border-b-0">
      <table class="w-full text-left border-collapse min-w-max">
        <thead>
          <tr class="bg-gray-50/80 dark:bg-slate-900/80 backdrop-blur-sm">
            <th 
              v-for="col in columns" 
              :key="col.key"
              class="px-6 py-4 text-xs font-bold text-gray-500 dark:text-slate-400 uppercase tracking-widest border-b border-gray-100 dark:border-slate-800 sticky top-0"
              :class="[
                col.align === 'center' && 'text-center',
                col.align === 'right' && 'text-right'
              ]"
            >
              {{ col.label }}
            </th>
          </tr>
        </thead>
        
        <tbody class="divide-y divide-gray-100 dark:divide-slate-800 bg-white dark:bg-[#020617]">
          <!-- Loading State -->
          <tr v-if="loading">
            <td :colspan="columns.length" class="px-6 py-12 text-center border-b border-gray-100 dark:border-slate-800">
              <div class="flex flex-col items-center justify-center gap-3 text-primary">
                <svg class="animate-spin h-8 w-8" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span class="text-sm font-semibold tracking-wide">Fetching data...</span>
              </div>
            </td>
          </tr>
          
          <!-- Empty State -->
          <tr v-else-if="data.length === 0">
            <td :colspan="columns.length" class="px-6 py-16 text-center border-b border-gray-100 dark:border-slate-800">
              <div class="flex flex-col items-center justify-center text-gray-400 dark:text-slate-500 space-y-3">
                <svg class="w-12 h-12 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
                <span class="text-sm font-medium">{{ emptyText }}</span>
              </div>
            </td>
          </tr>
          
          <!-- Data Rows -->
          <tr 
            v-else
            v-for="(item, index) in data" 
            :key="index"
            class="hover:bg-blue-50/30 dark:hover:bg-slate-800/40 transition-colors group"
          >
            <td 
              v-for="col in columns" 
              :key="col.key"
              class="px-6 py-4 text-sm text-gray-700 dark:text-slate-300 border-b border-gray-100 dark:border-slate-800 group-last:border-b-0"
              :class="[
                col.align === 'center' && 'text-center',
                col.align === 'right' && 'text-right'
              ]"
            >
              <slot :name="`cell(${col.key})`" :item="item" :value="item[col.key]">
                {{ item[col.key] }}
              </slot>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Footer / Pagination Slot -->
    <div v-if="$slots.pagination" class="px-6 py-4 bg-white dark:bg-[#020617] border border-gray-100 dark:border-slate-800 border-t-0 rounded-bl-xl rounded-br-xl">
      <slot name="pagination"></slot>
    </div>
  </div>
</template>
