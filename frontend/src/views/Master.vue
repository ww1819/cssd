<template>
  <div class="page md-page">
    <!-- 组织机构 -->
    <template v-if="active === 'org'">
      <div ref="orgPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="orgKeyword"
            clearable
            placeholder="搜索编码 / 名称 / 拼音"
            class="toolbar-search"
            @keyup.enter="loadOrg"
            @clear="loadOrg"
          />
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openOrgCreate">新增</el-button>
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="loadOrg">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadOrg">刷新</el-button>
        </div>
        <el-table
          ref="orgTableRef"
          v-loading="orgLoading"
          :data="orgRows"
          border
          height="520"
          class="md-table"
          :row-class-name="orgRowClassName"
          @selection-change="onOrgSelectionChange"
          @row-dblclick="onOrgRowDblclick"
        >
          <el-table-column type="selection" width="48" />
          <el-table-column type="index" label="序号" width="64" :index="orgIndex" />
          <el-table-column prop="campusCode" label="机构编码" min-width="120" sortable />
          <el-table-column prop="campusName" label="机构名称" min-width="160" sortable />
          <el-table-column prop="orgCode" label="机构代码" min-width="120" sortable />
          <el-table-column prop="socialCreditCode" label="社会统一信用代码" min-width="180" />
          <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
          <el-table-column prop="contactName" label="联系人" min-width="100" />
          <el-table-column prop="contactPhone" label="联系电话" min-width="120" />
          <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
          <el-table-column label="操作" width="330" fixed="right" align="center" header-align="center" class-name="org-ops-col">
            <template #default="{ row }">
              <div class="org-ops">
                <el-button class="org-ops-btn" type="primary" plain size="small" @click="openOrgView(row)">查看</el-button>
                <el-button class="org-ops-btn" type="success" plain size="small" @click="openOrgEdit(row)">修改</el-button>
                <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteOrgRow(row)">删除</el-button>
                <el-button class="org-ops-btn" type="warning" plain size="small" @click="openOrgAuth(row)">授权</el-button>
                <el-button class="org-ops-btn" type="info" plain size="small" @click="initOrgRow(row)">初始化</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-drawer
          v-model="orgDrawerVisible"
          direction="rtl"
          size="480px"
          :modal="true"
          :show-close="false"
          :append-to="orgPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetOrgForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ orgDialogTitle }}</span>
              <el-button type="danger" size="small" @click="orgDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="orgFormRef" :model="orgForm" :rules="orgRules" label-width="140px" :disabled="orgDialogMode === 'view'">
            <el-form-item label="机构编码" prop="campusCode">
              <el-input v-model="orgForm.campusCode" maxlength="32" placeholder="如 C02" />
            </el-form-item>
            <el-form-item label="机构名称" prop="campusName">
              <el-input v-model="orgForm.campusName" maxlength="64" placeholder="如 东院区" />
            </el-form-item>
            <el-form-item label="机构代码" prop="orgCode">
              <el-input v-model="orgForm.orgCode" maxlength="64" placeholder="选填" />
            </el-form-item>
            <el-form-item label="社会统一信用代码" prop="socialCreditCode">
              <el-input v-model="orgForm.socialCreditCode" maxlength="32" placeholder="选填" />
            </el-form-item>
            <el-form-item label="地址" prop="address">
              <el-input v-model="orgForm.address" maxlength="200" placeholder="选填" />
            </el-form-item>
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="orgForm.contactName" maxlength="64" placeholder="选填" />
            </el-form-item>
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="orgForm.contactPhone" maxlength="32" placeholder="选填" />
            </el-form-item>
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="orgForm.isActive" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="orgForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
            </el-form-item>
          </el-form>
          <template v-if="orgDialogMode !== 'view'" #footer>
            <div class="drawer-footer">
              <el-button @click="orgDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="orgSaving" @click="submitOrgForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 人员档案 -->
    <template v-else-if="active === 'staffs'">
      <div ref="staffPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="staffKeyword"
            clearable
            placeholder="搜索用户姓名"
            class="toolbar-search"
            @keyup.enter="loadStaff"
            @clear="loadStaff"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="loadStaff">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadStaff">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openStaffCreate">新增</el-button>
        </div>
        <el-table
          ref="staffTableRef"
          v-loading="staffLoading"
          :data="staffRows"
          border
          height="520"
          class="md-table"
          :row-class-name="staffRowClassName"
          @selection-change="onStaffSelectionChange"
          @row-dblclick="onStaffRowDblclick"
        >
          <el-table-column type="selection" width="48" />
          <el-table-column type="index" label="序号" width="64" :index="staffIndex" />
          <el-table-column prop="staffNo" label="工号" min-width="120" sortable />
          <el-table-column prop="staffName" label="姓名" min-width="140" sortable />
          <el-table-column prop="staffRole" label="岗位" min-width="140" sortable>
            <template #default="{ row }">{{ roleLabel(row.staffRole) }}</template>
          </el-table-column>
          <el-table-column prop="isActive" label="是否启用" width="100" align="center">
            <template #default="{ row }">{{ row.isActive === 1 || row.isActive === true ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
          <el-table-column label="操作" width="200" fixed="right" align="center" header-align="center">
            <template #default="{ row }">
              <div class="org-ops">
                <el-button class="org-ops-btn" type="success" plain size="small" @click="openStaffEdit(row)">修改</el-button>
                <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteStaffRow(row)">删除</el-button>
                <el-button class="org-ops-btn" type="warning" plain size="small" @click="openStaffAuth(row)">授权</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-drawer
          v-model="staffDrawerVisible"
          direction="rtl"
          size="640px"
          :modal="true"
          :show-close="false"
          :append-to="staffPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetStaffForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ staffDialogTitle }}</span>
              <el-button type="danger" size="small" @click="staffDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="staffFormRef" class="staff-form-grid" :model="staffForm" :rules="staffRules" label-width="108px">
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="工号" prop="staffNo">
                  <el-input v-model="staffForm.staffNo" maxlength="32" placeholder="如 ADMIN" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名" prop="staffName">
                  <el-input v-model="staffForm.staffName" maxlength="64" placeholder="请输入姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="岗位" prop="staffRole">
                  <el-select v-model="staffForm.staffRole" placeholder="请选择岗位" style="width: 100%">
                    <el-option
                      v-for="p in jobPostOptions"
                      :key="p.code"
                      :label="p.name"
                      :value="p.code"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input-number
                    v-model="staffForm.age"
                    :min="1"
                    :max="120"
                    controls-position="right"
                    placeholder="选填"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学历" prop="education">
                  <el-input v-model="staffForm.education" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业" prop="major">
                  <el-input v-model="staffForm.major" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称" prop="jobTitle">
                  <el-input v-model="staffForm.jobTitle" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="能级（层级）" prop="energyLevel">
                  <el-input v-model="staffForm.energyLevel" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否启用" prop="isActive">
                  <el-switch v-model="staffForm.isActive" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="staffForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="staffDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="staffSaving" @click="submitStaffForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 机构授权 -->
    <template v-else-if="active === 'org-auth'">
      <el-empty
        :description="orgAuthHint"
      />
    </template>

    <!-- 权限管理 -->
    <template v-else-if="active === 'perms'">
      <div class="toolbar">
        <span class="hint">按岗位授权一览（只读）；细粒度配置待权限模块落地</span>
      </div>
      <el-table :data="permRows" border height="520" class="md-table">
        <el-table-column prop="role" label="岗位" width="140" />
        <el-table-column prop="roleName" label="名称" width="120" />
        <el-table-column prop="menus" label="可见菜单" min-width="360" />
      </el-table>
    </template>

    <!-- 系统配置：左树 + 顶栏筛选 + 右表明细 -->
    <template v-else-if="active === 'sys-config'">
      <div class="sys-config-page">
        <div class="sys-config-toolbar">
          <div class="sys-config-filters">
            <span class="sys-config-label">组织机构：</span>
            <el-select
              v-model="sysCampusId"
              clearable
              filterable
              placeholder="请选择"
              class="sys-config-campus"
              @change="loadSysConfigRows"
            >
              <el-option
                v-for="c in sysCampusOptions"
                :key="c.id"
                :label="`${c.campusCode} ${c.campusName}`"
                :value="c.id"
              />
            </el-select>
            <span class="sys-config-label">名称：</span>
            <el-input
              v-model="sysNameKw"
              clearable
              placeholder="按名称搜索"
              class="sys-config-name"
              @keyup.enter="loadSysConfigRows"
              @clear="loadSysConfigRows"
            />
          </div>
          <div class="sys-config-actions">
            <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="loadSysConfigRows">搜索</el-button>
            <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openSysConfigCreate">新增</el-button>
            <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="initSysConfigCategory">初始化</el-button>
            <el-button class="toolbar-btn toolbar-btn--search" :icon="Setting" @click="onSysConfigSettings">设置</el-button>
          </div>
        </div>
        <div class="sys-config-body">
          <aside class="sys-config-tree">
            <el-tree
              ref="sysTreeRef"
              :data="sysConfigTree"
              node-key="id"
              highlight-current
              default-expand-all
              :expand-on-click-node="false"
              :current-node-key="sysCategoryId"
              @node-click="onSysCategoryClick"
            >
              <template #default="{ node, data }">
                <span class="sys-tree-node">
                  <el-icon class="sys-tree-ico"><Document /></el-icon>
                  <span>{{ data.label }}</span>
                </span>
              </template>
            </el-tree>
          </aside>
          <div class="sys-config-main">
            <el-table
              v-loading="sysConfigLoading"
              :data="sysConfigRows"
              border
              height="100%"
              class="md-table sys-config-table"
            >
              <el-table-column label="操作" width="140" align="center" header-align="center" fixed="left">
                <template #default="{ row }">
                  <div class="org-ops">
                    <el-button class="org-ops-btn" type="success" plain size="small" @click="openSysConfigEdit(row)">修改</el-button>
                    <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteSysConfigRow(row)">删除</el-button>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="code" label="编号" min-width="120" sortable />
              <el-table-column prop="name" label="名称" min-width="160" sortable />
              <el-table-column prop="value1" label="值1" min-width="140" sortable show-overflow-tooltip />
              <el-table-column prop="value2" label="值2" min-width="140" sortable show-overflow-tooltip />
            </el-table>
          </div>
        </div>

        <el-drawer
          v-model="sysConfigDrawerVisible"
          direction="rtl"
          size="480px"
          :modal="true"
          :show-close="false"
          :append-to-body="false"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetSysConfigForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ sysConfigDialogTitle }}</span>
              <el-button type="danger" size="small" @click="sysConfigDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="sysConfigFormRef" :model="sysConfigForm" :rules="sysConfigRules" label-width="80px">
            <el-form-item :label="sysCategoryId === JOB_POST_CATEGORY ? '岗位编码' : '编号'" prop="code">
              <el-input
                v-model="sysConfigForm.code"
                maxlength="64"
                :placeholder="sysCategoryId === JOB_POST_CATEGORY ? '如 admin' : '参数编号'"
              />
            </el-form-item>
            <el-form-item :label="sysCategoryId === JOB_POST_CATEGORY ? '岗位名称' : '名称'" prop="name">
              <el-input
                v-model="sysConfigForm.name"
                maxlength="128"
                :placeholder="sysCategoryId === JOB_POST_CATEGORY ? '如 管理员' : '参数名称'"
              />
            </el-form-item>
            <el-form-item label="值1" prop="value1">
              <el-input v-model="sysConfigForm.value1" maxlength="256" placeholder="选填" />
            </el-form-item>
            <el-form-item label="值2" prop="value2">
              <el-input v-model="sysConfigForm.value2" maxlength="256" placeholder="选填" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="sysConfigDrawerVisible = false">取消</el-button>
              <el-button type="primary" @click="submitSysConfigForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 其它主数据表 -->
    <template v-else-if="currentTab">
      <div class="toolbar">
        <el-button size="small" @click="load(currentTab)">刷新</el-button>
        <span v-if="currentTab.hint" class="hint">{{ currentTab.hint }}</span>
      </div>
      <el-table :data="currentTab.rows" border height="520" v-loading="currentTab.loading" class="md-table">
        <el-table-column
          v-for="c in currentTab.cols"
          :key="c.prop"
          :prop="c.prop"
          :label="c.label"
          min-width="120"
        />
      </el-table>
    </template>

    <el-dialog
      v-model="authDialogVisible"
      title="请选择"
      width="520px"
      class="org-auth-dialog"
      append-to-body
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div class="auth-toolbar">
        <el-button type="primary" :icon="Select" @click="authSelectAll">全选</el-button>
        <el-button type="primary" :icon="Close" @click="authClearAll">取消</el-button>
      </div>
      <div class="auth-tree-wrap">
        <el-tree
          ref="authTreeRef"
          :data="authMenuTree"
          show-checkbox
          node-key="id"
          default-expand-all
          :props="{ label: 'label', children: 'children' }"
        >
          <template #default="{ node }">
            <span class="auth-tree-node">
              <el-icon class="auth-tree-ico"><Rank /></el-icon>
              <span>{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
      <template #footer>
        <div class="auth-footer">
          <el-button type="primary" @click="submitMenuAuth">确定</el-button>
          <el-button @click="authDialogVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Select, Close, Rank, Document, Setting } from '@element-plus/icons-vue'
import http from '../api'

const route = useRoute()
const active = computed(() => route.query.tab || 'org')
const orgAuthHint = computed(() => {
  const code = route.query.campusCode
  const name = route.query.campusName
  if (code || name) return `组织机构授权（功能待开发）：${code || ''} ${name || ''}`.trim()
  return '组织机构授权（功能待开发）'
})
const orgLoading = ref(false)
const orgRows = ref([])
const orgSelected = ref([])
const orgTableRef = ref(null)
const orgPanelRef = ref(null)
const orgKeyword = ref('')
const orgDrawerVisible = ref(false)
const authDialogVisible = ref(false)
const authTreeRef = ref(null)
/** { kind: 'campus'|'staff', id, label } */
const authTarget = ref(null)
const staffLoading = ref(false)
const staffRows = ref([])
const staffSelected = ref([])
const staffTableRef = ref(null)
const staffPanelRef = ref(null)
const staffKeyword = ref('')
const staffDrawerVisible = ref(false)
const staffDialogMode = ref('create') // create | edit
const staffSaving = ref(false)
const staffFormRef = ref(null)
const staffForm = reactive({
  id: '',
  staffNo: '',
  staffName: '',
  staffRole: '',
  age: null,
  education: '',
  major: '',
  jobTitle: '',
  energyLevel: '',
  isActive: 1,
  remark: ''
})
const staffRules = {
  staffNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  staffName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  staffRole: [{ required: true, message: '请选择岗位', trigger: 'change' }]
}
const staffDialogTitle = computed(() =>
  staffDialogMode.value === 'edit' ? '修改人员档案信息' : '新增人员档案信息'
)
/** 岗位选项：取自系统配置「基础参数 → 岗位维护」 */
const JOB_POST_CATEGORY = 'job-post'
const JOB_POST_STORAGE = 'cssd_job_posts'
const jobPostSeed = [
  { code: 'admin', name: '管理员', value1: '', value2: '' },
  { code: 'nurse_lead', name: '护士长', value1: '', value2: '' },
  { code: 'operator', name: '操作员', value1: '', value2: '' },
  { code: 'qc', name: '质检员', value1: '', value2: '' },
  { code: 'delivery', name: '下送', value1: '', value2: '' },
  { code: 'clinical', name: '临床', value1: '', value2: '' },
  { code: 'surgeon', name: '术者', value1: '', value2: '' }
]
const jobPostOptions = ref([])
function readJobPosts() {
  try {
    const raw = localStorage.getItem(JOB_POST_STORAGE)
    if (raw) {
      const list = JSON.parse(raw)
      if (Array.isArray(list) && list.length) return list
    }
  } catch { /* ignore */ }
  return jobPostSeed.map((r, i) => ({
    id: `job_${r.code}_${i}`,
    code: r.code,
    name: r.name,
    value1: r.value1,
    value2: r.value2
  }))
}
function writeJobPosts(rows) {
  localStorage.setItem(JOB_POST_STORAGE, JSON.stringify(rows))
  jobPostOptions.value = rows
}
function refreshJobPostOptions() {
  jobPostOptions.value = readJobPosts()
}
function roleLabel(role) {
  const hit = jobPostOptions.value.find((p) => p.code === role)
    || readJobPosts().find((p) => p.code === role)
  return hit?.name || role || '—'
}
function defaultStaffRole() {
  return jobPostOptions.value[0]?.code || readJobPosts()[0]?.code || ''
}
/** 与侧栏菜单对应的授权树（前端暂存，接口落地后可对接） */
const authMenuTree = [
  { id: 'station', label: '工位大厅' },
  {
    id: 'supply',
    label: '供应中心',
    children: [
      {
        id: 'recycle',
        label: '回收管理',
        children: [
          { id: 'recycle-regular', label: '常规回收' },
          { id: 'recycle-manage', label: '回收管理' },
          { id: 'recycle-ext-recycle', label: '外器回收' },
          { id: 'recycle-ext-bill', label: '外器单据回收' },
          { id: 'recycle-ext-manage', label: '外器管理' },
          { id: 'recycle-return-reg', label: '退包登记' },
          { id: 'recycle-return-manage', label: '退包管理' }
        ]
      },
      {
        id: 'wash',
        label: '清洗管理',
        children: [
          { id: 'wash-register', label: '清洗登记' },
          { id: 'wash-manage', label: '清洗登记管理' },
          { id: 'wash-machine-end', label: '清洗机运行结束' }
        ]
      },
      { id: 'pack', label: '包装管理' },
      { id: 'ster', label: '发放管理' },
      { id: 'bypass', label: '无菌管理' }
    ]
  },
  {
    id: 'dept-mgmt',
    label: '科室管理',
    children: [
      { id: 'clinical', label: '科室申领与使用' },
      { id: 'dept-item-apply', label: '物品申请' },
      { id: 'dept-apply-query', label: '查询申请' },
      { id: 'dept-patient', label: '病人查询' },
      { id: 'dept-instrument-track', label: '器械追踪' }
    ]
  },
  { id: 'adverse', label: '不良事件处理' },
  { id: 'cost', label: '成本核算管理' },
  { id: 'report', label: '报表统计管理' },
  { id: 'trace', label: '追溯查询' },
  { id: 'alerts', label: '告警与召回' },
  {
    id: 'md',
    label: '系统管理',
    children: [
      { id: 'md-org', label: '组织机构维护' },
      { id: 'md-org-auth', label: '组织机构授权' },
      { id: 'md-campuses', label: '院区维护' },
      { id: 'md-warehouses', label: '仓库维护' },
      { id: 'md-depts', label: '科室维护' },
      { id: 'md-staffs', label: '人员档案' },
      { id: 'md-perms', label: '权限管理' },
      { id: 'md-instruments', label: '器械字典' },
      { id: 'md-templates', label: '包模板' },
      { id: 'md-devices', label: '设备' }
    ]
  }
]
function collectAuthKeys(nodes, out = []) {
  for (const n of nodes || []) {
    out.push(n.id)
    if (n.children?.length) collectAuthKeys(n.children, out)
  }
  return out
}
function authStorageKey(kind, id) {
  return `cssd_${kind}_menu_auth_${id}`
}
const orgDialogMode = ref('create') // create | edit | view
const orgSaving = ref(false)
const orgFormRef = ref(null)
const orgForm = reactive({
  id: '',
  campusCode: '',
  campusName: '',
  orgCode: '',
  socialCreditCode: '',
  address: '',
  contactName: '',
  contactPhone: '',
  isActive: 1,
  remark: ''
})
const orgRules = {
  campusCode: [{ required: true, message: '请输入机构编码', trigger: 'blur' }],
  campusName: [{ required: true, message: '请输入机构名称', trigger: 'blur' }]
}
const orgDialogTitle = computed(() => {
  if (orgDialogMode.value === 'edit') return '修改组织机构信息'
  if (orgDialogMode.value === 'view') return '查看组织机构信息'
  return '新增组织机构信息'
})

function orgIndex(i) {
  return i + 1
}
function onOrgSelectionChange(rows) {
  orgSelected.value = rows
}
function onOrgRowDblclick(row) {
  const selected = orgSelected.value.some((r) => r.id === row.id)
  orgTableRef.value?.toggleRowSelection(row, !selected)
}
function orgRowClassName({ row }) {
  return orgSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}

function fillOrgForm(row) {
  orgForm.id = row?.id || ''
  orgForm.campusCode = row?.campusCode || ''
  orgForm.campusName = row?.campusName || ''
  orgForm.orgCode = row?.orgCode || ''
  orgForm.socialCreditCode = row?.socialCreditCode || ''
  orgForm.address = row?.address || ''
  orgForm.contactName = row?.contactName || ''
  orgForm.contactPhone = row?.contactPhone || ''
  orgForm.isActive = row?.isActive ?? 1
  orgForm.remark = row?.remark || ''
}
function openOrgCreate() {
  orgDialogMode.value = 'create'
  resetOrgForm()
  orgDrawerVisible.value = true
}
function openOrgEdit(row) {
  orgDialogMode.value = 'edit'
  fillOrgForm(row)
  orgDrawerVisible.value = true
}
function openOrgView(row) {
  orgDialogMode.value = 'view'
  fillOrgForm(row)
  orgDrawerVisible.value = true
}
function openMenuAuth(kind, row, label) {
  authTarget.value = { kind, id: row.id, label }
  authDialogVisible.value = true
  nextTick(() => {
    let keys = []
    try {
      keys = JSON.parse(localStorage.getItem(authStorageKey(kind, row.id)) || '[]')
    } catch {
      keys = []
    }
    authTreeRef.value?.setCheckedKeys(Array.isArray(keys) ? keys : [], false)
  })
}
function openOrgAuth(row) {
  openMenuAuth('campus', row, row.campusName || row.campusCode)
}
function openStaffAuth(row) {
  openMenuAuth('staff', row, row.staffName || row.staffNo)
}
function authSelectAll() {
  authTreeRef.value?.setCheckedKeys(collectAuthKeys(authMenuTree), false)
}
function authClearAll() {
  authTreeRef.value?.setCheckedKeys([], false)
}
function submitMenuAuth() {
  const target = authTarget.value
  if (!target) return
  const checked = authTreeRef.value?.getCheckedKeys?.(false) || []
  const half = authTreeRef.value?.getHalfCheckedKeys?.() || []
  localStorage.setItem(authStorageKey(target.kind, target.id), JSON.stringify([...checked, ...half]))
  ElMessage.success(`已保存「${target.label}」菜单授权`)
  authDialogVisible.value = false
}
function resetOrgForm() {
  fillOrgForm(null)
  orgFormRef.value?.clearValidate?.()
}
async function submitOrgForm() {
  if (orgDialogMode.value === 'view') return
  const form = orgFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    orgSaving.value = true
    try {
      const payload = {
        campusCode: orgForm.campusCode.trim(),
        campusName: orgForm.campusName.trim(),
        orgCode: orgForm.orgCode?.trim() || null,
        socialCreditCode: orgForm.socialCreditCode?.trim() || null,
        address: orgForm.address?.trim() || null,
        contactName: orgForm.contactName?.trim() || null,
        contactPhone: orgForm.contactPhone?.trim() || null,
        isActive: orgForm.isActive,
        remark: orgForm.remark?.trim() || null
      }
      if (orgDialogMode.value === 'edit') payload.id = orgForm.id
      await http.post('/md/campuses', payload)
      ElMessage.success(orgDialogMode.value === 'edit' ? '组织机构已修改' : '组织机构已新增')
      orgDrawerVisible.value = false
      await loadOrg()
    } finally {
      orgSaving.value = false
    }
  })
}
async function deleteOrgRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除组织机构「${row.campusCode} ${row.campusName}」？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  await http.delete(`/md/campuses/${row.id}`)
  ElMessage.success('已删除')
  await loadOrg()
}
async function initOrgRow(row) {
  try {
    await ElMessageBox.confirm(
      `确认初始化组织机构「${row.campusCode} ${row.campusName}」的菜单授权等配置？`,
      '初始化确认',
      {
        type: 'warning',
        confirmButtonText: '初始化',
        cancelButtonText: '取消'
      }
    )
  } catch {
    return
  }
  localStorage.removeItem(authStorageKey('campus', row.id))
  ElMessage.success('已初始化')
}
function staffIndex(i) {
  return i + 1
}
function onStaffSelectionChange(rows) {
  staffSelected.value = rows
}
function onStaffRowDblclick(row) {
  const selected = staffSelected.value.some((r) => r.id === row.id)
  staffTableRef.value?.toggleRowSelection(row, !selected)
}
function staffRowClassName({ row }) {
  return staffSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadStaff() {
  refreshJobPostOptions()
  staffLoading.value = true
  try {
    const kw = staffKeyword.value?.trim() || undefined
    const res = await http.get('/md/staffs', { params: { size: 200, keyword: kw } })
    staffRows.value = res.data.records || []
    staffSelected.value = []
    staffTableRef.value?.clearSelection?.()
  } finally {
    staffLoading.value = false
  }
}
function fillStaffForm(row) {
  staffForm.id = row?.id || ''
  staffForm.staffNo = row?.staffNo || ''
  staffForm.staffName = row?.staffName || ''
  staffForm.staffRole = row?.staffRole || defaultStaffRole()
  staffForm.age = row?.age ?? null
  staffForm.education = row?.education || ''
  staffForm.major = row?.major || ''
  staffForm.jobTitle = row?.jobTitle || ''
  staffForm.energyLevel = row?.energyLevel || ''
  staffForm.isActive = row?.isActive ?? 1
  staffForm.remark = row?.remark || ''
}
function resetStaffForm() {
  fillStaffForm(null)
  staffFormRef.value?.clearValidate?.()
}
function openStaffCreate() {
  refreshJobPostOptions()
  staffDialogMode.value = 'create'
  resetStaffForm()
  staffDrawerVisible.value = true
}
function openStaffEdit(row) {
  refreshJobPostOptions()
  staffDialogMode.value = 'edit'
  fillStaffForm(row)
  staffDrawerVisible.value = true
}
async function submitStaffForm() {
  const form = staffFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    staffSaving.value = true
    try {
      const payload = {
        staffNo: staffForm.staffNo.trim(),
        staffName: staffForm.staffName.trim(),
        staffRole: staffForm.staffRole,
        age: staffForm.age == null || staffForm.age === '' ? null : Number(staffForm.age),
        education: staffForm.education?.trim() || null,
        major: staffForm.major?.trim() || null,
        jobTitle: staffForm.jobTitle?.trim() || null,
        energyLevel: staffForm.energyLevel?.trim() || null,
        isActive: staffForm.isActive,
        remark: staffForm.remark?.trim() || null
      }
      if (staffDialogMode.value === 'edit') payload.id = staffForm.id
      await http.post('/md/staffs', payload)
      ElMessage.success(staffDialogMode.value === 'edit' ? '人员档案已修改' : '人员档案已新增')
      staffDrawerVisible.value = false
      await loadStaff()
    } finally {
      staffSaving.value = false
    }
  })
}
async function deleteStaffRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除人员「${row.staffNo} ${row.staffName}」？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  await http.delete(`/md/staffs/${row.id}`)
  localStorage.removeItem(authStorageKey('staff', row.id))
  ElMessage.success('已删除')
  await loadStaff()
}

/** 系统配置：左树分类 + 右侧参数明细（按机构本地暂存，接口落地后可对接） */
const sysCampusOptions = ref([])
const sysCampusId = ref('')
const sysNameKw = ref('')
const sysCategoryId = ref('basic')
const sysTreeRef = ref(null)
const sysConfigLoading = ref(false)
const sysConfigRows = ref([])
const sysConfigDrawerVisible = ref(false)
const sysConfigDialogMode = ref('create')
const sysConfigFormRef = ref(null)
const sysConfigForm = reactive({
  id: '',
  code: '',
  name: '',
  value1: '',
  value2: ''
})
const sysConfigRules = {
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}
const sysConfigDialogTitle = computed(() => {
  const isJob = sysCategoryId.value === JOB_POST_CATEGORY
  if (sysConfigDialogMode.value === 'edit') return isJob ? '修改岗位' : '修改系统参数'
  return isJob ? '新增岗位' : '新增系统参数'
})
const sysConfigTree = [
  {
    id: 'root',
    label: '系统参数',
    children: [
      { id: 'basic', label: '基础参数|01' },
      { id: JOB_POST_CATEGORY, label: '岗位维护' },
      { id: 'expire', label: '效期提醒|05' },
      { id: 'recycle', label: '回收参数|10' },
      { id: 'wash', label: '清洗参数|15' },
      { id: 'pack', label: '包装参数|20' },
      { id: 'ster', label: '灭菌参数|25' },
      { id: 'dist', label: '发放参数|30' },
      { id: 'print', label: '打印参数|35' },
      { id: 'trace', label: '追溯参数|40' },
      { id: 'api', label: '接口参数|45' }
    ]
  }
]
const sysConfigSeed = {
  basic: [
    { code: '01', name: '默认院区编码', value1: '', value2: '' },
    { code: '02', name: '系统标题', value1: '消毒供应追溯系统', value2: '' }
  ],
  expire: [
    { code: '01', name: '近效期天数', value1: '7', value2: '天' },
    { code: '02', name: '过期预警', value1: '1', value2: '启用' }
  ],
  recycle: [{ code: '01', name: '回收超时(分钟)', value1: '120', value2: '' }],
  wash: [{ code: '01', name: '清洗默认程序', value1: '标准', value2: '' }],
  pack: [{ code: '01', name: '包装核对双人', value1: '1', value2: '是' }],
  ster: [{ code: '01', name: '灭菌温级默认', value1: '高温', value2: '' }],
  dist: [{ code: '01', name: '发放签收必填', value1: '1', value2: '是' }],
  print: [{ code: '01', name: '标签打印机', value1: '', value2: '' }],
  trace: [{ code: '01', name: '追溯保留月数', value1: '36', value2: '' }],
  api: [{ code: '01', name: 'HIS 同步开关', value1: '0', value2: '关闭' }]
}
function sysConfigStorageKey(campusId, categoryId) {
  return `cssd_sys_config_${campusId || 'default'}_${categoryId || 'basic'}`
}
function readSysConfigStore(campusId, categoryId) {
  if (categoryId === JOB_POST_CATEGORY) return readJobPosts()
  try {
    const raw = localStorage.getItem(sysConfigStorageKey(campusId, categoryId))
    if (raw) {
      const list = JSON.parse(raw)
      if (Array.isArray(list)) return list
    }
  } catch { /* ignore */ }
  const seed = sysConfigSeed[categoryId] || []
  return seed.map((r, i) => ({
    id: `${categoryId}_${r.code}_${i}`,
    code: r.code,
    name: r.name,
    value1: r.value1,
    value2: r.value2
  }))
}
function writeSysConfigStore(campusId, categoryId, rows) {
  if (categoryId === JOB_POST_CATEGORY) {
    writeJobPosts(rows)
    return
  }
  localStorage.setItem(sysConfigStorageKey(campusId, categoryId), JSON.stringify(rows))
}
async function loadSysConfigPage() {
  refreshJobPostOptions()
  try {
    const res = await http.get('/md/campuses', { params: { size: 200 } })
    sysCampusOptions.value = res.data.records || []
    if (!sysCampusId.value && sysCampusOptions.value.length) {
      sysCampusId.value = sysCampusOptions.value[0].id
    }
  } catch {
    sysCampusOptions.value = []
  }
  nextTick(() => {
    sysTreeRef.value?.setCurrentKey?.(sysCategoryId.value)
  })
  loadSysConfigRows()
}
function loadSysConfigRows() {
  sysConfigLoading.value = true
  try {
    let rows = readSysConfigStore(sysCampusId.value, sysCategoryId.value)
    const kw = sysNameKw.value?.trim()
    if (kw) {
      rows = rows.filter((r) => (r.name || '').includes(kw) || (r.code || '').includes(kw))
    }
    sysConfigRows.value = rows
  } finally {
    sysConfigLoading.value = false
  }
}
function onSysCategoryClick(data) {
  if (!data || data.id === 'root') return
  // 分组节点若无自身参数表，优先进入其下第一个子项（如基础参数 → 岗位维护可手动点选）
  sysCategoryId.value = data.id
  loadSysConfigRows()
}
function fillSysConfigForm(row) {
  sysConfigForm.id = row?.id || ''
  sysConfigForm.code = row?.code || ''
  sysConfigForm.name = row?.name || ''
  sysConfigForm.value1 = row?.value1 || ''
  sysConfigForm.value2 = row?.value2 || ''
}
function resetSysConfigForm() {
  fillSysConfigForm(null)
  sysConfigFormRef.value?.clearValidate?.()
}
function openSysConfigCreate() {
  if (!sysCategoryId.value || sysCategoryId.value === 'root') {
    ElMessage.warning('请先选择左侧参数分类')
    return
  }
  sysConfigDialogMode.value = 'create'
  resetSysConfigForm()
  sysConfigDrawerVisible.value = true
}
function openSysConfigEdit(row) {
  sysConfigDialogMode.value = 'edit'
  fillSysConfigForm(row)
  sysConfigDrawerVisible.value = true
}
async function submitSysConfigForm() {
  const form = sysConfigFormRef.value
  if (!form) return
  await form.validate((ok) => {
    if (!ok) return
    const rows = readSysConfigStore(sysCampusId.value, sysCategoryId.value)
    if (sysConfigDialogMode.value === 'edit') {
      const idx = rows.findIndex((r) => r.id === sysConfigForm.id)
      if (idx >= 0) {
        rows[idx] = {
          ...rows[idx],
          code: sysConfigForm.code.trim(),
          name: sysConfigForm.name.trim(),
          value1: sysConfigForm.value1?.trim() || '',
          value2: sysConfigForm.value2?.trim() || ''
        }
      }
    } else {
      rows.push({
        id: `${Date.now()}_${Math.random().toString(36).slice(2, 8)}`,
        code: sysConfigForm.code.trim(),
        name: sysConfigForm.name.trim(),
        value1: sysConfigForm.value1?.trim() || '',
        value2: sysConfigForm.value2?.trim() || ''
      })
    }
    writeSysConfigStore(sysCampusId.value, sysCategoryId.value, rows)
    sysConfigDrawerVisible.value = false
    ElMessage.success(sysConfigDialogMode.value === 'edit' ? '参数已修改' : '参数已新增')
    loadSysConfigRows()
  })
}
async function deleteSysConfigRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除参数「${row.code} ${row.name}」？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  const rows = readSysConfigStore(sysCampusId.value, sysCategoryId.value).filter((r) => r.id !== row.id)
  writeSysConfigStore(sysCampusId.value, sysCategoryId.value, rows)
  ElMessage.success('已删除')
  loadSysConfigRows()
}
async function initSysConfigCategory() {
  if (!sysCategoryId.value || sysCategoryId.value === 'root') {
    ElMessage.warning('请先选择左侧参数分类')
    return
  }
  try {
    await ElMessageBox.confirm('确认将该分类参数恢复为初始默认值？', '初始化确认', {
      type: 'warning',
      confirmButtonText: '初始化',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  if (sysCategoryId.value === JOB_POST_CATEGORY) {
    localStorage.removeItem(JOB_POST_STORAGE)
    refreshJobPostOptions()
  } else {
    localStorage.removeItem(sysConfigStorageKey(sysCampusId.value, sysCategoryId.value))
  }
  ElMessage.success('已初始化')
  loadSysConfigRows()
}
function onSysConfigSettings() {
  ElMessage.info('设置功能待开发')
}
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
    const kw = orgKeyword.value?.trim() || undefined
    const res = await http.get('/md/campuses', { params: { size: 200, keyword: kw } })
    orgRows.value = res.data.records || []
    orgSelected.value = []
    orgTableRef.value?.clearSelection?.()
  } finally {
    orgLoading.value = false
  }
}

function ensureData(name) {
  if (name === 'org') loadOrg()
  else if (name === 'staffs') loadStaff()
  else if (name === 'org-auth' || name === 'perms') return
  else if (name === 'sys-config') loadSysConfigPage()
  else {
    const t = dataTabs.find(x => x.key === name)
    if (t) load(t)
  }
}

watch(active, (name) => ensureData(name), { immediate: true })
onMounted(() => ensureData(active.value))
</script>
<style scoped>
.md-page {
  height: 100%;
  padding: 12px 12px 0;
  box-sizing: border-box;
  overflow: hidden;
}
.org-panel {
  position: relative;
  height: 100%;
  overflow: hidden;
}
/* 抽屉挂在内容容器内：绝对定位 + 透明蒙层，不挡顶栏、不变灰 */
.org-panel :deep(.org-drawer-modal.el-overlay),
.org-panel :deep(.el-overlay.is-drawer),
.org-panel :deep(.el-overlay) {
  position: absolute !important;
  inset: 0 !important;
  background-color: transparent !important;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.toolbar-search {
  width: 220px;
}
.toolbar-search :deep(.el-input__wrapper) {
  height: 32px;
  box-sizing: border-box;
}
.toolbar-btn {
  height: 32px !important;
  min-height: 32px !important;
  padding: 0 14px !important;
  margin: 0 !important;
  font-size: 13px;
  line-height: 1;
  box-sizing: border-box;
}
.toolbar-btn :deep(.el-icon) {
  font-size: 14px;
}
.toolbar-btn--search {
  background: color-mix(in srgb, var(--brand) 14%, #ffffff) !important;
  border-color: color-mix(in srgb, var(--brand) 35%, #dcdfe6) !important;
  color: var(--brand) !important;
}
.toolbar-btn--search:hover {
  background: color-mix(in srgb, var(--brand) 22%, #ffffff) !important;
  border-color: var(--brand) !important;
  color: var(--brand) !important;
}
.toolbar-btn--refresh {
  background: #eef5ff !important;
  border-color: #c6d8f5 !important;
  color: #3a6bb5 !important;
}
.toolbar-btn--refresh:hover {
  background: #e0ecff !important;
  border-color: #9db8e8 !important;
  color: #2f5ea3 !important;
}
.hint { color: var(--muted, #5a7380); font-size: 13px; }
.md-table {
  width: 100%;
  background: #fff;
}
/* 固定行高，操作按钮不得撑高 */
.md-table :deep(.el-table__body td.el-table__cell) {
  padding: 4px 0 !important;
  height: 40px;
}
.md-table :deep(.el-table__body td.el-table__cell .cell) {
  line-height: 22px;
}
/* 表头：浅灰底 + 参考图同款标题字体（微软雅黑/苹方，14px，半粗深灰） */
.md-table :deep(.el-table__header-wrapper th.el-table__cell),
.md-table :deep(.el-table__fixed-header-wrapper th.el-table__cell),
.md-table :deep(.el-table__header th.el-table__cell) {
  background-color: #f5f7fa !important;
  padding: 10px 0;
}
.md-table :deep(.el-table__header .cell) {
  font-family: "PingFang SC", "Microsoft YaHei", "Segoe UI", sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: #555555;
  line-height: 22px;
  letter-spacing: 0.02em;
}
.org-ops {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-wrap: nowrap;
  gap: 4px;
  height: 24px;
  line-height: 1;
}
.org-ops-btn {
  height: 24px !important;
  min-height: 24px !important;
  padding: 0 8px !important;
  margin: 0 !important;
  font-size: 12px !important;
  line-height: 22px !important;
}
/* 悬停：主题色约 16% 混白，一眼能看出当前行 */
.md-table :deep(.el-table__body tr:hover > td.el-table__cell) {
  background-color: color-mix(in srgb, var(--brand) 16%, #ffffff) !important;
}
/* 勾选：主题色约 32% 混白，比悬停更深一档，色差明显 */
.md-table :deep(.el-table__body tr.org-row-selected > td.el-table__cell) {
  background-color: color-mix(in srgb, var(--brand) 32%, #ffffff) !important;
}
/* 勾选后再悬停：再略加深，仍保持“已选”观感 */
.md-table :deep(.el-table__body tr.org-row-selected:hover > td.el-table__cell) {
  background-color: color-mix(in srgb, var(--brand) 40%, #ffffff) !important;
}
.org-drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
  padding-right: 0;
}
.org-drawer-title {
  flex: 1;
  min-width: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.4;
}
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.staff-form-grid :deep(.el-form-item) {
  margin-bottom: 14px;
}
.staff-form-grid :deep(.el-input-number .el-input__inner) {
  text-align: left;
}
.auth-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.auth-tree-wrap {
  max-height: 420px;
  overflow: auto;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  padding: 8px 10px;
}
.auth-tree-node {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}
.auth-tree-ico {
  color: #909399;
  font-size: 14px;
}
.auth-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}
.sys-config-page {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}
.sys-config-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
  flex-shrink: 0;
}
.sys-config-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.sys-config-label {
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
}
.sys-config-campus {
  width: 200px;
}
.sys-config-name {
  width: 180px;
}
.sys-config-name :deep(.el-input__wrapper),
.sys-config-campus :deep(.el-select__wrapper) {
  min-height: 32px;
}
.sys-config-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.sys-config-body {
  display: flex;
  flex: 1;
  min-height: 0;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}
.sys-config-tree {
  width: 220px;
  flex-shrink: 0;
  border-right: 1px solid #e4e7ed;
  overflow: auto;
  padding: 8px 4px;
  background: #fafbfc;
}
.sys-tree-node {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}
.sys-tree-ico {
  color: var(--brand);
  font-size: 14px;
}
.sys-config-main {
  flex: 1;
  min-width: 0;
  padding: 0;
  overflow: hidden;
}
.sys-config-table {
  height: 100%;
}
.sys-config-page :deep(.org-drawer-modal.el-overlay),
.sys-config-page :deep(.el-overlay) {
  position: absolute !important;
  inset: 0 !important;
  background-color: transparent !important;
}
</style>

<style>
.org-auth-dialog .el-dialog__body {
  padding-top: 8px;
  padding-bottom: 8px;
}
.org-auth-dialog .el-dialog__footer {
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 12px;
}
</style>
