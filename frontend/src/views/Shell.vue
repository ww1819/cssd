<template>
  <el-container class="shell">
    <el-aside width="228px" class="aside">
      <div class="logo">
        <span class="logo-mark" aria-hidden="true">
          <svg viewBox="0 0 32 32" width="28" height="28">
            <defs>
              <linearGradient id="logoGrad" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0%" stop-color="#1aa6a0" />
                <stop offset="100%" stop-color="#0b6e7a" />
              </linearGradient>
            </defs>
            <circle cx="16" cy="16" r="15" fill="url(#logoGrad)" />
            <path d="M10 16h12M16 10v12" stroke="#fff" stroke-width="2.4" stroke-linecap="round" />
          </svg>
        </span>
        <span class="logo-text">CSSD 追溯</span>
      </div>
      <nav class="nav">
        <div
          v-for="item in menus"
          :key="item.path"
          class="nav-row"
          @mouseenter="onEnter(item)"
          @mouseleave="onLeave"
        >
          <a
            href="javascript:;"
            class="nav-item"
            :class="{ active: isActive(item), open: flyKey === item.path }"
            @click.prevent="onClickMain(item)"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </a>
        </div>
      </nav>
    </el-aside>

    <Teleport to="body">
      <div
        v-show="flyMenu"
        class="flyout"
        :style="flyStyle"
        @mouseenter="onFlyEnter"
        @mouseleave="onLeave"
      >
        <div
          v-for="group in flyMenu?.groups || []"
          :key="group.title"
          class="fly-col"
        >
          <div class="fly-head">
            <el-icon :size="16" v-if="group.icon"><component :is="group.icon" /></el-icon>
            <span>{{ group.title }}</span>
          </div>
          <a
            v-for="sub in group.items"
            :key="sub.tab || sub.path"
            href="javascript:;"
            class="fly-link"
            :class="{ active: isSubActive(sub) }"
            @click.prevent="goSub(flyMenu, sub)"
          >{{ sub.label }}</a>
        </div>
      </div>
    </Teleport>

    <el-container class="work">
      <el-header class="hdr">
        <span class="user">{{ staff?.staffName }}（{{ staff?.staffNo }} / {{ roleLabel }}）</span>
        <el-button text class="logout" @click="logout">退出</el-button>
      </el-header>

      <!-- 多页签表头（参考云会计风格） -->
      <div class="tabbar">
        <button type="button" class="tab-home" title="工位大厅" @click="goHome">
          <el-icon :size="16"><HomeFilled /></el-icon>
        </button>
        <div class="tab-scroll">
          <div
            v-for="t in openTabs"
            :key="t.key"
            class="work-tab"
            :class="{ active: t.key === activeKey }"
            @click="activateTab(t)"
          >
            <span class="work-tab-title">{{ t.title }}</span>
            <button
              v-if="t.key !== '/station'"
              type="button"
              class="work-tab-close"
              title="关闭"
              @click.stop="closeTab(t)"
            >
              <el-icon :size="12"><Close /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Monitor, RefreshRight, Brush, Box, CircleCheck, Van,
  OfficeBuilding, FirstAidKit, Search, Bell, Setting,
  Share, Collection, User, HomeFilled, Close
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const mdTitles = {
  org: '组织机构',
  campuses: '院区维护',
  warehouses: '仓库维护',
  depts: '科室维护',
  staffs: '人员档案',
  perms: '权限管理',
  instruments: '器械字典',
  templates: '包模板',
  devices: '设备'
}

const menus = [
  { path: '/station', label: '工位大厅', icon: Monitor },
  { path: '/recycle', label: '回收管理', icon: RefreshRight },
  { path: '/wash', label: '清洗管理', icon: Brush },
  { path: '/pack', label: '包装管理', icon: Box },
  { path: '/ster', label: '发放管理', icon: CircleCheck },
  { path: '/dist', label: '发放/入库', icon: Van },
  { path: '/clinical', label: '科室申领与使用', icon: OfficeBuilding },
  { path: '/bypass', label: '无菌管理', icon: FirstAidKit },
  { path: '/trace', label: '追溯查询', icon: Search },
  { path: '/alerts', label: '告警与召回', icon: Bell },
  {
    path: '/md',
    label: '系统管理',
    icon: Setting,
    groups: [
      {
        title: '组织基础',
        icon: Share,
        items: [
          { label: '组织机构', tab: 'org' },
          { label: '院区维护', tab: 'campuses' },
          { label: '仓库维护', tab: 'warehouses' },
          { label: '科室维护', tab: 'depts' }
        ]
      },
      {
        title: '人员权限',
        icon: User,
        items: [
          { label: '人员档案', tab: 'staffs' },
          { label: '权限管理', tab: 'perms' }
        ]
      },
      {
        title: '业务字典',
        icon: Collection,
        items: [
          { label: '器械字典', tab: 'instruments' },
          { label: '包模板', tab: 'templates' },
          { label: '设备', tab: 'devices' }
        ]
      }
    ]
  }
]

const flyKey = ref(null)
const flyStyle = ref({})
let leaveTimer = null

const openTabs = ref([{ key: '/station', path: '/station', query: {}, title: '工位大厅' }])
const activeKey = ref('/station')

const flyMenu = computed(() => menus.find(m => m.path === flyKey.value && m.groups) || null)

const staff = computed(() => {
  try { return JSON.parse(localStorage.getItem('cssd_staff') || 'null') } catch { return null }
})
const roleMap = { admin: '管理员', nurse_lead: '护士长', operator: '操作员', qc: '质检员', delivery: '下送', clinical: '临床', surgeon: '术者' }
const roleLabel = computed(() => roleMap[staff.value?.role] || staff.value?.role)

function tabKey(path, query = {}) {
  if (path === '/md') return `/md?tab=${query.tab || 'org'}`
  return path
}

function titleOf(path, query = {}) {
  if (path === '/md') return mdTitles[query.tab || 'org'] || '系统管理'
  const m = menus.find(x => x.path === path)
  return m?.label || path
}

function ensureTab(path, query = {}) {
  const key = tabKey(path, query)
  const title = titleOf(path, query)
  const hit = openTabs.value.find(t => t.key === key)
  if (!hit) {
    openTabs.value.push({ key, path, query: { ...query }, title })
  } else {
    hit.title = title
  }
  activeKey.value = key
}

watch(
  () => [route.path, route.query.tab],
  () => {
    if (route.path === '/login') return
    const q = route.path === '/md' ? { tab: route.query.tab || 'org' } : {}
    ensureTab(route.path, q)
  },
  { immediate: true }
)

function activateTab(t) {
  router.push({ path: t.path, query: t.query })
}
function closeTab(t) {
  const idx = openTabs.value.findIndex(x => x.key === t.key)
  if (idx < 0) return
  const wasActive = activeKey.value === t.key
  openTabs.value.splice(idx, 1)
  if (!openTabs.value.length) {
    ensureTab('/station')
    router.push('/station')
    return
  }
  if (wasActive) {
    const next = openTabs.value[Math.max(0, idx - 1)]
    activateTab(next)
  }
}
function goHome() {
  router.push('/station')
}

function isActive(item) {
  if (item.path === '/md') return route.path === '/md' || route.path.startsWith('/md/')
  return route.path === item.path || route.path.startsWith(item.path + '/')
}
function isSubActive(sub) {
  return route.path === '/md' && (route.query.tab || 'org') === sub.tab
}

function placeFlyout(el) {
  const rect = el.getBoundingClientRect()
  flyStyle.value = {
    top: `${Math.max(72, rect.top - 8)}px`,
    left: `${rect.right + 8}px`
  }
}

function onEnter(item) {
  clearTimeout(leaveTimer)
  if (!item.groups) {
    flyKey.value = null
    return
  }
  flyKey.value = item.path
  nextTick(() => {
    const rows = document.querySelectorAll('.nav-row')
    const idx = menus.findIndex(m => m.path === item.path)
    const row = rows[idx]
    if (row) placeFlyout(row)
  })
}
function onFlyEnter() {
  clearTimeout(leaveTimer)
}
function onLeave() {
  clearTimeout(leaveTimer)
  leaveTimer = setTimeout(() => { flyKey.value = null }, 160)
}

function onClickMain(item) {
  if (item.groups) {
    const first = item.groups[0]?.items?.[0]
    router.push(first ? { path: item.path, query: { tab: first.tab } } : item.path)
    return
  }
  router.push(item.path)
}

function goSub(parent, sub) {
  flyKey.value = null
  router.push({ path: parent.path, query: { tab: sub.tab } })
}

function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>
<style scoped>
.shell { height: 100%; }
.work { min-width: 0; }

.aside {
  position: relative;
  z-index: 20;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #e8f1f4 0%, #e4eef2 45%, #e9f3f1 100%);
  border-right: 1px solid rgba(15, 90, 100, 0.06);
  box-shadow: inset -1px 0 0 rgba(255, 255, 255, 0.5);
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 22px 20px 18px;
}
.logo-mark { display: flex; line-height: 0; }
.logo-text {
  font-family: "Segoe UI Semibold", "PingFang SC", "Microsoft YaHei", sans-serif;
  font-weight: 650;
  font-size: 17px;
  letter-spacing: 0.02em;
  color: #0c4a52;
}

.nav {
  flex: 1;
  padding: 4px 12px 20px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 44px;
  padding: 0 14px;
  margin-bottom: 4px;
  border-radius: 10px;
  color: #3d5560;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.18s ease, color 0.18s ease, box-shadow 0.18s ease;
}
.nav-item :deep(.el-icon) { color: #5a7380; transition: color 0.18s ease; }
.nav-item:hover,
.nav-item.open {
  background: #fff;
  color: #0b7a84;
  box-shadow: 0 4px 14px rgba(11, 90, 100, 0.10);
}
.nav-item:hover :deep(.el-icon),
.nav-item.open :deep(.el-icon) { color: #0b7a84; }

.nav-item.active {
  background: #fff;
  color: #0b7a84;
  box-shadow: 0 4px 14px rgba(11, 90, 100, 0.10), 0 1px 2px rgba(11, 90, 100, 0.04);
}
.nav-item.active :deep(.el-icon) { color: #0b7a84; }

.hdr {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 52px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #e4ecee;
}
.user { color: #3d5560; font-size: 14px; }
.logout { color: #5a7380 !important; }

.tabbar {
  display: flex;
  align-items: stretch;
  height: 38px;
  background: #e9eef1;
  border-bottom: 1px solid #dde5e8;
  padding: 0 8px 0 4px;
  gap: 2px;
}
.tab-home {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  flex-shrink: 0;
  border: none;
  background: transparent;
  color: #5a7380;
  cursor: pointer;
  border-radius: 6px;
  margin: 4px 2px;
}
.tab-home:hover { background: rgba(255, 255, 255, 0.7); color: #0b7a84; }

.tab-scroll {
  display: flex;
  align-items: stretch;
  flex: 1;
  min-width: 0;
  overflow-x: auto;
  gap: 2px;
}
.tab-scroll::-webkit-scrollbar { height: 0; }

.work-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 100%;
  padding: 0 12px;
  color: #4a6270;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  user-select: none;
  border-radius: 6px 6px 0 0;
  margin-top: 4px;
  transition: background 0.15s ease, color 0.15s ease;
}
.work-tab:hover { color: #0b7a84; background: rgba(255, 255, 255, 0.45); }
.work-tab.active {
  background: #fff;
  color: #0b7a84;
  font-weight: 600;
  box-shadow: 0 -1px 0 #fff;
}
.work-tab-close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #8aa0aa;
  cursor: pointer;
  padding: 0;
}
.work-tab-close:hover {
  background: #e4eef0;
  color: #0b7a84;
}

.main {
  background: var(--paper);
  padding: 0;
}
</style>

<style>
.flyout {
  position: fixed;
  z-index: 3000;
  display: flex;
  gap: 28px;
  min-width: 420px;
  max-width: 720px;
  padding: 20px 24px 22px;
  background: #fff;
  border-radius: 12px;
  box-shadow:
    0 12px 40px rgba(12, 60, 70, 0.14),
    0 2px 8px rgba(12, 60, 70, 0.06);
  border: 1px solid rgba(15, 90, 100, 0.06);
}
.fly-col {
  min-width: 132px;
  flex: 1;
}
.fly-head {
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 10px;
  margin-bottom: 8px;
  border-bottom: 1px solid #e8eef0;
  color: #0c4a52;
  font-weight: 650;
  font-size: 14px;
}
.fly-head .el-icon { color: #0f7a84; }
.fly-link {
  display: block;
  padding: 8px 6px;
  color: #4a6270;
  font-size: 13px;
  text-decoration: none;
  border-radius: 6px;
  transition: background 0.15s ease, color 0.15s ease;
}
.fly-link:hover {
  background: #f0f7f8;
  color: #0b7a84;
}
.fly-link.active {
  color: #0b7a84;
  font-weight: 600;
  background: #e8f4f5;
}
</style>
