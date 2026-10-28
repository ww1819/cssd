<template>
  <el-container class="shell">
    <el-aside :width="asideWidth" class="aside" :class="{ collapsed: asideCollapsed }">
      <div class="logo">
        <span class="logo-mark" aria-hidden="true">
          <img class="logo-img" src="/aisipute-icon.ico" alt="CSSD" width="28" height="28" />
        </span>
        <span v-show="!asideCollapsed" class="logo-text">CSSD 追溯</span>
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
            :title="asideCollapsed ? item.label : undefined"
            @click.prevent="onClickMain(item)"
          >
            <span class="nav-ico" aria-hidden="true">
              <el-icon :size="18"><component :is="item.icon" /></el-icon>
            </span>
            <span class="nav-label">{{ item.label }}</span>
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
            :key="(sub.path || '') + ':' + (sub.tab || sub.label)"
            href="javascript:;"
            class="fly-link"
            :class="{ active: isSubActive(flyMenu, sub) }"
            @click.prevent="goSub(flyMenu, sub)"
          >{{ sub.label }}</a>
        </div>
      </div>
    </Teleport>

    <el-container class="work">
      <el-header class="hdr">
        <button
          type="button"
          class="aside-toggle"
          :title="asideCollapsed ? '展开菜单' : '收起菜单'"
          @click="toggleAside"
        >
          <el-icon :size="18"><Fold v-if="!asideCollapsed" /><Expand v-else /></el-icon>
        </button>
        <div class="hdr-org-unit">
          <span class="hdr-org-label">机构单位:</span>
          <el-select
            :model-value="selectedOrgId"
            filterable
            :clearable="isPlatformAdmin"
            :disabled="!isPlatformAdmin"
            placeholder="全部机构"
            class="hdr-org-select"
            @update:model-value="onHdrOrgChange"
          >
            <el-option
              v-for="o in orgOptions"
              :key="o.id"
              :label="`${o.campusCode}|${o.campusName}`"
              :value="o.id"
            />
          </el-select>
        </div>
        <div class="hdr-spacer" />
        <div class="hdr-user">
          <el-dropdown trigger="click" placement="bottom-end" @command="onUserCommand">
            <button type="button" class="user-trigger">
              <span class="user">{{ staff?.staffName }}（{{ staff?.staffNo }} / {{ roleLabel }}）</span>
              <el-icon :size="14" class="user-caret"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu class="user-menu">
                <el-dropdown-item command="theme">
                  <el-icon><Brush /></el-icon>
                  <span>自定义主题</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-popover
            placement="bottom-end"
            :width="300"
            trigger="hover"
            :show-after="180"
            popper-class="about-popper"
            @show="loadAbout"
          >
            <template #reference>
              <button type="button" class="about-btn" aria-label="系统信息">
                <el-icon :size="16"><MoreFilled /></el-icon>
              </button>
            </template>
            <div class="about-panel">
              <div class="about-title">系统信息</div>
              <dl class="about-dl">
                <div class="about-row">
                  <dt>系统名称</dt>
                  <dd>{{ about.name }}</dd>
                </div>
                <div class="about-row">
                  <dt>系统版本</dt>
                  <dd>{{ about.version }}</dd>
                </div>
                <div class="about-row">
                  <dt>前端版本</dt>
                  <dd>{{ about.frontend }}</dd>
                </div>
                <div class="about-row">
                  <dt>后端版本</dt>
                  <dd>{{ about.backend }}</dd>
                </div>
                <div class="about-row">
                  <dt>建设阶段</dt>
                  <dd>{{ about.build }}</dd>
                </div>
                <div class="about-row">
                  <dt>服务状态</dt>
                  <dd>
                    <span class="about-status" :class="about.status === 'UP' ? 'ok' : 'bad'">
                      {{ about.status === 'UP' ? '正常' : about.status }}
                    </span>
                  </dd>
                </div>
              </dl>
            </div>
          </el-popover>
        </div>
      </el-header>

      <!-- 多页签表头（参考云会计风格） -->
      <div class="tabbar">
        <button type="button" class="tab-home" title="首页" @click="goHome">
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

    <el-dialog
      v-model="themeVisible"
      title="自定义主题"
      width="620px"
      destroy-on-close
      class="theme-dialog"
      @closed="onThemeDialogClosed"
    >
      <p class="theme-hint">选择一套主题色。带「默认」标记的是系统当前默认主题（医疗青绿）。</p>
      <div class="theme-grid">
        <button
          v-for="t in themes"
          :key="t.id"
          type="button"
          class="theme-card"
          :class="{ active: themeDraft === t.id }"
          @click="previewTheme(t.id)"
        >
          <span class="theme-swatch" :style="{ background: `linear-gradient(135deg, ${t.vars.stationFrom}, ${t.vars.stationTo})` }" />
          <span class="theme-meta">
            <span class="theme-name">
              {{ t.name }}
              <em v-if="t.isDefault" class="theme-default">默认</em>
            </span>
            <span class="theme-desc">{{ t.desc }}</span>
          </span>
          <el-icon v-if="themeDraft === t.id" class="theme-check" :size="18"><Select /></el-icon>
        </button>
      </div>
      <template #footer>
        <el-button @click="cancelTheme">取消</el-button>
        <el-button type="primary" @click="confirmTheme">应用主题</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>
<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  initOrgContext,
  isPlatformAdmin,
  orgOptions,
  selectedOrgId,
  setSelectedOrgId
} from '../orgContext'
import {
  Monitor, RefreshRight, Brush, Box, CircleCheck, Van,
  OfficeBuilding, FirstAidKit, Search, Bell, Setting,
  Share, Collection, User, HomeFilled, Close, ArrowDown,
  Fold, Expand, MoreFilled, SwitchButton, Select, FolderOpened,
  Warning, Coin, DataAnalysis, Key, Tools
} from '@element-plus/icons-vue'
import { THEMES, applyTheme, getThemeId } from '../theme'

const router = useRouter()
const route = useRoute()
const themes = THEMES

const themeVisible = ref(false)
const themeSaved = ref(getThemeId())
const themeDraft = ref(getThemeId())

function openThemeDialog() {
  themeSaved.value = getThemeId()
  themeDraft.value = themeSaved.value
  themeVisible.value = true
}
function previewTheme(id) {
  themeDraft.value = id
  applyTheme(id)
}
function confirmTheme() {
  applyTheme(themeDraft.value)
  themeSaved.value = themeDraft.value
  themeVisible.value = false
  ElMessage.success('主题已应用')
}
function cancelTheme() {
  applyTheme(themeSaved.value)
  themeDraft.value = themeSaved.value
  themeVisible.value = false
}
function onThemeDialogClosed() {
  if (getThemeId() !== themeSaved.value) {
    applyTheme(themeSaved.value)
    themeDraft.value = themeSaved.value
  }
}

const asideCollapsed = ref(localStorage.getItem('cssd_aside_collapsed') === '1')
const asideWidth = computed(() => (asideCollapsed.value ? '64px' : '228px'))
function toggleAside() {
  asideCollapsed.value = !asideCollapsed.value
  localStorage.setItem('cssd_aside_collapsed', asideCollapsed.value ? '1' : '0')
  flyKey.value = null
}
function onHdrOrgChange(id) {
  setSelectedOrgId(id)
}
onMounted(() => {
  initOrgContext()
})

const FE_VERSION = '1.0.0'
const about = ref({
  name: '消毒供应追溯系统',
  version: FE_VERSION,
  frontend: FE_VERSION,
  backend: '—',
  build: '一期',
  status: '—'
})
let aboutLoaded = false
async function loadAbout() {
  if (aboutLoaded) return
  try {
    const raw = await fetch('/api/health')
    const body = await raw.json()
    const d = body?.data || {}
    about.value = {
      name: d.name || '消毒供应追溯系统',
      version: d.version || FE_VERSION,
      frontend: FE_VERSION,
      backend: d.backend || d.version || '—',
      build: d.build || '一期',
      status: d.status || 'UP'
    }
    aboutLoaded = true
  } catch {
    about.value = { ...about.value, status: '离线', backend: '—' }
  }
}

const mdTitles = {
  org: '组织机构维护',
  campuses: '院区维护',
  warehouses: '仓库维护',
  depts: '科室维护',
  staffs: '人员档案',
  perms: '权限管理',
  vendors: '供应商维护',
  manufacturers: '生产厂家维护',
  instruments: '器械基础档案',
  templates: '器械包档案',
  devices: '设备维护',
  units: '单位维护',
  'sys-config': '系统配置'
}

const recycleTitles = {
  regular: '常规回收',
  manage: '回收管理',
  'ext-recycle': '外器回收',
  'ext-bill': '外器单据回收',
  'ext-manage': '外器管理',
  'return-reg': '退包登记',
  'return-manage': '退包管理'
}

const washTitles = {
  register: '清洗登记',
  manage: '清洗登记管理',
  'machine-end': '清洗机运行结束'
}

const tabDefaults = { '/md': 'org', '/recycle': 'regular', '/wash': 'register' }

const supplyPaths = ['/recycle', '/wash', '/pack', '/ster', '/bypass']
const deptPaths = [
  '/clinical',
  '/dept/item-apply',
  '/dept/apply-query',
  '/dept/patient',
  '/dept/instrument-track'
]

const allMenus = [
  { path: '/station', label: '首页', icon: Monitor, authId: 'station' },
  {
    path: '/supply',
    label: '供应中心',
    icon: FolderOpened,
    authId: 'supply',
    groups: [
      {
        title: '回收管理',
        icon: RefreshRight,
        authId: 'recycle',
        items: [
          { label: '常规回收', path: '/recycle', tab: 'regular', authId: 'recycle-regular' },
          { label: '回收管理', path: '/recycle', tab: 'manage', authId: 'recycle-manage' },
          { label: '外器回收', path: '/recycle', tab: 'ext-recycle', authId: 'recycle-ext-recycle' },
          { label: '外器单据回收', path: '/recycle', tab: 'ext-bill', authId: 'recycle-ext-bill' },
          { label: '外器管理', path: '/recycle', tab: 'ext-manage', authId: 'recycle-ext-manage' },
          { label: '退包登记', path: '/recycle', tab: 'return-reg', authId: 'recycle-return-reg' },
          { label: '退包管理', path: '/recycle', tab: 'return-manage', authId: 'recycle-return-manage' }
        ]
      },
      {
        title: '清洗管理',
        icon: Brush,
        authId: 'wash',
        items: [
          { label: '清洗登记', path: '/wash', tab: 'register', authId: 'wash-register' },
          { label: '清洗登记管理', path: '/wash', tab: 'manage', authId: 'wash-manage' },
          { label: '清洗机运行结束', path: '/wash', tab: 'machine-end', authId: 'wash-machine-end' }
        ]
      },
      {
        title: '包装管理',
        icon: Box,
        authId: 'pack',
        items: [{ label: '包装管理', path: '/pack', authId: 'pack' }]
      },
      {
        title: '发放管理',
        icon: CircleCheck,
        authId: 'ster',
        items: [{ label: '发放管理', path: '/ster', authId: 'ster' }]
      },
      {
        title: '无菌管理',
        icon: FirstAidKit,
        authId: 'bypass',
        items: [{ label: '无菌管理', path: '/bypass', authId: 'bypass' }]
      }
    ]
  },
  {
    path: '/dept-mgmt',
    label: '科室管理',
    icon: OfficeBuilding,
    authId: 'dept-mgmt',
    groups: [
      {
        title: '科室业务',
        icon: OfficeBuilding,
        items: [
          { label: '科室申领与使用', path: '/clinical', authId: 'clinical' },
          { label: '物品申请', path: '/dept/item-apply', authId: 'dept-item-apply' },
          { label: '查询申请', path: '/dept/apply-query', authId: 'dept-apply-query' },
          { label: '病人查询', path: '/dept/patient', authId: 'dept-patient' },
          { label: '器械追踪', path: '/dept/instrument-track', authId: 'dept-instrument-track' }
        ]
      }
    ]
  },
  { path: '/adverse', label: '不良事件处理', icon: Warning, authId: 'adverse' },
  { path: '/cost', label: '成本核算管理', icon: Coin, authId: 'cost' },
  { path: '/report', label: '报表统计管理', icon: DataAnalysis, authId: 'report' },
  { path: '/trace', label: '追溯查询', icon: Search, authId: 'trace' },
  { path: '/alerts', label: '告警与召回', icon: Bell, authId: 'alerts' },
  {
    path: '/md',
    label: '系统管理',
    icon: Setting,
    authId: 'md',
    groups: [
      {
        title: '机构管理',
        icon: Key,
        items: [
          { label: '组织机构维护', tab: 'org', authId: 'md-org' }
        ]
      },
      {
        title: '组织基础',
        icon: Share,
        items: [
          { label: '院区维护', tab: 'campuses', authId: 'md-campuses' },
          { label: '仓库维护', tab: 'warehouses', authId: 'md-warehouses' },
          { label: '科室维护', tab: 'depts', authId: 'md-depts' }
        ]
      },
      {
        title: '人员权限',
        icon: User,
        items: [
          { label: '人员档案', tab: 'staffs', authId: 'md-staffs' },
          { label: '权限管理', tab: 'perms', authId: 'md-perms' }
        ]
      },
      {
        title: '业务字典',
        icon: Collection,
        items: [
          { label: '供应商维护', tab: 'vendors', authId: 'md-vendors' },
          { label: '生产厂家维护', tab: 'manufacturers', authId: 'md-manufacturers' },
          { label: '器械基础档案', tab: 'instruments', authId: 'md-instruments' },
          { label: '器械包档案', tab: 'templates', authId: 'md-templates' },
          { label: '设备维护', tab: 'devices', authId: 'md-devices' },
          { label: '单位维护', tab: 'units', authId: 'md-units' }
        ]
      },
      {
        title: '系统配置',
        icon: Tools,
        items: [
          { label: '系统配置', tab: 'sys-config', authId: 'md-sys-config' }
        ]
      }
    ]
  }
]

/** null = 不限制；Set = 仅这些授权键可见 */
function readMenuAuthSet() {
  try {
    const raw = localStorage.getItem('cssd_menu_auth')
    if (!raw) return null
    const list = JSON.parse(raw)
    if (!Array.isArray(list)) return null
    return new Set(list)
  } catch {
    return null
  }
}

function tabKey(path, query = {}) {
  const def = tabDefaults[path]
  if (def) return `${path}?tab=${query.tab || def}`
  return path
}

/** 按登录账号绑定的菜单授权过滤侧栏；无授权数据则显示全部（演示账号） */
const menus = computed(() => {
  const set = readMenuAuthSet()
  if (!set) return allMenus
  return allMenus
    .map((item) => {
      if (!item.groups) {
        return set.has(item.authId) ? item : null
      }
      const groups = (item.groups || [])
        .map((g) => {
          const explicit = (g.items || []).filter((sub) => set.has(sub.authId))
          if (explicit.length) return { ...g, items: explicit }
          if (set.has(item.authId) || (g.authId && set.has(g.authId))) {
            return { ...g, items: g.items || [] }
          }
          return null
        })
        .filter(Boolean)
      return groups.length ? { ...item, groups } : null
    })
    .filter(Boolean)
})

const flyKey = ref(null)
const flyStyle = ref({})
let leaveTimer = null

function defaultHomeTab() {
  const list = menus.value
  const first = list[0]
  if (!first) return { key: '/station', path: '/station', query: {}, title: '首页' }
  if (first.groups?.length) {
    const sub = first.groups[0]?.items?.[0]
    if (sub) {
      const path = sub.path || first.path
      const query = sub.tab ? { tab: sub.tab } : {}
      return { key: tabKey(path, query), path, query, title: sub.label || first.label }
    }
  }
  return { key: first.path, path: first.path, query: {}, title: first.label }
}

const openTabs = ref([defaultHomeTab()])
const activeKey = ref(openTabs.value[0]?.key || '/station')

const flyMenu = computed(() => menus.value.find(m => m.path === flyKey.value && m.groups) || null)

const staff = computed(() => {
  try { return JSON.parse(localStorage.getItem('cssd_staff') || 'null') } catch { return null }
})
const roleMap = { admin: '管理员', nurse_lead: '护士长', operator: '操作员', qc: '质检员', delivery: '下送', clinical: '临床', surgeon: '术者' }
const roleLabel = computed(() => roleMap[staff.value?.role] || staff.value?.role)

const pathTitles = {
  '/station': '首页',
  '/pack': '包装管理',
  '/ster': '发放管理',
  '/dist': '入库发放',
  '/adverse': '不良事件处理',
  '/cost': '成本核算管理',
  '/report': '报表统计管理',
  '/clinical': '科室申领与使用',
  '/dept/item-apply': '物品申请',
  '/dept/apply-query': '查询申请',
  '/dept/patient': '病人查询',
  '/dept/instrument-track': '器械追踪',
  '/bypass': '无菌管理',
  '/trace': '追溯查询',
  '/alerts': '告警与召回'
}

function titleOf(path, query = {}) {
  if (path === '/md') return mdTitles[query.tab || 'org'] || '系统管理'
  if (path === '/recycle') return recycleTitles[query.tab || 'regular'] || '回收管理'
  if (path === '/wash') return washTitles[query.tab || 'register'] || '清洗管理'
  return pathTitles[path] || menus.value.find(x => x.path === path)?.label || path
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
    const def = tabDefaults[route.path]
    if (def && !route.query.tab) {
      router.replace({ path: route.path, query: { tab: def } })
      return
    }
    const q = def ? { tab: route.query.tab || def } : {}
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
    const home = defaultHomeTab()
    ensureTab(home.path, home.query)
    router.push({ path: home.path, query: home.query })
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
  if (item.path === '/supply') return supplyPaths.includes(route.path)
  if (item.path === '/dept-mgmt') return deptPaths.includes(route.path)
  if (item.groups) return route.path === item.path || route.path.startsWith(item.path + '/')
  return route.path === item.path || route.path.startsWith(item.path + '/')
}
function isSubActive(parent, sub) {
  const path = sub.path || parent?.path
  if (!path || route.path !== path) return false
  if (sub.tab) {
    const def = tabDefaults[path] || ''
    return (route.query.tab || def) === sub.tab
  }
  return true
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
    const idx = menus.value.findIndex(m => m.path === item.path)
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
    if (first) {
      const path = first.path || item.path
      const query = first.tab ? { tab: first.tab } : {}
      router.push({ path, query })
      return
    }
    if (item.path !== '/supply') router.push(item.path)
    return
  }
  router.push(item.path)
}

function goSub(parent, sub) {
  flyKey.value = null
  const path = sub.path || parent.path
  const query = sub.tab ? { tab: sub.tab } : {}
  router.push({ path, query })
}

function onUserCommand(cmd) {
  if (cmd === 'theme') openThemeDialog()
  else if (cmd === 'logout') logout()
}
function logout() {
  const keepTheme = localStorage.getItem('cssd_theme')
  const keepAside = localStorage.getItem('cssd_aside_collapsed')
  localStorage.clear()
  if (keepTheme) localStorage.setItem('cssd_theme', keepTheme)
  if (keepAside) localStorage.setItem('cssd_aside_collapsed', keepAside)
  router.push('/login')
}
</script>
<style scoped>
.shell { height: 100%; }
.work {
  min-width: 0;
  height: 100%;
  overflow: hidden;
}

.aside {
  position: relative;
  z-index: 20;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, var(--aside-from) 0%, var(--aside-mid) 45%, var(--aside-to) 100%);
  border-right: 1px solid color-mix(in srgb, var(--brand-dark) 10%, transparent);
  box-shadow: inset -1px 0 0 rgba(255, 255, 255, 0.5);
  transition: width 0.2s ease, background 0.25s ease;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 64px;
  padding: 0 16px 0 18px;
  box-sizing: border-box;
}
.aside.collapsed .logo {
  justify-content: center;
  padding: 0;
}
.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  line-height: 0;
  border-radius: 50%;
  overflow: hidden;
}
.logo-img {
  display: block;
  width: 28px;
  height: 28px;
  object-fit: cover;
}
.logo-text {
  font-family: "Segoe UI Semibold", "PingFang SC", "Microsoft YaHei", sans-serif;
  font-weight: 650;
  font-size: 17px;
  letter-spacing: 0.02em;
  line-height: 28px;
  color: var(--brand-dark);
  white-space: nowrap;
}

.nav {
  flex: 1;
  padding: 4px 10px 16px;
  overflow-y: auto;
  overflow-x: hidden;
}
.aside.collapsed .nav { padding: 4px 8px 16px; }

.nav-row { margin-bottom: 2px; }

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 42px;
  padding: 0 12px;
  border-radius: 10px;
  color: var(--ink);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  line-height: 1;
  box-sizing: border-box;
  transition: background 0.18s ease, color 0.18s ease, box-shadow 0.18s ease;
}
.aside.collapsed .nav-item {
  justify-content: center;
  padding: 0;
  gap: 0;
}

/* 固定图标槽：所有菜单图标同一竖线，文字同一起点 */
.nav-ico {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  flex-shrink: 0;
}
.nav-item :deep(.el-icon) {
  display: block;
  width: 18px;
  height: 18px;
  color: var(--muted);
  transition: color 0.18s ease;
}
.nav-item :deep(.el-icon svg) {
  display: block;
  width: 18px;
  height: 18px;
}

.nav-label {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 22px;
}
.aside.collapsed .nav-label { display: none; }

.nav-item:hover,
.nav-item.open {
  background: #fff;
  color: var(--brand);
  box-shadow: 0 4px 14px color-mix(in srgb, var(--brand-dark) 12%, transparent);
}
.nav-item:hover :deep(.el-icon),
.nav-item.open :deep(.el-icon) { color: var(--brand); }

.nav-item.active {
  background: #fff;
  color: var(--brand);
  box-shadow: 0 4px 14px color-mix(in srgb, var(--brand-dark) 12%, transparent), 0 1px 2px color-mix(in srgb, var(--brand-dark) 6%, transparent);
}
.nav-item.active :deep(.el-icon) { color: var(--brand); }

.hdr {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 52px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #e4ecee;
  padding-left: 8px;
}
.aside-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin: 0;
  padding: 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #5a7380;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.15s ease, color 0.15s ease;
}
.aside-toggle:hover {
  background: var(--brand-soft);
  color: var(--brand);
}
.hdr-org-unit {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  margin-left: 8px;
  flex-shrink: 1;
}
.hdr-org-label {
  font-size: 13px;
  color: #5a7380;
  white-space: nowrap;
}
.hdr-org-select {
  width: auto;
  max-width: min(320px, 48vw);
  min-width: 120px;
}
.hdr-org-select :deep(.el-select__wrapper) {
  min-height: 28px;
  padding: 0 4px 0 0;
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  cursor: pointer;
}
.hdr-org-select :deep(.el-select__wrapper.is-focused),
.hdr-org-select :deep(.el-select__wrapper.is-hovering:not(.is-focused)) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}
.hdr-org-select :deep(.el-select__selection) {
  flex-wrap: nowrap;
}
.hdr-org-select :deep(.el-select__selected-item),
.hdr-org-select :deep(.el-select__placeholder) {
  font-size: 13px;
  color: #3d5560;
  font-weight: 500;
}
.hdr-org-select :deep(.el-select__caret) {
  color: #5a7380;
}
.hdr-spacer { flex: 1; }
.hdr-user {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  margin-right: 8px;
}
.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin: 0;
  padding: 6px 8px 6px 10px;
  border: none;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  color: #3d5560;
  transition: background 0.15s ease, color 0.15s ease;
}
.user-trigger:hover {
  background: var(--brand-soft);
  color: var(--brand);
}
.user { font-size: 14px; }
.user-caret { color: #8aa0aa; }
.user-trigger:hover .user-caret { color: var(--brand); }

.about-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  margin: 0;
  padding: 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #8aa0aa;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}
.about-btn :deep(.el-icon) {
  transform: rotate(90deg);
}
.about-btn:hover {
  background: var(--brand-soft);
  color: var(--brand);
}

.about-panel { padding: 2px 2px 4px; }
.about-title {
  font-size: 14px;
  font-weight: 650;
  color: var(--brand-dark);
  margin-bottom: 10px;
}
.about-dl { margin: 0; }
.about-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 5px 0;
  border-bottom: 1px solid #eef3f4;
  font-size: 13px;
}
.about-row:last-child { border-bottom: none; }
.about-row dt {
  width: 72px;
  flex-shrink: 0;
  margin: 0;
  color: #8aa0aa;
  font-weight: 400;
}
.about-row dd {
  margin: 0;
  color: #3d5560;
  word-break: break-all;
}
.about-status.ok { color: var(--brand); font-weight: 600; }
.about-status.bad { color: #c45656; font-weight: 600; }

.theme-hint {
  margin: 0 0 14px;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.5;
}
.theme-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.theme-card {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  margin: 0;
  padding: 12px;
  border: 1px solid #e4ecee;
  border-radius: 12px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.theme-card:hover {
  border-color: color-mix(in srgb, var(--brand) 45%, #e4ecee);
  box-shadow: 0 4px 14px color-mix(in srgb, var(--brand-dark) 10%, transparent);
}
.theme-card.active {
  border-color: var(--brand);
  box-shadow: 0 0 0 1px var(--brand), 0 4px 14px color-mix(in srgb, var(--brand-dark) 12%, transparent);
}
.theme-swatch {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  flex-shrink: 0;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.25);
}
.theme-meta { flex: 1; min-width: 0; }
.theme-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
}
.theme-default {
  font-style: normal;
  font-size: 11px;
  font-weight: 600;
  color: var(--brand);
  background: var(--brand-soft);
  padding: 1px 6px;
  border-radius: 999px;
}
.theme-desc {
  display: block;
  margin-top: 2px;
  font-size: 12px;
  color: var(--muted);
}
.theme-check { color: var(--brand); flex-shrink: 0; }

/* 页签栏：无底线、非边框；选中页签白底贴合下方内容区 */
.tabbar {
  display: flex;
  align-items: flex-end;
  height: 36px;
  /* 比左侧菜单树更淡，拉开色差 */
  background: color-mix(in srgb, var(--brand-soft) 38%, #ffffff);
  border: none;
  border-bottom: none;
  padding: 4px 8px 0 4px;
  gap: 2px;
  box-sizing: border-box;
}
.tab-home {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 32px;
  flex-shrink: 0;
  border: none;
  background: transparent;
  color: var(--muted);
  cursor: pointer;
  border-radius: 6px 6px 0 0;
  margin: 0 2px;
}
.tab-home:hover { color: var(--brand); background: rgba(255, 255, 255, 0.55); }

.tab-scroll {
  display: flex;
  align-items: flex-end;
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
  height: 32px;
  padding: 0 14px;
  color: var(--muted);
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  user-select: none;
  border: none;
  border-radius: 6px 6px 0 0;
  background: transparent;
  box-sizing: border-box;
  transition: background 0.15s ease, color 0.15s ease;
}
.work-tab:hover { color: var(--brand); background: rgba(255, 255, 255, 0.45); }
.work-tab.active {
  background: #fff;
  color: var(--brand);
  font-weight: 600;
  box-shadow: none;
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
  background: color-mix(in srgb, var(--brand) 12%, #fff);
  color: var(--brand);
}

.main {
  position: relative;
  flex: 1;
  min-height: 0;
  overflow: hidden;
  background: #fff;
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
  max-width: 960px;
  flex-wrap: wrap;
  padding: 20px 24px 22px;
  background: #fff;
  border-radius: 12px;
  box-shadow:
    0 12px 40px color-mix(in srgb, var(--brand-dark) 16%, transparent),
    0 2px 8px color-mix(in srgb, var(--brand-dark) 8%, transparent);
  border: 1px solid color-mix(in srgb, var(--brand-dark) 8%, transparent);
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
  color: var(--brand-dark);
  font-weight: 650;
  font-size: 14px;
}
.fly-head .el-icon { color: var(--brand); }
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
  background: var(--brand-soft);
  color: var(--brand);
}
.fly-link.active {
  color: var(--brand);
  font-weight: 600;
  background: var(--brand-soft);
}

/* 账号下拉：图标 + 文案，分隔线 */
.user-menu.el-dropdown-menu {
  min-width: 188px;
  padding: 8px;
  border-radius: 12px;
  border: 1px solid color-mix(in srgb, var(--brand-dark) 8%, transparent);
  box-shadow:
    0 12px 32px color-mix(in srgb, var(--brand-dark) 14%, transparent),
    0 2px 8px color-mix(in srgb, var(--brand-dark) 6%, transparent);
}
.user-menu .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 40px;
  padding: 0 12px;
  margin: 0;
  border-radius: 8px;
  line-height: 1;
  color: #3d5560;
  font-size: 14px;
}
.user-menu .el-dropdown-menu__item .el-icon {
  margin: 0;
  font-size: 16px;
  color: #5a7380;
}
.user-menu .el-dropdown-menu__item:not(.is-disabled):hover {
  background: var(--brand-soft);
  color: var(--brand);
}
.user-menu .el-dropdown-menu__item:not(.is-disabled):hover .el-icon {
  color: var(--brand);
}
.user-menu .el-dropdown-menu__item--divided {
  margin-top: 8px;
  border-top: 1px solid #e8eef0;
}
.user-menu .el-dropdown-menu__item--divided::before {
  display: none;
}
</style>
