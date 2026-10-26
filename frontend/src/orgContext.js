import { computed, ref } from 'vue'
import http from './api'

/** 平台 admin 在顶栏选择的机构（空=全部）；机构账号强制本机构 */
const STORAGE_KEY = 'cssd_ui_org_id'

export const orgOptions = ref([])
export const selectedOrgId = ref('')
export const orgContextReady = ref(false)

function readStaffOrgScope() {
  try {
    const s = JSON.parse(localStorage.getItem('cssd_staff') || 'null')
    if (!s) return { orgId: null, orgCode: null, isPlatform: true }
    if (String(s.staffNo || '').toLowerCase() === 'admin') {
      return { orgId: null, orgCode: null, isPlatform: true }
    }
    let orgId = s.orgId || null
    let orgCode = s.orgCode || null
    if (!orgId && !orgCode && s.staffNo) {
      const m = String(s.staffNo).match(/^cssd_(\d+)$/i)
      if (m) orgCode = m[1]
    }
    return { orgId, orgCode, isPlatform: !orgId && !orgCode }
  } catch {
    return { orgId: null, orgCode: null, isPlatform: true }
  }
}

export const isPlatformAdmin = computed(() => readStaffOrgScope().isPlatform)

/** 列表/接口用的机构 id：机构账号=本机构；平台=顶栏选择（空则不传=全部） */
export function effectiveOrgId() {
  const scope = readStaffOrgScope()
  if (!scope.isPlatform && scope.orgId) return scope.orgId
  return selectedOrgId.value || null
}

export function setSelectedOrgId(id) {
  const next = id || ''
  selectedOrgId.value = next
  if (readStaffOrgScope().isPlatform) {
    if (next) localStorage.setItem(STORAGE_KEY, next)
    else localStorage.removeItem(STORAGE_KEY)
  }
}

export async function initOrgContext() {
  const scope = readStaffOrgScope()
  try {
    const res = await http.get('/md/campuses', { params: { page: 1, size: 200 } })
    let rows = res.data?.records || []
    if (!scope.isPlatform) {
      rows = rows.filter(
        (r) =>
          (scope.orgId && r.id === scope.orgId) ||
          (scope.orgCode && String(r.campusCode) === String(scope.orgCode))
      )
    }
    orgOptions.value = rows
  } catch {
    orgOptions.value = []
  }

  if (!scope.isPlatform) {
    let id = scope.orgId
    if (!id && scope.orgCode) {
      const hit = orgOptions.value.find((r) => String(r.campusCode) === String(scope.orgCode))
      id = hit?.id || ''
    }
    if (!id && orgOptions.value.length) id = orgOptions.value[0].id
    selectedOrgId.value = id || ''
  } else {
    const saved = localStorage.getItem(STORAGE_KEY) || ''
    const ok = saved && orgOptions.value.some((o) => o.id === saved)
    selectedOrgId.value = ok ? saved : ''
  }
  orgContextReady.value = true
}
