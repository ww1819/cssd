<template>
  <div class="page md-page">
    <!-- 组织机构 -->
    <template v-if="active === 'org'">
      <div class="toolbar">
        <el-button size="small" @click="loadOrg">刷新</el-button>
      </div>
      <el-tree
        v-loading="orgLoading"
        :data="orgTree"
        :props="{ label: 'label', children: 'children' }"
        default-expand-all
        class="org-tree"
      />
    </template>

    <!-- 权限管理 -->
    <template v-else-if="active === 'perms'">
      <div class="toolbar">
        <span class="hint">按岗位授权一览（只读）；细粒度配置待权限模块落地</span>
      </div>
      <el-table :data="permRows" border height="520">
        <el-table-column prop="role" label="岗位" width="140" />
        <el-table-column prop="roleName" label="名称" width="120" />
        <el-table-column prop="menus" label="可见菜单" min-width="360" />
      </el-table>
    </template>

    <!-- 其它主数据表 -->
    <template v-else-if="currentTab">
      <div class="toolbar">
        <el-button size="small" @click="load(currentTab)">刷新</el-button>
        <span v-if="currentTab.hint" class="hint">{{ currentTab.hint }}</span>
      </div>
      <el-table :data="currentTab.rows" border height="520" v-loading="currentTab.loading">
        <el-table-column
          v-for="c in currentTab.cols"
          :key="c.prop"
          :prop="c.prop"
          :label="c.label"
          min-width="120"
        />
      </el-table>
    </template>
  </div>
</template>
<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import http from '../api'

const route = useRoute()
const active = computed(() => route.query.tab || 'org')
const orgLoading = ref(false)
const orgTree = ref([])

const dataTabs = reactive([
  {
    key: 'campuses',
    path: '/md/campuses',
    cols: [
      { prop: 'campusCode', label: '院区编码' },
      { prop: 'campusName', label: '院区名称' },
      { prop: 'isActive', label: '启用' }
    ],
    rows: [],
    loading: false
  },
  {
    key: 'warehouses',
    path: null,
    hint: '仓库主数据尚未建表，接口待开发',
    cols: [
      { prop: 'warehouseCode', label: '仓库编码' },
      { prop: 'warehouseName', label: '仓库名称' },
      { prop: 'campusName', label: '所属院区' },
      { prop: 'isActive', label: '启用' }
    ],
    rows: [],
    loading: false
  },
  {
    key: 'depts',
    path: '/md/depts',
    cols: [
      { prop: 'deptCode', label: '科室编码' },
      { prop: 'deptName', label: '科室名称' },
      { prop: 'deptKind', label: '类型' }
    ],
    rows: [],
    loading: false
  },
  {
    key: 'staffs',
    path: '/md/staffs',
    cols: [
      { prop: 'staffNo', label: '工号' },
      { prop: 'staffName', label: '姓名' },
      { prop: 'staffRole', label: '岗位' }
    ],
    rows: [],
    loading: false
  },
  {
    key: 'instruments',
    path: '/md/instruments',
    cols: [
      { prop: 'instrumentCode', label: '器械编码' },
      { prop: 'instrumentName', label: '器械名称' },
      { prop: 'spec', label: '规格' }
    ],
    rows: [],
    loading: false
  },
  {
    key: 'templates',
    path: '/md/templates',
    cols: [
      { prop: 'templateCode', label: '模板编码' },
      { prop: 'templateName', label: '模板名称' },
      { prop: 'packCategory', label: '类别' },
      { prop: 'needStore', label: '需入库' }
    ],
    rows: [],
    loading: false
  },
  {
    key: 'devices',
    path: '/md/devices',
    cols: [
      { prop: 'deviceCode', label: '设备编码' },
      { prop: 'deviceNo', label: '设备号' },
      { prop: 'deviceKind', label: '类型' },
      { prop: 'sterTempClass', label: '灭菌温级' }
    ],
    rows: [],
    loading: false
  }
])

const currentTab = computed(() => dataTabs.find(t => t.key === active.value) || null)

const permRows = [
  { role: 'admin', roleName: '管理员', menus: '系统管理、工位大厅、科室申领、追溯、召回、监测、报表、系统参数' },
  { role: 'nurse_lead', roleName: '护士长', menus: '系统管理、工位大厅、科室申领、追溯、召回、监测、报表、系统参数' },
  { role: 'operator', roleName: '操作员', menus: '工位大厅、追溯查询' },
  { role: 'qc', roleName: '质检员', menus: '监测、召回、追溯、报表' },
  { role: 'delivery', roleName: '下送', menus: '发放工位、追溯查询' },
  { role: 'clinical', roleName: '临床', menus: '科室申领与使用、追溯查询' },
  { role: 'surgeon', roleName: '术者', menus: '科室申领与使用、追溯查询' }
]

async function load(t) {
  if (!t?.path) {
    if (t) t.rows = []
    return
  }
  t.loading = true
  try {
    t.rows = (await http.get(t.path, { params: { size: 50 } })).data.records || []
  } finally {
    t.loading = false
  }
}

async function loadOrg() {
  orgLoading.value = true
  try {
    const [campuses, depts] = await Promise.all([
      http.get('/md/campuses', { params: { size: 200 } }),
      http.get('/md/depts', { params: { size: 500 } })
    ])
    const campusRows = campuses.data.records || []
    const deptRows = depts.data.records || []
    orgTree.value = campusRows.map(c => ({
      label: `${c.campusCode} ${c.campusName}`,
      children: deptRows
        .filter(d => d.campusId === c.id)
        .map(d => ({ label: `${d.deptCode} ${d.deptName}` }))
    }))
    const orphan = deptRows.filter(d => !d.campusId || !campusRows.some(c => c.id === d.campusId))
    if (orphan.length) {
      orgTree.value.push({
        label: '未归属院区',
        children: orphan.map(d => ({ label: `${d.deptCode} ${d.deptName}` }))
      })
    }
  } finally {
    orgLoading.value = false
  }
}

function ensureData(name) {
  if (name === 'org') loadOrg()
  else if (name === 'perms') return
  else {
    const t = dataTabs.find(x => x.key === name)
    if (t) load(t)
  }
}

watch(active, (name) => ensureData(name), { immediate: true })
onMounted(() => ensureData(active.value))
</script>
<style scoped>
.md-page { padding-top: 12px; }
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.hint { color: var(--muted, #5a7380); font-size: 13px; }
.org-tree {
  background: #fff;
  border: 1px solid #e4ecee;
  border-radius: 8px;
  padding: 12px 16px;
  min-height: 420px;
}
</style>
