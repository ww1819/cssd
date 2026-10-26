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
            <el-button type="success" :icon="Upload" :loading="staffImportSaving" :disabled="!staffImportRows.length" @click="submitStaffImport">导入</el-button>
            <span v-if="staffImportFileName" class="staff-import-file">{{ staffImportFileName }}</span>
          </div>
          <el-table :data="staffImportRows" border height="320" class="md-table" empty-text="请先选择文件并点击解析">
            <el-table-column type="index" label="序号" width="56" />
            <el-table-column prop="staffNo" label="工号" min-width="100" show-overflow-tooltip />
            <el-table-column prop="staffName" label="姓名" min-width="100" show-overflow-tooltip />
            <el-table-column prop="staffRole" label="岗位编码" min-width="100" show-overflow-tooltip />
            <el-table-column prop="password" label="密码" min-width="90" show-overflow-tooltip />
            <el-table-column prop="contactName" label="联系人" min-width="90" show-overflow-tooltip />
            <el-table-column prop="contactPhone" label="联系电话" min-width="110" show-overflow-tooltip />
            <el-table-column prop="deptCode" label="科室编码" min-width="100" show-overflow-tooltip />
            <el-table-column prop="userType" label="用户类型" width="90" show-overflow-tooltip />
            <el-table-column prop="error" label="校验" min-width="120" show-overflow-tooltip />
          </el-table>
          <template #footer>
            <div class="auth-footer">
              <el-button @click="staffImportVisible = false">关闭</el-button>
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
          <el-form ref="staffFormRef" class="staff-form-grid" :model="staffForm" :rules="staffRules" label-width="108px">
            <el-row :gutter="12">
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
import { Plus, Search, Refresh, Select, Close, Rank, Document, Setting, Folder, Upload, Download } from '@element-plus/icons-vue'
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
  { id: 'station', label: '工位大厅', disabled: true },
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
      { id: 'md-instruments', label: '器械字典' },
      { id: 'md-templates', label: '包模板' },
      { id: 'md-devices', label: '设备' },
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
/** 按允许的菜单键裁剪授权树；始终保留工位大厅 */
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
  // 工位大厅默认授权，禁止取消
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
    // 取消时仍保留默认「工位大厅」
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
    ElMessage.warning('请先对该组织机构完成菜单授权（工位大厅为默认），再执行初始化')
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
      `管理员账号已生成（仅保留已授权菜单，含默认工位大厅）\n账号：${staffNo}\n密码：${data.password || staffNo}\n姓名：${data.staffName || ''}`,
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
    const oid = listOrgId(scope)
    if (oid) {
      const filterScope = { ...scope, orgId: oid, isPlatform: false }
      rows = rows.filter((r) => rowBelongsToOrg(r, filterScope))
      if (staffRoleFilter.value) {
        rows = rows.filter((r) => String(r.staffRole || '') === String(staffRoleFilter.value))
      }
      staffTotal.value = rows.length
      // 前端兜底分页
      const start = (staffPage.value - 1) * staffPageSize.value
      rows = rows.slice(start, start + staffPageSize.value)
    } else {
      staffTotal.value = Number(res.data.total || 0)
    }
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

const STAFF_IMPORT_HEADERS = [
  '工号', '姓名', '岗位编码', '密码', '年龄', '学历', '专业', '职称', '能级',
  '联系人', '联系电话', 'QQ', '电子邮箱', '科室编码', '用户类型', '是否启用', '备注'
]
const staffImportVisible = ref(false)
const staffImportFile = ref(null)
const staffImportFileName = ref('')
const staffImportRows = ref([])
const staffImportSaving = ref(false)
function openStaffImport() {
  staffImportFile.value = null
  staffImportFileName.value = ''
  staffImportRows.value = []
  staffImportVisible.value = true
}
function downloadStaffImportTemplate() {
  // UTF-8 BOM CSV：浏览器直读稳定；Excel 也能正确认中文表头
  const sample = [
    STAFF_IMPORT_HEADERS.join(','),
    '10283,张三,admin,10283,30,本科,护理,护师,N1,张三,13800000000,,,0001,normal,1,'
  ].join('\r\n')
  const blob = new Blob(['\ufeff' + sample], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '人员档案导入模板.csv'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('模板已下载')
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
    工号: ['工号', '员工号', '用户名', '账号'],
    姓名: ['姓名', '名称', '员工姓名'],
    岗位编码: ['岗位编码', '岗位', '角色编码', 'staffRole'],
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
    科室编码: ['科室编码', '科室', '部门编码'],
    用户类型: ['用户类型'],
    是否启用: ['是否启用', '启用'],
    备注: ['备注']
  }
  const map = {}
  Object.keys(aliases).forEach((key) => {
    const i = headers.findIndex((h) => {
      const nh = normalizeImportHeader(h)
      return aliases[key].some((a) => {
        const na = normalizeImportHeader(a)
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
    // 模板固定列序兜底
    if (headers.length >= 2 && (headers[0] === '工号' || headers[0].includes('工号'))) {
      const fixed = {}
      STAFF_IMPORT_HEADERS.forEach((h, idx) => { fixed[h] = idx })
      return { headerIndex: i, headers, col: fixed }
    }
  }
  return null
}
async function parseStaffImportFile() {
  if (!staffImportFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  refreshJobPostOptions()
  await loadStaffDeptLookup()
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
      // 再试其它分隔符
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
  const roleSet = new Set((jobPostOptions.value.length ? jobPostOptions.value : readJobPosts()).map((p) => p.code))
  const rows = []
  for (let li = headerIndex + 1; li < matrix.length; li++) {
    const cols = matrix[li]
    const staffNo = getFrom(cols, '工号')
    const staffName = getFrom(cols, '姓名')
    if (!staffNo && !staffName) continue
    const staffRole = getFrom(cols, '岗位编码') || defaultStaffRole()
    const deptCode = getFrom(cols, '科室编码')
    const deptHit = staffDeptLookup.value.find((d) => String(d.deptCode) === String(deptCode))
    const userType = getFrom(cols, '用户类型') === 'admin' ? 'admin' : 'normal'
    const activeRaw = getFrom(cols, '是否启用')
    const isActive = activeRaw === '0' || activeRaw === '否' || activeRaw.toLowerCase() === 'false' ? 0 : 1
    const ageRaw = getFrom(cols, '年龄')
    const errors = []
    if (!staffNo) errors.push('缺工号')
    if (!staffName) errors.push('缺姓名')
    if (staffRole && !roleSet.has(staffRole)) errors.push('岗位编码无效')
    if (deptCode && !deptHit) errors.push('科室编码无效')
    rows.push({
      staffNo,
      staffName,
      staffRole,
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
      deptCode,
      deptId: deptHit?.id || null,
      userType,
      isActive,
      remark: getFrom(cols, '备注') || null,
      error: errors.join('；')
    })
  }
  staffImportRows.value = rows
  if (!rows.length) ElMessage.warning('未解析到数据行，请在模板中填写后再解析')
  else ElMessage.success(`已解析 ${rows.length} 行`)
}
async function submitStaffImport() {
  const rows = staffImportRows.value.filter((r) => !r.error)
  if (!rows.length) {
    ElMessage.warning('没有可导入的有效数据')
    return
  }
  staffImportSaving.value = true
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
          userType: r.userType,
          orgId,
          orgCode: orgHit?.campusCode || null,
          isActive: r.isActive,
          remark: r.remark
        })
        ok++
      } catch {
        fail++
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
    localStorage.removeItem(orgScopedStorageKey(JOB_POST_STORAGE))
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

const dataTabs = reactive([
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

function ensureData(name) {
  if (name === 'org') loadOrg()
  else if (name === 'staffs') loadStaff()
  else if (name === 'perms') loadPerms()
  else if (name === 'campuses') loadCampusPage()
  else if (name === 'warehouses') loadWarehousePage()
  else if (name === 'depts') {
    loadDeptTree()
    loadDeptPage()
  }
  else if (name === 'sys-config') loadSysConfigPage()
  else {
    const t = dataTabs.find(x => x.key === name)
    if (t) load(t)
  }
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
/* 抽屉挂在内容容器内：绝对定位 + 透明蒙层，不挡顶栏、不变灰 */
.org-panel :deep(.org-drawer-modal.el-overlay),
.org-panel :deep(.el-overlay.is-drawer),
.org-panel :deep(.el-overlay) {
  position: absolute !important;
  inset: 0 !important;
  background-color: transparent !important;
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
</style>
