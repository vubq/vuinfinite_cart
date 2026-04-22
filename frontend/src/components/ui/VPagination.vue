<script setup lang="ts">
import { computed } from 'vue'
import VSelect from './VSelect.vue'

interface Props {
  currentPage: number
  totalPages: number
  totalElements: number
  pageSize: number
  pageSizeOptions?: number[]
}

const props = defineProps<Props>()
const emit = defineEmits(['update:currentPage', 'update:pageSize', 'change'])

const normalizedTotalPages = computed(() => Math.max(props.totalPages, 1))
const pageSizeOptions = computed(() => props.pageSizeOptions?.length ? props.pageSizeOptions : [10, 20, 50])

const firstItem = computed(() => {
  if (props.totalElements === 0) return 0
  return ((props.currentPage - 1) * props.pageSize) + 1
})

const lastItem = computed(() => {
  if (props.totalElements === 0) return 0
  return Math.min(props.currentPage * props.pageSize, props.totalElements)
})

const pageItems = computed<(number | string)[]>(() => {
  if (props.totalPages <= 1) return [1]

  const items: (number | string)[] = []
  const maxVisiblePages = 7
  let start = Math.max(1, props.currentPage - Math.floor(maxVisiblePages / 2))
  let end = Math.min(props.totalPages, start + maxVisiblePages - 1)

  if (end - start + 1 < maxVisiblePages) {
    start = Math.max(1, end - maxVisiblePages + 1)
  }

  if (start > 1) {
    items.push(1)
  }

  if (start > 2) {
    items.push('start-ellipsis')
  }

  for (let page = start; page <= end; page++) {
    items.push(page)
  }

  if (end < props.totalPages - 1) {
    items.push('end-ellipsis')
  }

  if (end < props.totalPages) {
    items.push(props.totalPages)
  }

  return items
})

const changePage = (page: number) => {
  if (page >= 1 && page <= props.totalPages && page !== props.currentPage) {
    emit('update:currentPage', page)
    emit('change', page)
  }
}

</script>

<template>
  <div class="border-t border-slate-200/70 px-4 py-4 sm:px-6">
    <div class="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
      <div class="flex flex-col gap-3 lg:flex-row lg:items-center lg:gap-6">
        <div>
          <p class="text-[10px] font-extrabold uppercase tracking-[0.22em] text-slate-400">
            Showing
          </p>
          <p class="mt-1 text-[13px] font-semibold text-slate-600">
            <span class="font-black text-slate-900">{{ firstItem }}</span>
            -
            <span class="font-black text-slate-900">{{ lastItem }}</span>
            of
            <span class="font-black text-emerald-700">{{ totalElements }}</span>
          </p>
        </div>

        <div class="hidden lg:block h-10 w-px bg-slate-200"></div>

        <div>
          <p class="text-[10px] font-extrabold uppercase tracking-[0.22em] text-slate-400">
            Rows Per Page
          </p>
          <div class="mt-1 min-w-[132px]">
            <VSelect
              :model-value="pageSize"
              :options="pageSizeOptions.map(option => ({ label: `${option} rows`, value: option }))"
              placeholder=""
              size="sm"
              @update:model-value="emit('update:pageSize', $event)"
            />
          </div>
        </div>

        <div class="hidden lg:block h-10 w-px bg-slate-200"></div>

        <div>
          <p class="text-[10px] font-extrabold uppercase tracking-[0.22em] text-slate-400">
            Page
          </p>
          <p class="mt-1 text-[13px] font-semibold text-slate-600">
            <span class="font-black text-slate-900">{{ currentPage }}</span>
            /
            <span class="font-black text-slate-900">{{ normalizedTotalPages }}</span>
          </p>
        </div>
      </div>

      <div class="flex items-center justify-between gap-3 sm:hidden">
        <button
          @click="changePage(currentPage - 1)"
          :disabled="currentPage <= 1"
          class="inline-flex min-w-[112px] items-center justify-center rounded-2xl border border-slate-200 bg-white px-4 py-3 text-[12px] font-bold uppercase tracking-[0.14em] text-slate-600 shadow-[0_6px_18px_-14px_rgba(15,23,42,0.35)] transition-all hover:border-slate-300 hover:text-slate-900 disabled:cursor-not-allowed disabled:opacity-40"
        >
          Previous
        </button>

        <div class="rounded-2xl border border-emerald-100 bg-emerald-50 px-4 py-3 text-[11px] font-black uppercase tracking-[0.18em] text-emerald-700">
          {{ currentPage }} / {{ normalizedTotalPages }}
        </div>

        <button
          @click="changePage(currentPage + 1)"
          :disabled="currentPage >= normalizedTotalPages"
          class="inline-flex min-w-[112px] items-center justify-center rounded-2xl border border-slate-200 bg-white px-4 py-3 text-[12px] font-bold uppercase tracking-[0.14em] text-slate-600 shadow-[0_6px_18px_-14px_rgba(15,23,42,0.35)] transition-all hover:border-slate-300 hover:text-slate-900 disabled:cursor-not-allowed disabled:opacity-40"
        >
          Next
        </button>
      </div>

      <div class="hidden sm:flex sm:items-center sm:justify-end">
        <nav class="flex items-center gap-2" aria-label="Pagination">
          <button
            @click="changePage(1)"
            :disabled="currentPage <= 1"
            class="inline-flex h-10 items-center justify-center rounded-2xl px-3 text-[10px] font-extrabold uppercase tracking-[0.18em] text-slate-400 transition-all hover:bg-slate-50 hover:text-slate-700 disabled:cursor-not-allowed disabled:opacity-30"
          >
            First
          </button>

          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage <= 1"
            class="inline-flex h-10 w-10 items-center justify-center rounded-2xl border border-transparent text-slate-400 transition-all hover:border-slate-200 hover:bg-slate-50 hover:text-slate-700 disabled:cursor-not-allowed disabled:opacity-30"
          >
            <span class="sr-only">Previous</span>
            <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true"><path fill-rule="evenodd" d="M12.79 5.23a.75.75 0 01-.02 1.06L8.832 10l3.938 3.71a.75.75 0 11-1.04 1.08l-4.5-4.25a.75.75 0 010-1.08l4.5-4.25a.75.75 0 011.06.02z" clip-rule="evenodd" /></svg>
          </button>

          <template v-for="item in pageItems" :key="item">
            <span
              v-if="typeof item !== 'number'"
              class="inline-flex h-10 min-w-[2.5rem] items-center justify-center px-2 text-[12px] font-black tracking-[0.2em] text-slate-300"
            >
              ...
            </span>

            <button
              v-else
              @click="changePage(item)"
              class="inline-flex h-10 min-w-[2.5rem] items-center justify-center rounded-2xl px-3 text-[12px] font-black transition-all"
              :class="item === currentPage
                ? 'bg-emerald-600 text-white shadow-[0_10px_20px_-12px_rgba(5,150,105,0.9)]'
                : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'"
            >
              {{ item }}
            </button>
          </template>

          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage >= normalizedTotalPages"
            class="inline-flex h-10 w-10 items-center justify-center rounded-2xl border border-transparent text-slate-400 transition-all hover:border-slate-200 hover:bg-slate-50 hover:text-slate-700 disabled:cursor-not-allowed disabled:opacity-30"
          >
            <span class="sr-only">Next</span>
            <svg class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true"><path fill-rule="evenodd" d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z" clip-rule="evenodd" /></svg>
          </button>

          <button
            @click="changePage(normalizedTotalPages)"
            :disabled="currentPage >= normalizedTotalPages"
            class="inline-flex h-10 items-center justify-center rounded-2xl px-3 text-[10px] font-extrabold uppercase tracking-[0.18em] text-slate-400 transition-all hover:bg-slate-50 hover:text-slate-700 disabled:cursor-not-allowed disabled:opacity-30"
          >
            Last
          </button>
        </nav>
      </div>
    </div>
  </div>
</template>
