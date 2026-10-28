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
          @keyup.enter="searchOrg"
          @clear="searchOrg"
        />
        <el-button v-if="isPlatformAdmin" type="primary" class="toolbar-btn" :icon="Plus" @click="openOrgCreate">新增</el-button>
        <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchOrg">搜索</el-button>
        <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadOrg">刷新</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="orgTableRef"
            v-loading="orgLoading"
            :data="orgRows"
            border
            height="100%"
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
            <el-table-column label="操作" :width="isPlatformAdmin ? 330 : 200" fixed="right" align="center" header-align="center" class-name="org-ops-col">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="primary" plain size="small" @click="openOrgView(row)">查看</el-button>
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openOrgEdit(row)">修改</el-button>
                  <template v-if="isPlatformAdmin">
                    <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteOrgRow(row)">删除</el-button>
                    <el-button class="org-ops-btn" type="warning" plain size="small" @click="openOrgAuth(row)">授权</el-button>
                    <el-button class="org-ops-btn" type="info" plain size="small" @click="initOrgRow(row)">初始化</el-button>
                  </template>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="orgPage"
            v-model:page-size="orgPageSize"
            :total="orgTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadOrg"
            @size-change="onOrgPageSizeChange"
          />
        </div>
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
      <div ref="staffPanelRef" class="org-panel dept-page">
        <div class="dept-body">
          <aside class="dept-side">
            <el-tree
              ref="staffRoleTreeRef"
              class="dept-tree"
              :data="staffRoleTreeData"
              node-key="id"
              highlight-current
              default-expand-all
              :expand-on-click-node="false"
              :current-node-key="staffRoleFilter || 'root'"
              @node-click="onStaffRoleTreeClick"
            >
              <template #default="{ data }">
                <span class="sys-tree-node">
                  <el-icon class="sys-tree-ico"><Folder v-if="data.id === 'root'" /><Document v-else /></el-icon>
                  <span>{{ data.label }}</span>
                </span>
              </template>
            </el-tree>
          </aside>
          <div class="dept-main">
            <div class="toolbar">
              <el-input
                v-model="staffKeyword"
                clearable
                placeholder="搜索用户姓名"
                class="toolbar-search"
                @keyup.enter="searchStaff"
                @clear="searchStaff"
              />
              <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchStaff">搜索</el-button>
              <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadStaff">刷新</el-button>
              <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openStaffCreate">新增</el-button>
              <el-button type="primary" plain class="toolbar-btn" :icon="Upload" @click="openStaffImport">导入</el-button>
            </div>
            <div class="md-table-wrap">
              <el-table
                ref="staffTableRef"
                v-loading="staffLoading"
                :data="staffRows"
                border
                height="100%"
                class="md-table"
                :row-class-name="staffRowClassName"
                @selection-change="onStaffSelectionChange"
                @row-dblclick="onStaffRowDblclick"
              >
                <el-table-column type="selection" width="48" />
                <el-table-column type="index" label="序号" width="64" :index="staffIndex" />
                <el-table-column prop="staffNo" label="工号" min-width="120" sortable show-overflow-tooltip />
                <el-table-column prop="staffName" label="姓名" min-width="140" sortable show-overflow-tooltip />
                <el-table-column prop="staffRole" label="岗位" min-width="140" sortable show-overflow-tooltip>
                  <template #default="{ row }">{{ roleLabel(row.staffRole) }}</template>
                </el-table-column>
                <el-table-column prop="deptId" label="科室" min-width="140" sortable show-overflow-tooltip>
                  <template #default="{ row }">{{ staffDeptLabel(row.deptId) }}</template>
                </el-table-column>
                <el-table-column prop="contactName" label="联系人" min-width="100" sortable show-overflow-tooltip />
                <el-table-column prop="contactPhone" label="联系电话" min-width="120" sortable show-overflow-tooltip />
                <el-table-column prop="isActive" label="启用" width="80" align="center">
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
            </div>
            <div class="md-pager">
              <el-pagination
                v-model:current-page="staffPage"
                v-model:page-size="staffPageSize"
                :total="staffTotal"
                :page-sizes="[10, 20, 50]"
                layout="prev, pager, next, jumper, total, sizes"
                @current-change="loadStaff"
                @size-change="onStaffPageSizeChange"
              />
            </div>
          </div>
        </div>
        <el-dialog
          v-model="staffImportVisible"
          title="人员导入"
          width="720px"
          class="org-auth-dialog staff-import-dialog"
          append-to-body
          destroy-on-close
          :show-close="false"
          :close-on-click-modal="false"
        >
          <div class="staff-import-toolbar">
            <el-button type="primary" :icon="Download" @click="downloadStaffImportTemplate">下载模板</el-button>
            <el-upload
              :auto-upload="false"
              :show-file-list="false"
              accept=".xls,.xlsx,.csv,text/csv,.txt,application/vnd.ms-excel"
              :on-change="onStaffImportFileChange"
            >
              <el-button type="primary" plain :icon="Document">选择文件</el-button>
            </el-upload>
            <el-button type="primary" plain :icon="Search" :disabled="!staffImportFile" @click="parseStaffImportFile">解析</el-button>
            <el-button type="success" :icon="Upload" :loading="staffImportSaving" :disabled="!staffImportRows.length || staffImportSaving" @click="submitStaffImport">导入</el-button>
            <span v-if="staffImportFileName" class="staff-import-file">{{ staffImportFileName }}</span>
          </div>
          <div v-if="staffImportSaving" class="staff-import-progress">
            <el-progress
              :percentage="staffImportProgress"
              :stroke-width="14"
              striped
              striped-flow
              status="success"
            />
            <div class="staff-import-progress-text">正在导入 {{ staffImportDone }}/{{ staffImportTotal }}…</div>
          </div>
          <el-table :data="staffImportRows" border height="320" class="md-table" empty-text="请先选择文件并点击解析">
            <el-table-column type="index" label="序号" width="56" />
            <el-table-column prop="staffNo" label="工号" min-width="100" show-overflow-tooltip />
            <el-table-column prop="staffName" label="姓名" min-width="100" show-overflow-tooltip />
            <el-table-column prop="roleName" label="岗位名称" min-width="100" show-overflow-tooltip />
            <el-table-column prop="password" label="密码" min-width="90" show-overflow-tooltip />
            <el-table-column prop="contactName" label="联系人" min-width="90" show-overflow-tooltip />
            <el-table-column prop="contactPhone" label="联系电话" min-width="110" show-overflow-tooltip />
            <el-table-column prop="deptName" label="科室名称" min-width="120" show-overflow-tooltip />
            <el-table-column label="校验" min-width="140" fixed="right" align="center" header-align="center" show-overflow-tooltip>
              <template #default="{ row }">
                <span :class="row.error ? 'staff-import-fail' : 'staff-import-ok'">
                  {{ row.error || '通过' }}
                </span>
              </template>
            </el-table-column>
          </el-table>
          <template #footer>
            <div class="auth-footer">
              <el-button :disabled="staffImportSaving" @click="staffImportVisible = false">关闭</el-button>
            </div>
          </template>
        </el-dialog>
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
          <el-tabs v-model="staffFormTab" class="staff-form-tabs">
            <el-tab-pane label="基础信息" name="basic" />
            <el-tab-pane label="资质信息" name="qualify" />
            <el-tab-pane label="培训记录" name="train" />
          </el-tabs>
          <el-form ref="staffFormRef" class="staff-form-grid" :model="staffForm" :rules="staffRules" label-width="108px">
            <el-row v-show="staffFormTab === 'basic'" :gutter="12">
              <el-col :span="12">
                <el-form-item label="组织机构" prop="orgId">
                  <el-select
                    v-model="staffForm.orgId"
                    :disabled="!isPlatformAdmin"
                    filterable
                    placeholder="请选择组织机构"
                    style="width: 100%"
                    @change="onStaffOrgChange"
                  >
                    <el-option
                      v-for="o in orgOptions"
                      :key="o.id"
                      :label="`${o.campusCode}|${o.campusName}`"
                      :value="o.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户类型" prop="userType">
                  <el-select
                    v-model="staffForm.userType"
                    :disabled="!canEditStaffUserType"
                    placeholder="请选择"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="t in staffUserTypeOptions"
                      :key="t.code"
                      :label="t.name"
                      :value="t.code"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
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
                <el-form-item label="密码" prop="password">
                  <el-input v-model="staffForm.password" maxlength="64" placeholder="明文密码，默认同工号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系人" prop="contactName">
                  <el-input v-model="staffForm.contactName" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="contactPhone">
                  <el-input v-model="staffForm.contactPhone" maxlength="32" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="QQ" prop="qq">
                  <el-input v-model="staffForm.qq" maxlength="32" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="电子邮箱" prop="email">
                  <el-input v-model="staffForm.email" maxlength="128" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="科室" prop="deptId">
                  <el-select
                    v-model="staffForm.deptId"
                    clearable
                    filterable
                    placeholder="请选择科室"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="d in staffDeptOptions"
                      :key="d.id"
                      :label="`${d.deptCode}|${d.deptName}`"
                      :value="d.id"
                    />
                  </el-select>
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
            <el-row v-show="staffFormTab === 'qualify'" :gutter="12">
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
            </el-row>
            <div v-show="staffFormTab === 'train'" class="staff-train-pane">
              <el-table :data="staffTrainRows" border size="small" empty-text="暂无培训记录" class="md-table">
                <el-table-column type="index" label="序号" width="56" />
                <el-table-column prop="trainName" label="培训名称" min-width="140" show-overflow-tooltip />
                <el-table-column prop="trainDate" label="培训日期" width="110" />
                <el-table-column prop="trainer" label="培训人" min-width="100" show-overflow-tooltip />
                <el-table-column prop="result" label="结果" width="90" />
                <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
              </el-table>
            </div>
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

    <!-- 权限管理（布局对齐人员档案） -->
    <template v-else-if="active === 'perms'">
      <div ref="permPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="permKeyword"
            clearable
            placeholder="搜索岗位编码 / 名称"
            class="toolbar-search"
            @keyup.enter="searchPerms"
            @clear="searchPerms"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchPerms">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadPerms">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openPermCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="permTableRef"
            v-loading="permLoading"
            :data="permRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="permRowClassName"
            @selection-change="onPermSelectionChange"
            @row-dblclick="onPermRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="permIndex" />
            <el-table-column prop="role" label="岗位编码" min-width="120" sortable />
            <el-table-column prop="roleName" label="岗位名称" min-width="140" sortable />
            <el-table-column prop="menus" label="可见菜单" min-width="360" show-overflow-tooltip />
            <el-table-column label="操作" width="200" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openPermEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deletePermRow(row)">删除</el-button>
                  <el-button class="org-ops-btn" type="warning" plain size="small" @click="openPermAuth(row)">授权</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="permPage"
            v-model:page-size="permPageSize"
            :total="permTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadPerms"
            @size-change="onPermPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="permDrawerVisible"
          direction="rtl"
          size="480px"
          :modal="true"
          :show-close="false"
          :append-to="permPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetPermForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ permDialogTitle }}</span>
              <el-button type="danger" size="small" @click="permDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="permFormRef" :model="permForm" :rules="permRules" label-width="100px">
            <el-form-item label="岗位编码" prop="role">
              <el-input
                v-model="permForm.role"
                maxlength="64"
                placeholder="如 admin"
                :disabled="permDialogMode === 'edit'"
              />
            </el-form-item>
            <el-form-item label="岗位名称" prop="roleName">
              <el-input v-model="permForm.roleName" maxlength="64" placeholder="如 管理员" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="permDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="permSaving" @click="submitPermForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
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
              <el-table-column prop="code" :label="sysConfigCodeLabel" min-width="120" sortable />
              <el-table-column prop="name" :label="sysConfigNameLabel" min-width="160" sortable />
              <el-table-column v-if="!isDictLikeSysCategory" prop="value1" label="值1" min-width="140" sortable show-overflow-tooltip />
              <el-table-column v-if="!isDictLikeSysCategory" prop="value2" label="值2" min-width="140" sortable show-overflow-tooltip />
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
            <el-form-item :label="sysConfigCodeLabel" prop="code">
              <el-input
                v-model="sysConfigForm.code"
                maxlength="64"
                :placeholder="sysConfigCodePlaceholder"
              />
            </el-form-item>
            <el-form-item :label="sysConfigNameLabel" prop="name">
              <el-input
                v-model="sysConfigForm.name"
                maxlength="128"
                :placeholder="sysConfigNamePlaceholder"
              />
            </el-form-item>
            <el-form-item v-if="!isDictLikeSysCategory" label="值1" prop="value1">
              <el-input v-model="sysConfigForm.value1" maxlength="256" placeholder="选填" />
            </el-form-item>
            <el-form-item v-if="!isDictLikeSysCategory" label="值2" prop="value2">
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

    <!-- 院区维护 -->
    <template v-else-if="active === 'campuses'">
      <div ref="campusPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="campusKeyword"
            clearable
            placeholder="搜索院区编码 / 名称"
            class="toolbar-search"
            @keyup.enter="searchCampusPage"
            @clear="searchCampusPage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchCampusPage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadCampusPage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openCampusCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="campusTableRef"
            v-loading="campusLoading"
            :data="campusRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="campusRowClassName"
            @selection-change="onCampusSelectionChange"
            @row-dblclick="onCampusRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="campusIndex" />
            <el-table-column prop="orgLabel" label="组织机构" min-width="160" sortable />
            <el-table-column prop="areaCode" label="院区编码" min-width="120" sortable />
            <el-table-column prop="areaName" label="院区名称" min-width="160" sortable />
            <el-table-column prop="isActive" label="是否启用" width="100" align="center">
              <template #default="{ row }">{{ row.isActive === 1 || row.isActive === true ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openCampusEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteCampusRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="campusPage"
            v-model:page-size="campusPageSize"
            :total="campusTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadCampusPage"
            @size-change="onCampusPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="campusDrawerVisible"
          direction="rtl"
          size="480px"
          :modal="true"
          :show-close="false"
          :append-to="campusPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetCampusForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ campusDialogMode === 'edit' ? '修改院区信息' : '新增院区信息' }}</span>
              <el-button type="danger" size="small" @click="campusDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="campusFormRef" :model="campusForm" :rules="campusRules" label-width="100px">
            <el-form-item label="组织机构" prop="orgId">
              <el-select
                v-model="campusForm.orgId"
                filterable
                placeholder="请选择组织机构"
                style="width: 100%"
              >
                <el-option
                  v-for="o in orgOptions"
                  :key="o.id"
                  :label="`${o.campusCode} ${o.campusName}`"
                  :value="o.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="院区编码" prop="areaCode">
              <el-input v-model="campusForm.areaCode" maxlength="32" placeholder="如 A01" />
            </el-form-item>
            <el-form-item label="院区名称" prop="areaName">
              <el-input v-model="campusForm.areaName" maxlength="64" placeholder="请输入院区名称" />
            </el-form-item>
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="campusForm.isActive" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="campusForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="campusDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="campusSaving" @click="submitCampusForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 仓库维护 -->
    <template v-else-if="active === 'warehouses'">
      <div ref="whPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="whKeyword"
            clearable
            placeholder="搜索仓库编码 / 名称"
            class="toolbar-search"
            @keyup.enter="searchWarehousePage"
            @clear="searchWarehousePage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchWarehousePage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadWarehousePage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openWhCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="whTableRef"
            v-loading="whLoading"
            :data="whRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="whRowClassName"
            @selection-change="onWhSelectionChange"
            @row-dblclick="onWhRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="whIndex" />
            <el-table-column prop="warehouseCode" label="仓库编码" min-width="120" sortable />
            <el-table-column prop="warehouseName" label="仓库名称" min-width="160" sortable />
            <el-table-column prop="campusName" label="所属院区" min-width="140" sortable />
            <el-table-column prop="isActive" label="是否启用" width="100" align="center">
              <template #default="{ row }">{{ row.isActive === 1 || row.isActive === true ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openWhEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteWhRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="whPage"
            v-model:page-size="whPageSize"
            :total="whTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadWarehousePage"
            @size-change="onWhPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="whDrawerVisible"
          direction="rtl"
          size="480px"
          :modal="true"
          :show-close="false"
          :append-to="whPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetWhForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ whDialogMode === 'edit' ? '修改仓库信息' : '新增仓库信息' }}</span>
              <el-button type="danger" size="small" @click="whDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="whFormRef" :model="whForm" :rules="whRules" label-width="100px">
            <el-form-item label="仓库编码" prop="warehouseCode">
              <el-input v-model="whForm.warehouseCode" maxlength="32" placeholder="如 WH01" />
            </el-form-item>
            <el-form-item label="仓库名称" prop="warehouseName">
              <el-input v-model="whForm.warehouseName" maxlength="64" placeholder="请输入仓库名称" />
            </el-form-item>
            <el-form-item label="所属院区" prop="campusId">
              <el-select v-model="whForm.campusId" clearable filterable placeholder="请选择院区" style="width: 100%">
                <el-option
                  v-for="c in mdCampusOptions"
                  :key="c.id"
                  :label="`${c.campusCode} ${c.campusName}`"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="whForm.isActive" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="whDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="whSaving" @click="submitWhForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 科室维护：左树 + 右表明细（机构单位在 Shell 顶栏全局显示） -->
    <template v-else-if="active === 'depts'">
      <div ref="deptPanelRef" class="org-panel dept-page">
        <div class="dept-body">
          <aside class="dept-side">
            <el-tree
              ref="deptTreeRef"
              class="dept-tree"
              :data="deptTreeData"
              node-key="id"
              highlight-current
              default-expand-all
              :expand-on-click-node="false"
              :current-node-key="deptTreeFilterId || 'root'"
              @node-click="onDeptTreeClick"
            >
              <template #default="{ data }">
                <span class="sys-tree-node">
                  <el-icon class="sys-tree-ico"><Folder v-if="data.id === 'root'" /><Document v-else /></el-icon>
                  <span>{{ data.label }}</span>
                </span>
              </template>
            </el-tree>
          </aside>
          <div class="dept-main">
            <div class="toolbar">
              <el-input
                v-model="deptKeyword"
                clearable
                placeholder="搜索科室编码 / 名称"
                class="toolbar-search"
                @keyup.enter="searchDeptPage"
                @clear="searchDeptPage"
              />
              <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchDeptPage">搜索</el-button>
              <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="refreshDeptPage">刷新</el-button>
              <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openDeptCreate">新增</el-button>
            </div>
            <div class="md-table-wrap">
              <el-table
                ref="deptTableRef"
                v-loading="deptLoading"
                :data="deptRows"
                border
                height="100%"
                class="md-table"
                :row-class-name="deptRowClassName"
                @selection-change="onDeptSelectionChange"
                @row-dblclick="onDeptRowDblclick"
              >
                <el-table-column type="selection" width="48" />
                <el-table-column type="index" label="序号" width="64" :index="deptIndex" />
                <el-table-column prop="deptCode" label="科室编码" min-width="110" sortable />
                <el-table-column prop="deptName" label="科室名称" min-width="140" sortable />
                <el-table-column prop="parentName" label="上级科室" min-width="140" sortable show-overflow-tooltip />
                <el-table-column prop="campusName" label="所属院区" min-width="140" sortable />
                <el-table-column prop="location" label="位置" min-width="120" sortable show-overflow-tooltip />
                <el-table-column prop="contactName" label="责任人" min-width="100" sortable />
                <el-table-column prop="contactPhone" label="责任人电话" min-width="120" sortable />
                <el-table-column prop="deptPhone" label="科室电话" min-width="120" sortable />
                <el-table-column prop="isActive" label="是否启用" width="100" align="center">
                  <template #default="{ row }">{{ row.isActive === 1 || row.isActive === true ? '是' : '否' }}</template>
                </el-table-column>
                <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
                  <template #default="{ row }">
                    <div class="org-ops">
                      <el-button class="org-ops-btn" type="success" plain size="small" @click="openDeptEdit(row)">修改</el-button>
                      <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteDeptRow(row)">删除</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div class="md-pager">
              <el-pagination
                v-model:current-page="deptPage"
                v-model:page-size="deptPageSize"
                :total="deptTotal"
                :page-sizes="[10, 20, 50]"
                layout="prev, pager, next, jumper, total, sizes"
                @current-change="loadDeptPage"
                @size-change="onDeptPageSizeChange"
              />
            </div>
          </div>
        </div>
        <el-drawer
          v-model="deptDrawerVisible"
          direction="rtl"
          size="560px"
          :modal="true"
          :show-close="false"
          :append-to="deptPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetDeptForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ deptDialogMode === 'edit' ? '修改科室信息' : '新增科室信息' }}</span>
              <el-button type="danger" size="small" @click="deptDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="deptFormRef" :model="deptForm" :rules="deptRules" label-width="110px">
            <el-form-item label="科室编码" prop="deptCode">
              <el-input v-model="deptForm.deptCode" maxlength="32" placeholder="如 OR01" />
            </el-form-item>
            <el-form-item label="科室名称" prop="deptName">
              <el-input v-model="deptForm.deptName" maxlength="64" placeholder="请输入科室名称" />
            </el-form-item>
            <el-form-item label="上级科室" prop="parentId">
              <el-select v-model="deptForm.parentId" clearable filterable placeholder="无则留空" style="width: 100%">
                <el-option
                  v-for="d in deptParentOptions"
                  :key="d.id"
                  :label="`${d.deptCode}|${d.deptName}`"
                  :value="d.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="所属院区" prop="campusId">
              <el-select v-model="deptForm.campusId" clearable filterable placeholder="请选择院区" style="width: 100%">
                <el-option
                  v-for="c in mdCampusOptions"
                  :key="c.id"
                  :label="`${c.campusCode} ${c.campusName}`"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="位置" prop="location">
              <el-input v-model="deptForm.location" maxlength="200" placeholder="选填" />
            </el-form-item>
            <el-form-item label="责任人" prop="contactName">
              <el-input v-model="deptForm.contactName" maxlength="64" placeholder="选填" />
            </el-form-item>
            <el-form-item label="责任人电话" prop="contactPhone">
              <el-input v-model="deptForm.contactPhone" maxlength="32" placeholder="选填" />
            </el-form-item>
            <el-form-item label="科室电话" prop="deptPhone">
              <el-input v-model="deptForm.deptPhone" maxlength="32" placeholder="选填" />
            </el-form-item>
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="deptForm.isActive" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="deptForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="deptDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="deptSaving" @click="submitDeptForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 供应商维护 -->
    <template v-else-if="active === 'vendors'">
      <div ref="vendorPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="vendorKeyword"
            clearable
            placeholder="搜索供应商编码 / 名称 / 简码"
            class="toolbar-search"
            @keyup.enter="searchVendorPage"
            @clear="searchVendorPage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchVendorPage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadVendorPage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openVendorCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="vendorTableRef"
            v-loading="vendorLoading"
            :data="vendorRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="vendorRowClassName"
            @selection-change="onVendorSelectionChange"
            @row-dblclick="onVendorRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="vendorIndex" />
            <el-table-column prop="vendorCode" label="供应商编码" min-width="120" sortable show-overflow-tooltip />
            <el-table-column prop="vendorName" label="供应商名称" min-width="140" sortable show-overflow-tooltip />
            <el-table-column prop="shortCode" label="简码" min-width="90" show-overflow-tooltip />
            <el-table-column prop="socialCreditCode" label="统一社会信用代码" min-width="160" show-overflow-tooltip />
            <el-table-column prop="legalPerson" label="法人" min-width="90" show-overflow-tooltip />
            <el-table-column prop="salesman" label="业务员" min-width="90" show-overflow-tooltip />
            <el-table-column prop="contactPhone" label="联系电话" min-width="120" show-overflow-tooltip />
            <el-table-column prop="companyAddress" label="公司地址" min-width="160" show-overflow-tooltip />
            <el-table-column prop="email" label="邮箱" min-width="140" show-overflow-tooltip />
            <el-table-column label="供应商类型" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">{{ formatVendorTypes(row.vendorTypes) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openVendorEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteVendorRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="vendorPage"
            v-model:page-size="vendorPageSize"
            :total="vendorTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadVendorPage"
            @size-change="onVendorPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="vendorDrawerVisible"
          direction="rtl"
          size="520px"
          :modal="true"
          :show-close="false"
          :append-to="vendorPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetVendorForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ vendorDialogMode === 'edit' ? '修改供应商信息' : '新增供应商信息' }}</span>
              <el-button type="danger" size="small" @click="vendorDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-tabs v-model="vendorFormTab" class="staff-form-tabs">
            <el-tab-pane label="基础信息" name="basic" />
            <el-tab-pane label="证件信息" name="cert" />
          </el-tabs>
          <el-form ref="vendorFormRef" class="staff-form-grid" :model="vendorForm" :rules="vendorRules" label-width="108px">
            <div v-show="vendorFormTab === 'basic'" class="vendor-form-grid">
              <el-form-item v-if="isPlatformAdmin" class="vendor-form-span-2" label="组织机构" prop="orgId">
                <el-select v-model="vendorForm.orgId" filterable placeholder="请选择组织机构" style="width: 100%">
                  <el-option v-for="o in orgOptions" :key="o.id" :label="`${o.campusCode}|${o.campusName}`" :value="o.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="供应商编码" prop="vendorCode">
                <el-input
                  v-model="vendorForm.vendorCode"
                  maxlength="64"
                  :disabled="vendorDialogMode === 'edit'"
                  :placeholder="vendorDialogMode === 'edit' ? '' : '新增留空则自动生成 G000001'"
                />
              </el-form-item>
              <el-form-item label="供应商名称" prop="vendorName">
                <el-input v-model="vendorForm.vendorName" maxlength="200" placeholder="请输入供应商名称" @input="onVendorNameInput" />
              </el-form-item>
              <el-form-item label="简码" prop="shortCode">
                <el-input v-model="vendorForm.shortCode" maxlength="64" placeholder="根据名称自动生成" />
              </el-form-item>
              <el-form-item label="注册资金" prop="registeredCapital">
                <el-input v-model="vendorForm.registeredCapital" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="公司负责人" prop="companyPrincipal">
                <el-input v-model="vendorForm.companyPrincipal" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="业务员" prop="salesman">
                <el-input v-model="vendorForm.salesman" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="vendorForm.contactPhone" maxlength="32" placeholder="选填" />
              </el-form-item>
              <el-form-item label="传真" prop="fax">
                <el-input v-model="vendorForm.fax" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="vendorForm.email" maxlength="128" placeholder="选填" />
              </el-form-item>
              <el-form-item label="网址" prop="website">
                <el-input v-model="vendorForm.website" maxlength="255" placeholder="选填" />
              </el-form-item>
              <el-form-item class="vendor-form-span-2" label="公司地址" prop="companyAddress">
                <el-input v-model="vendorForm.companyAddress" maxlength="255" placeholder="选填" />
              </el-form-item>
              <el-form-item class="vendor-form-span-2" label="经营范围" prop="businessScope">
                <el-input v-model="vendorForm.businessScope" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
              </el-form-item>
              <el-form-item label="启用" prop="isActive">
                <el-switch v-model="vendorForm.isActive" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item class="vendor-form-span-2" label="备注" prop="remark">
                <el-input v-model="vendorForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
              </el-form-item>
              <el-form-item class="vendor-form-span-2" label="供应商类型" prop="vendorTypes">
                <div class="vendor-type-lights">
                  <button
                    v-for="t in vendorTypeOptions"
                    :key="t.value"
                    type="button"
                    class="vendor-type-light"
                    :class="{ 'is-on': vendorForm.vendorTypes.includes(t.value) }"
                    @click="toggleVendorType(t.value)"
                  >
                    {{ t.label }}
                  </button>
                  <span v-if="!vendorTypeOptions.length" class="hint">请先在系统配置「供应商类型维护」中维护类型</span>
                </div>
              </el-form-item>
            </div>
            <div v-show="vendorFormTab === 'cert'">
              <el-form-item label="统一社会信用代码" prop="socialCreditCode">
                <el-input v-model="vendorForm.socialCreditCode" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="法人" prop="legalPerson">
                <el-input v-model="vendorForm.legalPerson" maxlength="64" placeholder="选填" />
              </el-form-item>
            </div>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="vendorDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="vendorSaving" @click="submitVendorForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 器械包档案 -->
    <template v-else-if="active === 'templates'">
      <div ref="tplPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="tplKeyword"
            clearable
            placeholder="搜索包编码 / 名称 / 简码"
            class="toolbar-search"
            @keyup.enter="searchTplPage"
            @clear="searchTplPage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchTplPage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadTplPage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openTplCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="tplTableRef"
            v-loading="tplLoading"
            :data="tplRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="tplRowClassName"
            @selection-change="onTplSelectionChange"
            @row-dblclick="onTplRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="tplIndex" />
            <el-table-column prop="templateCode" label="包编码" min-width="120" sortable show-overflow-tooltip />
            <el-table-column prop="templateName" label="包名称" min-width="140" sortable show-overflow-tooltip />
            <el-table-column prop="shortCode" label="简码" min-width="90" show-overflow-tooltip />
            <el-table-column label="包类别" min-width="110" show-overflow-tooltip>
              <template #default="{ row }">{{ formatPackCategory(row.packCategory) }}</template>
            </el-table-column>
            <el-table-column prop="shelfLifeDays" label="有效期(天)" width="100" align="center" />
            <el-table-column prop="basketLoadQty" label="一篮装量" width="90" align="center" />
            <el-table-column label="需入库" width="80" align="center">
              <template #default="{ row }">{{ row.needStore === 0 ? '否' : '是' }}</template>
            </el-table-column>
            <el-table-column label="是否启用" width="90" align="center">
              <template #default="{ row }">{{ row.isActive === 0 ? '停用' : '启用' }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openTplEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteTplRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="tplPage"
            v-model:page-size="tplPageSize"
            :total="tplTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadTplPage"
            @size-change="onTplPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="tplDrawerVisible"
          direction="rtl"
          size="560px"
          :modal="true"
          :show-close="false"
          :append-to="tplPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetTplForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ tplDialogMode === 'edit' ? '修改器械包信息' : '新增器械包信息' }}</span>
              <el-button type="danger" size="small" @click="tplDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-tabs v-model="tplFormTab" class="staff-form-tabs">
            <el-tab-pane label="基础信息" name="basic" />
            <el-tab-pane label="流程开关" name="flow" />
          </el-tabs>
          <el-form ref="tplFormRef" class="staff-form-grid" :model="tplForm" :rules="tplRules" label-width="112px">
            <div v-show="tplFormTab === 'basic'">
              <el-form-item v-if="isPlatformAdmin" label="组织机构" prop="orgId">
                <el-select v-model="tplForm.orgId" filterable placeholder="请选择组织机构" style="width: 100%">
                  <el-option v-for="o in orgOptions" :key="o.id" :label="`${o.campusCode}|${o.campusName}`" :value="o.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="包编码" prop="templateCode">
                <el-input
                  v-model="tplForm.templateCode"
                  maxlength="64"
                  :disabled="tplDialogMode === 'edit'"
                  :placeholder="tplDialogMode === 'edit' ? '' : '新增留空则自动生成 B000001'"
                />
              </el-form-item>
              <el-form-item label="包名称" prop="templateName">
                <el-input v-model="tplForm.templateName" maxlength="200" placeholder="请输入包名称" @input="onTplNameInput" />
              </el-form-item>
              <el-form-item label="简码" prop="shortCode">
                <el-input v-model="tplForm.shortCode" maxlength="64" placeholder="根据名称自动生成" />
              </el-form-item>
              <el-form-item label="包类别" prop="packCategory">
                <el-select v-model="tplForm.packCategory" placeholder="请选择包类别" style="width: 100%">
                  <el-option v-for="c in packCategoryOptions" :key="c.value" :label="c.label" :value="c.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="有效期(天)" prop="shelfLifeDays">
                <el-input-number v-model="tplForm.shelfLifeDays" :min="0" :max="9999" controls-position="right" style="width: 100%" />
              </el-form-item>
              <el-form-item label="一篮装量" prop="basketLoadQty">
                <el-input-number v-model="tplForm.basketLoadQty" :min="0" :max="9999" controls-position="right" style="width: 100%" />
              </el-form-item>
              <el-form-item label="安全库存" prop="safetyStockQty">
                <el-input-number v-model="tplForm.safetyStockQty" :min="0" :max="9999" controls-position="right" style="width: 100%" />
              </el-form-item>
              <el-form-item label="灭菌方式" prop="sterMethod">
                <el-select v-model="tplForm.sterMethod" clearable placeholder="选填" style="width: 100%">
                  <el-option label="高温" value="high" />
                  <el-option label="低温" value="low" />
                </el-select>
              </el-form-item>
              <el-form-item label="是否启用" prop="isActive">
                <el-switch v-model="tplForm.isActive" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="备注" prop="remark">
                <el-input v-model="tplForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
              </el-form-item>
            </div>
            <div v-show="tplFormTab === 'flow'">
              <el-form-item label="需清洗" prop="needWash">
                <el-switch v-model="tplForm.needWash" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="清洗方式" prop="washMode">
                <el-select v-model="tplForm.washMode" style="width: 100%">
                  <el-option label="打散" value="scatter" />
                  <el-option label="整篮" value="whole_basket" />
                </el-select>
              </el-form-item>
              <el-form-item label="需包装" prop="needPack">
                <el-switch v-model="tplForm.needPack" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="需入库" prop="needStore">
                <el-switch v-model="tplForm.needStore" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="需回收" prop="needRecycle">
                <el-switch v-model="tplForm.needRecycle" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="植入物" prop="isImplant">
                <el-switch v-model="tplForm.isImplant" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </div>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="tplDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="tplSaving" @click="submitTplForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 器械基础档案 -->
    <template v-else-if="active === 'instruments'">
      <div ref="instrumentPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="instrumentKeyword"
            clearable
            placeholder="搜索器械编码 / 名称 / 简码"
            class="toolbar-search"
            @keyup.enter="searchInstrumentPage"
            @clear="searchInstrumentPage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchInstrumentPage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadInstrumentPage">刷新</el-button>
          <el-button type="primary" plain class="toolbar-btn" :icon="Upload" @click="openInstrumentImport">导入</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openInstrumentCreate">新增</el-button>
        </div>
        <el-dialog
          v-model="instrumentImportVisible"
          title="器械导入"
          width="920px"
          class="org-auth-dialog staff-import-dialog"
          append-to-body
          destroy-on-close
          :show-close="false"
          :close-on-click-modal="false"
        >
          <div class="staff-import-toolbar">
            <el-button type="primary" :icon="Download" @click="downloadInstrumentImportTemplate">下载模板</el-button>
            <el-upload
              :auto-upload="false"
              :show-file-list="false"
              accept=".xls,.xlsx,.csv,text/csv,.txt,application/vnd.ms-excel"
              :on-change="onInstrumentImportFileChange"
            >
              <el-button type="primary" plain :icon="Document">选择文件</el-button>
            </el-upload>
            <el-button type="primary" plain :icon="Search" :disabled="!instrumentImportFile" @click="parseInstrumentImportFile">解析</el-button>
            <el-button type="success" :icon="Upload" :loading="instrumentImportSaving" :disabled="!instrumentImportRows.length || instrumentImportSaving" @click="submitInstrumentImport">导入</el-button>
            <span v-if="instrumentImportFileName" class="staff-import-file">{{ instrumentImportFileName }}</span>
          </div>
          <div v-if="instrumentImportSaving" class="staff-import-progress">
            <el-progress
              :percentage="instrumentImportProgress"
              :stroke-width="14"
              striped
              striped-flow
              status="success"
            />
            <div class="staff-import-progress-text">正在导入 {{ instrumentImportDone }}/{{ instrumentImportTotal }}…</div>
          </div>
          <el-table :data="instrumentImportRows" border height="360" class="md-table" empty-text="请先选择文件并点击解析">
            <el-table-column type="index" label="序号" width="56" />
            <el-table-column prop="instrumentCode" label="器械编码" min-width="100" show-overflow-tooltip />
            <el-table-column prop="instrumentName" label="器械名称" min-width="110" show-overflow-tooltip />
            <el-table-column prop="aliasName" label="别名" min-width="90" show-overflow-tooltip />
            <el-table-column prop="englishName" label="英文名称" min-width="110" show-overflow-tooltip />
            <el-table-column prop="brandName" label="品牌" min-width="90" show-overflow-tooltip />
            <el-table-column prop="spec" label="规格" min-width="80" show-overflow-tooltip />
            <el-table-column prop="model" label="型号" min-width="80" show-overflow-tooltip />
            <el-table-column prop="udi" label="UDI" min-width="110" show-overflow-tooltip />
            <el-table-column prop="manufacturerName" label="生产厂家" min-width="110" show-overflow-tooltip />
            <el-table-column prop="vendorName" label="供应商" min-width="110" show-overflow-tooltip />
            <el-table-column prop="registerNo" label="注册证号" min-width="110" show-overflow-tooltip />
            <el-table-column prop="registerValidUntil" label="注册证有效期" min-width="120" show-overflow-tooltip />
            <el-table-column prop="packRequirement" label="包装要求" min-width="110" show-overflow-tooltip />
            <el-table-column label="校验" min-width="150" fixed="right" align="center" header-align="center" show-overflow-tooltip>
              <template #default="{ row }">
                <span :class="row.error ? 'staff-import-fail' : 'staff-import-ok'">
                  {{ row.error || '通过' }}
                </span>
              </template>
            </el-table-column>
          </el-table>
          <template #footer>
            <div class="auth-footer">
              <el-button :disabled="instrumentImportSaving" @click="instrumentImportVisible = false">关闭</el-button>
            </div>
          </template>
        </el-dialog>
        <div class="md-table-wrap">
          <el-table
            ref="instrumentTableRef"
            v-loading="instrumentLoading"
            :data="instrumentRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="instrumentRowClassName"
            @selection-change="onInstrumentSelectionChange"
            @row-dblclick="onInstrumentRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="instrumentIndex" />
            <el-table-column prop="instrumentCode" label="器械编码" min-width="120" sortable show-overflow-tooltip />
            <el-table-column prop="instrumentName" label="器械名称" min-width="140" sortable show-overflow-tooltip />
            <el-table-column prop="shortCode" label="简码" min-width="90" show-overflow-tooltip />
            <el-table-column prop="spec" label="规格" min-width="100" show-overflow-tooltip />
            <el-table-column prop="model" label="型号" min-width="100" show-overflow-tooltip />
            <el-table-column prop="unitName" label="单位" min-width="80" show-overflow-tooltip />
            <el-table-column label="单价" min-width="90" align="right" header-align="center" show-overflow-tooltip>
              <template #default="{ row }">{{ formatInstrumentCostPrice(row.costPrice) }}</template>
            </el-table-column>
            <el-table-column prop="registerNo" label="注册证号" min-width="140" show-overflow-tooltip />
            <el-table-column label="注册证有效期" min-width="120" show-overflow-tooltip>
              <template #default="{ row }">{{ formatInstrumentDate(row.registerValidUntil) }}</template>
            </el-table-column>
            <el-table-column prop="manufacturerName" label="生产厂家" min-width="120" show-overflow-tooltip />
            <el-table-column prop="vendorName" label="供应商" min-width="120" show-overflow-tooltip />
            <el-table-column prop="currentStatus" label="当前状态" min-width="90" show-overflow-tooltip />
            <el-table-column prop="barcode" label="器械条码" min-width="120" show-overflow-tooltip />
            <el-table-column label="启用" width="80" align="center">
              <template #default="{ row }">{{ row.isActive === 0 ? '停用' : '启用' }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
            <el-table-column label="操作" width="200" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="primary" plain size="small" @click="openInstrumentCopy(row)">复制</el-button>
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openInstrumentEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteInstrumentRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="instrumentPage"
            v-model:page-size="instrumentPageSize"
            :total="instrumentTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadInstrumentPage"
            @size-change="onInstrumentPageSizeChange"
          />
        </div>
        <el-dialog
          v-model="instrumentDrawerVisible"
          width="100%"
          class="org-auth-dialog instrument-dialog"
          :append-to-body="false"
          destroy-on-close
          :show-close="false"
          :lock-scroll="false"
          :close-on-click-modal="false"
          :align-center="false"
          top="0"
          modal-class="org-drawer-modal instrument-dialog-modal"
          @closed="resetInstrumentForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ instrumentDialogMode === 'edit' ? '修改器械信息' : '新增器械信息' }}</span>
              <el-button type="danger" size="small" @click="instrumentDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <div class="instrument-dialog-body">
            <el-tabs v-model="instrumentFormTab" class="staff-form-tabs instrument-dialog-tabs">
              <el-tab-pane label="基础信息" name="basic" />
              <el-tab-pane label="图片" name="images" />
              <el-tab-pane label="附件" name="attachments" />
            </el-tabs>
            <div class="instrument-dialog-scroll">
              <el-form ref="instrumentFormRef" class="staff-form-grid instrument-dialog-form" :model="instrumentForm" :rules="instrumentRules" label-width="96px">
                <div v-show="instrumentFormTab === 'basic'" class="instrument-form-grid">
                  <el-form-item v-if="isPlatformAdmin" class="instrument-form-span-5" label="组织机构" prop="orgId">
                    <el-select
                      v-model="instrumentForm.orgId"
                      filterable
                      placeholder="请选择组织机构"
                      style="width: 100%"
                      @change="onInstrumentOrgChange"
                    >
                      <el-option v-for="o in orgOptions" :key="o.id" :label="`${o.campusCode}|${o.campusName}`" :value="o.id" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="器械编码" prop="instrumentCode">
                    <el-input
                      v-model="instrumentForm.instrumentCode"
                      maxlength="64"
                      :disabled="instrumentDialogMode === 'edit'"
                      :placeholder="instrumentDialogMode === 'edit' ? '' : '留空自动生成'"
                    />
                  </el-form-item>
                  <el-form-item label="器械名称" prop="instrumentName">
                    <el-input v-model="instrumentForm.instrumentName" maxlength="200" placeholder="请输入器械名称" @input="onInstrumentNameInput" />
                  </el-form-item>
                  <el-form-item label="别名" prop="aliasName">
                    <el-input v-model="instrumentForm.aliasName" maxlength="200" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="英文名称" prop="englishName">
                    <el-input v-model="instrumentForm.englishName" maxlength="200" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="简码" prop="shortCode">
                    <el-input v-model="instrumentForm.shortCode" maxlength="64" placeholder="自动生成" />
                  </el-form-item>
                  <el-form-item label="品牌" prop="brandName">
                    <el-input v-model="instrumentForm.brandName" maxlength="128" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="规格" prop="spec">
                    <el-input v-model="instrumentForm.spec" maxlength="128" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="型号" prop="model">
                    <el-input v-model="instrumentForm.model" maxlength="128" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="UDI" prop="udi">
                    <el-input v-model="instrumentForm.udi" maxlength="128" placeholder="唯一器械标识" />
                  </el-form-item>
                  <el-form-item label="生产厂家" prop="manufacturerId">
                    <el-select v-model="instrumentForm.manufacturerId" filterable clearable placeholder="请选择" style="width: 100%">
                      <el-option v-for="m in instrumentMfrOptions" :key="m.id" :label="m.mfrName" :value="m.id" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="供应商" prop="vendorId">
                    <el-select v-model="instrumentForm.vendorId" filterable clearable placeholder="请选择" style="width: 100%">
                      <el-option v-for="v in instrumentVendorOptions" :key="v.id" :label="v.vendorName" :value="v.id" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="注册证号" prop="registerNo">
                    <el-input v-model="instrumentForm.registerNo" maxlength="128" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="注册证有效期" prop="registerValidUntil" label-width="108px">
                    <el-date-picker
                      v-model="instrumentForm.registerValidUntil"
                      type="date"
                      value-format="YYYY-MM-DD"
                      placeholder="选填"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item label="材质" prop="materialName">
                    <el-input v-model="instrumentForm.materialName" maxlength="128" placeholder="如 不锈钢" />
                  </el-form-item>
                  <el-form-item label="器械条码" prop="barcode">
                    <el-input v-model="instrumentForm.barcode" maxlength="64" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="器械分类" prop="instrumentCategory">
                    <el-select v-model="instrumentForm.instrumentCategory" clearable filterable allow-create placeholder="请选择或输入" style="width: 100%">
                      <el-option v-for="o in instrumentCategoryOptions" :key="o" :label="o" :value="o" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="器械类型" prop="instrumentType">
                    <el-select v-model="instrumentForm.instrumentType" clearable filterable allow-create placeholder="请选择或输入" style="width: 100%">
                      <el-option v-for="o in instrumentTypeOptions" :key="o" :label="o" :value="o" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="灭菌方式" prop="sterMethod">
                    <el-select v-model="instrumentForm.sterMethod" clearable placeholder="请选择" style="width: 100%">
                      <el-option v-for="o in instrumentSterMethodOptions" :key="o.value" :label="o.label" :value="o.value" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="清洗方式" prop="washMethod">
                    <el-select v-model="instrumentForm.washMethod" clearable filterable allow-create placeholder="请选择或输入" style="width: 100%">
                      <el-option v-for="o in instrumentWashMethodOptions" :key="o" :label="o" :value="o" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="包装要求" prop="packRequirement">
                    <el-input v-model="instrumentForm.packRequirement" maxlength="255" placeholder="选填" />
                  </el-form-item>
                  <el-form-item label="单价" prop="costPrice">
                    <el-input-number
                      v-model="instrumentForm.costPrice"
                      :min="0"
                      :precision="4"
                      :controls="false"
                      placeholder="选填"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item label="单位" prop="unitId">
                    <el-select v-model="instrumentForm.unitId" filterable clearable placeholder="请选择" style="width: 100%">
                      <el-option v-for="u in instrumentUnitOptions" :key="u.id" :label="u.unitName" :value="u.id" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="存放属性" prop="storageAttr">
                    <el-select v-model="instrumentForm.storageAttr" clearable filterable allow-create placeholder="请选择或输入" style="width: 100%">
                      <el-option v-for="o in instrumentStorageAttrOptions" :key="o" :label="o" :value="o" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="当前状态" prop="currentStatus">
                    <el-select v-model="instrumentForm.currentStatus" placeholder="请选择" style="width: 100%">
                      <el-option v-for="o in instrumentStatusOptions" :key="o" :label="o" :value="o" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="启用" prop="isActive">
                    <el-switch v-model="instrumentForm.isActive" :active-value="1" :inactive-value="0" />
                  </el-form-item>
                  <el-form-item label="高值器械" prop="isHighValue">
                    <el-switch v-model="instrumentForm.isHighValue" :active-value="1" :inactive-value="0" />
                  </el-form-item>
                  <el-form-item label="重点监控器械" prop="isKeyMonitor" label-width="108px">
                    <el-switch v-model="instrumentForm.isKeyMonitor" :active-value="1" :inactive-value="0" />
                  </el-form-item>
                  <el-form-item class="instrument-form-span-5" label="备注" prop="remark">
                    <el-input
                      v-model="instrumentForm.remark"
                      type="textarea"
                      :rows="3"
                      maxlength="500"
                      placeholder="特殊注意事项（如禁止超声清洗、轻拿轻放）"
                    />
                  </el-form-item>
                </div>
                <div v-show="instrumentFormTab === 'images'" class="device-ext-pane">
                  <div class="device-ext-toolbar">
                    <el-upload
                      :auto-upload="false"
                      :show-file-list="false"
                      accept=".jpg,.jpeg,.png,.webp,.gif"
                      :on-change="onInstrumentImageChange"
                    >
                      <el-button type="primary" plain>上传图片</el-button>
                    </el-upload>
                    <span class="hint">支持 jpg/png，单张不超过 3MB</span>
                  </div>
                  <div v-if="instrumentImageList.length" class="device-image-grid">
                    <div v-for="(img, idx) in instrumentImageList" :key="`${img.name}-${idx}`" class="device-image-item">
                      <img :src="img.dataUrl" :alt="img.name" />
                      <div class="device-image-meta">
                        <span :title="img.name">{{ img.name }}</span>
                        <el-button link type="danger" @click="removeInstrumentImage(idx)">删除</el-button>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无器械图片" :image-size="72" />
                </div>
                <div v-show="instrumentFormTab === 'attachments'" class="device-ext-pane">
                  <div class="device-ext-toolbar">
                    <el-upload
                      :auto-upload="false"
                      :show-file-list="false"
                      :on-change="onInstrumentAttachmentChange"
                    >
                      <el-button type="primary" plain>上传附件</el-button>
                    </el-upload>
                    <span class="hint">单文件不超过 5MB</span>
                  </div>
                  <el-table v-if="instrumentAttachmentList.length" :data="instrumentAttachmentList" border size="small" class="md-table">
                    <el-table-column prop="name" label="文件名" min-width="180" show-overflow-tooltip />
                    <el-table-column label="操作" width="140" align="center">
                      <template #default="{ row, $index }">
                        <el-button link type="primary" @click="previewInstrumentAttachment(row)">查看</el-button>
                        <el-button link type="danger" @click="removeInstrumentAttachment($index)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-empty v-else description="暂无附件" :image-size="72" />
                </div>
              </el-form>
            </div>
          </div>
          <template #footer>
            <div class="drawer-footer instrument-dialog-footer">
              <el-button @click="instrumentDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="instrumentSaving" @click="submitInstrumentForm">确定</el-button>
            </div>
          </template>
        </el-dialog>
      </div>
    </template>

    <!-- 生产厂家维护 -->
    <template v-else-if="active === 'manufacturers'">
      <div ref="mfrPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="mfrKeyword"
            clearable
            placeholder="搜索生产厂家编码 / 名称 / 简码"
            class="toolbar-search"
            @keyup.enter="searchMfrPage"
            @clear="searchMfrPage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchMfrPage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadMfrPage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openMfrCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="mfrTableRef"
            v-loading="mfrLoading"
            :data="mfrRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="mfrRowClassName"
            @selection-change="onMfrSelectionChange"
            @row-dblclick="onMfrRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="mfrIndex" />
            <el-table-column prop="mfrCode" label="生产厂家编码" min-width="120" sortable show-overflow-tooltip />
            <el-table-column prop="mfrName" label="生产厂家名称" min-width="140" sortable show-overflow-tooltip />
            <el-table-column prop="shortCode" label="简码" min-width="90" show-overflow-tooltip />
            <el-table-column prop="socialCreditCode" label="统一社会信用代码" min-width="160" show-overflow-tooltip />
            <el-table-column prop="legalPerson" label="法人" min-width="90" show-overflow-tooltip />
            <el-table-column prop="salesman" label="业务员" min-width="90" show-overflow-tooltip />
            <el-table-column prop="contactPhone" label="联系电话" min-width="120" show-overflow-tooltip />
            <el-table-column prop="companyAddress" label="公司地址" min-width="160" show-overflow-tooltip />
            <el-table-column prop="email" label="邮箱" min-width="140" show-overflow-tooltip />
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openMfrEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteMfrRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="mfrPage"
            v-model:page-size="mfrPageSize"
            :total="mfrTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadMfrPage"
            @size-change="onMfrPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="mfrDrawerVisible"
          direction="rtl"
          size="520px"
          :modal="true"
          :show-close="false"
          :append-to="mfrPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetMfrForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ mfrDialogMode === 'edit' ? '修改生产厂家信息' : '新增生产厂家信息' }}</span>
              <el-button type="danger" size="small" @click="mfrDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-tabs v-model="mfrFormTab" class="staff-form-tabs">
            <el-tab-pane label="基础信息" name="basic" />
            <el-tab-pane label="证件信息" name="cert" />
          </el-tabs>
          <el-form ref="mfrFormRef" class="staff-form-grid" :model="mfrForm" :rules="mfrRules" label-width="128px">
            <div v-show="mfrFormTab === 'basic'">
              <el-form-item v-if="isPlatformAdmin" label="组织机构" prop="orgId">
                <el-select v-model="mfrForm.orgId" filterable placeholder="请选择组织机构" style="width: 100%">
                  <el-option v-for="o in orgOptions" :key="o.id" :label="`${o.campusCode}|${o.campusName}`" :value="o.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="生产厂家编码" prop="mfrCode">
                <el-input
                  v-model="mfrForm.mfrCode"
                  maxlength="64"
                  :disabled="mfrDialogMode === 'edit'"
                  :placeholder="mfrDialogMode === 'edit' ? '' : '新增留空则自动生成 S000001'"
                />
              </el-form-item>
              <el-form-item label="生产厂家名称" prop="mfrName">
                <el-input v-model="mfrForm.mfrName" maxlength="200" placeholder="请输入生产厂家名称" @input="onMfrNameInput" />
              </el-form-item>
              <el-form-item label="简码" prop="shortCode">
                <el-input v-model="mfrForm.shortCode" maxlength="64" placeholder="根据名称自动生成" />
              </el-form-item>
              <el-form-item label="业务员" prop="salesman">
                <el-input v-model="mfrForm.salesman" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="mfrForm.contactPhone" maxlength="32" placeholder="选填" />
              </el-form-item>
              <el-form-item label="公司地址" prop="companyAddress">
                <el-input v-model="mfrForm.companyAddress" maxlength="255" placeholder="选填" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="mfrForm.email" maxlength="128" placeholder="选填" />
              </el-form-item>
              <el-form-item label="是否启用" prop="isActive">
                <el-switch v-model="mfrForm.isActive" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="备注" prop="remark">
                <el-input v-model="mfrForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
              </el-form-item>
            </div>
            <div v-show="mfrFormTab === 'cert'">
              <el-form-item label="统一社会信用代码" prop="socialCreditCode">
                <el-input v-model="mfrForm.socialCreditCode" maxlength="64" placeholder="选填" />
              </el-form-item>
              <el-form-item label="法人" prop="legalPerson">
                <el-input v-model="mfrForm.legalPerson" maxlength="64" placeholder="选填" />
              </el-form-item>
            </div>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="mfrDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="mfrSaving" @click="submitMfrForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 设备维护 -->
    <template v-else-if="active === 'devices'">
      <div ref="devicePanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="deviceKeyword"
            clearable
            placeholder="搜索设备编码 / 名称 / 厂家 / 供应商"
            class="toolbar-search"
            @keyup.enter="searchDevicePage"
            @clear="searchDevicePage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchDevicePage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadDevicePage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openDeviceCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="deviceTableRef"
            v-loading="deviceLoading"
            :data="deviceRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="deviceRowClassName"
            @selection-change="onDeviceSelectionChange"
            @row-dblclick="onDeviceRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="deviceIndex" />
            <el-table-column prop="deviceCode" label="设备编码" min-width="110" sortable show-overflow-tooltip />
            <el-table-column prop="deviceName" label="设备名称" min-width="120" sortable show-overflow-tooltip />
            <el-table-column prop="spec" label="规格" min-width="90" show-overflow-tooltip />
            <el-table-column prop="model" label="型号" min-width="90" show-overflow-tooltip />
            <el-table-column prop="unit" label="单位" width="70" />
            <el-table-column prop="originalValue" label="原值(元)" width="100" />
            <el-table-column prop="serialNo" label="序列号(SN)" min-width="110" show-overflow-tooltip />
            <el-table-column prop="deviceType" label="设备类型" min-width="100" show-overflow-tooltip />
            <el-table-column prop="installLocation" label="安装位置" min-width="110" show-overflow-tooltip />
            <el-table-column prop="areaName" label="所在区域" min-width="110" show-overflow-tooltip />
            <el-table-column prop="capabilityParams" label="设备能力参数" min-width="130" show-overflow-tooltip />
            <el-table-column prop="enabledDate" label="启用日期" width="110" />
            <el-table-column prop="deviceStatus" label="设备状态" width="90" />
            <el-table-column prop="manufacturerName" label="生产厂家" min-width="120" show-overflow-tooltip />
            <el-table-column prop="vendorName" label="供应商" min-width="120" show-overflow-tooltip />
            <el-table-column prop="brandName" label="品牌" min-width="100" show-overflow-tooltip />
            <el-table-column prop="countryName" label="国别" width="90" show-overflow-tooltip />
            <el-table-column prop="deviceRegNo" label="医疗器械注册证号" min-width="140" show-overflow-tooltip />
            <el-table-column prop="productionDate" label="生产日期" width="110" />
            <el-table-column prop="operatorName" label="设备操作责任人" min-width="120" show-overflow-tooltip />
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openDeviceEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteDeviceRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="devicePage"
            v-model:page-size="devicePageSize"
            :total="deviceTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadDevicePage"
            @size-change="onDevicePageSizeChange"
          />
        </div>
        <el-drawer
          v-model="deviceDrawerVisible"
          direction="rtl"
          size="780px"
          :modal="true"
          :show-close="false"
          :append-to="devicePanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetDeviceForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ deviceDialogMode === 'edit' ? '修改设备信息' : '新增设备信息' }}</span>
              <el-button type="danger" size="small" @click="deviceDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-tabs v-model="deviceFormTab" class="staff-form-tabs">
            <el-tab-pane label="基础信息" name="basic" />
            <el-tab-pane label="设备卡片" name="card" />
            <el-tab-pane label="设备图片" name="images" />
            <el-tab-pane label="保养记录" name="maintainLog" />
            <el-tab-pane label="不良事件" name="adverse" />
            <el-tab-pane label="维保信息" name="maintain" />
            <el-tab-pane label="资质证件" name="cert" />
          </el-tabs>
          <el-form ref="deviceFormRef" class="staff-form-grid" :model="deviceForm" :rules="deviceRules" label-width="120px">
            <el-row v-show="deviceFormTab === 'basic'" :gutter="12">
              <el-col v-if="isPlatformAdmin" :span="24">
                <el-form-item label="组织机构" prop="orgId">
                  <el-select
                    v-model="deviceForm.orgId"
                    filterable
                    placeholder="请选择组织机构"
                    style="width: 100%"
                    @change="onDeviceOrgChange"
                  >
                    <el-option v-for="o in orgOptions" :key="o.id" :label="`${o.campusCode}|${o.campusName}`" :value="o.id" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="设备编码" prop="deviceCode">
                  <el-input
                    v-model="deviceForm.deviceCode"
                    maxlength="64"
                    :disabled="deviceDialogMode === 'edit'"
                    :placeholder="deviceDialogMode === 'edit' ? '' : '留空自动生成 D000001'"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="设备名称" prop="deviceName">
                  <el-input v-model="deviceForm.deviceName" maxlength="200" placeholder="请输入设备名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="规格" prop="spec">
                  <el-input v-model="deviceForm.spec" maxlength="128" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="型号" prop="model">
                  <el-input v-model="deviceForm.model" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="单位" prop="unit">
                  <el-select
                    v-model="deviceForm.unit"
                    filterable
                    clearable
                    placeholder="请选择单位"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="u in deviceUnitOptions"
                      :key="u.id"
                      :label="u.unitName"
                      :value="u.unitName"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="原值(元)" prop="originalValue">
                  <el-input-number
                    v-model="deviceForm.originalValue"
                    :min="0"
                    :precision="2"
                    :controls="false"
                    placeholder="选填"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="序列号(SN)" prop="serialNo">
                  <el-input v-model="deviceForm.serialNo" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="设备类型" prop="deviceType">
                  <el-input v-model="deviceForm.deviceType" maxlength="64" placeholder="如 清洗机 / 灭菌器" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="安装位置" prop="installLocation">
                  <el-input v-model="deviceForm.installLocation" maxlength="128" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所在区域" prop="areaName">
                  <el-input v-model="deviceForm.areaName" maxlength="128" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用日期" prop="enabledDate">
                  <el-date-picker v-model="deviceForm.enabledDate" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="设备状态" prop="deviceStatus">
                  <el-select v-model="deviceForm.deviceStatus" placeholder="请选择" style="width: 100%">
                    <el-option label="空闲" value="空闲" />
                    <el-option label="使用中" value="使用中" />
                    <el-option label="停用" value="停用" />
                    <el-option label="维修" value="维修" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="操作责任人" prop="operatorId">
                  <el-select
                    v-model="deviceForm.operatorId"
                    filterable
                    clearable
                    placeholder="请选择人员"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="s in deviceStaffOptions"
                      :key="s.id"
                      :label="s.staffName"
                      :value="s.id"
                    >
                      <span>{{ s.staffName }}</span>
                      <span style="float: right; color: #909399; font-size: 12px">{{ s.staffNo }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="生产厂家" prop="manufacturerId">
                  <el-select
                    v-model="deviceForm.manufacturerId"
                    filterable
                    clearable
                    placeholder="请选择生产厂家"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="m in deviceMfrOptions"
                      :key="m.id"
                      :label="m.mfrName"
                      :value="m.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="供应商" prop="vendorId">
                  <el-select
                    v-model="deviceForm.vendorId"
                    filterable
                    clearable
                    placeholder="请选择供应商"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="v in deviceVendorOptions"
                      :key="v.id"
                      :label="v.vendorName"
                      :value="v.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="品牌" prop="brandName">
                  <el-input v-model="deviceForm.brandName" maxlength="128" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="国别" prop="countryName">
                  <el-input v-model="deviceForm.countryName" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="医疗器械注册证号" prop="deviceRegNo" label-width="140px">
                  <el-input v-model="deviceForm.deviceRegNo" maxlength="128" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="生产日期" prop="productionDate">
                  <el-date-picker v-model="deviceForm.productionDate" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用" prop="isActive">
                  <el-switch v-model="deviceForm.isActive" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="设备能力参数" prop="capabilityParams">
                  <el-input v-model="deviceForm.capabilityParams" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="deviceForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
                </el-form-item>
              </el-col>
            </el-row>
            <div v-show="deviceFormTab === 'card'" class="device-asset-card-wrap">
              <div class="device-ext-toolbar">
                <el-button type="primary" plain :icon="Printer" @click="printDeviceAssetCard">打印</el-button>
              </div>
              <div ref="deviceAssetCardRef" class="device-asset-card-print">
                <table class="device-asset-card">
                  <thead>
                    <tr>
                      <th colspan="3" class="device-asset-card__title">{{ deviceAssetCardTitle }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td class="device-asset-card__label">资产名称</td>
                      <td class="device-asset-card__value" colspan="2">{{ deviceForm.deviceName || '—' }}</td>
                    </tr>
                    <tr>
                      <td class="device-asset-card__label">卡片编号</td>
                      <td class="device-asset-card__value" colspan="2">{{ deviceForm.deviceCode || '—' }}</td>
                    </tr>
                    <tr>
                      <td class="device-asset-card__label">原值(元)</td>
                      <td class="device-asset-card__value" colspan="2">{{ deviceCardOriginalValueText }}</td>
                    </tr>
                    <tr>
                      <td class="device-asset-card__label">规格型号</td>
                      <td class="device-asset-card__value">{{ deviceCardSpecModel || '—' }}</td>
                      <td class="device-asset-card__qr" rowspan="2">
                        <img v-if="deviceCardQrUrl" :src="deviceCardQrUrl" alt="设备二维码" />
                        <span v-else class="device-asset-card__qr-empty">保存后生成</span>
                      </td>
                    </tr>
                    <tr>
                      <td class="device-asset-card__label">存放地点</td>
                      <td class="device-asset-card__value">{{ deviceForm.installLocation || deviceForm.areaName || '—' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <p class="device-asset-card__hint">卡片字段均取自基础信息，此处仅展示；可打印资产卡片。</p>
            </div>
            <div v-show="deviceFormTab === 'images'" class="device-ext-pane">
              <div class="device-ext-toolbar">
                <el-upload
                  :auto-upload="false"
                  :show-file-list="false"
                  accept=".jpg,.jpeg,.png,.webp,.gif"
                  :on-change="onDeviceImageChange"
                >
                  <el-button type="primary" plain>上传图片</el-button>
                </el-upload>
                <span class="hint">支持 jpg/png，单张不超过 3MB</span>
              </div>
              <div v-if="deviceImageList.length" class="device-image-grid">
                <div v-for="(img, idx) in deviceImageList" :key="`${img.name}-${idx}`" class="device-image-item">
                  <img :src="img.dataUrl" :alt="img.name" />
                  <div class="device-image-meta">
                    <span :title="img.name">{{ img.name }}</span>
                    <el-button link type="danger" @click="removeDeviceImage(idx)">删除</el-button>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无设备图片" :image-size="72" />
            </div>
            <div v-show="deviceFormTab === 'maintainLog'" class="device-ext-pane">
              <div class="device-ext-toolbar">
                <el-button type="primary" plain @click="addDeviceMaintainLog">新增保养记录</el-button>
              </div>
              <el-table :data="deviceMaintainLogList" border size="small" class="md-table">
                <el-table-column label="保养日期" width="160">
                  <template #default="{ row }">
                    <el-date-picker v-model="row.maintainAt" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 100%" />
                  </template>
                </el-table-column>
                <el-table-column label="保养内容" min-width="180">
                  <template #default="{ row }">
                    <el-input v-model="row.content" maxlength="500" placeholder="选填" />
                  </template>
                </el-table-column>
                <el-table-column label="操作人" width="120">
                  <template #default="{ row }">
                    <el-input v-model="row.operator" maxlength="64" placeholder="选填" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80" align="center">
                  <template #default="{ $index }">
                    <el-button link type="danger" @click="removeDeviceMaintainLog($index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-show="deviceFormTab === 'adverse'" class="device-ext-pane">
              <div class="device-ext-toolbar">
                <el-button type="primary" plain @click="addDeviceAdverseEvent">新增不良事件</el-button>
              </div>
              <el-table :data="deviceAdverseList" border size="small" class="md-table">
                <el-table-column label="发生日期" width="160">
                  <template #default="{ row }">
                    <el-date-picker v-model="row.eventAt" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 100%" />
                  </template>
                </el-table-column>
                <el-table-column label="事件标题" min-width="140">
                  <template #default="{ row }">
                    <el-input v-model="row.title" maxlength="128" placeholder="选填" />
                  </template>
                </el-table-column>
                <el-table-column label="事件描述" min-width="180">
                  <template #default="{ row }">
                    <el-input v-model="row.content" maxlength="500" placeholder="选填" />
                  </template>
                </el-table-column>
                <el-table-column label="等级" width="110">
                  <template #default="{ row }">
                    <el-select v-model="row.level" placeholder="选填" clearable style="width: 100%">
                      <el-option label="一般" value="一般" />
                      <el-option label="严重" value="严重" />
                      <el-option label="重大" value="重大" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80" align="center">
                  <template #default="{ $index }">
                    <el-button link type="danger" @click="removeDeviceAdverseEvent($index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <el-row v-show="deviceFormTab === 'maintain'" :gutter="12">
              <el-col :span="12">
                <el-form-item label="维保周期" prop="maintainCycle">
                  <el-input v-model="deviceForm.maintainCycle" maxlength="64" placeholder="如 30天 / 每季度" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="维保到期预警" prop="maintainExpireWarn">
                  <el-input v-model="deviceForm.maintainExpireWarn" maxlength="64" placeholder="如 提前7天" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="上次维保时间" prop="lastMaintainAt">
                  <el-date-picker v-model="deviceForm.lastMaintainAt" type="date" value-format="YYYY-MM-DD" placeholder="选填" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="维保计划" prop="maintainPlan">
                  <el-input v-model="deviceForm.maintainPlan" type="textarea" :rows="3" maxlength="500" placeholder="选填" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row v-show="deviceFormTab === 'cert'" :gutter="12">
              <el-col :span="12">
                <el-form-item label="检定证书编号" prop="inspectCertNo">
                  <el-input v-model="deviceForm.inspectCertNo" maxlength="64" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="检定有效期" prop="inspectValidUntil">
                  <el-date-picker v-model="deviceForm.inspectValidUntil" type="date" value-format="YYYY-MM-DD" placeholder="计量强检设备" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="证书附件" prop="certAttachment">
                  <div class="device-cert-upload">
                    <el-upload
                      :auto-upload="false"
                      :show-file-list="false"
                      accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"
                      :on-change="onDeviceCertFileChange"
                    >
                      <el-button type="primary" plain>选择附件</el-button>
                    </el-upload>
                    <span v-if="deviceForm.certAttachment" class="device-cert-name">{{ deviceForm.certAttachment }}</span>
                    <el-button v-if="deviceForm.certAttachment" link type="danger" @click="clearDeviceCert">清除</el-button>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="deviceDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="deviceSaving" @click="submitDeviceForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <!-- 单位维护 -->
    <template v-else-if="active === 'units'">
      <div ref="unitPanelRef" class="org-panel">
        <div class="toolbar">
          <el-input
            v-model="unitKeyword"
            clearable
            placeholder="搜索单位编码 / 名称"
            class="toolbar-search"
            @keyup.enter="searchUnitPage"
            @clear="searchUnitPage"
          />
          <el-button class="toolbar-btn toolbar-btn--search" :icon="Search" @click="searchUnitPage">搜索</el-button>
          <el-button class="toolbar-btn toolbar-btn--refresh" :icon="Refresh" @click="loadUnitPage">刷新</el-button>
          <el-button type="primary" class="toolbar-btn" :icon="Plus" @click="openUnitCreate">新增</el-button>
        </div>
        <div class="md-table-wrap">
          <el-table
            ref="unitTableRef"
            v-loading="unitLoading"
            :data="unitRows"
            border
            height="100%"
            class="md-table"
            :row-class-name="unitRowClassName"
            @selection-change="onUnitSelectionChange"
            @row-dblclick="onUnitRowDblclick"
          >
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="64" :index="unitIndex" />
            <el-table-column prop="orgLabel" label="组织机构" min-width="160" sortable />
            <el-table-column prop="unitCode" label="单位编码" min-width="120" sortable />
            <el-table-column prop="unitName" label="单位名称" min-width="160" sortable />
            <el-table-column prop="isActive" label="是否启用" width="100" align="center">
              <template #default="{ row }">{{ row.isActive === 1 || row.isActive === true ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="140" fixed="right" align="center" header-align="center">
              <template #default="{ row }">
                <div class="org-ops">
                  <el-button class="org-ops-btn" type="success" plain size="small" @click="openUnitEdit(row)">修改</el-button>
                  <el-button class="org-ops-btn" type="danger" plain size="small" @click="deleteUnitRow(row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="md-pager">
          <el-pagination
            v-model:current-page="unitPage"
            v-model:page-size="unitPageSize"
            :total="unitTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, total, sizes"
            @current-change="loadUnitPage"
            @size-change="onUnitPageSizeChange"
          />
        </div>
        <el-drawer
          v-model="unitDrawerVisible"
          direction="rtl"
          size="480px"
          :modal="true"
          :show-close="false"
          :append-to="unitPanelRef || 'body'"
          :lock-scroll="false"
          :close-on-click-modal="false"
          modal-class="org-drawer-modal"
          destroy-on-close
          @closed="resetUnitForm"
        >
          <template #header>
            <div class="org-drawer-header">
              <span class="org-drawer-title">{{ unitDialogMode === 'edit' ? '修改单位信息' : '新增单位信息' }}</span>
              <el-button type="danger" size="small" @click="unitDrawerVisible = false">关闭</el-button>
            </div>
          </template>
          <el-form ref="unitFormRef" :model="unitForm" :rules="unitRules" label-width="100px">
            <el-form-item label="组织机构" prop="orgId">
              <el-select
                v-model="unitForm.orgId"
                filterable
                placeholder="请选择组织机构"
                style="width: 100%"
                :disabled="!isPlatformAdmin"
              >
                <el-option
                  v-for="o in orgOptions"
                  :key="o.id"
                  :label="`${o.campusCode} ${o.campusName}`"
                  :value="o.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="单位编码" prop="unitCode">
              <el-input
                v-model="unitForm.unitCode"
                maxlength="32"
                :disabled="unitDialogMode === 'edit'"
                :placeholder="unitDialogMode === 'edit' ? '' : '留空自动生成 D000001'"
              />
            </el-form-item>
            <el-form-item label="单位名称" prop="unitName">
              <el-input v-model="unitForm.unitName" maxlength="64" placeholder="请输入单位名称" />
            </el-form-item>
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="unitForm.isActive" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="unitForm.remark" type="textarea" :rows="2" maxlength="500" placeholder="选填" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="drawer-footer">
              <el-button @click="unitDrawerVisible = false">取消</el-button>
              <el-button type="primary" :loading="unitSaving" @click="submitUnitForm">确定</el-button>
            </div>
          </template>
        </el-drawer>
      </div>
    </template>

    <el-dialog
      v-model="authDialogVisible"
      :title="authDialogTitle"
      width="560px"
      class="org-auth-dialog"
      append-to-body
      destroy-on-close
      :show-close="false"
      :close-on-click-modal="false"
      @opened="onAuthDialogOpened"
    >
      <el-tabs v-if="authShowScopeTabs" v-model="authTab" class="auth-tabs">
        <el-tab-pane label="菜单权限" name="menu" />
        <el-tab-pane label="科室权限" name="dept" />
        <el-tab-pane label="仓库权限" name="warehouse" />
      </el-tabs>
      <div class="auth-toolbar">
        <el-button type="primary" :icon="Select" @click="authSelectAll">全选</el-button>
        <el-button type="primary" :icon="Close" @click="authClearAll">取消</el-button>
      </div>
      <div v-show="!authShowScopeTabs || authTab === 'menu'" class="auth-tree-wrap">
        <el-tree
          ref="authTreeRef"
          :data="authDialogTree"
          show-checkbox
          node-key="id"
          default-expand-all
          :props="{ label: 'label', children: 'children', disabled: 'disabled' }"
          @check-change="onAuthTreeCheckChange"
        >
          <template #default="{ node }">
            <span class="auth-tree-node">
              <el-icon class="auth-tree-ico"><Rank /></el-icon>
              <span>{{ node.label }}</span>
              <span v-if="node.data?.id === 'station'" class="auth-default-tag">默认</span>
            </span>
          </template>
        </el-tree>
      </div>
      <div v-show="authShowScopeTabs && authTab === 'dept'" class="auth-tree-wrap">
        <el-tree
          ref="authDeptTreeRef"
          :data="authDeptTreeData"
          show-checkbox
          node-key="id"
          default-expand-all
          :props="{ label: 'label', children: 'children' }"
        >
          <template #default="{ data }">
            <span class="auth-tree-node">
              <el-icon class="auth-tree-ico"><Folder v-if="data.id === 'root'" /><Document v-else /></el-icon>
              <span>{{ data.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
      <div v-show="authShowScopeTabs && authTab === 'warehouse'" class="auth-tree-wrap">
        <el-tree
          ref="authWarehouseTreeRef"
          :data="authWarehouseTreeData"
          show-checkbox
          node-key="id"
          default-expand-all
          :props="{ label: 'label', children: 'children' }"
        >
          <template #default="{ data }">
            <span class="auth-tree-node">
              <el-icon class="auth-tree-ico"><Folder v-if="data.id === 'root'" /><Document v-else /></el-icon>
              <span>{{ data.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
      <template #footer>
        <div class="auth-footer">
          <el-button type="primary" @click="submitMenuAuth">确定</el-button>
          <el-button @click="authDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Select, Close, Rank, Document, Setting, Folder, Upload, Download, Printer } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import http from '../api'
import {
  effectiveOrgId,
  initOrgContext,
  orgOptions,
  selectedOrgId
} from '../orgContext'

const route = useRoute()
const active = computed(() => route.query.tab || 'org')
/** 当前登录机构范围：平台账号无 org；机构账号有 orgId 或 orgCode（含 cssd_01→01） */
function readStaffOrgScope() {
  try {
    const s = JSON.parse(localStorage.getItem('cssd_staff') || 'null')
    if (!s) return { orgId: null, orgCode: null, isPlatform: true }
    // 平台 admin：忽略本地残留 orgId/orgCode（机构账号登录后残留会导致院区列表被滤空）
    if (String(s.staffNo || '').toLowerCase() === 'admin') {
      if (s.orgId || s.orgCode) {
        delete s.orgId
        delete s.orgCode
        try { localStorage.setItem('cssd_staff', JSON.stringify(s)) } catch { /* ignore */ }
      }
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
function persistStaffOrgScope(orgId, orgCode) {
  try {
    const s = JSON.parse(localStorage.getItem('cssd_staff') || '{}')
    if (orgId) s.orgId = orgId
    if (orgCode) s.orgCode = orgCode
    localStorage.setItem('cssd_staff', JSON.stringify(s))
  } catch { /* ignore */ }
}
/** 解析并缓存本机构 id（仅有编码时用组织列表反查） */
async function resolveStaffOrgScope() {
  const scope = readStaffOrgScope()
  if (scope.isPlatform || scope.orgId) return scope
  if (!scope.orgCode) return scope
  try {
    const res = await http.get('/md/campuses', { params: { page: 1, size: 200 } })
    const hit = (res.data.records || []).find((r) => String(r.campusCode) === String(scope.orgCode))
    if (hit?.id) {
      persistStaffOrgScope(hit.id, hit.campusCode)
      return { orgId: hit.id, orgCode: hit.campusCode, isPlatform: false }
    }
  } catch { /* ignore */ }
  return scope
}
function orgScopedStorageKey(base) {
  const s = readStaffOrgScope()
  if (s.isPlatform) return base
  return `${base}__${s.orgId || s.orgCode || 'org'}`
}
function rowBelongsToOrg(row, scope) {
  if (!scope || scope.isPlatform) return true
  if (scope.orgId && (row.orgId === scope.orgId || row.id === scope.orgId || row.remark === `ORG:${scope.orgId}`)) {
    return true
  }
  if (scope.orgCode) {
    if (String(row.orgCode) === String(scope.orgCode)) return true
    if (String(row.campusCode) === String(scope.orgCode)) return true
    if (String(row.staffNo || '').toLowerCase() === `cssd_${scope.orgCode}`.toLowerCase()) return true
  }
  return false
}
/** 平台管理员可维护全部机构；机构账号仅见本机构 */
const isPlatformAdmin = computed(() => readStaffOrgScope().isPlatform)
const orgLoading = ref(false)
const orgRows = ref([])
const orgSelected = ref([])
const orgTableRef = ref(null)
const orgPanelRef = ref(null)
const orgKeyword = ref('')
const orgPage = ref(1)
const orgPageSize = ref(10)
const orgTotal = ref(0)
const orgDrawerVisible = ref(false)
const authDialogVisible = ref(false)
const authTreeRef = ref(null)
const authDeptTreeRef = ref(null)
const authWarehouseTreeRef = ref(null)
const authTab = ref('menu')
const authPendingDeptKeys = ref([])
const authPendingWarehouseKeys = ref([])
const authDeptTreeData = ref([{ id: 'root', label: '科室', children: [] }])
const authWarehouseTreeData = ref([{ id: 'root', label: '仓库', children: [] }])
const authDialogTitle = computed(() => '授权')
const authShowScopeTabs = computed(() => authTarget.value?.kind === 'staff')
/** { kind: 'campus'|'staff', id, label } */
const authTarget = ref(null)
const staffLoading = ref(false)
const staffRows = ref([])
const staffSelected = ref([])
const staffTableRef = ref(null)
const staffPanelRef = ref(null)
const staffKeyword = ref('')
const staffPage = ref(1)
const staffPageSize = ref(10)
const staffTotal = ref(0)
const staffDrawerVisible = ref(false)
const staffDialogMode = ref('create') // create | edit
const staffSaving = ref(false)
const staffFormTab = ref('basic') // basic | qualify | train
const staffTrainRows = ref([])
const staffFormRef = ref(null)
const staffForm = reactive({
  id: '',
  staffNo: '',
  staffName: '',
  staffRole: '',
  password: '',
  age: null,
  education: '',
  major: '',
  jobTitle: '',
  energyLevel: '',
  contactName: '',
  contactPhone: '',
  qq: '',
  email: '',
  deptId: '',
  userType: 'normal',
  orgId: '',
  isActive: 1,
  remark: ''
})
const staffDeptOptions = ref([])
/** 列表展示科室名称用 */
const staffDeptLookup = ref([])
function staffDeptLabel(deptId) {
  if (!deptId) return '—'
  const hit = staffDeptLookup.value.find((d) => d.id === deptId)
    || staffDeptOptions.value.find((d) => d.id === deptId)
  if (!hit) return '—'
  return hit.deptCode && hit.deptName ? `${hit.deptCode}|${hit.deptName}` : (hit.deptName || hit.deptCode || '—')
}
const staffUserTypeOptions = [
  { code: 'normal', name: '普通用户' },
  { code: 'admin', name: '管理员' }
]
/** 仅平台 admin，或当前登录用户类型为管理员，可改他人用户类型 */
const canEditStaffUserType = computed(() => {
  try {
    const s = JSON.parse(localStorage.getItem('cssd_staff') || 'null')
    if (!s) return false
    const no = String(s.staffNo || '')
    if (no.toLowerCase() === 'admin') return true
    if (String(s.userType || '').toLowerCase() === 'admin') return true
    // 兼容尚未回填 userType 的机构初始化账号 cssd_01…
    if (/^cssd_\d+$/i.test(no)) return true
    return false
  } catch {
    return false
  }
})
const staffRules = {
  staffNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  staffName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  staffRole: [{ required: true, message: '请选择岗位', trigger: 'change' }],
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}
const staffDialogTitle = computed(() =>
  staffDialogMode.value === 'edit' ? '修改人员档案信息' : '新增人员档案信息'
)
/** 岗位选项：取自系统配置「基础参数 → 岗位维护」 */
const JOB_POST_CATEGORY = 'job-post'
const JOB_POST_STORAGE = 'cssd_job_posts'
/** 供应商类型：取自系统配置「岗位维护」后的「供应商类型维护」 */
const VENDOR_TYPE_CATEGORY = 'vendor-type'
const VENDOR_TYPE_STORAGE = 'cssd_vendor_types'
const jobPostSeed = [
  { code: 'admin', name: '管理员', value1: '', value2: '' },
  { code: 'nurse_lead', name: '护士长', value1: '', value2: '' },
  { code: 'operator', name: '操作员', value1: '', value2: '' },
  { code: 'qc', name: '质检员', value1: '', value2: '' },
  { code: 'delivery', name: '下送', value1: '', value2: '' },
  { code: 'clinical', name: '临床', value1: '', value2: '' },
  { code: 'surgeon', name: '术者', value1: '', value2: '' }
]
const vendorTypeSeed = [
  { code: 'external_instrument', name: '外来器械供应商', value1: '', value2: '' },
  { code: 'implant', name: '植入物供应商', value1: '', value2: '' },
  { code: 'reusable_owned', name: '自有复用器械供应商', value1: '', value2: '' },
  { code: 'cssd_consumable', name: 'CSSD 耗材供应商', value1: '', value2: '' },
  { code: 'device_maintain', name: '设备维保服务商', value1: '', value2: '' }
]
const jobPostOptions = ref([])
const vendorTypeOptions = ref([])
function readJobPosts() {
  try {
    const raw = localStorage.getItem(orgScopedStorageKey(JOB_POST_STORAGE))
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
  localStorage.setItem(orgScopedStorageKey(JOB_POST_STORAGE), JSON.stringify(rows))
  jobPostOptions.value = rows
}
function refreshJobPostOptions() {
  jobPostOptions.value = readJobPosts()
  buildStaffRoleTree()
}
function readVendorTypes() {
  try {
    const raw = localStorage.getItem(orgScopedStorageKey(VENDOR_TYPE_STORAGE))
    if (raw) {
      const list = JSON.parse(raw)
      if (Array.isArray(list) && list.length) return list
    }
  } catch { /* ignore */ }
  return vendorTypeSeed.map((r, i) => ({
    id: `vtype_${r.code}_${i}`,
    code: r.code,
    name: r.name,
    value1: r.value1,
    value2: r.value2
  }))
}
function writeVendorTypes(rows) {
  localStorage.setItem(orgScopedStorageKey(VENDOR_TYPE_STORAGE), JSON.stringify(rows))
  vendorTypeOptions.value = rows.map((r) => ({ value: r.code, label: r.name }))
}
function refreshVendorTypeOptions() {
  vendorTypeOptions.value = readVendorTypes().map((r) => ({ value: r.code, label: r.name }))
}
function roleLabel(role) {
  const hit = jobPostOptions.value.find((p) => p.code === role)
    || readJobPosts().find((p) => p.code === role)
  return hit?.name || role || '—'
}
function defaultStaffRole() {
  if (staffRoleFilter.value) return staffRoleFilter.value
  return jobPostOptions.value[0]?.code || readJobPosts()[0]?.code || ''
}
const staffRoleTreeRef = ref(null)
/** 左侧岗位树当前选中编码；空=全部岗位 */
const staffRoleFilter = ref('')
const staffRoleTreeData = ref([{ id: 'root', label: '岗位', children: [] }])
function buildStaffRoleTree() {
  const posts = jobPostOptions.value.length ? jobPostOptions.value : readJobPosts()
  staffRoleTreeData.value = [{
    id: 'root',
    label: '岗位',
    children: posts.map((p) => ({
      id: p.code,
      label: `${p.code}|${p.name}`
    }))
  }]
  nextTick(() => {
    staffRoleTreeRef.value?.setCurrentKey?.(staffRoleFilter.value || 'root')
  })
}
function onStaffRoleTreeClick(data) {
  staffRoleFilter.value = data?.id === 'root' ? '' : data?.id || ''
  staffPage.value = 1
  loadStaff()
}
const AUTH_DEFAULT_MENUS = ['station']
/** 与侧栏菜单对应的完整授权树（组织机构授权用） */
const authMenuTreeFull = [
  { id: 'station', label: '首页', disabled: true },
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
      { id: 'md-campuses', label: '院区维护' },
      { id: 'md-warehouses', label: '仓库维护' },
      { id: 'md-depts', label: '科室维护' },
      { id: 'md-staffs', label: '人员档案' },
      { id: 'md-perms', label: '权限管理' },
      { id: 'md-vendors', label: '供应商维护' },
      { id: 'md-manufacturers', label: '生产厂家维护' },
      { id: 'md-instruments', label: '器械基础档案' },
      { id: 'md-templates', label: '器械包档案' },
      { id: 'md-devices', label: '设备维护' },
      { id: 'md-units', label: '单位维护' },
      { id: 'md-sys-config', label: '系统配置' }
    ]
  }
]
/** 弹窗实际展示的授权树：人员/角色授权时按机构已授权菜单裁剪 */
const authDialogTree = ref(authMenuTreeFull.map((n) => ({ ...n })))
function collectAuthKeys(nodes, out = []) {
  for (const n of nodes || []) {
    out.push(n.id)
    if (n.children?.length) collectAuthKeys(n.children, out)
  }
  return out
}
/** 按允许的菜单键裁剪授权树；始终保留首页 */
function filterAuthMenuTree(nodes, allowedKeys) {
  if (!allowedKeys || !allowedKeys.length) {
    return authMenuTreeFull.filter((n) => AUTH_DEFAULT_MENUS.includes(n.id))
  }
  const allowed = new Set(allowedKeys)
  AUTH_DEFAULT_MENUS.forEach((k) => allowed.add(k))
  const walk = (list) => {
    const out = []
    for (const n of list || []) {
      if (n.children?.length) {
        const kids = walk(n.children)
        if (kids.length || allowed.has(n.id)) {
          out.push({ ...n, children: kids.length ? kids : undefined })
        }
      } else if (allowed.has(n.id)) {
        out.push({ ...n })
      }
    }
    return out
  }
  return walk(nodes)
}
function readCurrentUserMenuAuth() {
  try {
    const list = JSON.parse(localStorage.getItem('cssd_menu_auth') || 'null')
    return Array.isArray(list) ? list : null
  } catch {
    return null
  }
}
function intersectMenuKeys(a, b) {
  if (!a) return b || []
  if (!b) return a || []
  const setB = new Set(b)
  return a.filter((k) => setB.has(k))
}
function applyAuthDialogTree(allowedKeys) {
  // null = 全量（仅组织机构授权）
  if (allowedKeys == null) {
    authDialogTree.value = authMenuTreeFull
  } else {
    authDialogTree.value = filterAuthMenuTree(authMenuTreeFull, allowedKeys)
  }
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
  return (orgPage.value - 1) * orgPageSize.value + i + 1
}
function searchOrg() {
  orgPage.value = 1
  loadOrg()
}
function onOrgPageSizeChange() {
  orgPage.value = 1
  loadOrg()
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
/** 待回显到授权树的 keys（弹窗 @opened 时应用） */
const authPendingKeys = ref([])
function mergeDefaultAuthKeys(keys) {
  const set = new Set(AUTH_DEFAULT_MENUS)
  ;(Array.isArray(keys) ? keys : []).forEach((k) => {
    if (k) set.add(k)
  })
  return [...set]
}
function parseMenuAuthField(raw) {
  if (Array.isArray(raw)) return raw
  if (typeof raw === 'string' && raw.trim()) {
    try {
      const v = JSON.parse(raw)
      return Array.isArray(v) ? v : []
    } catch {
      return []
    }
  }
  return []
}
function applyAuthTreeChecks() {
  const keys = mergeDefaultAuthKeys(authPendingKeys.value)
  nextTick(() => {
    // leafOnly=true：避免父节点 key（如 md）把未授权子菜单一并勾上
    authTreeRef.value?.setCheckedKeys(keys, true)
    authDeptTreeRef.value?.setCheckedKeys(authPendingDeptKeys.value || [], true)
    authWarehouseTreeRef.value?.setCheckedKeys(authPendingWarehouseKeys.value || [], true)
  })
}
function onAuthDialogOpened() {
  applyAuthTreeChecks()
}
function onAuthTreeCheckChange(data, checked) {
  // 首页默认授权，禁止取消
  if (data?.id === 'station' && !checked) {
    nextTick(() => {
      authTreeRef.value?.setChecked('station', true, false)
    })
  }
}
function buildAuthDeptTree(rows) {
  const list = Array.isArray(rows) ? rows : []
  const map = new Map()
  list.forEach((r) => {
    map.set(r.id, { id: r.id, label: `${r.deptCode}|${r.deptName}`, children: [] })
  })
  const roots = []
  list.forEach((r) => {
    const node = map.get(r.id)
    if (r.parentId && map.has(r.parentId)) map.get(r.parentId).children.push(node)
    else roots.push(node)
  })
  authDeptTreeData.value = [{ id: 'root', label: '科室', children: roots }]
}
function buildAuthWarehouseTree(rows) {
  const list = Array.isArray(rows) ? rows : []
  authWarehouseTreeData.value = [{
    id: 'root',
    label: '仓库',
    children: list.map((r) => ({
      id: r.id,
      label: `${r.warehouseCode || ''}|${r.warehouseName || ''}`.replace(/^\|/, '').replace(/\|$/, '') || r.id
    }))
  }]
}
async function loadAuthScopeTrees(orgId) {
  const oid = orgId || effectiveOrgId()
  try {
    const deptParams = { page: 1, size: 500 }
    if (oid) deptParams.orgId = oid
    const deptRes = await http.get('/md/depts', { params: deptParams })
    buildAuthDeptTree(deptRes.data.records || [])
  } catch {
    buildAuthDeptTree([])
  }
  try {
    const whParams = { page: 1, size: 500 }
    if (oid) whParams.orgId = oid
    const whRes = await http.get('/md/warehouses', { params: whParams })
    buildAuthWarehouseTree(whRes.data.records || [])
  } catch {
    buildAuthWarehouseTree([])
  }
}
function openMenuAuth(kind, row, label, presetKeys, allowedKeys, scopeAuth) {
  authTarget.value = { kind, id: row.id, label, row }
  authTab.value = 'menu'
  applyAuthDialogTree(allowedKeys === undefined ? null : allowedKeys)
  let keys = []
  // 显式传入数组（含空数组）时以接口结果为准，勿回退到可能过期的 localStorage
  if (Array.isArray(presetKeys)) {
    keys = presetKeys
  } else {
    try {
      keys = JSON.parse(localStorage.getItem(authStorageKey(kind, row.id)) || '[]')
    } catch {
      keys = []
    }
  }
  // 人员/角色授权：勾选结果限制在可见树内
  if (allowedKeys != null) {
    const visible = new Set(collectAuthKeys(authDialogTree.value))
    keys = keys.filter((k) => visible.has(k))
  }
  authPendingKeys.value = mergeDefaultAuthKeys(keys)
  authPendingDeptKeys.value = parseMenuAuthField(scopeAuth?.deptAuth)
  authPendingWarehouseKeys.value = parseMenuAuthField(scopeAuth?.warehouseAuth)
  authDialogVisible.value = true
}
async function openOrgAuth(row) {
  let keys = parseMenuAuthField(row.menuAuth)
  if (!keys.length) {
    try {
      keys = JSON.parse(localStorage.getItem(authStorageKey('campus', row.id)) || '[]')
    } catch {
      keys = []
    }
  }
  // 若库中无授权但账号已初始化，尝试从接口再取一次最新机构
  if (!keys.length) {
    try {
      const res = await http.get('/md/campuses', { params: { page: 1, size: 200, keyword: row.campusCode } })
      const hit = (res.data.records || []).find((r) => r.id === row.id)
      keys = parseMenuAuthField(hit?.menuAuth)
    } catch { /* ignore */ }
  }
  // 组织机构授权：展示完整菜单树
  openMenuAuth('campus', row, row.campusName || row.campusCode, keys, null)
}
async function openStaffAuth(row) {
  let keys = []
  let orgMenuAuth = []
  let deptAuth = []
  let warehouseAuth = []
  // 机构已授权菜单：始终单独拉取，作为人员授权树上限
  const org = orgRows.value.find((o) => o.id === row.orgId)
    || (await loadOrgForAuth(row.orgId))
  orgMenuAuth = parseMenuAuthField(org?.menuAuth)
  try {
    const res = await http.get(`/md/staffs/${row.id}/menu-auth`)
    keys = parseMenuAuthField(res.data?.menuAuth)
    deptAuth = parseMenuAuthField(res.data?.deptAuth)
    warehouseAuth = parseMenuAuthField(res.data?.warehouseAuth)
    const fromApi = parseMenuAuthField(res.data?.orgMenuAuth)
    if (fromApi.length) orgMenuAuth = fromApi
  } catch {
    try {
      keys = JSON.parse(localStorage.getItem(authStorageKey('staff', row.id)) || '[]')
    } catch {
      keys = []
    }
  }
  // 人员授权树 = 机构已授权 ∩（机构账号再与自身菜单取交集）；禁止展示未授权菜单
  let allowed = orgMenuAuth
  if (!isPlatformAdmin.value) {
    const mine = readCurrentUserMenuAuth() || []
    allowed = orgMenuAuth.length
      ? intersectMenuKeys(orgMenuAuth, mine)
      : mine
  }
  if (!allowed?.length) allowed = [...AUTH_DEFAULT_MENUS]
  await loadAuthScopeTrees(row.orgId || effectiveOrgId())
  openMenuAuth('staff', row, row.staffName || row.staffNo, keys, allowed, {
    deptAuth,
    warehouseAuth
  })
}
async function loadOrgForAuth(orgId) {
  if (!orgId) return null
  const hit = orgRows.value.find((o) => o.id === orgId)
  if (hit) return hit
  try {
    const res = await http.get('/md/campuses', { params: { page: 1, size: 200 } })
    const rows = res.data.records || []
    return rows.find((r) => r.id === orgId) || null
  } catch {
    return null
  }
}
function openPermAuth(row) {
  // 角色授权同样限制在当前登录账号可见菜单内（机构管理员不可授机构未授权项）
  const mine = readCurrentUserMenuAuth()
  const allowed = isPlatformAdmin.value ? null : (mine || [])
  openMenuAuth('role', { id: row.role }, row.roleName || row.role, undefined, allowed)
}
function authSelectAll() {
  if (!authShowScopeTabs.value || authTab.value === 'menu') {
    authTreeRef.value?.setCheckedKeys(collectAuthKeys(authDialogTree.value), false)
  } else if (authTab.value === 'dept') {
    authDeptTreeRef.value?.setCheckedKeys(collectAuthLeafKeys(authDeptTreeData.value), true)
  } else if (authTab.value === 'warehouse') {
    authWarehouseTreeRef.value?.setCheckedKeys(collectAuthLeafKeys(authWarehouseTreeData.value), true)
  }
}
function authClearAll() {
  if (!authShowScopeTabs.value || authTab.value === 'menu') {
    // 取消时仍保留默认「首页」
    authTreeRef.value?.setCheckedKeys([...AUTH_DEFAULT_MENUS], false)
  } else if (authTab.value === 'dept') {
    authDeptTreeRef.value?.setCheckedKeys([], true)
  } else if (authTab.value === 'warehouse') {
    authWarehouseTreeRef.value?.setCheckedKeys([], true)
  }
}
/** 收集授权树叶子节点 id（保存/回显只用叶子，避免父节点半选导致全勾） */
function collectAuthLeafKeys(nodes, out = []) {
  for (const n of nodes || []) {
    if (n.children?.length) collectAuthLeafKeys(n.children, out)
    else if (n.id && n.id !== 'root') out.push(n.id)
  }
  return out
}
async function submitMenuAuth() {
  const target = authTarget.value
  if (!target) return
  // 只取叶子勾选；不要合并 half-checked 父节点（否则下次 setCheckedKeys 会勾上全部子项）
  const checkedLeaves = authTreeRef.value?.getCheckedKeys?.(true) || []
  let keys = mergeDefaultAuthKeys(checkedLeaves)
  // 人员授权不得超出弹窗可见范围（机构已授权菜单）
  if (target.kind === 'staff' || target.kind === 'role') {
    const visibleLeaves = new Set(collectAuthLeafKeys(authDialogTree.value))
    keys = keys.filter((k) => visibleLeaves.has(k) || AUTH_DEFAULT_MENUS.includes(k))
    keys = mergeDefaultAuthKeys(keys)
  } else if (target.kind === 'campus') {
    // 机构授权同样只存叶子，父级由侧栏按子项推导
    const visibleLeaves = new Set(collectAuthLeafKeys(authDialogTree.value))
    keys = keys.filter((k) => visibleLeaves.has(k) || AUTH_DEFAULT_MENUS.includes(k))
    keys = mergeDefaultAuthKeys(keys)
  }
  const deptIds = (authDeptTreeRef.value?.getCheckedKeys?.(true) || [])
    .filter((id) => id && id !== 'root')
  const warehouseIds = (authWarehouseTreeRef.value?.getCheckedKeys?.(true) || [])
    .filter((id) => id && id !== 'root')
  localStorage.setItem(authStorageKey(target.kind, target.id), JSON.stringify(keys))
  if (target.kind === 'campus') {
    try {
      await http.put(`/md/campuses/${target.id}/menu-auth`, { menuKeys: keys })
      const row = orgRows.value.find((r) => r.id === target.id)
      if (row) row.menuAuth = JSON.stringify(keys)
    } catch (e) {
      ElMessage.error(e?.message || '保存机构授权失败')
      return
    }
  } else if (target.kind === 'staff') {
    try {
      const res = await http.put(`/md/staffs/${target.id}/menu-auth`, {
        menuKeys: keys,
        deptIds,
        warehouseIds
      })
      const saved = parseMenuAuthField(res.data?.menuAuth)
      keys = saved.length ? saved : keys
      localStorage.setItem(authStorageKey('staff', target.id), JSON.stringify(keys))
    } catch (e) {
      ElMessage.error(e?.message || '保存人员授权失败')
      return
    }
  }
  ElMessage.success(`已保存「${target.label}」授权`)
  authDialogVisible.value = false
  if (target.kind === 'role') loadPerms()
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
  let authKeys = mergeDefaultAuthKeys(parseMenuAuthField(row.menuAuth))
  if (authKeys.length <= AUTH_DEFAULT_MENUS.length) {
    try {
      authKeys = mergeDefaultAuthKeys(
        JSON.parse(localStorage.getItem(authStorageKey('campus', row.id)) || '[]')
      )
    } catch {
      authKeys = [...AUTH_DEFAULT_MENUS]
    }
  }
  if (authKeys.length <= AUTH_DEFAULT_MENUS.length) {
    ElMessage.warning('请先对该组织机构完成菜单授权（首页为默认），再执行初始化')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认初始化组织机构「${row.campusCode} ${row.campusName}」？首次将生成管理员账号（cssd_01 / cssd_02…），密码与账号相同；已初始化过则不会重复建号。`,
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
  try {
    const res = await http.post(`/md/campuses/${row.id}/init-admin`, { menuKeys: authKeys })
    const data = res.data || {}
    const staffNo = data.staffNo || ''
    const staffId = data.staffId || ''
    if (staffId) {
      localStorage.setItem(authStorageKey('staff', staffId), JSON.stringify(data.menuAuth || authKeys))
    }
    if (data.alreadyInitialized) {
      await ElMessageBox.alert(
        `该机构已初始化，不再重复创建账号\n账号：${staffNo}\n姓名：${data.staffName || ''}`,
        '已初始化',
        { confirmButtonText: '知道了', type: 'info' }
      )
      return
    }
    await ElMessageBox.alert(
      `管理员账号已生成（仅保留已授权菜单，含默认首页）\n账号：${staffNo}\n密码：${data.password || staffNo}\n姓名：${data.staffName || ''}`,
      '初始化成功',
      { confirmButtonText: '知道了', type: 'success' }
    )
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '初始化失败')
  }
}
function staffIndex(i) {
  return (staffPage.value - 1) * staffPageSize.value + i + 1
}
function searchStaff() {
  staffPage.value = 1
  loadStaff()
}
function onStaffPageSizeChange() {
  staffPage.value = 1
  loadStaff()
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
async function loadStaffDeptLookup() {
  try {
    const oid = effectiveOrgId()
    const params = { page: 1, size: 500 }
    if (oid) params.orgId = oid
    const res = await http.get('/md/depts', { params })
    staffDeptLookup.value = res.data.records || []
  } catch {
    staffDeptLookup.value = []
  }
}
async function loadStaff() {
  refreshJobPostOptions()
  staffLoading.value = true
  try {
    await loadStaffDeptLookup()
    const scope = await resolveStaffOrgScope()
    const kw = staffKeyword.value?.trim() || undefined
    const params = {
      page: staffPage.value,
      size: staffPageSize.value,
      keyword: kw
    }
    if (staffRoleFilter.value) params.staffRole = staffRoleFilter.value
    const res = await http.get('/md/staffs', { params })
    let rows = res.data.records || []
    // 服务端已按机构/岗位/关键字分页；前端仅做归属兜底，禁止再 slice，否则 total 变成当前页条数、第 2 页会空
    const oid = listOrgId(scope)
    if (oid) {
      const filterScope = { ...scope, orgId: oid, isPlatform: false }
      rows = rows.filter((r) => rowBelongsToOrg(r, filterScope))
    }
    staffTotal.value = Number(res.data.total || 0)
    staffRows.value = rows
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
  staffForm.password = row?.password || row?.staffNo || ''
  staffForm.age = row?.age ?? null
  staffForm.education = row?.education || ''
  staffForm.major = row?.major || ''
  staffForm.jobTitle = row?.jobTitle || ''
  staffForm.energyLevel = row?.energyLevel || ''
  staffForm.contactName = row?.contactName || ''
  staffForm.contactPhone = row?.contactPhone || ''
  staffForm.qq = row?.qq || ''
  staffForm.email = row?.email || ''
  staffForm.deptId = row?.deptId || ''
  staffForm.userType = normalizeStaffUserType(row)
  staffForm.orgId = row?.orgId || effectiveOrgId() || ''
  staffForm.isActive = row?.isActive ?? 1
  staffForm.remark = row?.remark || ''
}
/** 新增默认普通用户；cssd_* / 平台 admin 默认管理员 */
function normalizeStaffUserType(row) {
  if (!row) return 'normal'
  const raw = String(row.userType || '').toLowerCase()
  if (raw === 'admin') return 'admin'
  if (raw === 'normal') return 'normal'
  const no = String(row.staffNo || '')
  if (no.toLowerCase() === 'admin' || /^cssd_\d+$/i.test(no)) return 'admin'
  return 'normal'
}
function resetStaffForm() {
  fillStaffForm(null)
  staffFormTab.value = 'basic'
  staffTrainRows.value = []
  staffFormRef.value?.clearValidate?.()
}
async function loadStaffDeptOptions(orgId) {
  const oid = orgId || staffForm.orgId || effectiveOrgId()
  if (!oid) {
    staffDeptOptions.value = []
    return
  }
  try {
    const params = { page: 1, size: 500, orgId: oid }
    const res = await http.get('/md/depts', { params })
    staffDeptOptions.value = res.data.records || []
  } catch {
    staffDeptOptions.value = []
  }
}
async function onStaffOrgChange() {
  staffForm.deptId = ''
  await loadStaffDeptOptions(staffForm.orgId)
}
async function openStaffCreate() {
  refreshJobPostOptions()
  await loadOrgOptions()
  staffDialogMode.value = 'create'
  resetStaffForm()
  staffFormTab.value = 'basic'
  staffForm.password = ''
  if (!isPlatformAdmin.value) {
    const scope = await resolveStaffOrgScope()
    staffForm.orgId = scope.orgId || effectiveOrgId() || ''
  } else if (!staffForm.orgId) {
    staffForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  }
  await loadStaffDeptOptions(staffForm.orgId)
  staffDrawerVisible.value = true
}

/** 导入列（逻辑名，不含 *）；用户类型不进模板，导入固定为普通用户 */
const STAFF_IMPORT_HEADERS = [
  '工号', '姓名', '岗位名称', '密码', '年龄', '学历', '专业', '职称', '能级',
  '联系人', '联系电话', 'QQ', '电子邮箱', '科室名称', '是否启用', '备注'
]
const STAFF_IMPORT_REQUIRED = new Set(['工号', '姓名'])
const staffImportVisible = ref(false)
const staffImportFile = ref(null)
const staffImportFileName = ref('')
const staffImportRows = ref([])
const staffImportSaving = ref(false)
const staffImportDone = ref(0)
const staffImportTotal = ref(0)
const staffImportProgress = computed(() => {
  const total = staffImportTotal.value
  if (!total) return 0
  return Math.min(100, Math.round((staffImportDone.value / total) * 100))
})
function openStaffImport() {
  staffImportFile.value = null
  staffImportFileName.value = ''
  staffImportRows.value = []
  staffImportSaving.value = false
  staffImportDone.value = 0
  staffImportTotal.value = 0
  staffImportVisible.value = true
}
function downloadStaffImportTemplate() {
  refreshJobPostOptions()
  const escapeHtml = (s) => String(s ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  const colCount = STAFF_IMPORT_HEADERS.length
  const cellStyle = "font-family:'等线',DengXian,sans-serif;font-size:12pt;border:0.5pt solid #000;text-align:center;vertical-align:middle;mso-number-format:'\\@';"
  const titleStyle = "font-family:'等线',DengXian,sans-serif;font-size:16pt;font-weight:bold;text-align:center;vertical-align:middle;border:none;"
  const headerCells = STAFF_IMPORT_HEADERS.map((h) => {
    const label = STAFF_IMPORT_REQUIRED.has(h) ? `${h}*` : h
    const color = STAFF_IMPORT_REQUIRED.has(h) ? 'color:#FF0000;font-weight:bold;' : 'font-weight:bold;'
    return `<td style="${cellStyle}${color}">${escapeHtml(label)}</td>`
  }).join('')
  const sampleRole = (jobPostOptions.value.find((p) => p.code === 'admin') || jobPostOptions.value[0] || { name: '管理员' }).name
  const sampleVals = ['10283', '张三', sampleRole, '10283', '30', '本科', '护理', '护师', 'N1', '张三', '13800000000', '', '', '', '1', '']
  const dataCells = sampleVals.map((v) => `<td style="${cellStyle}">${escapeHtml(v)}</td>`).join('')
  const html = `<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
<head><meta charset="UTF-8">
<!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>
<x:Name>人员档案导入</x:Name>
<x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions>
</x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->
</head>
<body>
<table border="0" cellspacing="0" cellpadding="4">
<tr><td colspan="${colCount}" style="${titleStyle}">人员档案导入</td></tr>
<tr>${headerCells}</tr>
<tr>${dataCells}</tr>
</table>
</body></html>`
  const blob = new Blob(['\ufeff' + html], { type: 'application/vnd.ms-excel;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '人员档案导入模板.xls'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('模板已下载（.xls，可用 Excel 打开编辑）')
}
function onStaffImportFileChange(file) {
  staffImportFile.value = file?.raw || null
  staffImportFileName.value = file?.name || ''
  staffImportRows.value = []
  if (staffImportFile.value) {
    nextTick(() => parseStaffImportFile())
  }
}
function normalizeImportHeader(h) {
  return String(h || '')
    .replace(/^\ufeff/, '')
    .replace(/[\u200B-\u200D\uFEFF]/g, '')
    .replace(/^["'\s]+|["'\s]+$/g, '')
    .replace(/\s+/g, '')
    .trim()
}
function detectCsvDelimiter(headerLine) {
  const candidates = [',', '\t', ';', '，']
  let best = ','
  let bestCount = -1
  for (const d of candidates) {
    const count = splitCsvLine(headerLine, d).filter(Boolean).length
    if (count > bestCount) {
      bestCount = count
      best = d
    }
  }
  return best
}
function splitCsvLine(line, delimiter = ',') {
  const out = []
  let cur = ''
  let inQ = false
  for (let i = 0; i < line.length; i++) {
    const ch = line[i]
    if (ch === '"') {
      if (inQ && line[i + 1] === '"') { cur += '"'; i++ }
      else inQ = !inQ
    } else if (ch === delimiter && !inQ) {
      out.push(cur)
      cur = ''
    } else cur += ch
  }
  out.push(cur)
  return out.map((s) => String(s || '').replace(/^\ufeff/, '').replace(/[\u200B-\u200D\uFEFF]/g, '').trim())
}
function decodeImportBuffer(buf) {
  const encodings = ['utf-8', 'gb18030', 'gbk']
  let fallback = ''
  for (const enc of encodings) {
    try {
      const text = new TextDecoder(enc, { fatal: false }).decode(buf)
      if (!fallback) fallback = text
      if (/工号/.test(text) && /姓名/.test(text)) return text
      if (!text.includes('\uFFFD') && /工号/.test(text)) return text
    } catch { /* try next */ }
  }
  return fallback || new TextDecoder('utf-8').decode(buf)
}
function parseHtmlTableRows(html) {
  const doc = new DOMParser().parseFromString(html, 'text/html')
  const table = doc.querySelector('table')
  if (!table) return null
  const rows = []
  table.querySelectorAll('tr').forEach((tr) => {
    const cells = [...tr.querySelectorAll('th,td')].map((td) => String(td.textContent || '').replace(/^\ufeff/, '').replace(/[\u200B-\u200D\uFEFF]/g, '').trim())
    if (cells.some((c) => c)) rows.push(cells)
  })
  return rows.length ? rows : null
}
function resolveHeaderIndex(headers) {
  const aliases = {
    工号: ['工号', '工号*', '员工号', '用户名', '账号'],
    姓名: ['姓名', '姓名*', '名称', '员工姓名'],
    岗位名称: ['岗位名称', '岗位', '岗位编码', '角色编码', 'staffRole'],
    密码: ['密码', '口令', 'password'],
    年龄: ['年龄'],
    学历: ['学历'],
    专业: ['专业'],
    职称: ['职称'],
    能级: ['能级', '能级（层级）', '层级'],
    联系人: ['联系人'],
    联系电话: ['联系电话', '电话', '手机'],
    QQ: ['QQ', 'qq'],
    电子邮箱: ['电子邮箱', '邮箱', 'email', 'Email'],
    科室名称: ['科室名称', '科室', '科室编码', '部门编码', '部门名称'],
    是否启用: ['是否启用', '启用'],
    备注: ['备注']
  }
  const map = {}
  Object.keys(aliases).forEach((key) => {
    const i = headers.findIndex((h) => {
      const nh = normalizeImportHeader(h).replace(/\*+$/, '')
      return aliases[key].some((a) => {
        const na = normalizeImportHeader(a).replace(/\*+$/, '')
        return nh === na || nh.includes(na) || na.includes(nh)
      })
    })
    map[key] = i
  })
  return map
}
function pickHeaderRow(matrix) {
  const scan = Math.min(matrix.length, 8)
  for (let i = 0; i < scan; i++) {
    const headers = (matrix[i] || []).map(normalizeImportHeader)
    const col = resolveHeaderIndex(headers)
    if (col['工号'] >= 0 && col['姓名'] >= 0) {
      return { headerIndex: i, headers, col }
    }
    // 模板固定列序兜底（跳过标题行）
    const first = headers[0] || ''
    if (headers.length >= 2 && (first === '工号' || first === '工号*' || first.includes('工号'))) {
      const fixed = {}
      STAFF_IMPORT_HEADERS.forEach((h, idx) => { fixed[h] = idx })
      return { headerIndex: i, headers, col: fixed }
    }
  }
  return null
}
function matchStaffRoleByLabel(raw) {
  const posts = jobPostOptions.value.length ? jobPostOptions.value : readJobPosts()
  const v = String(raw || '').trim()
  if (!v) return { code: defaultStaffRole(), name: roleLabel(defaultStaffRole()), ok: true }
  const nv = normalizeImportHeader(v)
  const byName = posts.find((p) => String(p.name) === v || normalizeImportHeader(p.name) === nv)
  if (byName) return { code: byName.code, name: byName.name, ok: true }
  // 兼容旧模板误填编码
  const byCode = posts.find((p) => String(p.code) === v || normalizeImportHeader(p.code) === nv)
  if (byCode) return { code: byCode.code, name: byCode.name, ok: true }
  return { code: '', name: v, ok: false }
}
function matchDeptByLabel(raw) {
  const v = String(raw || '').trim()
  if (!v) return { hit: null, ok: true }
  const nv = normalizeImportHeader(v)
  const hit = staffDeptLookup.value.find((d) => {
    const name = String(d.deptName || '')
    const code = String(d.deptCode || '')
    return name === v || normalizeImportHeader(name) === nv
      || code === v || normalizeImportHeader(code) === nv
  })
  return { hit: hit || null, ok: !!hit }
}
/** 拉取本机构已有工号，供导入解析时查重 */
async function loadExistingStaffNoSet() {
  const set = new Set()
  const size = 500
  try {
    const first = await http.get('/md/staffs', { params: { page: 1, size } })
    for (const r of (first.data.records || [])) {
      const no = String(r.staffNo || '').trim()
      if (no) set.add(no)
    }
    const total = Number(first.data.total || 0)
    const pages = Math.min(Math.ceil(total / size) || 1, 40)
    for (let page = 2; page <= pages; page++) {
      const res = await http.get('/md/staffs', { params: { page, size } })
      for (const r of (res.data.records || [])) {
        const no = String(r.staffNo || '').trim()
        if (no) set.add(no)
      }
    }
  } catch { /* 查重失败时仍允许解析，导入时由后端兜底 */ }
  return set
}
async function parseStaffImportFile() {
  if (!staffImportFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  refreshJobPostOptions()
  await loadStaffDeptLookup()
  const existingStaffNos = await loadExistingStaffNoSet()
  const buf = await staffImportFile.value.arrayBuffer()
  const text = decodeImportBuffer(buf)
  const candidates = []
  if (/<table[\s>]/i.test(text)) {
    const htmlMatrix = parseHtmlTableRows(text)
    if (htmlMatrix?.length) candidates.push(htmlMatrix)
  }
  {
    const lines = text.replace(/^\ufeff/, '').split(/\r?\n/).filter((l) => l.trim())
    if (lines.length) {
      const delimiter = detectCsvDelimiter(lines[0])
      candidates.push(lines.map((l) => splitCsvLine(l, delimiter)))
      for (const d of [',', '\t', ';', '，']) {
        if (d === delimiter) continue
        candidates.push(lines.map((l) => splitCsvLine(l, d)))
      }
    }
  }
  let picked = null
  let matrix = null
  for (const m of candidates) {
    const hit = pickHeaderRow(m)
    if (hit) {
      matrix = m
      picked = hit
      break
    }
  }
  if (!picked || !matrix) {
    const preview = (candidates[0]?.[0] || []).map(normalizeImportHeader).filter(Boolean).slice(0, 8).join('、')
    ElMessage.error(preview
      ? `模板表头不正确（识别到：${preview}），请重新点击「下载模板」后再选择该文件解析`
      : '未能识别文件内容，请重新下载模板')
    staffImportRows.value = []
    return
  }
  const { headerIndex, col } = picked
  const getFrom = (cols, key) => {
    const i = col[key]
    return i >= 0 ? String(cols[i] ?? '').trim() : ''
  }
  const rows = []
  const seenInFile = new Set()
  for (let li = headerIndex + 1; li < matrix.length; li++) {
    const cols = matrix[li]
    const staffNo = getFrom(cols, '工号')
    const staffName = getFrom(cols, '姓名')
    if (!staffNo && !staffName) continue
    // 跳过仍是表头/标题的误解析行
    if (normalizeImportHeader(staffNo).includes('工号') || staffName === '人员档案导入') continue
    const roleHit = matchStaffRoleByLabel(getFrom(cols, '岗位名称'))
    const deptRaw = getFrom(cols, '科室名称')
    const deptHit = matchDeptByLabel(deptRaw)
    const activeRaw = getFrom(cols, '是否启用')
    const isActive = activeRaw === '0' || activeRaw === '否' || activeRaw.toLowerCase() === 'false' ? 0 : 1
    const ageRaw = getFrom(cols, '年龄')
    const errors = []
    if (!staffNo) errors.push('缺工号')
    if (!staffName) errors.push('缺姓名')
    if (!roleHit.ok) errors.push('岗位名称无效')
    if (deptRaw && !deptHit.ok) errors.push('科室名称无效')
    if (staffNo && existingStaffNos.has(staffNo)) {
      errors.push('系统已存在，请勿重复导入')
    } else if (staffNo && seenInFile.has(staffNo)) {
      errors.push('文件内工号重复')
    }
    if (staffNo) seenInFile.add(staffNo)
    rows.push({
      staffNo,
      staffName,
      staffRole: roleHit.code || defaultStaffRole(),
      roleName: roleHit.name || roleLabel(roleHit.code),
      password: getFrom(cols, '密码') || staffNo,
      age: ageRaw ? Number(ageRaw) : null,
      education: getFrom(cols, '学历') || null,
      major: getFrom(cols, '专业') || null,
      jobTitle: getFrom(cols, '职称') || null,
      energyLevel: getFrom(cols, '能级') || null,
      contactName: getFrom(cols, '联系人') || null,
      contactPhone: getFrom(cols, '联系电话') || null,
      qq: getFrom(cols, 'QQ') || null,
      email: getFrom(cols, '电子邮箱') || null,
      deptName: deptHit.hit?.deptName || deptRaw || '',
      deptId: deptHit.hit?.id || null,
      userType: 'normal',
      isActive,
      remark: getFrom(cols, '备注') || null,
      error: errors.join('；')
    })
  }
  staffImportRows.value = rows
  if (!rows.length) ElMessage.warning('未解析到数据行，请在模板中填写后再解析')
  else {
    const dup = rows.filter((r) => r.error && String(r.error).includes('系统已存在')).length
    const tip = dup ? `，其中 ${dup} 条工号系统已存在` : ''
    ElMessage.success(`已解析 ${rows.length} 行${tip}`)
  }
}
async function submitStaffImport() {
  if (!staffImportRows.value.length) {
    ElMessage.warning('请先解析出数据后再导入')
    return
  }
  if (staffImportRows.value.some((r) => r.error)) {
    ElMessage.warning('有校验未通过的请核对！')
    return
  }
  const rows = staffImportRows.value
  staffImportSaving.value = true
  staffImportDone.value = 0
  staffImportTotal.value = rows.length
  let ok = 0
  let fail = 0
  try {
    const orgId = effectiveOrgId() || orgOptions.value[0]?.id || null
    const orgHit = orgOptions.value.find((o) => o.id === orgId)
    for (const r of rows) {
      try {
        await http.post('/md/staffs', {
          staffNo: r.staffNo,
          staffName: r.staffName,
          staffRole: r.staffRole,
          password: r.password,
          age: Number.isFinite(r.age) ? r.age : null,
          education: r.education,
          major: r.major,
          jobTitle: r.jobTitle,
          energyLevel: r.energyLevel,
          contactName: r.contactName,
          contactPhone: r.contactPhone,
          qq: r.qq,
          email: r.email,
          deptId: r.deptId,
          userType: 'normal',
          orgId,
          orgCode: orgHit?.campusCode || null,
          isActive: r.isActive,
          remark: r.remark
        })
        ok++
      } catch {
        fail++
      } finally {
        staffImportDone.value += 1
      }
    }
    ElMessage.success(`导入完成：成功 ${ok} 条${fail ? `，失败 ${fail} 条` : ''}`)
    if (ok) {
      staffImportVisible.value = false
      await loadStaff()
    }
  } finally {
    staffImportSaving.value = false
  }
}
/** 新增时：密码为空则跟随工号作为默认明文 */
watch(
  () => staffForm.staffNo,
  (no) => {
    if (staffDialogMode.value !== 'create') return
    if (!staffForm.password?.trim()) {
      staffForm.password = (no || '').trim()
    }
  }
)
async function openStaffEdit(row) {
  refreshJobPostOptions()
  await loadOrgOptions()
  staffDialogMode.value = 'edit'
  staffFormTab.value = 'basic'
  staffTrainRows.value = []
  fillStaffForm(row)
  if (!isPlatformAdmin.value) {
    const scope = await resolveStaffOrgScope()
    staffForm.orgId = scope.orgId || staffForm.orgId || effectiveOrgId() || ''
  }
  await loadStaffDeptOptions(staffForm.orgId)
  staffDrawerVisible.value = true
}
async function submitStaffForm() {
  const form = staffFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    staffSaving.value = true
    try {
      const orgHit = orgOptions.value.find((o) => o.id === staffForm.orgId)
      const payload = {
        staffNo: staffForm.staffNo.trim(),
        staffName: staffForm.staffName.trim(),
        staffRole: staffForm.staffRole,
        password: staffForm.password?.trim() || staffForm.staffNo.trim(),
        age: staffForm.age == null || staffForm.age === '' ? null : Number(staffForm.age),
        education: staffForm.education?.trim() || null,
        major: staffForm.major?.trim() || null,
        jobTitle: staffForm.jobTitle?.trim() || null,
        energyLevel: staffForm.energyLevel?.trim() || null,
        contactName: staffForm.contactName?.trim() || null,
        contactPhone: staffForm.contactPhone?.trim() || null,
        qq: staffForm.qq?.trim() || null,
        email: staffForm.email?.trim() || null,
        deptId: staffForm.deptId || null,
        userType: canEditStaffUserType.value ? (staffForm.userType || 'normal') : staffForm.userType || 'normal',
        orgId: staffForm.orgId || null,
        orgCode: orgHit?.campusCode || null,
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
const isDictLikeSysCategory = computed(() =>
  sysCategoryId.value === JOB_POST_CATEGORY || sysCategoryId.value === VENDOR_TYPE_CATEGORY
)
const sysConfigDialogTitle = computed(() => {
  const cat = sysCategoryId.value
  if (sysConfigDialogMode.value === 'edit') {
    if (cat === JOB_POST_CATEGORY) return '修改岗位'
    if (cat === VENDOR_TYPE_CATEGORY) return '修改供应商类型'
    return '修改系统参数'
  }
  if (cat === JOB_POST_CATEGORY) return '新增岗位'
  if (cat === VENDOR_TYPE_CATEGORY) return '新增供应商类型'
  return '新增系统参数'
})
const sysConfigCodeLabel = computed(() => {
  if (sysCategoryId.value === JOB_POST_CATEGORY) return '岗位编码'
  if (sysCategoryId.value === VENDOR_TYPE_CATEGORY) return '类型编码'
  return '编号'
})
const sysConfigNameLabel = computed(() => {
  if (sysCategoryId.value === JOB_POST_CATEGORY) return '岗位名称'
  if (sysCategoryId.value === VENDOR_TYPE_CATEGORY) return '类型名称'
  return '名称'
})
const sysConfigCodePlaceholder = computed(() => {
  if (sysCategoryId.value === JOB_POST_CATEGORY) return '如 admin'
  if (sysCategoryId.value === VENDOR_TYPE_CATEGORY) return '如 implant'
  return '参数编号'
})
const sysConfigNamePlaceholder = computed(() => {
  if (sysCategoryId.value === JOB_POST_CATEGORY) return '如 管理员'
  if (sysCategoryId.value === VENDOR_TYPE_CATEGORY) return '如 植入物供应商'
  return '参数名称'
})
const sysConfigTree = [
  {
    id: 'root',
    label: '系统参数',
    children: [
      { id: 'basic', label: '基础参数|01' },
      { id: JOB_POST_CATEGORY, label: '岗位维护' },
      { id: VENDOR_TYPE_CATEGORY, label: '供应商类型维护' },
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
  if (categoryId === VENDOR_TYPE_CATEGORY) return readVendorTypes()
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
  if (categoryId === VENDOR_TYPE_CATEGORY) {
    writeVendorTypes(rows)
    return
  }
  localStorage.setItem(sysConfigStorageKey(campusId, categoryId), JSON.stringify(rows))
}
async function loadSysConfigPage() {
  refreshJobPostOptions()
  refreshVendorTypeOptions()
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
    localStorage.removeItem(orgScopedStorageKey(JOB_POST_STORAGE))
    refreshJobPostOptions()
  } else if (sysCategoryId.value === VENDOR_TYPE_CATEGORY) {
    localStorage.removeItem(orgScopedStorageKey(VENDOR_TYPE_STORAGE))
    refreshVendorTypeOptions()
  } else {
    localStorage.removeItem(sysConfigStorageKey(sysCampusId.value, sysCategoryId.value))
  }
  ElMessage.success('已初始化')
  loadSysConfigRows()
}
function onSysConfigSettings() {
  ElMessage.info('设置功能待开发')
}
const mdCampusOptions = ref([])
async function loadMdCampusOptions() {
  try {
    const scope = await resolveStaffOrgScope()
    const oid = listOrgId(scope)
    const res = await http.get('/md/areas', {
      params: { size: 200, ...(oid ? { orgId: oid } : {}) }
    })
    const list = res.data.records || []
    mdCampusOptions.value = list.map((a) => ({
      id: a.id,
      campusCode: a.areaCode,
      campusName: a.areaName
    }))
  } catch {
    mdCampusOptions.value = []
  }
}
function campusNameById(id) {
  const hit = mdCampusOptions.value.find((c) => c.id === id)
  return hit ? `${hit.campusCode} ${hit.campusName}` : ''
}

/** 院区维护 */
const campusLoading = ref(false)
const campusRows = ref([])
const campusSelected = ref([])
const campusTableRef = ref(null)
const campusPanelRef = ref(null)
const campusKeyword = ref('')
const campusPage = ref(1)
const campusPageSize = ref(10)
const campusTotal = ref(0)
const campusDrawerVisible = ref(false)
const campusDialogMode = ref('create')
const campusSaving = ref(false)
const campusFormRef = ref(null)
const campusForm = reactive({
  id: '',
  orgId: '',
  areaCode: '',
  areaName: '',
  isActive: 1,
  remark: ''
})
const campusRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  areaCode: [{ required: true, message: '请输入院区编码', trigger: 'blur' }],
  areaName: [{ required: true, message: '请输入院区名称', trigger: 'blur' }]
}
function campusIndex(i) {
  return (campusPage.value - 1) * campusPageSize.value + i + 1
}
function searchCampusPage() {
  campusPage.value = 1
  loadCampusPage()
}
function onCampusPageSizeChange() {
  campusPage.value = 1
  loadCampusPage()
}
function onCampusSelectionChange(rows) { campusSelected.value = rows }
function onCampusRowDblclick(row) {
  campusTableRef.value?.toggleRowSelection(row, !campusSelected.value.some((r) => r.id === row.id))
}
function campusRowClassName({ row }) {
  return campusSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadOrgOptions() {
  await initOrgContext()
}
function orgLabelById(id) {
  const hit = orgOptions.value.find((o) => o.id === id)
  return hit ? `${hit.campusCode} ${hit.campusName}` : ''
}
/** 列表查询机构：机构账号固定本机构；平台用顶栏选择（空=全部） */
function listOrgId(scope) {
  if (!scope.isPlatform && scope.orgId) return scope.orgId
  return effectiveOrgId() || undefined
}
async function loadCampusPage() {
  campusLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = campusKeyword.value?.trim() || undefined
    const params = { page: Number(campusPage.value) || 1, size: Number(campusPageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/areas', { params })
    const records = res?.data?.records
    const rows = (Array.isArray(records) ? records : []).map((r) => ({
      ...r,
      orgLabel: orgLabelById(r.orgId) || ''
    }))
    campusTotal.value = Number(res?.data?.total || rows.length || 0)
    campusRows.value = rows
    campusSelected.value = []
  } catch (e) {
    console.error('loadCampusPage failed', e)
    ElMessage.error(e?.message || '院区列表加载失败')
  } finally {
    campusLoading.value = false
  }
}
function fillCampusForm(row) {
  campusForm.id = row?.id || ''
  campusForm.orgId = row?.orgId || ''
  campusForm.areaCode = row?.areaCode || ''
  campusForm.areaName = row?.areaName || ''
  campusForm.isActive = row?.isActive ?? 1
  campusForm.remark = row?.remark || ''
}
function resetCampusForm() {
  fillCampusForm(null)
  campusFormRef.value?.clearValidate?.()
}
async function openCampusCreate() {
  await loadCampusPage()
  campusDialogMode.value = 'create'
  resetCampusForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) campusForm.orgId = oid
  campusDrawerVisible.value = true
}
function openCampusEdit(row) {
  loadOrgOptions()
  campusDialogMode.value = 'edit'
  fillCampusForm(row)
  campusDrawerVisible.value = true
}
async function submitCampusForm() {
  const form = campusFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    campusSaving.value = true
    try {
      const payload = {
        orgId: campusForm.orgId,
        areaCode: campusForm.areaCode.trim(),
        areaName: campusForm.areaName.trim(),
        isActive: campusForm.isActive,
        remark: campusForm.remark?.trim() || null
      }
      if (campusDialogMode.value === 'edit') payload.id = campusForm.id
      await http.post('/md/areas', payload)
      ElMessage.success(campusDialogMode.value === 'edit' ? '院区已修改' : '院区已新增')
      campusDrawerVisible.value = false
      await loadCampusPage()
      await loadMdCampusOptions()
    } catch {
      // 如编码重复：刷新列表，避免「库里有、页面空」的错觉
      await loadCampusPage()
    } finally {
      campusSaving.value = false
    }
  })
}
async function deleteCampusRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除院区「${row.areaCode} ${row.areaName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/areas/${row.id}`)
  ElMessage.success('已删除')
  await loadCampusPage()
  await loadMdCampusOptions()
}

/** 仓库维护：后端按 org_id 隔离；admin 全量，cssd_01 仅本机构 */
const whLoading = ref(false)
const whRows = ref([])
const whSelected = ref([])
const whTableRef = ref(null)
const whPanelRef = ref(null)
const whKeyword = ref('')
const whPage = ref(1)
const whPageSize = ref(10)
const whTotal = ref(0)
const whDrawerVisible = ref(false)
const whDialogMode = ref('create')
const whSaving = ref(false)
const whFormRef = ref(null)
const whForm = reactive({
  id: '', warehouseCode: '', warehouseName: '', campusId: '', isActive: 1
})
const whRules = {
  warehouseCode: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  warehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  campusId: [{ required: true, message: '请选择所属院区', trigger: 'change' }]
}
function onWhSelectionChange(rows) { whSelected.value = rows }
function onWhRowDblclick(row) {
  whTableRef.value?.toggleRowSelection(row, !whSelected.value.some((r) => r.id === row.id))
}
function whRowClassName({ row }) {
  return whSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
function whIndex(i) {
  return (whPage.value - 1) * whPageSize.value + i + 1
}
function searchWarehousePage() {
  whPage.value = 1
  loadWarehousePage()
}
function onWhPageSizeChange() {
  whPage.value = 1
  loadWarehousePage()
}
async function loadWarehousePage() {
  whLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadMdCampusOptions()
    const kw = whKeyword.value?.trim() || undefined
    const params = {
      page: Number(whPage.value) || 1,
      size: Number(whPageSize.value) || 10
    }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/warehouses', { params })
    const records = Array.isArray(res?.data?.records) ? res.data.records : []
    whRows.value = records.map((r) => ({
      ...r,
      campusName: campusNameById(r.campusId) || ''
    }))
    whTotal.value = Number(res?.data?.total || records.length || 0)
    whSelected.value = []
  } catch (e) {
    console.error('loadWarehousePage failed', e)
    ElMessage.error(e?.message || '仓库列表加载失败')
  } finally {
    whLoading.value = false
  }
}
function fillWhForm(row) {
  whForm.id = row?.id || ''
  whForm.warehouseCode = row?.warehouseCode || ''
  whForm.warehouseName = row?.warehouseName || ''
  whForm.campusId = row?.campusId || ''
  whForm.isActive = row?.isActive ?? 1
}
function resetWhForm() {
  fillWhForm(null)
  whFormRef.value?.clearValidate?.()
}
async function openWhCreate() {
  await loadMdCampusOptions()
  whDialogMode.value = 'create'
  resetWhForm()
  // 机构账号若只有一个院区，默认选中
  if (mdCampusOptions.value.length === 1) {
    whForm.campusId = mdCampusOptions.value[0].id
  }
  whDrawerVisible.value = true
}
async function openWhEdit(row) {
  await loadMdCampusOptions()
  whDialogMode.value = 'edit'
  fillWhForm(row)
  whDrawerVisible.value = true
}
async function submitWhForm() {
  const form = whFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    whSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const payload = {
        warehouseCode: whForm.warehouseCode.trim(),
        warehouseName: whForm.warehouseName.trim(),
        campusId: whForm.campusId || null,
        isActive: whForm.isActive
      }
      if (!scope.isPlatform && scope.orgId) payload.orgId = scope.orgId
      if (whDialogMode.value === 'edit') payload.id = whForm.id
      await http.post('/md/warehouses', payload)
      whDrawerVisible.value = false
      ElMessage.success(whDialogMode.value === 'edit' ? '仓库已修改' : '仓库已新增')
      await loadWarehousePage()
    } catch {
      await loadWarehousePage()
    } finally {
      whSaving.value = false
    }
  })
}
async function deleteWhRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除仓库「${row.warehouseCode} ${row.warehouseName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/warehouses/${row.id}`)
  ElMessage.success('已删除')
  await loadWarehousePage()
}

/** 科室维护 */
const deptLoading = ref(false)
const deptRows = ref([])
const deptSelected = ref([])
const deptTableRef = ref(null)
const deptPanelRef = ref(null)
const deptTreeRef = ref(null)
const deptKeyword = ref('')
const deptPage = ref(1)
const deptPageSize = ref(10)
const deptTotal = ref(0)
const deptTreeFilterId = ref('')
const deptAllRows = ref([])
const deptTreeData = ref([{ id: 'root', label: '科室', children: [] }])
const deptDrawerVisible = ref(false)
const deptDialogMode = ref('create')
const deptSaving = ref(false)
const deptFormRef = ref(null)
const deptForm = reactive({
  id: '',
  deptCode: '',
  deptName: '',
  parentId: '',
  campusId: '',
  location: '',
  contactName: '',
  contactPhone: '',
  deptPhone: '',
  isActive: 1,
  remark: ''
})
const deptRules = {
  deptCode: [{ required: true, message: '请输入科室编码', trigger: 'blur' }],
  deptName: [{ required: true, message: '请输入科室名称', trigger: 'blur' }]
}
const deptParentOptions = computed(() =>
  deptAllRows.value.filter((d) => d.id && d.id !== deptForm.id)
)
function deptIndex(i) {
  return (deptPage.value - 1) * deptPageSize.value + i + 1
}
function searchDeptPage() {
  deptPage.value = 1
  loadDeptPage()
}
function onDeptPageSizeChange() {
  deptPage.value = 1
  loadDeptPage()
}
function refreshDeptPage() {
  loadDeptTree()
  loadDeptPage()
}
function onDeptSelectionChange(rows) { deptSelected.value = rows }
function onDeptRowDblclick(row) {
  deptTableRef.value?.toggleRowSelection(row, !deptSelected.value.some((r) => r.id === row.id))
}
function deptRowClassName({ row }) {
  return deptSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
function buildDeptTree(rows) {
  const map = new Map()
  rows.forEach((r) => {
    map.set(r.id, {
      id: r.id,
      label: `${r.deptCode || ''}|${r.deptName || ''}`,
      children: []
    })
  })
  const roots = []
  rows.forEach((r) => {
    const node = map.get(r.id)
    if (r.parentId && map.has(r.parentId)) map.get(r.parentId).children.push(node)
    else roots.push(node)
  })
  deptTreeData.value = [{ id: 'root', label: '科室', children: roots }]
}
async function loadDeptTree() {
  try {
    await loadOrgOptions()
    const params = { page: 1, size: 500 }
    const scope = await resolveStaffOrgScope()
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/depts', { params })
    deptAllRows.value = res.data.records || []
    buildDeptTree(deptAllRows.value)
    await nextTick()
    deptTreeRef.value?.setCurrentKey?.(deptTreeFilterId.value || 'root')
  } catch {
    deptAllRows.value = []
    deptTreeData.value = [{ id: 'root', label: '科室', children: [] }]
  }
}
function onDeptTreeClick(data) {
  deptTreeFilterId.value = data?.id === 'root' ? '' : data?.id || ''
  deptPage.value = 1
  loadDeptPage()
}
function parentNameById(id) {
  if (!id) return ''
  const hit = deptAllRows.value.find((d) => d.id === id)
  return hit ? `${hit.deptCode}|${hit.deptName}` : ''
}
async function loadDeptPage() {
  deptLoading.value = true
  try {
    await loadMdCampusOptions()
    if (!deptAllRows.value.length) await loadDeptTree()
    const kw = deptKeyword.value?.trim() || undefined
    const params = {
      page: deptPage.value,
      size: deptPageSize.value,
      keyword: kw
    }
    const scope = await resolveStaffOrgScope()
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    if (deptTreeFilterId.value) params.deptId = deptTreeFilterId.value
    const res = await http.get('/md/depts', { params })
    deptRows.value = (res.data.records || []).map((r) => ({
      ...r,
      campusName: campusNameById(r.campusId) || r.campusName || '',
      parentName: parentNameById(r.parentId)
    }))
    deptTotal.value = Number(res.data.total || 0)
    deptSelected.value = []
    deptTableRef.value?.clearSelection?.()
  } finally {
    deptLoading.value = false
  }
}
function fillDeptForm(row) {
  deptForm.id = row?.id || ''
  deptForm.deptCode = row?.deptCode || ''
  deptForm.deptName = row?.deptName || ''
  deptForm.parentId = row?.parentId || ''
  deptForm.campusId = row?.campusId || ''
  deptForm.location = row?.location || ''
  deptForm.contactName = row?.contactName || ''
  deptForm.contactPhone = row?.contactPhone || ''
  deptForm.deptPhone = row?.deptPhone || ''
  deptForm.isActive = row?.isActive ?? 1
  deptForm.remark = row?.remark || ''
}
function resetDeptForm() {
  fillDeptForm(null)
  deptFormRef.value?.clearValidate?.()
}
function openDeptCreate() {
  loadMdCampusOptions()
  loadDeptTree()
  deptDialogMode.value = 'create'
  resetDeptForm()
  if (deptTreeFilterId.value) deptForm.parentId = deptTreeFilterId.value
  deptDrawerVisible.value = true
}
function openDeptEdit(row) {
  loadMdCampusOptions()
  loadDeptTree()
  deptDialogMode.value = 'edit'
  fillDeptForm(row)
  deptDrawerVisible.value = true
}
async function submitDeptForm() {
  const form = deptFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    deptSaving.value = true
    try {
      const payload = {
        deptCode: deptForm.deptCode.trim(),
        deptName: deptForm.deptName.trim(),
        parentId: deptForm.parentId || null,
        campusId: deptForm.campusId || null,
        location: deptForm.location?.trim() || null,
        contactName: deptForm.contactName?.trim() || null,
        contactPhone: deptForm.contactPhone?.trim() || null,
        deptPhone: deptForm.deptPhone?.trim() || null,
        deptKind: 'clinical',
        isActive: deptForm.isActive,
        remark: deptForm.remark?.trim() || null
      }
      if (deptDialogMode.value === 'edit') payload.id = deptForm.id
      await http.post('/md/depts', payload)
      ElMessage.success(deptDialogMode.value === 'edit' ? '科室已修改' : '科室已新增')
      deptDrawerVisible.value = false
      await loadDeptTree()
      await loadDeptPage()
    } finally {
      deptSaving.value = false
    }
  })
}
async function deleteDeptRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除科室「${row.deptCode} ${row.deptName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/depts/${row.id}`)
  ElMessage.success('已删除')
  await loadDeptTree()
  await loadDeptPage()
}

const permLoading = ref(false)
const permRows = ref([])
const permSelected = ref([])
const permTableRef = ref(null)
const permPanelRef = ref(null)
const permKeyword = ref('')
const permPage = ref(1)
const permPageSize = ref(10)
const permTotal = ref(0)
const permDrawerVisible = ref(false)
const permDialogMode = ref('create')
const permSaving = ref(false)
const permFormRef = ref(null)
const permForm = reactive({
  id: '',
  role: '',
  roleName: ''
})
const permRules = {
  role: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }]
}
const permDialogTitle = computed(() =>
  permDialogMode.value === 'edit' ? '修改岗位权限' : '新增岗位权限'
)
const permDefaultMenus = {
  admin: '系统管理、工位大厅、科室申领、追溯、召回、监测、报表、系统参数',
  nurse_lead: '系统管理、工位大厅、科室申领、追溯、召回、监测、报表、系统参数',
  operator: '工位大厅、追溯查询',
  qc: '监测、召回、追溯、报表',
  delivery: '发放工位、追溯查询',
  clinical: '科室申领与使用、追溯查询',
  surgeon: '科室申领与使用、追溯查询'
}
function collectMenuLabels(nodes, keySet, out = []) {
  for (const n of nodes || []) {
    if (keySet.has(n.id)) out.push(n.label)
    if (n.children?.length) collectMenuLabels(n.children, keySet, out)
  }
  return out
}
function readRoleMenuText(roleCode) {
  let keys = []
  try {
    keys = JSON.parse(localStorage.getItem(authStorageKey('role', roleCode)) || '[]')
  } catch {
    keys = []
  }
  if (Array.isArray(keys) && keys.length) {
    const labels = collectMenuLabels(authMenuTreeFull, new Set(keys))
    if (labels.length) return labels.join('、')
  }
  return permDefaultMenus[roleCode] || '—'
}
function permIndex(i) {
  return (permPage.value - 1) * permPageSize.value + i + 1
}
function searchPerms() {
  permPage.value = 1
  loadPerms()
}
function onPermPageSizeChange() {
  permPage.value = 1
  loadPerms()
}
function onPermSelectionChange(rows) {
  permSelected.value = rows
}
function onPermRowDblclick(row) {
  const selected = permSelected.value.some((r) => r.role === row.role)
  permTableRef.value?.toggleRowSelection(row, !selected)
}
function permRowClassName({ row }) {
  return permSelected.value.some((r) => r.role === row.role) ? 'org-row-selected' : ''
}
function loadPerms() {
  permLoading.value = true
  try {
    refreshJobPostOptions()
    const kw = permKeyword.value?.trim()
    let rows = readJobPosts().map((p) => ({
      id: p.id,
      role: p.code,
      roleName: p.name,
      menus: readRoleMenuText(p.code)
    }))
    if (kw) {
      rows = rows.filter(
        (r) => (r.role || '').includes(kw) || (r.roleName || '').includes(kw)
      )
    }
    permTotal.value = rows.length
    const start = (permPage.value - 1) * permPageSize.value
    permRows.value = rows.slice(start, start + permPageSize.value)
    permSelected.value = []
    permTableRef.value?.clearSelection?.()
  } finally {
    permLoading.value = false
  }
}
function fillPermForm(row) {
  permForm.id = row?.id || ''
  permForm.role = row?.role || ''
  permForm.roleName = row?.roleName || ''
}
function resetPermForm() {
  fillPermForm(null)
  permFormRef.value?.clearValidate?.()
}
function openPermCreate() {
  refreshJobPostOptions()
  permDialogMode.value = 'create'
  resetPermForm()
  permDrawerVisible.value = true
}
function openPermEdit(row) {
  refreshJobPostOptions()
  permDialogMode.value = 'edit'
  fillPermForm(row)
  permDrawerVisible.value = true
}
async function submitPermForm() {
  const form = permFormRef.value
  if (!form) return
  await form.validate((ok) => {
    if (!ok) return
    permSaving.value = true
    try {
      const code = permForm.role.trim()
      const name = permForm.roleName.trim()
      const posts = readJobPosts()
      if (permDialogMode.value === 'edit') {
        const idx = posts.findIndex((p) => p.id === permForm.id || p.code === code)
        if (idx < 0) {
          ElMessage.error('未找到该岗位')
          return
        }
        posts[idx] = { ...posts[idx], code, name: name }
      } else {
        if (posts.some((p) => p.code === code)) {
          ElMessage.warning('岗位编码已存在')
          return
        }
        posts.push({
          id: `job_${code}_${Date.now()}`,
          code,
          name,
          value1: '',
          value2: ''
        })
      }
      writeJobPosts(posts)
      permDrawerVisible.value = false
      ElMessage.success(permDialogMode.value === 'edit' ? '岗位已修改' : '岗位已新增')
      loadPerms()
    } finally {
      permSaving.value = false
    }
  })
}
async function deletePermRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除岗位「${row.role} ${row.roleName}」？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  const posts = readJobPosts().filter((p) => p.code !== row.role)
  writeJobPosts(posts)
  localStorage.removeItem(authStorageKey('role', row.role))
  ElMessage.success('已删除')
  loadPerms()
}

async function loadOrg() {
  orgLoading.value = true
  try {
    const kw = orgKeyword.value?.trim() || undefined
    const scope = readStaffOrgScope()
    const res = await http.get('/md/campuses', {
      params: { page: orgPage.value, size: orgPageSize.value, keyword: kw }
    })
    let rows = res.data.records || []
    // 机构账号 / 平台顶栏选中机构：前端按机构过滤
    if (!scope.isPlatform) {
      rows = rows.filter(
        (r) =>
          (scope.orgId && r.id === scope.orgId) ||
          (scope.orgCode && String(r.campusCode) === String(scope.orgCode))
      )
      orgTotal.value = rows.length
    } else if (selectedOrgId.value) {
      rows = rows.filter((r) => r.id === selectedOrgId.value)
      orgTotal.value = rows.length
    } else {
      orgTotal.value = Number(res.data.total || 0)
    }
    orgRows.value = rows
    orgSelected.value = []
    orgTableRef.value?.clearSelection?.()
  } finally {
    orgLoading.value = false
  }
}

/** 器械包档案 */
const packCategoryOptions = [
  { value: 'cssd_set', label: '常规包' },
  { value: 'or_set', label: '手术室包' },
  { value: 'loan', label: '寄消包' },
  { value: 'external', label: '外来器械包' }
]
function formatPackCategory(v) {
  return packCategoryOptions.find((x) => x.value === v)?.label || v || ''
}
const tplLoading = ref(false)
const tplRows = ref([])
const tplSelected = ref([])
const tplTableRef = ref(null)
const tplPanelRef = ref(null)
const tplKeyword = ref('')
const tplPage = ref(1)
const tplPageSize = ref(10)
const tplTotal = ref(0)
const tplDrawerVisible = ref(false)
const tplDialogMode = ref('create')
const tplSaving = ref(false)
const tplFormTab = ref('basic')
const tplFormRef = ref(null)
let tplNameInitialsTimer = null
const tplForm = reactive({
  id: '',
  orgId: '',
  templateCode: '',
  templateName: '',
  shortCode: '',
  packCategory: 'cssd_set',
  shelfLifeDays: null,
  basketLoadQty: null,
  safetyStockQty: null,
  sterMethod: '',
  needWash: 1,
  washMode: 'scatter',
  needPack: 1,
  needStore: 1,
  needRecycle: 1,
  isImplant: 0,
  isActive: 1,
  remark: ''
})
const tplRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  templateName: [{ required: true, message: '请输入包名称', trigger: 'blur' }],
  packCategory: [{ required: true, message: '请选择包类别', trigger: 'change' }]
}
function tplIndex(i) {
  return (tplPage.value - 1) * tplPageSize.value + i + 1
}
function searchTplPage() {
  tplPage.value = 1
  loadTplPage()
}
function onTplPageSizeChange() {
  tplPage.value = 1
  loadTplPage()
}
function onTplSelectionChange(rows) { tplSelected.value = rows }
function onTplRowDblclick(row) {
  tplTableRef.value?.toggleRowSelection(row, !tplSelected.value.some((r) => r.id === row.id))
}
function tplRowClassName({ row }) {
  return tplSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadTplPage() {
  tplLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = tplKeyword.value?.trim() || undefined
    const params = { page: Number(tplPage.value) || 1, size: Number(tplPageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/templates', { params })
    tplRows.value = res?.data?.records || []
    tplTotal.value = Number(res?.data?.total || 0)
    tplSelected.value = []
  } catch (e) {
    ElMessage.error(e?.message || '器械包列表加载失败')
  } finally {
    tplLoading.value = false
  }
}
function fillTplForm(row) {
  tplForm.id = row?.id || ''
  tplForm.orgId = row?.orgId || ''
  tplForm.templateCode = row?.templateCode || ''
  tplForm.templateName = row?.templateName || ''
  tplForm.shortCode = row?.shortCode || ''
  tplForm.packCategory = row?.packCategory || 'cssd_set'
  tplForm.shelfLifeDays = row?.shelfLifeDays ?? null
  tplForm.basketLoadQty = row?.basketLoadQty ?? null
  tplForm.safetyStockQty = row?.safetyStockQty ?? null
  tplForm.sterMethod = row?.sterMethod || ''
  tplForm.needWash = row?.needWash ?? 1
  tplForm.washMode = row?.washMode || 'scatter'
  tplForm.needPack = row?.needPack ?? 1
  tplForm.needStore = row?.needStore ?? 1
  tplForm.needRecycle = row?.needRecycle ?? 1
  tplForm.isImplant = row?.isImplant ?? 0
  tplForm.isActive = row?.isActive ?? 1
  tplForm.remark = row?.remark || ''
}
function resetTplForm() {
  fillTplForm(null)
  tplFormTab.value = 'basic'
  tplFormRef.value?.clearValidate?.()
}
function onTplNameInput() {
  clearTimeout(tplNameInitialsTimer)
  tplNameInitialsTimer = setTimeout(async () => {
    tplForm.shortCode = await fetchNameInitials(tplForm.templateName)
  }, 200)
}
async function openTplCreate() {
  await loadOrgOptions()
  tplDialogMode.value = 'create'
  resetTplForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) tplForm.orgId = oid
  else if (isPlatformAdmin.value) tplForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  tplDrawerVisible.value = true
}
async function openTplEdit(row) {
  await loadOrgOptions()
  tplDialogMode.value = 'edit'
  tplFormTab.value = 'basic'
  fillTplForm(row)
  tplDrawerVisible.value = true
}
async function submitTplForm() {
  const form = tplFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    tplSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const oid = listOrgId(scope) || tplForm.orgId
      const payload = {
        orgId: oid,
        templateCode: tplForm.templateCode?.trim() || null,
        templateName: tplForm.templateName.trim(),
        shortCode: (tplForm.shortCode?.trim() || '').toUpperCase() || null,
        packCategory: tplForm.packCategory,
        shelfLifeDays: tplForm.shelfLifeDays,
        basketLoadQty: tplForm.basketLoadQty,
        safetyStockQty: tplForm.safetyStockQty,
        sterMethod: tplForm.sterMethod || null,
        needWash: tplForm.needWash,
        washMode: tplForm.washMode || 'scatter',
        needPack: tplForm.needPack,
        needStore: tplForm.needStore,
        needRecycle: tplForm.needRecycle,
        isImplant: tplForm.isImplant,
        isActive: tplForm.isActive,
        remark: tplForm.remark?.trim() || null
      }
      if (tplDialogMode.value === 'edit') payload.id = tplForm.id
      await http.post('/md/templates', payload)
      ElMessage.success(tplDialogMode.value === 'edit' ? '器械包已修改' : '器械包已新增')
      tplDrawerVisible.value = false
      await loadTplPage()
    } finally {
      tplSaving.value = false
    }
  })
}
async function deleteTplRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除器械包「${row.templateCode} ${row.templateName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/templates/${row.id}`)
  ElMessage.success('已删除')
  await loadTplPage()
}

/** 器械基础档案 */
const instrumentLoading = ref(false)
const instrumentRows = ref([])
const instrumentSelected = ref([])
const instrumentTableRef = ref(null)
const instrumentPanelRef = ref(null)
const instrumentKeyword = ref('')
const instrumentPage = ref(1)
const instrumentPageSize = ref(10)
const instrumentTotal = ref(0)
const instrumentDrawerVisible = ref(false)
const instrumentDialogMode = ref('create')
const instrumentSaving = ref(false)
const instrumentFormTab = ref('basic')
const instrumentFormRef = ref(null)
const instrumentImageList = ref([])
const instrumentAttachmentList = ref([])
const instrumentMfrOptions = ref([])
const instrumentVendorOptions = ref([])
const instrumentUnitOptions = ref([])
const instrumentCategoryOptions = ['手术器械', '诊疗器械', '植入器械', '外来器械', '其他']
const instrumentTypeOptions = ['锐器', '钝器', '腔镜', '动力器械', '管路', '其他']
const instrumentSterMethodOptions = [
  { value: 'high', label: '高温蒸汽' },
  { value: 'low', label: '低温' },
  { value: 'eo', label: '环氧乙烷' },
  { value: 'plasma', label: '过氧化氢等离子' }
]
const instrumentWashMethodOptions = ['手工清洗', '超声清洗', '机洗', '禁止超声清洗']
const instrumentStorageAttrOptions = ['常温干燥', '恒温', '避光', '专柜存放']
const instrumentStatusOptions = ['在用', '停用', '维修', '借出', '报废']
let instrumentNameInitialsTimer = null
const instrumentForm = reactive({
  id: '',
  orgId: '',
  instrumentCode: '',
  instrumentName: '',
  shortCode: '',
  aliasName: '',
  englishName: '',
  brandName: '',
  spec: '',
  model: '',
  udi: '',
  registerNo: '',
  registerValidUntil: '',
  barcode: '',
  materialName: '',
  instrumentCategory: '',
  instrumentType: '',
  sterMethod: '',
  washMethod: '',
  packRequirement: '',
  costPrice: null,
  unitId: '',
  manufacturerId: '',
  vendorId: '',
  storageAttr: '',
  isHighValue: 0,
  isKeyMonitor: 0,
  currentStatus: '在用',
  isActive: 1,
  remark: ''
})
const instrumentRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  instrumentName: [{ required: true, message: '请输入器械名称', trigger: 'blur' }]
}
function instrumentIndex(i) {
  return (instrumentPage.value - 1) * instrumentPageSize.value + i + 1
}
function searchInstrumentPage() {
  instrumentPage.value = 1
  loadInstrumentPage()
}
function onInstrumentPageSizeChange() {
  instrumentPage.value = 1
  loadInstrumentPage()
}
function onInstrumentSelectionChange(rows) { instrumentSelected.value = rows }
function onInstrumentRowDblclick(row) {
  instrumentTableRef.value?.toggleRowSelection(row, !instrumentSelected.value.some((r) => r.id === row.id))
}
function instrumentRowClassName({ row }) {
  return instrumentSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadInstrumentPage() {
  instrumentLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = instrumentKeyword.value?.trim() || undefined
    const params = { page: Number(instrumentPage.value) || 1, size: Number(instrumentPageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/instruments', { params })
    instrumentRows.value = res?.data?.records || []
    instrumentTotal.value = Number(res?.data?.total || 0)
    instrumentSelected.value = []
  } catch (e) {
    ElMessage.error(e?.message || '器械列表加载失败')
  } finally {
    instrumentLoading.value = false
  }
}
function parseInstrumentJsonList(raw) {
  if (Array.isArray(raw)) return raw
  if (!raw) return []
  try {
    const v = typeof raw === 'string' ? JSON.parse(raw) : raw
    return Array.isArray(v) ? v : []
  } catch {
    return []
  }
}
function fillInstrumentForm(row) {
  instrumentForm.id = row?.id || ''
  instrumentForm.orgId = row?.orgId || ''
  instrumentForm.instrumentCode = row?.instrumentCode || ''
  instrumentForm.instrumentName = row?.instrumentName || ''
  instrumentForm.shortCode = row?.shortCode || ''
  instrumentForm.aliasName = row?.aliasName || ''
  instrumentForm.englishName = row?.englishName || ''
  instrumentForm.brandName = row?.brandName || ''
  instrumentForm.spec = row?.spec || ''
  instrumentForm.model = row?.model || ''
  instrumentForm.udi = row?.udi || ''
  instrumentForm.registerNo = row?.registerNo || ''
  instrumentForm.registerValidUntil = row?.registerValidUntil || ''
  instrumentForm.barcode = row?.barcode || ''
  instrumentForm.materialName = row?.materialName || ''
  instrumentForm.instrumentCategory = row?.instrumentCategory || ''
  instrumentForm.instrumentType = row?.instrumentType || ''
  instrumentForm.sterMethod = row?.sterMethod || ''
  instrumentForm.washMethod = row?.washMethod || ''
  instrumentForm.packRequirement = row?.packRequirement || ''
  instrumentForm.costPrice = row?.costPrice != null && row?.costPrice !== '' ? Number(row.costPrice) : null
  instrumentForm.unitId = row?.unitId || ''
  instrumentForm.manufacturerId = row?.manufacturerId || ''
  instrumentForm.vendorId = row?.vendorId || ''
  instrumentForm.storageAttr = row?.storageAttr || ''
  instrumentForm.isHighValue = row?.isHighValue ?? 0
  instrumentForm.isKeyMonitor = row?.isKeyMonitor ?? 0
  instrumentForm.currentStatus = row?.currentStatus || '在用'
  instrumentForm.isActive = row?.isActive ?? 1
  instrumentForm.remark = row?.remark || ''
  instrumentImageList.value = parseInstrumentJsonList(row?.instrumentImages).map((x) => ({
    name: x?.name || 'image',
    dataUrl: x?.dataUrl || ''
  })).filter((x) => x.dataUrl)
  instrumentAttachmentList.value = parseInstrumentJsonList(row?.instrumentAttachments).map((x) => ({
    name: x?.name || 'file',
    dataUrl: x?.dataUrl || ''
  })).filter((x) => x.dataUrl)
}
function resetInstrumentForm() {
  fillInstrumentForm(null)
  instrumentFormTab.value = 'basic'
  instrumentMfrOptions.value = []
  instrumentVendorOptions.value = []
  instrumentUnitOptions.value = []
  instrumentFormRef.value?.clearValidate?.()
}
async function loadInstrumentDictOptions(orgId) {
  const params = { page: 1, size: 500 }
  if (orgId) params.orgId = orgId
  try {
    const [mfrRes, vendorRes, unitRes] = await Promise.all([
      http.get('/md/manufacturers', { params }),
      http.get('/md/vendors', { params }),
      http.get('/md/units', { params })
    ])
    instrumentMfrOptions.value = (mfrRes?.data?.records || []).filter((r) => r.isActive !== 0)
    instrumentVendorOptions.value = (vendorRes?.data?.records || []).filter((r) => r.isActive !== 0)
    instrumentUnitOptions.value = (unitRes?.data?.records || []).filter((r) => r.isActive !== 0)
  } catch (e) {
    instrumentMfrOptions.value = []
    instrumentVendorOptions.value = []
    instrumentUnitOptions.value = []
    ElMessage.error(e?.message || '厂家/供应商/单位选项加载失败')
  }
}
async function onInstrumentOrgChange(orgId) {
  instrumentForm.manufacturerId = ''
  instrumentForm.vendorId = ''
  instrumentForm.unitId = ''
  await loadInstrumentDictOptions(orgId)
}
function onInstrumentNameInput() {
  clearTimeout(instrumentNameInitialsTimer)
  instrumentNameInitialsTimer = setTimeout(async () => {
    instrumentForm.shortCode = await fetchNameInitials(instrumentForm.instrumentName)
  }, 200)
}
function onInstrumentImageChange(file) {
  const raw = file?.raw
  if (!raw) return
  if (raw.size > 3 * 1024 * 1024) {
    ElMessage.warning('单张图片请控制在 3MB 以内')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    instrumentImageList.value.push({ name: raw.name, dataUrl: String(reader.result || '') })
  }
  reader.readAsDataURL(raw)
}
function removeInstrumentImage(idx) {
  instrumentImageList.value.splice(idx, 1)
}
function onInstrumentAttachmentChange(file) {
  const raw = file?.raw
  if (!raw) return
  if (raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('附件请控制在 5MB 以内')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    instrumentAttachmentList.value.push({ name: raw.name, dataUrl: String(reader.result || '') })
  }
  reader.readAsDataURL(raw)
}
function removeInstrumentAttachment(idx) {
  instrumentAttachmentList.value.splice(idx, 1)
}
function previewInstrumentAttachment(row) {
  if (!row?.dataUrl) return
  const a = document.createElement('a')
  a.href = row.dataUrl
  a.target = '_blank'
  a.rel = 'noopener'
  a.download = row.name || 'attachment'
  a.click()
}
function formatInstrumentCostPrice(v) {
  if (v == null || v === '') return ''
  const n = Number(v)
  return Number.isFinite(n) ? n.toFixed(4).replace(/\.?0+$/, '') : String(v)
}
function formatInstrumentDate(v) {
  if (!v) return ''
  const s = String(v)
  return s.length >= 10 ? s.slice(0, 10) : s
}
async function openInstrumentCreate() {
  await loadOrgOptions()
  instrumentDialogMode.value = 'create'
  resetInstrumentForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) instrumentForm.orgId = oid
  else if (isPlatformAdmin.value) instrumentForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  await loadInstrumentDictOptions(instrumentForm.orgId)
  instrumentDrawerVisible.value = true
}
async function openInstrumentCopy(row) {
  await loadOrgOptions()
  instrumentDialogMode.value = 'create'
  instrumentFormTab.value = 'basic'
  fillInstrumentForm(row)
  // 复制为新增：清空主键与唯一字段，其余沿用
  instrumentForm.id = ''
  instrumentForm.instrumentCode = ''
  instrumentForm.barcode = ''
  instrumentForm.udi = ''
  await loadInstrumentDictOptions(instrumentForm.orgId)
  instrumentDrawerVisible.value = true
}
async function openInstrumentEdit(row) {
  await loadOrgOptions()
  instrumentDialogMode.value = 'edit'
  instrumentFormTab.value = 'basic'
  fillInstrumentForm(row)
  await loadInstrumentDictOptions(instrumentForm.orgId)
  instrumentDrawerVisible.value = true
}
async function submitInstrumentForm() {
  const form = instrumentFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) {
      instrumentFormTab.value = 'basic'
      return
    }
    instrumentSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const oid = listOrgId(scope) || instrumentForm.orgId
      const payload = {
        orgId: oid,
        instrumentCode: instrumentForm.instrumentCode?.trim() || null,
        instrumentName: instrumentForm.instrumentName.trim(),
        shortCode: (instrumentForm.shortCode?.trim() || '').toUpperCase() || null,
        aliasName: instrumentForm.aliasName?.trim() || null,
        englishName: instrumentForm.englishName?.trim() || null,
        brandName: instrumentForm.brandName?.trim() || null,
        spec: instrumentForm.spec?.trim() || null,
        model: instrumentForm.model?.trim() || null,
        udi: instrumentForm.udi?.trim() || null,
        registerNo: instrumentForm.registerNo?.trim() || null,
        registerValidUntil: instrumentForm.registerValidUntil || null,
        barcode: instrumentForm.barcode?.trim() || null,
        materialName: instrumentForm.materialName?.trim() || null,
        instrumentCategory: instrumentForm.instrumentCategory || null,
        instrumentType: instrumentForm.instrumentType || null,
        sterMethod: instrumentForm.sterMethod || null,
        washMethod: instrumentForm.washMethod || null,
        packRequirement: instrumentForm.packRequirement?.trim() || null,
        costPrice: instrumentForm.costPrice != null && instrumentForm.costPrice !== '' ? instrumentForm.costPrice : null,
        unitId: instrumentForm.unitId || null,
        manufacturerId: instrumentForm.manufacturerId || null,
        vendorId: instrumentForm.vendorId || null,
        storageAttr: instrumentForm.storageAttr || null,
        isHighValue: instrumentForm.isHighValue,
        isKeyMonitor: instrumentForm.isKeyMonitor,
        currentStatus: instrumentForm.currentStatus || '在用',
        isActive: instrumentForm.isActive,
        remark: instrumentForm.remark?.trim() || null,
        instrumentImages: JSON.stringify(instrumentImageList.value.filter((x) => x?.dataUrl)),
        instrumentAttachments: JSON.stringify(instrumentAttachmentList.value.filter((x) => x?.dataUrl))
      }
      if (instrumentDialogMode.value === 'edit') payload.id = instrumentForm.id
      await http.post('/md/instruments', payload)
      ElMessage.success(instrumentDialogMode.value === 'edit' ? '器械已修改' : '器械已新增')
      instrumentDrawerVisible.value = false
      await loadInstrumentPage()
    } finally {
      instrumentSaving.value = false
    }
  })
}
async function deleteInstrumentRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除器械「${row.instrumentCode} ${row.instrumentName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/instruments/${row.id}`)
  ElMessage.success('已删除')
  await loadInstrumentPage()
}

/** 器械基础档案导入 */
const INSTRUMENT_IMPORT_HEADERS = [
  '器械编码', '器械名称', '别名', '英文名称', '品牌', '规格', '型号', 'UDI',
  '生产厂家', '供应商', '注册证号', '注册证有效期', '材质', '器械条码',
  '器械分类', '器械类型', '灭菌方式', '清洗方式', '包装要求'
]
const INSTRUMENT_IMPORT_REQUIRED = new Set(['器械名称'])
const instrumentImportVisible = ref(false)
const instrumentImportFile = ref(null)
const instrumentImportFileName = ref('')
const instrumentImportRows = ref([])
const instrumentImportSaving = ref(false)
const instrumentImportDone = ref(0)
const instrumentImportTotal = ref(0)
const instrumentImportProgress = computed(() => {
  const total = instrumentImportTotal.value
  if (!total) return 0
  return Math.min(100, Math.round((instrumentImportDone.value / total) * 100))
})
async function openInstrumentImport() {
  instrumentImportFile.value = null
  instrumentImportFileName.value = ''
  instrumentImportRows.value = []
  instrumentImportSaving.value = false
  instrumentImportDone.value = 0
  instrumentImportTotal.value = 0
  await loadOrgOptions()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope) || effectiveOrgId() || orgOptions.value[0]?.id || ''
  await loadInstrumentDictOptions(oid)
  instrumentImportVisible.value = true
}
function downloadInstrumentImportTemplate() {
  const sampleMfr = instrumentMfrOptions.value[0]?.mfrName || '演示厂家'
  const sampleVendor = instrumentVendorOptions.value[0]?.vendorName || '演示供应商'
  const headers = INSTRUMENT_IMPORT_HEADERS.map((h) => (INSTRUMENT_IMPORT_REQUIRED.has(h) ? `${h}*` : h))
  const sampleVals = [
    'SSJ-0001', '持针器', '针持', 'Needle Holder', '示例品牌', '18cm', '标准型', 'UDI0001',
    sampleMfr, sampleVendor, '械注准20200001', '2030-12-31', '不锈钢', '6900000000001',
    '手术器械', '锐器', '高温蒸汽', '机洗', '独立包装'
  ]
  const aoa = [headers, sampleVals]
  const ws = XLSX.utils.aoa_to_sheet(aoa)
  // 全部按文本，避免 Excel 把编码/日期改成数字
  const range = XLSX.utils.decode_range(ws['!ref'] || 'A1')
  for (let R = range.s.r; R <= range.e.r; R++) {
    for (let C = range.s.c; C <= range.e.c; C++) {
      const addr = XLSX.utils.encode_cell({ r: R, c: C })
      if (!ws[addr]) continue
      ws[addr].t = 's'
      ws[addr].z = '@'
    }
  }
  ws['!cols'] = headers.map(() => ({ wch: 14 }))
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '器械导入')
  XLSX.writeFile(wb, '器械基础档案导入模板.xlsx')
  ElMessage.success('模板已下载（.xlsx），请勿改表头列名后填写再导入')
}
function onInstrumentImportFileChange(file) {
  instrumentImportFile.value = file?.raw || null
  instrumentImportFileName.value = file?.name || ''
  instrumentImportRows.value = []
  if (instrumentImportFile.value) {
    nextTick(() => parseInstrumentImportFile())
  }
}
function decodeInstrumentImportBuffer(buf) {
  const encodings = ['utf-8', 'utf-16le', 'gb18030', 'gbk']
  let fallback = ''
  for (const enc of encodings) {
    try {
      const text = new TextDecoder(enc, { fatal: false }).decode(buf)
      if (!fallback) fallback = text
      if (/器械名称/.test(text)) return text
      if (!text.includes('\uFFFD') && /器械编码/.test(text)) return text
    } catch { /* try next */ }
  }
  return fallback || new TextDecoder('utf-8').decode(buf)
}
function normalizeInstrumentHeaderCell(h) {
  return normalizeImportHeader(h).replace(/\*+$/, '').toLowerCase()
}
function resolveInstrumentHeaderIndex(headers) {
  const aliases = {
    器械编码: ['器械编码', 'instrumentcode', '编码'],
    器械名称: ['器械名称', 'instrumentname', '名称'],
    别名: ['别名', 'aliasname'],
    英文名称: ['英文名称', '英文名', 'englishname'],
    品牌: ['品牌', '品牌名称', 'brandname', 'brand'],
    规格: ['规格', 'spec'],
    型号: ['型号', 'model'],
    UDI: ['udi', 'udi码', '唯一器械标识'],
    生产厂家: ['生产厂家', '厂家', '厂家名称', '制造商', '生产厂商', 'manufacturername', 'manufacturer'],
    供应商: ['供应商', '供应商名称', '供货商', 'vendorname', 'vendor'],
    注册证号: ['注册证号', '注册证', 'registerno'],
    注册证有效期: ['注册证有效期', '有效期', '注册证到期日', 'registervaliduntil'],
    材质: ['材质', '材料', 'materialname'],
    器械条码: ['器械条码', '条码', 'barcode'],
    器械分类: ['器械分类', '分类', 'instrumentcategory'],
    器械类型: ['器械类型', '类型', 'instrumenttype'],
    灭菌方式: ['灭菌方式', 'stermethod'],
    清洗方式: ['清洗方式', 'washmethod'],
    包装要求: ['包装要求', '包装', 'packrequirement']
  }
  // 短别名（名称/编码/厂家）可能误伤长表头，优先精确匹配完整表头
  const normalized = headers.map(normalizeInstrumentHeaderCell)
  const used = new Set()
  const map = {}
  const tryMatch = (key, allowShort) => {
    if (map[key] >= 0) return
    const list = aliases[key] || []
    for (let i = 0; i < normalized.length; i++) {
      if (used.has(i)) continue
      const nh = normalized[i]
      if (!nh) continue
      for (const a of list) {
        const na = normalizeInstrumentHeaderCell(a)
        if (!na) continue
        const short = na.length <= 2
        if (!allowShort && short) continue
        if (nh === na) {
          map[key] = i
          used.add(i)
          return
        }
      }
    }
  }
  Object.keys(aliases).forEach((key) => { map[key] = -1 })
  Object.keys(aliases).forEach((key) => tryMatch(key, false))
  Object.keys(aliases).forEach((key) => tryMatch(key, true))
  return map
}
function scoreInstrumentHeaderCol(col) {
  return INSTRUMENT_IMPORT_HEADERS.reduce((n, h) => n + (col[h] >= 0 ? 1 : 0), 0)
}
function pickInstrumentHeaderRow(matrix) {
  const scan = Math.min(matrix.length, 10)
  let best = null
  let bestScore = -1
  for (let i = 0; i < scan; i++) {
    const headers = (matrix[i] || []).map((h) => String(h ?? ''))
    const normalized = headers.map(normalizeInstrumentHeaderCell)
    const first = normalized[0] || ''
    // 模板列序完全一致时直接按位置取值（最稳）
    if (first === '器械编码') {
      let matched = 0
      for (let c = 0; c < Math.min(headers.length, INSTRUMENT_IMPORT_HEADERS.length); c++) {
        const expect = normalizeInstrumentHeaderCell(INSTRUMENT_IMPORT_HEADERS[c])
        const actual = normalized[c] || ''
        if (!actual) continue
        if (actual === expect || actual === expect + '*' || (INSTRUMENT_IMPORT_HEADERS[c] === 'UDI' && (actual === 'udi' || actual === 'udi码'))) {
          matched++
        }
      }
      if (matched >= 8) {
        const fixed = {}
        INSTRUMENT_IMPORT_HEADERS.forEach((h, idx) => { fixed[h] = idx })
        return { headerIndex: i, headers, col: fixed, score: INSTRUMENT_IMPORT_HEADERS.length }
      }
    }
    const col = resolveInstrumentHeaderIndex(headers)
    const score = scoreInstrumentHeaderCol(col)
    if (col['器械名称'] >= 0 && score > bestScore) {
      bestScore = score
      best = { headerIndex: i, headers, col, score }
    }
  }
  return best && best.score >= 2 ? best : null
}
function cellToImportText(cell) {
  if (cell == null || cell === '') return ''
  if (cell instanceof Date && !Number.isNaN(cell.getTime())) {
    const y = cell.getFullYear()
    const m = String(cell.getMonth() + 1).padStart(2, '0')
    const d = String(cell.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }
  if (typeof cell === 'object' && cell.v != null) {
    if (cell.t === 'd' && cell.v instanceof Date) return cellToImportText(cell.v)
    return String(cell.w ?? cell.v ?? '').replace(/^\ufeff/, '').trim()
  }
  return String(cell).replace(/^\ufeff/, '').trim()
}
function parseInstrumentExcelMatrix(buf) {
  try {
    const wb = XLSX.read(buf, { type: 'array', cellDates: true })
    const name = wb.SheetNames?.[0]
    if (!name) return null
    const sheet = wb.Sheets[name]
    if (!sheet || !sheet['!ref']) return null
    const range = XLSX.utils.decode_range(sheet['!ref'])
    // 至少保留模板列数，防止尾列被 Excel 裁掉后丢「包装要求」等
    range.e.c = Math.max(range.e.c, INSTRUMENT_IMPORT_HEADERS.length - 1)
    const rows = []
    for (let R = range.s.r; R <= range.e.r; R++) {
      const row = []
      let any = false
      for (let C = range.s.c; C <= range.e.c; C++) {
        const addr = XLSX.utils.encode_cell({ r: R, c: C })
        const text = cellToImportText(sheet[addr])
        if (text) any = true
        row.push(text)
      }
      if (any) rows.push(row)
    }
    return rows.length ? rows : null
  } catch {
    return null
  }
}
function matchInstrumentMfrByLabel(raw) {
  const v = String(raw || '').trim()
  if (!v) return { hit: null, ok: true }
  const nv = normalizeImportHeader(v)
  const hit = instrumentMfrOptions.value.find((m) => {
    const name = String(m.mfrName || '')
    const code = String(m.mfrCode || '')
    return name === v || normalizeImportHeader(name) === nv
      || code === v || normalizeImportHeader(code) === nv
  })
  return { hit: hit || null, ok: !!hit }
}
function matchInstrumentVendorByLabel(raw) {
  const v = String(raw || '').trim()
  if (!v) return { hit: null, ok: true }
  const nv = normalizeImportHeader(v)
  const hit = instrumentVendorOptions.value.find((m) => {
    const name = String(m.vendorName || '')
    const code = String(m.vendorCode || '')
    return name === v || normalizeImportHeader(name) === nv
      || code === v || normalizeImportHeader(code) === nv
  })
  return { hit: hit || null, ok: !!hit }
}
function matchInstrumentSterMethod(raw) {
  const v = String(raw || '').trim()
  if (!v) return { value: '', ok: true }
  const nv = normalizeImportHeader(v)
  const byValue = instrumentSterMethodOptions.find((o) => o.value === v || normalizeImportHeader(o.value) === nv)
  if (byValue) return { value: byValue.value, ok: true }
  const byLabel = instrumentSterMethodOptions.find((o) => {
    const label = String(o.label || '')
    return label === v || normalizeImportHeader(label) === nv
      || normalizeImportHeader(label).includes(nv) || nv.includes(normalizeImportHeader(label))
  })
  if (byLabel) return { value: byLabel.value, ok: true }
  if (['高温', '蒸汽', '高温蒸汽灭菌'].some((k) => nv.includes(normalizeImportHeader(k)))) {
    return { value: 'high', ok: true }
  }
  if (nv.includes('环氧') || nv === 'eo') return { value: 'eo', ok: true }
  if (nv.includes('等离子') || nv === 'plasma') return { value: 'plasma', ok: true }
  if (nv.includes('低温') || nv === 'low') return { value: 'low', ok: true }
  return { value: '', ok: false }
}
function normalizeInstrumentImportDate(raw) {
  const v = String(raw || '').trim()
  if (!v) return { value: null, ok: true }
  if (/^\d{4}-\d{2}-\d{2}$/.test(v)) return { value: v, ok: true }
  const m = v.match(/^(\d{4})[-\/.年](\d{1,2})[-\/.月](\d{1,2})/)
  if (m) {
    const y = m[1]
    const mo = String(m[2]).padStart(2, '0')
    const d = String(m[3]).padStart(2, '0')
    return { value: `${y}-${mo}-${d}`, ok: true }
  }
  // Excel 序列日（约 1900 体系）
  if (/^\d+(\.\d+)?$/.test(v)) {
    const serial = Number(v)
    if (serial >= 20000 && serial <= 80000) {
      const utc = Date.UTC(1899, 11, 30) + Math.floor(serial) * 86400000
      const dt = new Date(utc)
      const y = dt.getUTCFullYear()
      const mo = String(dt.getUTCMonth() + 1).padStart(2, '0')
      const d = String(dt.getUTCDate()).padStart(2, '0')
      return { value: `${y}-${mo}-${d}`, ok: true }
    }
  }
  return { value: null, ok: false }
}
async function loadExistingInstrumentCodeSet() {
  const set = new Set()
  const size = 500
  try {
    const scope = await resolveStaffOrgScope()
    const oid = listOrgId(scope)
    const base = { page: 1, size }
    if (oid) base.orgId = oid
    const first = await http.get('/md/instruments', { params: base })
    for (const r of (first.data.records || [])) {
      const code = String(r.instrumentCode || '').trim()
      if (code) set.add(code)
    }
    const total = Number(first.data.total || 0)
    const pages = Math.min(Math.ceil(total / size) || 1, 40)
    for (let page = 2; page <= pages; page++) {
      const res = await http.get('/md/instruments', { params: { ...base, page } })
      for (const r of (res.data.records || [])) {
        const code = String(r.instrumentCode || '').trim()
        if (code) set.add(code)
      }
    }
  } catch { /* 查重失败时仍允许解析 */ }
  return set
}
async function parseInstrumentImportFile() {
  if (!instrumentImportFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope) || effectiveOrgId() || orgOptions.value[0]?.id || ''
  await loadInstrumentDictOptions(oid)
  const existingCodes = await loadExistingInstrumentCodeSet()
  const buf = await instrumentImportFile.value.arrayBuffer()
  const text = decodeInstrumentImportBuffer(buf)
  const candidates = []
  // 优先 SheetJS（真实 xlsx/xls）；其次 HTML 表；最后纯 CSV
  const excelMatrix = parseInstrumentExcelMatrix(buf)
  if (excelMatrix?.length) candidates.push(excelMatrix)
  if (/<table[\s>]/i.test(text)) {
    const htmlMatrix = parseHtmlTableRows(text)
    if (htmlMatrix?.length) candidates.push(htmlMatrix)
  }
  if (!excelMatrix?.length) {
    const lines = text.replace(/^\ufeff/, '').split(/\r?\n/).filter((l) => l.trim())
    if (lines.length) {
      const delimiter = detectCsvDelimiter(lines[0])
      candidates.push(lines.map((l) => splitCsvLine(l, delimiter)))
    }
  }
  let picked = null
  let matrix = null
  let bestScore = -1
  for (const m of candidates) {
    const hit = pickInstrumentHeaderRow(m)
    if (hit && hit.score > bestScore) {
      bestScore = hit.score
      matrix = m
      picked = hit
    }
  }
  if (!picked || !matrix) {
    const preview = (candidates[0]?.[0] || []).map(normalizeImportHeader).filter(Boolean).slice(0, 8).join('、')
    ElMessage.error(preview
      ? `模板表头不正确（识别到：${preview}），请重新点击「下载模板」后再选择该文件解析`
      : '未能识别文件内容，请重新下载模板')
    instrumentImportRows.value = []
    return
  }
  const { headerIndex, col } = picked
  const getFrom = (cols, key) => {
    const i = col[key]
    if (i == null || i < 0) return ''
    return String(cols[i] ?? '').replace(/^\ufeff/, '').trim()
  }
  const rows = []
  const seenInFile = new Set()
  for (let li = headerIndex + 1; li < matrix.length; li++) {
    const cols = matrix[li] || []
    const instrumentCode = getFrom(cols, '器械编码')
    const instrumentName = getFrom(cols, '器械名称')
    if (!instrumentCode && !instrumentName) continue
    if (normalizeImportHeader(instrumentCode).includes('器械编码') || instrumentName === '器械基础档案导入') continue
    if (normalizeImportHeader(instrumentName).includes('器械名称')) continue
    const mfrRaw = getFrom(cols, '生产厂家')
    const vendorRaw = getFrom(cols, '供应商')
    const mfrHit = matchInstrumentMfrByLabel(mfrRaw)
    const vendorHit = matchInstrumentVendorByLabel(vendorRaw)
    const sterHit = matchInstrumentSterMethod(getFrom(cols, '灭菌方式'))
    const dateHit = normalizeInstrumentImportDate(getFrom(cols, '注册证有效期'))
    const errors = []
    if (!instrumentName) errors.push('缺器械名称')
    // 厂家/供应商不在字典时导入过程自动创建，此处不拦截
    if (getFrom(cols, '灭菌方式') && !sterHit.ok) errors.push('灭菌方式无效')
    if (getFrom(cols, '注册证有效期') && !dateHit.ok) errors.push('注册证有效期格式错误')
    if (instrumentCode && existingCodes.has(instrumentCode)) {
      errors.push('系统已存在，请勿重复导入')
    } else if (instrumentCode && seenInFile.has(instrumentCode)) {
      errors.push('文件内器械编码重复')
    }
    if (instrumentCode) seenInFile.add(instrumentCode)
    rows.push({
      instrumentCode,
      instrumentName,
      aliasName: getFrom(cols, '别名') || null,
      englishName: getFrom(cols, '英文名称') || null,
      brandName: getFrom(cols, '品牌') || null,
      spec: getFrom(cols, '规格') || null,
      model: getFrom(cols, '型号') || null,
      udi: getFrom(cols, 'UDI') || null,
      manufacturerName: (mfrHit.hit?.mfrName || mfrRaw || '').trim() || null,
      manufacturerId: mfrHit.hit?.id || null,
      vendorName: (vendorHit.hit?.vendorName || vendorRaw || '').trim() || null,
      vendorId: vendorHit.hit?.id || null,
      registerNo: getFrom(cols, '注册证号') || null,
      registerValidUntil: dateHit.value,
      materialName: getFrom(cols, '材质') || null,
      barcode: getFrom(cols, '器械条码') || null,
      instrumentCategory: getFrom(cols, '器械分类') || null,
      instrumentType: getFrom(cols, '器械类型') || null,
      sterMethod: sterHit.value || null,
      washMethod: getFrom(cols, '清洗方式') || null,
      packRequirement: getFrom(cols, '包装要求') || null,
      error: errors.join('；')
    })
  }
  instrumentImportRows.value = rows
  if (!rows.length) ElMessage.warning('未解析到数据行，请在模板中填写后再解析')
  else {
    const bad = rows.filter((r) => r.error).length
    const withExt = rows.filter((r) => r.brandName || r.englishName || r.udi || r.manufacturerName || r.vendorName || r.packRequirement).length
    const tip = bad ? `，其中 ${bad} 条校验未通过` : ''
    ElMessage.success(`已解析 ${rows.length} 行${tip}（识别 ${bestScore} 列，含扩展字段 ${withExt} 行）`)
  }
}
async function ensureInstrumentMfrId(orgId, name, id) {
  if (id) return id
  const n = String(name || '').trim()
  if (!n) return null
  const hit = matchInstrumentMfrByLabel(n)
  if (hit.hit?.id) return hit.hit.id
  const res = await http.post('/md/manufacturers', { orgId, mfrName: n, isActive: 1 })
  const newId = res?.data?.id || null
  if (newId) {
    instrumentMfrOptions.value = [
      ...instrumentMfrOptions.value,
      { id: newId, mfrName: res?.data?.mfrName || n, mfrCode: res?.data?.mfrCode || '' }
    ]
  }
  return newId
}
async function ensureInstrumentVendorId(orgId, name, id) {
  if (id) return id
  const n = String(name || '').trim()
  if (!n) return null
  const hit = matchInstrumentVendorByLabel(n)
  if (hit.hit?.id) return hit.hit.id
  const res = await http.post('/md/vendors', { orgId, vendorName: n, isActive: 1 })
  const newId = res?.data?.id || null
  if (newId) {
    instrumentVendorOptions.value = [
      ...instrumentVendorOptions.value,
      { id: newId, vendorName: res?.data?.vendorName || n, vendorCode: res?.data?.vendorCode || '' }
    ]
  }
  return newId
}
async function submitInstrumentImport() {
  if (!instrumentImportRows.value.length) {
    ElMessage.warning('请先解析出数据后再导入')
    return
  }
  if (instrumentImportRows.value.some((r) => r.error)) {
    ElMessage.warning('有校验未通过的请核对！')
    return
  }
  const rows = instrumentImportRows.value
  instrumentImportSaving.value = true
  instrumentImportDone.value = 0
  instrumentImportTotal.value = rows.length
  let ok = 0
  let fail = 0
  try {
    const scope = await resolveStaffOrgScope()
    const orgId = listOrgId(scope) || effectiveOrgId() || orgOptions.value[0]?.id || null
    let lastErr = ''
    for (const r of rows) {
      try {
        const manufacturerId = await ensureInstrumentMfrId(orgId, r.manufacturerName, r.manufacturerId)
        const vendorId = await ensureInstrumentVendorId(orgId, r.vendorName, r.vendorId)
        const payload = {
          orgId,
          instrumentCode: r.instrumentCode || null,
          instrumentName: r.instrumentName,
          aliasName: r.aliasName,
          englishName: r.englishName,
          brandName: r.brandName,
          spec: r.spec,
          model: r.model,
          udi: r.udi,
          manufacturerId,
          manufacturerName: r.manufacturerName || null,
          vendorId,
          vendorName: r.vendorName || null,
          registerNo: r.registerNo,
          registerValidUntil: r.registerValidUntil,
          materialName: r.materialName,
          barcode: r.barcode,
          instrumentCategory: r.instrumentCategory,
          instrumentType: r.instrumentType,
          sterMethod: r.sterMethod,
          washMethod: r.washMethod,
          packRequirement: r.packRequirement,
          isHighValue: 0,
          isKeyMonitor: 0,
          currentStatus: '在用',
          isActive: 1
        }
        await http.post('/md/instruments', payload)
        ok++
      } catch (e) {
        fail++
        lastErr = e?.message || e?.response?.data?.message || lastErr
      } finally {
        instrumentImportDone.value += 1
      }
    }
    ElMessage.success(`导入完成：成功 ${ok} 条${fail ? `，失败 ${fail} 条${lastErr ? `（${lastErr}）` : ''}` : ''}`)
    if (ok) {
      instrumentImportVisible.value = false
      await loadInstrumentPage()
    }
  } finally {
    instrumentImportSaving.value = false
  }
}

/** 供应商维护 */
const vendorLoading = ref(false)
const vendorRows = ref([])
const vendorSelected = ref([])
const vendorTableRef = ref(null)
const vendorPanelRef = ref(null)
const vendorKeyword = ref('')
const vendorPage = ref(1)
const vendorPageSize = ref(10)
const vendorTotal = ref(0)
const vendorDrawerVisible = ref(false)
const vendorDialogMode = ref('create')
const vendorSaving = ref(false)
const vendorFormTab = ref('basic')
const vendorFormRef = ref(null)
let vendorNameInitialsTimer = null
const vendorForm = reactive({
  id: '',
  orgId: '',
  vendorCode: '',
  vendorName: '',
  shortCode: '',
  socialCreditCode: '',
  legalPerson: '',
  salesman: '',
  contactPhone: '',
  companyAddress: '',
  email: '',
  registeredCapital: '',
  companyPrincipal: '',
  businessScope: '',
  fax: '',
  website: '',
  vendorTypes: [],
  isActive: 1,
  remark: ''
})
const vendorRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  vendorName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}
function parseVendorTypes(raw) {
  if (Array.isArray(raw)) return raw.filter(Boolean)
  if (!raw) return []
  return String(raw).split(/[,，;；]/).map((s) => s.trim()).filter(Boolean)
}
function formatVendorTypes(raw) {
  const opts = vendorTypeOptions.value.length
    ? vendorTypeOptions.value
    : readVendorTypes().map((r) => ({ value: r.code, label: r.name }))
  const set = new Set(parseVendorTypes(raw))
  const labels = opts.filter((t) => set.has(t.value)).map((t) => t.label)
  // 字典已删但历史仍存的编码，直接展示编码兜底
  for (const code of set) {
    if (!opts.some((t) => t.value === code)) labels.push(code)
  }
  return labels.join('、') || ''
}
function toggleVendorType(value) {
  const idx = vendorForm.vendorTypes.indexOf(value)
  if (idx >= 0) vendorForm.vendorTypes.splice(idx, 1)
  else vendorForm.vendorTypes.push(value)
}
function vendorIndex(i) {
  return (vendorPage.value - 1) * vendorPageSize.value + i + 1
}
function searchVendorPage() {
  vendorPage.value = 1
  loadVendorPage()
}
function onVendorPageSizeChange() {
  vendorPage.value = 1
  loadVendorPage()
}
function onVendorSelectionChange(rows) { vendorSelected.value = rows }
function onVendorRowDblclick(row) {
  vendorTableRef.value?.toggleRowSelection(row, !vendorSelected.value.some((r) => r.id === row.id))
}
function vendorRowClassName({ row }) {
  return vendorSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadVendorPage() {
  vendorLoading.value = true
  try {
    refreshVendorTypeOptions()
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = vendorKeyword.value?.trim() || undefined
    const params = { page: Number(vendorPage.value) || 1, size: Number(vendorPageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/vendors', { params })
    vendorRows.value = res?.data?.records || []
    vendorTotal.value = Number(res?.data?.total || 0)
    vendorSelected.value = []
  } catch (e) {
    ElMessage.error(e?.message || '供应商列表加载失败')
  } finally {
    vendorLoading.value = false
  }
}
function fillVendorForm(row) {
  vendorForm.id = row?.id || ''
  vendorForm.orgId = row?.orgId || ''
  vendorForm.vendorCode = row?.vendorCode || ''
  vendorForm.vendorName = row?.vendorName || ''
  vendorForm.shortCode = row?.shortCode || ''
  vendorForm.socialCreditCode = row?.socialCreditCode || ''
  vendorForm.legalPerson = row?.legalPerson || ''
  vendorForm.salesman = row?.salesman || ''
  vendorForm.contactPhone = row?.contactPhone || ''
  vendorForm.companyAddress = row?.companyAddress || ''
  vendorForm.email = row?.email || ''
  vendorForm.registeredCapital = row?.registeredCapital || ''
  vendorForm.companyPrincipal = row?.companyPrincipal || ''
  vendorForm.businessScope = row?.businessScope || ''
  vendorForm.fax = row?.fax || ''
  vendorForm.website = row?.website || ''
  vendorForm.vendorTypes = parseVendorTypes(row?.vendorTypes)
  vendorForm.isActive = row?.isActive ?? 1
  vendorForm.remark = row?.remark || ''
}
function resetVendorForm() {
  fillVendorForm(null)
  vendorFormTab.value = 'basic'
  vendorFormRef.value?.clearValidate?.()
}
async function fetchNameInitials(name) {
  const n = String(name || '').trim()
  if (!n) return ''
  try {
    const res = await http.get('/md/name-initials', { params: { name: n } })
    return String(res?.data?.initials || '').toUpperCase()
  } catch {
    return ''
  }
}
function onVendorNameInput() {
  clearTimeout(vendorNameInitialsTimer)
  vendorNameInitialsTimer = setTimeout(async () => {
    vendorForm.shortCode = await fetchNameInitials(vendorForm.vendorName)
  }, 200)
}
async function openVendorCreate() {
  await loadOrgOptions()
  refreshVendorTypeOptions()
  vendorDialogMode.value = 'create'
  resetVendorForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) vendorForm.orgId = oid
  else if (isPlatformAdmin.value) vendorForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  vendorDrawerVisible.value = true
}
async function openVendorEdit(row) {
  await loadOrgOptions()
  refreshVendorTypeOptions()
  vendorDialogMode.value = 'edit'
  vendorFormTab.value = 'basic'
  fillVendorForm(row)
  vendorDrawerVisible.value = true
}
async function submitVendorForm() {
  const form = vendorFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    vendorSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const oid = listOrgId(scope) || vendorForm.orgId
      const payload = {
        orgId: oid,
        vendorCode: vendorForm.vendorCode?.trim() || null,
        vendorName: vendorForm.vendorName.trim(),
        shortCode: (vendorForm.shortCode?.trim() || '').toUpperCase() || null,
        socialCreditCode: vendorForm.socialCreditCode?.trim() || null,
        legalPerson: vendorForm.legalPerson?.trim() || null,
        salesman: vendorForm.salesman?.trim() || null,
        contactPhone: vendorForm.contactPhone?.trim() || null,
        companyAddress: vendorForm.companyAddress?.trim() || null,
        email: vendorForm.email?.trim() || null,
        registeredCapital: vendorForm.registeredCapital?.trim() || null,
        companyPrincipal: vendorForm.companyPrincipal?.trim() || null,
        businessScope: vendorForm.businessScope?.trim() || null,
        fax: vendorForm.fax?.trim() || null,
        website: vendorForm.website?.trim() || null,
        vendorTypes: vendorForm.vendorTypes.length ? vendorForm.vendorTypes.join(',') : null,
        isActive: vendorForm.isActive,
        remark: vendorForm.remark?.trim() || null
      }
      if (vendorDialogMode.value === 'edit') payload.id = vendorForm.id
      await http.post('/md/vendors', payload)
      ElMessage.success(vendorDialogMode.value === 'edit' ? '供应商已修改' : '供应商已新增')
      vendorDrawerVisible.value = false
      await loadVendorPage()
    } finally {
      vendorSaving.value = false
    }
  })
}
async function deleteVendorRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除供应商「${row.vendorCode} ${row.vendorName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/vendors/${row.id}`)
  ElMessage.success('已删除')
  await loadVendorPage()
}

/** 生产厂家维护 */
const mfrLoading = ref(false)
const mfrRows = ref([])
const mfrSelected = ref([])
const mfrTableRef = ref(null)
const mfrPanelRef = ref(null)
const mfrKeyword = ref('')
const mfrPage = ref(1)
const mfrPageSize = ref(10)
const mfrTotal = ref(0)
const mfrDrawerVisible = ref(false)
const mfrDialogMode = ref('create')
const mfrSaving = ref(false)
const mfrFormTab = ref('basic')
const mfrFormRef = ref(null)
let mfrNameInitialsTimer = null
const mfrForm = reactive({
  id: '',
  orgId: '',
  mfrCode: '',
  mfrName: '',
  shortCode: '',
  socialCreditCode: '',
  legalPerson: '',
  salesman: '',
  contactPhone: '',
  companyAddress: '',
  email: '',
  isActive: 1,
  remark: ''
})
const mfrRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  mfrName: [{ required: true, message: '请输入生产厂家名称', trigger: 'blur' }]
}
function mfrIndex(i) {
  return (mfrPage.value - 1) * mfrPageSize.value + i + 1
}
function searchMfrPage() {
  mfrPage.value = 1
  loadMfrPage()
}
function onMfrPageSizeChange() {
  mfrPage.value = 1
  loadMfrPage()
}
function onMfrSelectionChange(rows) { mfrSelected.value = rows }
function onMfrRowDblclick(row) {
  mfrTableRef.value?.toggleRowSelection(row, !mfrSelected.value.some((r) => r.id === row.id))
}
function mfrRowClassName({ row }) {
  return mfrSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadMfrPage() {
  mfrLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = mfrKeyword.value?.trim() || undefined
    const params = { page: Number(mfrPage.value) || 1, size: Number(mfrPageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/manufacturers', { params })
    mfrRows.value = res?.data?.records || []
    mfrTotal.value = Number(res?.data?.total || 0)
    mfrSelected.value = []
  } catch (e) {
    ElMessage.error(e?.message || '生产厂家列表加载失败')
  } finally {
    mfrLoading.value = false
  }
}
function fillMfrForm(row) {
  mfrForm.id = row?.id || ''
  mfrForm.orgId = row?.orgId || ''
  mfrForm.mfrCode = row?.mfrCode || ''
  mfrForm.mfrName = row?.mfrName || ''
  mfrForm.shortCode = row?.shortCode || ''
  mfrForm.socialCreditCode = row?.socialCreditCode || ''
  mfrForm.legalPerson = row?.legalPerson || ''
  mfrForm.salesman = row?.salesman || ''
  mfrForm.contactPhone = row?.contactPhone || ''
  mfrForm.companyAddress = row?.companyAddress || ''
  mfrForm.email = row?.email || ''
  mfrForm.isActive = row?.isActive ?? 1
  mfrForm.remark = row?.remark || ''
}
function resetMfrForm() {
  fillMfrForm(null)
  mfrFormTab.value = 'basic'
  mfrFormRef.value?.clearValidate?.()
}
function onMfrNameInput() {
  clearTimeout(mfrNameInitialsTimer)
  mfrNameInitialsTimer = setTimeout(async () => {
    mfrForm.shortCode = await fetchNameInitials(mfrForm.mfrName)
  }, 200)
}
async function openMfrCreate() {
  await loadOrgOptions()
  mfrDialogMode.value = 'create'
  resetMfrForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) mfrForm.orgId = oid
  else if (isPlatformAdmin.value) mfrForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  mfrDrawerVisible.value = true
}
async function openMfrEdit(row) {
  await loadOrgOptions()
  mfrDialogMode.value = 'edit'
  mfrFormTab.value = 'basic'
  fillMfrForm(row)
  mfrDrawerVisible.value = true
}
async function submitMfrForm() {
  const form = mfrFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    mfrSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const oid = listOrgId(scope) || mfrForm.orgId
      const payload = {
        orgId: oid,
        mfrCode: mfrForm.mfrCode?.trim() || null,
        mfrName: mfrForm.mfrName.trim(),
        shortCode: (mfrForm.shortCode?.trim() || '').toUpperCase() || null,
        socialCreditCode: mfrForm.socialCreditCode?.trim() || null,
        legalPerson: mfrForm.legalPerson?.trim() || null,
        salesman: mfrForm.salesman?.trim() || null,
        contactPhone: mfrForm.contactPhone?.trim() || null,
        companyAddress: mfrForm.companyAddress?.trim() || null,
        email: mfrForm.email?.trim() || null,
        isActive: mfrForm.isActive,
        remark: mfrForm.remark?.trim() || null
      }
      if (mfrDialogMode.value === 'edit') payload.id = mfrForm.id
      await http.post('/md/manufacturers', payload)
      ElMessage.success(mfrDialogMode.value === 'edit' ? '生产厂家已修改' : '生产厂家已新增')
      mfrDrawerVisible.value = false
      await loadMfrPage()
    } finally {
      mfrSaving.value = false
    }
  })
}
async function deleteMfrRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除生产厂家「${row.mfrCode} ${row.mfrName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/manufacturers/${row.id}`)
  ElMessage.success('已删除')
  await loadMfrPage()
}

/** 设备维护 */
const deviceLoading = ref(false)
const deviceRows = ref([])
const deviceSelected = ref([])
const deviceTableRef = ref(null)
const devicePanelRef = ref(null)
const deviceKeyword = ref('')
const devicePage = ref(1)
const devicePageSize = ref(10)
const deviceTotal = ref(0)
const deviceDrawerVisible = ref(false)
const deviceDialogMode = ref('create')
const deviceSaving = ref(false)
const deviceFormTab = ref('basic')
const deviceFormRef = ref(null)
const deviceMfrOptions = ref([])
const deviceVendorOptions = ref([])
const deviceUnitOptions = ref([])
const deviceStaffOptions = ref([])
const deviceForm = reactive({
  id: '',
  orgId: '',
  deviceCode: '',
  deviceName: '',
  spec: '',
  model: '',
  unit: '',
  serialNo: '',
  deviceType: '',
  installLocation: '',
  areaName: '',
  capabilityParams: '',
  enabledDate: '',
  deviceStatus: '空闲',
  operatorId: '',
  operatorName: '',
  manufacturerId: '',
  vendorId: '',
  brandName: '',
  countryName: '',
  deviceRegNo: '',
  productionDate: '',
  originalValue: null,
  purchaseDate: '',
  maintainCycle: '',
  maintainPlan: '',
  lastMaintainAt: '',
  maintainExpireWarn: '',
  inspectCertNo: '',
  inspectValidUntil: '',
  certAttachment: '',
  certAttachmentData: '',
  isActive: 1,
  remark: ''
})
const deviceImageList = ref([])
const deviceMaintainLogList = ref([])
const deviceAdverseList = ref([])
const deviceRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}
const deviceAssetCardRef = ref(null)
const deviceAssetCardTitle = computed(() => {
  const org = orgOptions.value.find((o) => o.id === deviceForm.orgId)
  const name = org?.campusName || '本机构'
  return `${name}专用固定资产`
})
const deviceCardSpecModel = computed(() => {
  const parts = [deviceForm.spec, deviceForm.model].map((s) => (s || '').trim()).filter(Boolean)
  return [...new Set(parts)].join(' ')
})
const deviceCardOriginalValueText = computed(() => {
  const v = deviceForm.originalValue
  if (v == null || v === '') return '—'
  const n = Number(v)
  return Number.isFinite(n) ? n.toFixed(2) : String(v)
})
const deviceCardQrUrl = computed(() => {
  const code = (deviceForm.deviceCode || deviceForm.id || '').trim()
  if (!code) return ''
  return `https://api.qrserver.com/v1/create-qr-code/?size=120x120&margin=6&data=${encodeURIComponent(code)}`
})
function printDeviceAssetCard() {
  const el = deviceAssetCardRef.value
  if (!el) return
  const win = window.open('', '_blank', 'width=720,height=640')
  if (!win) {
    ElMessage.warning('浏览器拦截了打印窗口，请允许弹窗后重试')
    return
  }
  win.document.write(`<!DOCTYPE html><html><head><meta charset="utf-8"><title>${deviceAssetCardTitle.value}</title>
<style>
  body{margin:24px;font-family:Microsoft YaHei,sans-serif;color:#303133}
  .device-asset-card{width:420px;border-collapse:collapse;table-layout:fixed;border:1px solid #333}
  .device-asset-card th,.device-asset-card td{border:1px solid #333;padding:6px 8px;font-size:12px;vertical-align:middle}
  .device-asset-card__title{text-align:center;font-size:14px;font-weight:600;padding:8px}
  .device-asset-card__label{width:26%;background:#f5f5f5;white-space:nowrap}
  .device-asset-card__value{word-break:break-all}
  .device-asset-card__qr{width:30%;text-align:center}
  .device-asset-card__qr img{width:88px;height:88px}
</style></head><body>${el.innerHTML}</body></html>`)
  win.document.close()
  win.focus()
  setTimeout(() => {
    win.print()
    win.close()
  }, 300)
}
function parseDeviceJsonList(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) return raw
  try {
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}
function deviceIndex(i) {
  return (devicePage.value - 1) * devicePageSize.value + i + 1
}
function searchDevicePage() {
  devicePage.value = 1
  loadDevicePage()
}
function onDevicePageSizeChange() {
  devicePage.value = 1
  loadDevicePage()
}
function onDeviceSelectionChange(rows) { deviceSelected.value = rows }
function onDeviceRowDblclick(row) {
  deviceTableRef.value?.toggleRowSelection(row, !deviceSelected.value.some((r) => r.id === row.id))
}
function deviceRowClassName({ row }) {
  return deviceSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadDevicePage() {
  deviceLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = deviceKeyword.value?.trim() || undefined
    const params = { page: Number(devicePage.value) || 1, size: Number(devicePageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/devices', { params })
    deviceRows.value = res?.data?.records || []
    deviceTotal.value = Number(res?.data?.total || 0)
    deviceSelected.value = []
  } catch (e) {
    ElMessage.error(e?.message || '设备列表加载失败')
  } finally {
    deviceLoading.value = false
  }
}
function fillDeviceForm(row) {
  deviceForm.id = row?.id || ''
  deviceForm.orgId = row?.orgId || ''
  deviceForm.deviceCode = row?.deviceCode || ''
  deviceForm.deviceName = row?.deviceName || ''
  deviceForm.spec = row?.spec || ''
  deviceForm.model = row?.model || ''
  deviceForm.unit = row?.unit || ''
  deviceForm.serialNo = row?.serialNo || ''
  deviceForm.deviceType = row?.deviceType || ''
  deviceForm.installLocation = row?.installLocation || ''
  deviceForm.areaName = row?.areaName || ''
  deviceForm.capabilityParams = row?.capabilityParams || ''
  deviceForm.enabledDate = row?.enabledDate || ''
  deviceForm.deviceStatus = row?.deviceStatus || '空闲'
  deviceForm.operatorId = row?.operatorId || ''
  deviceForm.operatorName = row?.operatorName || ''
  deviceForm.manufacturerId = row?.manufacturerId || ''
  deviceForm.vendorId = row?.vendorId || ''
  deviceForm.brandName = row?.brandName || ''
  deviceForm.countryName = row?.countryName || ''
  deviceForm.deviceRegNo = row?.deviceRegNo || ''
  deviceForm.productionDate = row?.productionDate || ''
  deviceForm.originalValue = row?.originalValue != null && row?.originalValue !== '' ? Number(row.originalValue) : null
  deviceForm.purchaseDate = row?.purchaseDate || ''
  deviceForm.maintainCycle = row?.maintainCycle || ''
  deviceForm.maintainPlan = row?.maintainPlan || ''
  deviceForm.lastMaintainAt = row?.lastMaintainAt || ''
  deviceForm.maintainExpireWarn = row?.maintainExpireWarn || ''
  deviceForm.inspectCertNo = row?.inspectCertNo || ''
  deviceForm.inspectValidUntil = row?.inspectValidUntil || ''
  deviceForm.certAttachment = row?.certAttachment || ''
  // 列表不回填 base64；未重传时由后端按文件名保留原附件
  deviceForm.certAttachmentData = ''
  deviceForm.isActive = row?.isActive ?? 1
  deviceForm.remark = row?.remark || ''
  deviceImageList.value = parseDeviceJsonList(row?.deviceImages).map((x) => ({
    name: x?.name || 'image',
    dataUrl: x?.dataUrl || ''
  })).filter((x) => x.dataUrl)
  deviceMaintainLogList.value = parseDeviceJsonList(row?.maintainLogs).map((x) => ({
    maintainAt: x?.maintainAt || '',
    content: x?.content || '',
    operator: x?.operator || ''
  }))
  deviceAdverseList.value = parseDeviceJsonList(row?.adverseEvents).map((x) => ({
    eventAt: x?.eventAt || '',
    title: x?.title || '',
    content: x?.content || '',
    level: x?.level || ''
  }))
}
function resetDeviceForm() {
  fillDeviceForm(null)
  deviceFormTab.value = 'basic'
  deviceMfrOptions.value = []
  deviceVendorOptions.value = []
  deviceUnitOptions.value = []
  deviceStaffOptions.value = []
  deviceFormRef.value?.clearValidate?.()
}
function onDeviceImageChange(file) {
  const raw = file?.raw
  if (!raw) return
  if (raw.size > 3 * 1024 * 1024) {
    ElMessage.warning('单张图片请控制在 3MB 以内')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    deviceImageList.value.push({ name: raw.name, dataUrl: String(reader.result || '') })
  }
  reader.readAsDataURL(raw)
}
function removeDeviceImage(idx) {
  deviceImageList.value.splice(idx, 1)
}
function addDeviceMaintainLog() {
  deviceMaintainLogList.value.push({ maintainAt: '', content: '', operator: '' })
}
function removeDeviceMaintainLog(idx) {
  deviceMaintainLogList.value.splice(idx, 1)
}
function addDeviceAdverseEvent() {
  deviceAdverseList.value.push({ eventAt: '', title: '', content: '', level: '' })
}
function removeDeviceAdverseEvent(idx) {
  deviceAdverseList.value.splice(idx, 1)
}
async function loadDeviceDictOptions(orgId) {
  const params = { page: 1, size: 500 }
  if (orgId) params.orgId = orgId
  try {
    const [mfrRes, vendorRes, unitRes, staffRes] = await Promise.all([
      http.get('/md/manufacturers', { params }),
      http.get('/md/vendors', { params }),
      http.get('/md/units', { params }),
      http.get('/md/staffs', { params })
    ])
    deviceMfrOptions.value = (mfrRes?.data?.records || []).filter((r) => r.isActive !== 0)
    deviceVendorOptions.value = (vendorRes?.data?.records || []).filter((r) => r.isActive !== 0)
    deviceUnitOptions.value = (unitRes?.data?.records || []).filter((r) => r.isActive !== 0)
    deviceStaffOptions.value = (staffRes?.data?.records || []).filter((r) => r.isActive !== 0)
  } catch (e) {
    deviceMfrOptions.value = []
    deviceVendorOptions.value = []
    deviceUnitOptions.value = []
    deviceStaffOptions.value = []
    ElMessage.error(e?.message || '设备字典选项加载失败')
  }
}
async function onDeviceOrgChange(orgId) {
  deviceForm.manufacturerId = ''
  deviceForm.vendorId = ''
  deviceForm.unit = ''
  deviceForm.operatorId = ''
  deviceForm.operatorName = ''
  await loadDeviceDictOptions(orgId)
}
function onDeviceCertFileChange(file) {
  const raw = file?.raw
  if (!raw) return
  if (raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('证书附件请控制在 5MB 以内')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    deviceForm.certAttachment = raw.name
    deviceForm.certAttachmentData = String(reader.result || '')
  }
  reader.readAsDataURL(raw)
}
function clearDeviceCert() {
  deviceForm.certAttachment = ''
  deviceForm.certAttachmentData = ''
}
async function openDeviceCreate() {
  await loadOrgOptions()
  deviceDialogMode.value = 'create'
  resetDeviceForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) deviceForm.orgId = oid
  else if (isPlatformAdmin.value) deviceForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  await loadDeviceDictOptions(deviceForm.orgId)
  deviceDrawerVisible.value = true
}
async function openDeviceEdit(row) {
  await loadOrgOptions()
  deviceDialogMode.value = 'edit'
  deviceFormTab.value = 'basic'
  fillDeviceForm(row)
  await loadDeviceDictOptions(deviceForm.orgId)
  deviceDrawerVisible.value = true
}
async function submitDeviceForm() {
  const form = deviceFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    deviceSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const oid = listOrgId(scope) || deviceForm.orgId
      const payload = {
        orgId: oid,
        deviceCode: deviceForm.deviceCode?.trim() || null,
        deviceName: deviceForm.deviceName.trim(),
        spec: deviceForm.spec?.trim() || null,
        model: deviceForm.model?.trim() || null,
        unit: deviceForm.unit?.trim() || null,
        serialNo: deviceForm.serialNo?.trim() || null,
        deviceType: deviceForm.deviceType?.trim() || null,
        installLocation: deviceForm.installLocation?.trim() || null,
        areaName: deviceForm.areaName?.trim() || null,
        capabilityParams: deviceForm.capabilityParams?.trim() || null,
        enabledDate: deviceForm.enabledDate || null,
        deviceStatus: deviceForm.deviceStatus || '空闲',
        operatorId: deviceForm.operatorId || null,
        manufacturerId: deviceForm.manufacturerId || null,
        vendorId: deviceForm.vendorId || null,
        brandName: deviceForm.brandName?.trim() || null,
        countryName: deviceForm.countryName?.trim() || null,
        deviceRegNo: deviceForm.deviceRegNo?.trim() || null,
        productionDate: deviceForm.productionDate || null,
        originalValue: deviceForm.originalValue != null && deviceForm.originalValue !== '' ? deviceForm.originalValue : null,
        purchaseDate: deviceForm.purchaseDate || null,
        deviceImages: JSON.stringify(deviceImageList.value.filter((x) => x?.dataUrl)),
        maintainLogs: JSON.stringify(deviceMaintainLogList.value),
        adverseEvents: JSON.stringify(deviceAdverseList.value),
        maintainCycle: deviceForm.maintainCycle?.trim() || null,
        maintainPlan: deviceForm.maintainPlan?.trim() || null,
        lastMaintainAt: deviceForm.lastMaintainAt || null,
        maintainExpireWarn: deviceForm.maintainExpireWarn?.trim() || null,
        inspectCertNo: deviceForm.inspectCertNo?.trim() || null,
        inspectValidUntil: deviceForm.inspectValidUntil || null,
        certAttachment: deviceForm.certAttachment?.trim() || null,
        certAttachmentData: deviceForm.certAttachmentData || null,
        isActive: deviceForm.isActive,
        remark: deviceForm.remark?.trim() || null
      }
      if (deviceDialogMode.value === 'edit') payload.id = deviceForm.id
      await http.post('/md/devices', payload)
      ElMessage.success(deviceDialogMode.value === 'edit' ? '设备已修改' : '设备已新增')
      deviceDrawerVisible.value = false
      await loadDevicePage()
    } finally {
      deviceSaving.value = false
    }
  })
}
async function deleteDeviceRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除设备「${row.deviceCode} ${row.deviceName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/devices/${row.id}`)
  ElMessage.success('已删除')
  await loadDevicePage()
}

/** 单位维护（仿院区维护） */
const unitLoading = ref(false)
const unitRows = ref([])
const unitSelected = ref([])
const unitTableRef = ref(null)
const unitPanelRef = ref(null)
const unitKeyword = ref('')
const unitPage = ref(1)
const unitPageSize = ref(10)
const unitTotal = ref(0)
const unitDrawerVisible = ref(false)
const unitDialogMode = ref('create')
const unitSaving = ref(false)
const unitFormRef = ref(null)
const unitForm = reactive({
  id: '',
  orgId: '',
  unitCode: '',
  unitName: '',
  isActive: 1,
  remark: ''
})
const unitRules = {
  orgId: [{ required: true, message: '请选择组织机构', trigger: 'change' }],
  unitName: [{ required: true, message: '请输入单位名称', trigger: 'blur' }]
}
function unitIndex(i) {
  return (unitPage.value - 1) * unitPageSize.value + i + 1
}
function searchUnitPage() {
  unitPage.value = 1
  loadUnitPage()
}
function onUnitPageSizeChange() {
  unitPage.value = 1
  loadUnitPage()
}
function onUnitSelectionChange(rows) { unitSelected.value = rows }
function onUnitRowDblclick(row) {
  unitTableRef.value?.toggleRowSelection(row, !unitSelected.value.some((r) => r.id === row.id))
}
function unitRowClassName({ row }) {
  return unitSelected.value.some((r) => r.id === row.id) ? 'org-row-selected' : ''
}
async function loadUnitPage() {
  unitLoading.value = true
  try {
    const scope = await resolveStaffOrgScope()
    await loadOrgOptions()
    const kw = unitKeyword.value?.trim() || undefined
    const params = { page: Number(unitPage.value) || 1, size: Number(unitPageSize.value) || 10 }
    if (kw) params.keyword = kw
    const oid = listOrgId(scope)
    if (oid) params.orgId = oid
    const res = await http.get('/md/units', { params })
    const records = res?.data?.records
    const rows = (Array.isArray(records) ? records : []).map((r) => ({
      ...r,
      orgLabel: orgLabelById(r.orgId) || ''
    }))
    unitTotal.value = Number(res?.data?.total || rows.length || 0)
    unitRows.value = rows
    unitSelected.value = []
  } catch (e) {
    ElMessage.error(e?.message || '单位列表加载失败')
  } finally {
    unitLoading.value = false
  }
}
function fillUnitForm(row) {
  unitForm.id = row?.id || ''
  unitForm.orgId = row?.orgId || ''
  unitForm.unitCode = row?.unitCode || ''
  unitForm.unitName = row?.unitName || ''
  unitForm.isActive = row?.isActive ?? 1
  unitForm.remark = row?.remark || ''
}
function resetUnitForm() {
  fillUnitForm(null)
  unitFormRef.value?.clearValidate?.()
}
async function openUnitCreate() {
  await loadOrgOptions()
  unitDialogMode.value = 'create'
  resetUnitForm()
  const scope = await resolveStaffOrgScope()
  const oid = listOrgId(scope)
  if (oid) unitForm.orgId = oid
  else if (isPlatformAdmin.value) unitForm.orgId = effectiveOrgId() || orgOptions.value[0]?.id || ''
  unitDrawerVisible.value = true
}
async function openUnitEdit(row) {
  await loadOrgOptions()
  unitDialogMode.value = 'edit'
  fillUnitForm(row)
  unitDrawerVisible.value = true
}
async function submitUnitForm() {
  const form = unitFormRef.value
  if (!form) return
  await form.validate(async (ok) => {
    if (!ok) return
    unitSaving.value = true
    try {
      const scope = await resolveStaffOrgScope()
      const oid = listOrgId(scope) || unitForm.orgId
      const payload = {
        orgId: oid,
        unitCode: unitForm.unitCode?.trim() || null,
        unitName: unitForm.unitName.trim(),
        isActive: unitForm.isActive,
        remark: unitForm.remark?.trim() || null
      }
      if (unitDialogMode.value === 'edit') payload.id = unitForm.id
      await http.post('/md/units', payload)
      ElMessage.success(unitDialogMode.value === 'edit' ? '单位已修改' : '单位已新增')
      unitDrawerVisible.value = false
      await loadUnitPage()
    } catch {
      await loadUnitPage()
    } finally {
      unitSaving.value = false
    }
  })
}
async function deleteUnitRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除单位「${row.unitCode} ${row.unitName}」？`, '删除确认', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    })
  } catch { return }
  await http.delete(`/md/units/${row.id}`)
  ElMessage.success('已删除')
  await loadUnitPage()
}

function ensureData(name) {
  if (name === 'org') loadOrg()
  else if (name === 'staffs') loadStaff()
  else if (name === 'perms') loadPerms()
  else if (name === 'campuses') loadCampusPage()
  else if (name === 'warehouses') loadWarehousePage()
  else if (name === 'vendors') loadVendorPage()
  else if (name === 'manufacturers') loadMfrPage()
  else if (name === 'instruments') loadInstrumentPage()
  else if (name === 'templates') loadTplPage()
  else if (name === 'devices') loadDevicePage()
  else if (name === 'units') loadUnitPage()
  else if (name === 'depts') {
    loadDeptTree()
    loadDeptPage()
  }
  else if (name === 'sys-config') loadSysConfigPage()
}

watch(active, (name) => ensureData(name), { immediate: true })
watch(selectedOrgId, () => {
  const name = active.value
  if (name === 'depts') {
    deptTreeFilterId.value = ''
    deptPage.value = 1
    loadDeptTree()
    loadDeptPage()
  } else if (name === 'staffs') {
    staffRoleFilter.value = ''
    staffPage.value = 1
    loadStaff()
  } else {
    ensureData(name)
  }
})
onMounted(async () => {
  await initOrgContext()
  ensureData(active.value)
})
</script>
<style scoped>
.md-page {
  height: 100%;
  /* 右侧不留 padding，抽屉才能贴齐工作区右缘 */
  padding: 12px 0 0 12px;
  box-sizing: border-box;
  overflow: hidden;
}
.org-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}
.md-table-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.md-table-wrap :deep(.el-table) {
  width: 100%;
}
/* 列宽超出时明细区出现横向滚动条（位于翻页条上方） */
.md-table-wrap :deep(.el-table__inner-wrapper),
.md-table-wrap :deep(.el-table__body-wrapper),
.md-table-wrap :deep(.el-scrollbar__wrap) {
  overflow-x: auto !important;
}
/* 仅加粗底部横向滚动条；纵向宽度不改 */
.md-table-wrap :deep(.el-scrollbar__wrap)::-webkit-scrollbar,
.md-table-wrap :deep(.el-table__body-wrapper)::-webkit-scrollbar {
  height: 16px;
}
.md-table-wrap :deep(.el-scrollbar__wrap)::-webkit-scrollbar-thumb,
.md-table-wrap :deep(.el-table__body-wrapper)::-webkit-scrollbar-thumb {
  border-radius: 8px;
}
.md-table-wrap :deep(.el-scrollbar__bar.is-horizontal) {
  height: 12px !important;
}
.md-table-wrap :deep(.el-scrollbar__bar.is-horizontal .el-scrollbar__thumb) {
  border-radius: 6px;
}
.md-pager {
  flex-shrink: 0;
  padding: 10px 12px 8px 0;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
  gap: 4px;
  box-sizing: border-box;
}
/* 抽屉/弹窗挂在内容容器内：绝对定位，不挡顶栏、页签与左侧菜单 */
.org-panel :deep(.org-drawer-modal.el-overlay),
.org-panel :deep(.el-overlay.is-drawer),
.org-panel :deep(.el-overlay) {
  position: absolute !important;
  inset: 0 !important;
  background-color: transparent !important;
}
/* 器械弹窗：铺满主工作区，仅盖住列表区域 */
.org-panel :deep(.instrument-dialog-modal.el-overlay) {
  background-color: color-mix(in srgb, var(--brand-dark, #0b5f66) 22%, transparent) !important;
  z-index: 30 !important;
}
.org-panel :deep(.instrument-dialog-modal .el-overlay-dialog) {
  position: absolute !important;
  inset: 0 !important;
  overflow: hidden;
  display: flex !important;
  align-items: stretch !important;
  justify-content: stretch !important;
  padding: 10px 12px 12px !important;
  box-sizing: border-box;
}
.org-panel :deep(.instrument-dialog.el-dialog) {
  position: relative !important;
  width: 100% !important;
  max-width: none !important;
  height: 100% !important;
  margin: 0 !important;
  top: auto !important;
  left: auto !important;
  transform: none !important;
  display: flex !important;
  flex-direction: column !important;
  overflow: hidden;
}
/* 右侧抽屉贴齐容器右缘，去掉圆角造成的“悬空”感 */
.org-panel :deep(.el-drawer.rtl),
.org-panel :deep(.el-drawer),
.sys-config-page :deep(.el-drawer.rtl),
.sys-config-page :deep(.el-drawer) {
  right: 0 !important;
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  padding-right: 12px;
  box-sizing: border-box;
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
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
  white-space: nowrap;
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
/* 抽屉顶栏：主题色浅渐变，相对白底表单与顶栏有明显色差，随 --brand 换主题 */
.org-panel :deep(.el-drawer__header),
.sys-config-page :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 0;
  border-bottom: 1px solid color-mix(in srgb, var(--brand) 28%, #d0d7dc);
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--brand) 28%, #ffffff) 0%,
    color-mix(in srgb, var(--brand) 16%, var(--brand-soft, #eef6f7)) 55%,
    color-mix(in srgb, var(--brand) 22%, #ffffff) 100%
  );
}
.org-drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;
  padding: 14px 16px;
  background: transparent;
}
.org-drawer-title {
  flex: 1;
  min-width: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ink, var(--el-text-color-primary));
  line-height: 1.4;
}
.drawer-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  width: 100%;
}
.org-panel :deep(.el-drawer__footer),
.sys-config-page :deep(.el-drawer__footer) {
  display: flex;
  justify-content: center;
  padding: 12px 16px 16px;
}
.staff-form-tabs {
  margin: 0 0 8px;
}
.staff-form-tabs :deep(.el-tabs__header) {
  margin-bottom: 8px;
}
.staff-form-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
.staff-train-pane {
  min-height: 200px;
}
.staff-form-grid :deep(.el-form-item) {
  margin-bottom: 14px;
}
.staff-form-grid :deep(.el-input-number .el-input__inner) {
  text-align: left;
}
.vendor-type-lights {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}
.vendor-type-light {
  border: 1px solid #dcdfe6;
  background: #f5f7fa;
  color: #606266;
  border-radius: 4px;
  padding: 6px 10px;
  font-size: 12px;
  line-height: 1.3;
  cursor: pointer;
  transition: all 0.15s ease;
}
.vendor-type-light:hover {
  border-color: var(--brand, #1aa6a0);
  color: var(--brand, #1aa6a0);
}
.vendor-type-light.is-on {
  background: var(--brand, #1aa6a0);
  border-color: var(--brand, #1aa6a0);
  color: #fff;
}
.device-cert-upload {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  width: 100%;
}
.device-cert-name {
  font-size: 13px;
  color: #606266;
  word-break: break-all;
}
.device-asset-card-wrap {
  padding: 4px 0 8px;
}
.device-asset-card-print {
  max-width: 420px;
}
.device-asset-card {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  background: #fff;
  border: 1px solid #c0c4cc;
}
.device-asset-card th,
.device-asset-card td {
  border: 1px solid #c0c4cc;
  padding: 6px 8px;
  font-size: 12px;
  color: #303133;
  vertical-align: middle;
  line-height: 1.35;
}
.device-asset-card__title {
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
  padding: 8px;
}
.device-asset-card__label {
  width: 26%;
  background: #fafafa;
  color: #606266;
  white-space: nowrap;
}
.device-asset-card__value {
  width: 44%;
  word-break: break-all;
}
.device-asset-card__qr {
  width: 30%;
  text-align: center;
  vertical-align: middle;
  background: #fff;
  padding: 4px;
}
.device-asset-card__qr img {
  width: 88px;
  height: 88px;
  display: inline-block;
}
.device-asset-card__qr-empty {
  color: #c0c4cc;
  font-size: 11px;
}
.device-asset-card__hint {
  margin: 8px 0 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.device-ext-pane {
  padding: 4px 0 8px;
}
.device-ext-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.device-image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.device-image-item {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}
.device-image-item img {
  width: 100%;
  height: 110px;
  object-fit: cover;
  display: block;
  background: #f5f7fa;
}
.device-image-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  padding: 6px 8px;
  font-size: 12px;
  color: #606266;
}
.device-image-meta span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
.auth-default-tag {
  margin-left: 4px;
  font-size: 11px;
  color: #fff;
  background: var(--brand, #1aa6a0);
  border-radius: 3px;
  padding: 0 5px;
  line-height: 16px;
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
.dept-page {
  overflow: hidden;
}
.dept-body {
  display: flex;
  flex: 1;
  min-height: 0;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}
.dept-side {
  width: 260px;
  flex-shrink: 0;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  background: #fafbfc;
  min-height: 0;
}
.dept-tree {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 6px 4px;
}
.dept-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  padding: 8px 10px 0;
  overflow: hidden;
}
.dept-main .toolbar {
  flex-shrink: 0;
}
.dept-main .md-table-wrap {
  flex: 1;
  min-height: 0;
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
/* 授权弹窗：顶栏铺满整宽并盖住上沿，不留白边 */
.org-auth-dialog.el-dialog {
  padding: 0;
  overflow: hidden;
}
.org-auth-dialog .el-dialog__header {
  margin: 0;
  margin-right: 0;
  padding: 14px 16px;
  border-bottom: 1px solid color-mix(in srgb, var(--brand) 28%, #d0d7dc);
  background: linear-gradient(
    90deg,
    color-mix(in srgb, var(--brand) 28%, #ffffff) 0%,
    color-mix(in srgb, var(--brand) 16%, var(--brand-soft, #eef6f7)) 55%,
    color-mix(in srgb, var(--brand) 22%, #ffffff) 100%
  );
}
.org-auth-dialog .el-dialog__title {
  color: var(--ink, var(--el-text-color-primary));
  font-weight: 600;
}
.org-auth-dialog .el-dialog__body {
  padding: 8px 16px 8px;
}
.org-auth-dialog .auth-tabs {
  margin: 0 0 8px;
}
.org-auth-dialog .auth-tabs .el-tabs__header {
  margin-bottom: 8px;
}
.org-auth-dialog .auth-tabs .el-tabs__nav-wrap::after {
  height: 1px;
}
.org-auth-dialog .el-dialog__footer {
  border-top: 1px solid var(--el-border-color-lighter);
  padding: 12px 16px 16px;
  display: flex;
  justify-content: center;
}
.instrument-dialog.el-dialog {
  max-width: none;
}
.instrument-dialog .el-dialog__header {
  padding: 0;
  flex-shrink: 0;
}
.instrument-dialog .org-drawer-header {
  width: 100%;
  box-sizing: border-box;
  padding: 14px 16px;
}
.instrument-dialog .el-dialog__body {
  flex: 1 1 auto;
  min-height: 0;
  max-height: none;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0 16px;
}
.instrument-dialog-body {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  min-height: 0;
  height: 100%;
}
.instrument-dialog-tabs {
  flex-shrink: 0;
  margin: 4px 0 0;
  background: #fff;
  z-index: 2;
}
.instrument-dialog-scroll {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  padding: 4px 4px 12px;
}
.instrument-form-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  column-gap: 10px;
  row-gap: 2px;
  align-items: start;
}
.instrument-form-grid > .el-form-item {
  margin-bottom: 12px;
}
.instrument-form-span-5 {
  grid-column: 1 / -1;
}
.vendor-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 16px;
  row-gap: 2px;
  align-items: start;
}
.vendor-form-grid > .el-form-item {
  margin-bottom: 12px;
}
.vendor-form-span-2 {
  grid-column: 1 / -1;
}
.instrument-dialog .el-dialog__footer {
  flex-shrink: 0;
  justify-content: center !important;
}
.instrument-dialog .drawer-footer,
.instrument-dialog .instrument-dialog-footer {
  width: 100%;
  justify-content: center !important;
}
.org-auth-dialog .auth-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  width: 100%;
}
.staff-import-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.staff-import-file {
  font-size: 13px;
  color: var(--muted, #5a7380);
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.staff-import-ok {
  color: #67c23a;
  font-weight: 600;
}
.staff-import-fail {
  color: #f56c6c;
  font-weight: 600;
}
.staff-import-progress {
  margin: 0 0 12px;
}
.staff-import-progress-text {
  margin-top: 6px;
  font-size: 13px;
  color: var(--muted, #5a7380);
  text-align: center;
}
</style>
