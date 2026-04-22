<template>
  <div class="space-y-6 animate-in fade-in duration-500" @click="closeContextMenu">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900 tracking-tight">Media Explorer</h1>
        <p class="text-[13px] text-slate-500 mt-1 font-medium italic">Manage your Cloudinary assets with ease.</p>
      </div>
      <div class="flex gap-3">
        <!-- Selection action bar -->
        <template v-if="selectedFiles.length > 0">
          <span class="self-center text-sm font-bold text-slate-500">{{ selectedFiles.length }} selected</span>
          <VButton variant="secondary" size="md" @click="clearSelection">Deselect All</VButton>
          <VButton variant="secondary" size="md" class="!bg-red-50 !text-red-600 !border-red-100 hover:!bg-red-100" @click="confirmBulkDelete">
            Delete ({{ selectedFiles.length }})
          </VButton>
        </template>
        <template v-else>
          <VButton variant="secondary" size="md" @click="showFolderModal = true">
             New Folder
          </VButton>
          <VButton variant="primary" size="md" class="shadow-emerald-200/50 shadow-lg" @click="triggerUpload">
             Upload Files
          </VButton>
        </template>
        <input type="file" ref="fileInput" class="hidden" multiple @change="handleFileUpload" />
      </div>
    </div>

    <!-- Upload Progress Bar -->
    <div v-if="uploadProgress > 0" class="space-y-1">
        <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
            <div
                class="h-full bg-gradient-to-r from-emerald-400 to-emerald-600 rounded-full transition-all duration-300 ease-out"
                :style="{ width: uploadProgress + '%' }"
            />
        </div>
        <p class="text-[11px] text-slate-400 font-medium text-right">{{ uploadProgress }}% uploaded</p>
    </div>

    <!-- Navigation and Scope -->
    <div class="flex items-center justify-between bg-white/50 p-2 rounded-2xl border border-slate-200/50 backdrop-blur-sm">
        <div class="flex items-center gap-2 px-2">
            <button @click="navigate('/')" class="p-1.5 hover:bg-slate-100 rounded-lg transition-colors text-slate-400 hover:text-slate-600">
                <Icon icon="ph:house-bold" class="w-5 h-5" />
            </button>
            <div class="flex items-center text-sm font-bold text-slate-400">
                <template v-for="(part, index) in pathParts" :key="index">
                    <span class="mx-1">/</span>
                    <button @click="navigateToPart(index)" class="hover:text-emerald-600 transition-colors">
                        {{ part }}
                    </button>
                </template>
            </div>
        </div>
        <div class="flex bg-slate-100 p-1 rounded-xl">
            <button
                v-for="s in scopes" :key="s.value"
                @click="scope = s.value"
                class="px-4 py-1.5 text-xs font-bold rounded-lg transition-all"
                :class="scope === s.value ? 'bg-white text-emerald-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'"
            >
                {{ s.label }}
            </button>
        </div>
    </div>

    <!-- Explorer Grid -->
    <div v-if="loading" class="p-20 flex justify-center items-center">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600"></div>
    </div>

    <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
        <!-- Folders -->
        <div
            v-for="folder in folders" :key="folder"
            @click="navigate(currentPath + '/' + folder)"
            @contextmenu.prevent="openFolderContextMenu($event, folder)"
            class="group cursor-pointer select-none"
        >
            <div class="aspect-square bg-white border border-slate-200/60 rounded-[1.25rem] shadow-[0_2px_10px_-3px_rgba(0,0,0,0.04)] flex flex-col items-center justify-center gap-2.5 transition-all duration-200 group-hover:border-emerald-300/60 group-hover:shadow-[0_8px_24px_-6px_rgba(5,150,105,0.1)] group-hover:-translate-y-0.5">
                <div class="w-11 h-11 rounded-2xl bg-emerald-50 border border-emerald-100 flex items-center justify-center">
                    <Icon icon="ph:folder-duotone" class="w-6 h-6 text-emerald-600" />
                </div>
                <div class="w-full px-3">
                    <p class="text-[11px] font-bold text-slate-700 text-center truncate">{{ folder }}</p>
                    <p class="text-[10px] text-slate-400 text-center font-semibold">Folder</p>
                </div>
            </div>
        </div>

        <!-- Files -->
        <div
            v-for="file in files" :key="file.public_id"
            @contextmenu.prevent="openFileContextMenu($event, file)"
            class="group/file select-none"
        >
            <div
                @click="previewFile(file)"
                class="aspect-square bg-white border rounded-[1.25rem] overflow-hidden transition-all duration-200 cursor-pointer relative"
                :class="isSelected(file.public_id)
                    ? 'border-emerald-400 shadow-[0_4px_20px_-4px_rgba(5,150,105,0.25)] -translate-y-0.5'
                    : 'border-slate-200/60 shadow-[0_2px_10px_-3px_rgba(0,0,0,0.04)] hover:border-emerald-300/60 hover:shadow-[0_8px_24px_-6px_rgba(5,150,105,0.1)] hover:-translate-y-0.5'"
            >
                <img :src="file.secure_url" class="w-full h-full object-cover" loading="lazy" />

                <!-- Selected overlay -->
                <div v-if="isSelected(file.public_id)" class="absolute inset-0 bg-emerald-500/10 ring-2 ring-inset ring-emerald-400/30" />

                <!-- Checkbox -->
                <div
                    @click.stop="toggleSelect(file.public_id)"
                    class="absolute top-2 left-2 transition-all duration-150"
                    :class="isSelected(file.public_id) ? 'opacity-100' : 'opacity-0 group-hover/file:opacity-100'"
                >
                    <div
                        class="w-5 h-5 rounded-md flex items-center justify-center shadow-sm border transition-all"
                        :class="isSelected(file.public_id)
                            ? 'bg-emerald-500 border-emerald-500'
                            : 'bg-white/90 border-slate-200 hover:border-emerald-400'"
                    >
                        <Icon v-if="isSelected(file.public_id)" icon="ph:check-bold" class="w-3 h-3 text-white" />
                    </div>
                </div>
            </div>

            <div class="mt-2 px-1">
                <p class="text-[11px] font-bold text-slate-700 text-center truncate" :title="fileName(file.public_id)">
                    {{ fileName(file.public_id) }}
                </p>
            </div>
        </div>

        <!-- Empty State -->
        <div v-if="folders.length === 0 && files.length === 0" class="col-span-full">
            <div class="bg-white border border-dashed border-slate-200/80 rounded-[1.25rem] p-16 text-center">
                <div class="w-14 h-14 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-center mx-auto mb-4">
                    <Icon icon="ph:stack" class="w-7 h-7 text-slate-300" />
                </div>
                <p class="text-[13px] font-bold text-slate-500">This directory is empty</p>
                <p class="text-[11px] text-slate-400 mt-1">Upload files or create a new folder to get started</p>
            </div>
        </div>
    </div>

    <!-- ─── Right-Click Context Menu ──────────────────────────────── -->
    <Teleport to="body">
        <Transition name="ctx-menu">
            <div
                v-if="contextMenu.visible"
                :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }"
                class="fixed z-[9999] w-[190px] bg-white border border-slate-200/80 rounded-[1.1rem] shadow-[0_8px_40px_-8px_rgba(0,0,0,0.15)] overflow-hidden"
                @click.stop
            >
                <!-- Context label -->
                <div class="px-3.5 pt-3 pb-2 border-b border-slate-100">
                    <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                        {{ contextMenu.type === 'folder' ? '📁 ' + contextMenu.target : '🖼️ ' + fileName(contextMenu.target?.public_id || '') }}
                    </p>
                </div>

                <div class="py-1.5">
                    <!-- Folder actions -->
                    <template v-if="contextMenu.type === 'folder'">
                        <button
                            @click="openRenameFolderModal(contextMenu.target)"
                            class="ctx-btn"
                        >
                            <span class="ctx-icon bg-blue-50 text-blue-600"><Icon icon="ph:pencil-simple-bold" class="w-3.5 h-3.5" /></span>
                            Rename
                        </button>
                        <button
                            @click="confirmDeleteFolder(contextMenu.target)"
                            class="ctx-btn ctx-btn-danger"
                        >
                            <span class="ctx-icon bg-red-50 text-red-500"><Icon icon="ph:trash-bold" class="w-3.5 h-3.5" /></span>
                            Delete Folder
                        </button>
                    </template>

                    <!-- File actions -->
                    <template v-if="contextMenu.type === 'file'">
                        <button
                            @click="previewFile(contextMenu.target); closeContextMenu()"
                            class="ctx-btn"
                        >
                            <span class="ctx-icon bg-emerald-50 text-emerald-600"><Icon icon="ph:arrow-square-out-bold" class="w-3.5 h-3.5" /></span>
                            Open in new tab
                        </button>
                        <button
                            @click="toggleSelect(contextMenu.target.public_id); closeContextMenu()"
                            class="ctx-btn"
                        >
                            <span class="ctx-icon bg-slate-100 text-slate-500">
                                <Icon :icon="isSelected(contextMenu.target.public_id) ? 'ph:check-square-bold' : 'ph:square-bold'" class="w-3.5 h-3.5" />
                            </span>
                            {{ isSelected(contextMenu.target.public_id) ? 'Deselect' : 'Select' }}
                        </button>
                        <button
                            @click="selectAll(); closeContextMenu()"
                            class="ctx-btn"
                        >
                            <span class="ctx-icon bg-slate-100 text-slate-500"><Icon icon="ph:check-square-offset-bold" class="w-3.5 h-3.5" /></span>
                            Select All ({{ files.length }})
                        </button>
                        <div class="my-1 mx-3 border-t border-slate-100" />
                        <button
                            @click="openRenameModal(contextMenu.target)"
                            class="ctx-btn"
                        >
                            <span class="ctx-icon bg-blue-50 text-blue-600"><Icon icon="ph:pencil-simple-bold" class="w-3.5 h-3.5" /></span>
                            Rename
                        </button>
                        <button
                            @click="confirmDelete(contextMenu.target)"
                            class="ctx-btn ctx-btn-danger"
                        >
                            <span class="ctx-icon bg-red-50 text-red-500"><Icon icon="ph:trash-bold" class="w-3.5 h-3.5" /></span>
                            Delete
                        </button>
                    </template>
                </div>
            </div>
        </Transition>
    </Teleport>

    <!-- ─── Modals ─────────────────────────────────────────────── -->

    <!-- Create Folder Modal -->
    <VModal v-model="showFolderModal" title="New Folder" maxWidth="sm">
        <VInput v-model="newFolderName" label="Folder Name" placeholder="e.g. avatars" required />
        <template #footer>
            <VButton variant="secondary" @click="showFolderModal = false">Cancel</VButton>
            <VButton variant="primary" :loading="submitting" @click="handleCreateFolder">Create</VButton>
        </template>
    </VModal>

    <!-- Rename File Modal -->
    <VModal v-model="showRenameModal" title="Rename File" maxWidth="sm">
        <VInput v-model="newName" label="New Name" placeholder="e.g. hero-banner" required />
        <template #footer>
            <VButton variant="secondary" @click="showRenameModal = false">Cancel</VButton>
            <VButton variant="primary" :loading="submitting" @click="handleRename">Rename</VButton>
        </template>
    </VModal>

    <!-- Rename Folder Modal -->
    <VModal v-model="showRenameFolderModal" title="Rename Folder" maxWidth="sm">
        <VInput v-model="newFolderRenameName" label="New Folder Name" placeholder="e.g. banners" required />
        <template #footer>
            <VButton variant="secondary" @click="showRenameFolderModal = false">Cancel</VButton>
            <VButton variant="primary" :loading="submitting" @click="handleRenameFolder">Rename</VButton>
        </template>
    </VModal>

    <!-- Confirm Delete File(s) Dialog -->
    <VConfirmDialog
        v-model="showConfirmDialog"
        :title="bulkDelete ? 'Delete Multiple Assets' : 'Delete Asset'"
        :message="bulkDelete
            ? `Are you sure you want to delete ${selectedFiles.length} files? This action cannot be undone.`
            : 'Are you sure you want to delete this file? This action cannot be undone.'"
        confirmText="Delete"
        variant="danger"
        :loading="submitting"
        @confirm="handleDelete"
    />

    <!-- Confirm Delete Folder Dialog -->
    <VConfirmDialog
        v-model="showConfirmFolderDelete"
        title="Delete Folder"
        :message="`Are you sure you want to delete folder &quot;${folderToDelete}&quot; and ALL its contents? This action cannot be undone.`"
        confirmText="Delete"
        variant="danger"
        :loading="submitting"
        @confirm="handleDeleteFolder"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Icon } from '@iconify/vue'
import VButton from '@/components/ui/VButton.vue'
import VModal from '@/components/ui/VModal.vue'
import VInput from '@/components/ui/VInput.vue'
import VConfirmDialog from '@/components/ui/VConfirmDialog.vue'
import adminApi from '@/api/adminApi'

const scope = ref('upload')
const scopes = [
    { label: 'Public Assets', value: 'upload' },
    { label: 'Private Storage', value: 'private' }
]

const currentPath = ref('')
const folders = ref<string[]>([])
const files = ref<any[]>([])
const loading = ref(true)
const submitting = ref(false)

const pathParts = computed(() => currentPath.value.split('/').filter(p => p))

const navigate = (path: string) => {
    currentPath.value = path.replace(/\/+/g, '/')
    if (currentPath.value.startsWith('/')) currentPath.value = currentPath.value.substring(1)
    clearSelection()
    fetchData()
}

const navigateToPart = (index: number) => {
    const parts = pathParts.value.slice(0, index + 1)
    navigate('/' + parts.join('/'))
}

const fetchData = async () => {
    loading.value = true
    try {
        const [fRes, cRes] = await Promise.all([
            adminApi.get('/admin/media/folders', { params: { path: currentPath.value } }),
            adminApi.get('/admin/media/files', { params: { path: currentPath.value, type: scope.value } })
        ])
        folders.value = fRes.data.data || []
        files.value = cRes.data.data || []
    } catch (err) {
        console.error('Failed to load media', err)
    } finally {
        loading.value = false
    }
}

watch(scope, () => {
    currentPath.value = ''
    fetchData()
})

// ─── Right-Click Context Menu ─────────────────────────────────────────────────
const contextMenu = ref({
    visible: false,
    x: 0,
    y: 0,
    type: '' as 'folder' | 'file' | '',
    target: null as any
})

const openFolderContextMenu = (event: MouseEvent, folder: string) => {
    positionContextMenu(event)
    contextMenu.value.type = 'folder'
    contextMenu.value.target = folder
}

const openFileContextMenu = (event: MouseEvent, file: any) => {
    positionContextMenu(event)
    contextMenu.value.type = 'file'
    contextMenu.value.target = file
}

const positionContextMenu = (event: MouseEvent) => {
    const menuWidth = 180
    const menuHeight = 130
    const x = Math.min(event.clientX, window.innerWidth - menuWidth - 8)
    const y = Math.min(event.clientY, window.innerHeight - menuHeight - 8)
    contextMenu.value = { ...contextMenu.value, visible: true, x, y }
}

const closeContextMenu = () => {
    contextMenu.value.visible = false
}

onMounted(() => {
    fetchData()
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeContextMenu() })
    document.addEventListener('scroll', closeContextMenu, true)
})

onUnmounted(() => {
    document.removeEventListener('scroll', closeContextMenu, true)
})

// ─── Upload ───────────────────────────────────────────────────────────────────
const fileInput = ref<HTMLInputElement | null>(null)
const uploadProgress = ref(0)
const triggerUpload = () => fileInput.value?.click()

const handleFileUpload = async (event: any) => {
    const fileList = event.target.files
    if (!fileList || fileList.length === 0) return

    const formData = new FormData()
    for (let i = 0; i < fileList.length; i++) formData.append('files', fileList[i])
    formData.append('folder', currentPath.value)
    formData.append('isPrivate', (scope.value === 'private').toString())

    submitting.value = true
    uploadProgress.value = 0
    try {
        await adminApi.post('/admin/media/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            onUploadProgress: (e: any) => {
                if (e.total) uploadProgress.value = Math.round((e.loaded / e.total) * 100)
            }
        })
        await fetchData()
    } catch (err) {
        console.error('Upload failed', err)
    } finally {
        submitting.value = false
        uploadProgress.value = 0
        if (fileInput.value) fileInput.value.value = ''
    }
}

// ─── Rename File ──────────────────────────────────────────────────────────────
const showRenameModal = ref(false)
const itemToRename = ref<any>(null)
const newName = ref('')

const openRenameModal = (file: any) => {
    closeContextMenu()
    itemToRename.value = file
    newName.value = fileName(file.public_id).replace(/\.[^/.]+$/, '')
    showRenameModal.value = true
}

const handleRename = async () => {
    if (!itemToRename.value || !newName.value) return
    submitting.value = true
    try {
        const oldPublicId = itemToRename.value.public_id
        const parts = oldPublicId.split('/')
        parts[parts.length - 1] = newName.value
        await adminApi.put('/admin/media/files/rename', null, {
            params: { fromPublicId: oldPublicId, toPublicId: parts.join('/'), type: scope.value }
        })
        showRenameModal.value = false
        await fetchData()
    } catch (err) {
        console.error('Rename failed', err)
    } finally {
        submitting.value = false
    }
}

// ─── Rename Folder ────────────────────────────────────────────────────────────
const showRenameFolderModal = ref(false)
const folderToRename = ref('')
const newFolderRenameName = ref('')

const openRenameFolderModal = (folder: string) => {
    closeContextMenu()
    folderToRename.value = folder
    newFolderRenameName.value = folder
    showRenameFolderModal.value = true
}

const handleRenameFolder = async () => {
    if (!folderToRename.value || !newFolderRenameName.value) return
    submitting.value = true
    try {
        const parent = currentPath.value
        const oldPath = parent ? `${parent}/${folderToRename.value}` : folderToRename.value
        const newPath = parent ? `${parent}/${newFolderRenameName.value}` : newFolderRenameName.value
        await adminApi.put('/admin/media/folders/rename', null, {
            params: { oldPath, newPath, type: scope.value }
        })
        showRenameFolderModal.value = false
        await fetchData()
    } catch (err) {
        console.error('Rename folder failed', err)
    } finally {
        submitting.value = false
    }
}

// ─── Selection ────────────────────────────────────────────────────────────────
const selectedFiles = ref<string[]>([])
const toggleSelect = (publicId: string) => {
    const idx = selectedFiles.value.indexOf(publicId)
    if (idx === -1) selectedFiles.value.push(publicId)
    else selectedFiles.value.splice(idx, 1)
}
const isSelected = (publicId: string) => selectedFiles.value.includes(publicId)
const clearSelection = () => { selectedFiles.value = [] }
const selectAll = () => { selectedFiles.value = files.value.map((f: any) => f.public_id) }

// ─── Delete File(s) ───────────────────────────────────────────────────────────
const showConfirmDialog = ref(false)
const bulkDelete = ref(false)
const singleItemToDelete = ref<any>(null)

const confirmDelete = (item: any) => {
    closeContextMenu()
    bulkDelete.value = false
    singleItemToDelete.value = item
    showConfirmDialog.value = true
}

const confirmBulkDelete = () => {
    bulkDelete.value = true
    showConfirmDialog.value = true
}

const handleDelete = async () => {
    const publicIds = bulkDelete.value
        ? selectedFiles.value
        : [singleItemToDelete.value.public_id]
    if (publicIds.length === 0) return
    submitting.value = true
    try {
        await adminApi.delete('/admin/media/files', {
            params: { publicIds: publicIds.join(','), type: scope.value }
        })
        showConfirmDialog.value = false
        if (bulkDelete.value) clearSelection()
        await fetchData()
    } catch (err) {
        console.error('Delete failed', err)
    } finally {
        submitting.value = false
    }
}

// ─── Delete Folder ────────────────────────────────────────────────────────────
const showConfirmFolderDelete = ref(false)
const folderToDelete = ref('')

const confirmDeleteFolder = (folder: string) => {
    closeContextMenu()
    folderToDelete.value = folder
    showConfirmFolderDelete.value = true
}

const handleDeleteFolder = async () => {
    if (!folderToDelete.value) return
    submitting.value = true
    try {
        const fullPath = currentPath.value ? `${currentPath.value}/${folderToDelete.value}` : folderToDelete.value
        await adminApi.delete('/admin/media/folders', {
            params: { path: fullPath, type: scope.value }
        })
        showConfirmFolderDelete.value = false
        await fetchData()
    } catch (err) {
        console.error('Delete folder failed', err)
    } finally {
        submitting.value = false
    }
}

// ─── Create Folder ────────────────────────────────────────────────────────────
const showFolderModal = ref(false)
const newFolderName = ref('')

const handleCreateFolder = async () => {
    if (!newFolderName.value) return
    submitting.value = true
    try {
        const path = currentPath.value ? `${currentPath.value}/${newFolderName.value}` : newFolderName.value
        await adminApi.post('/admin/media/folders', null, { params: { path } })
        showFolderModal.value = false
        newFolderName.value = ''
        await fetchData()
    } catch (err) {
        console.error('Failed to create folder', err)
    } finally {
        submitting.value = false
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
const previewFile = async (file: any) => {
    if (scope.value === 'private') {
        const res = await adminApi.get('/admin/media/private-url', { params: { publicId: file.public_id } })
        window.open(res.data.data, '_blank')
    } else {
        window.open(file.secure_url, '_blank')
    }
}

const fileName = (publicId: string) => {
    const parts = publicId.split('/')
    return parts[parts.length - 1]
}
</script>

<style scoped>
/* Context menu transition */
.ctx-menu-enter-active,
.ctx-menu-leave-active {
    transition: opacity 0.12s ease, transform 0.12s ease;
}
.ctx-menu-enter-from,
.ctx-menu-leave-to {
    opacity: 0;
    transform: scale(0.96) translateY(-6px);
}

/* Context menu button rows */
.ctx-btn {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 9px;
    padding: 7px 12px;
    font-size: 12px;
    font-weight: 600;
    color: #334155;
    transition: background 0.1s;
    cursor: pointer;
    text-align: left;
    background: transparent;
    border: none;
}
.ctx-btn:hover {
    background: #f8fafc;
}
.ctx-btn-danger {
    color: #dc2626;
}
.ctx-btn-danger:hover {
    background: #fef2f2;
}

/* Icon badge in context menu */
.ctx-icon {
    width: 24px;
    height: 24px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}
</style>
