<template>
  <div class="page">
    <div class="report-head">主数据</div>
    <el-tabs>
      <el-tab-pane v-for="t in tabs" :key="t.key" :label="t.label">
        <el-button size="small" @click="load(t)">刷新</el-button>
        <el-table :data="t.rows" border height="480" style="margin-top:8px">
          <el-table-column v-for="c in t.cols" :key="c" :prop="c" :label="c" min-width="120" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import { onMounted, reactive } from 'vue'
import http from '../api'
const tabs = reactive([
  { key: 'campuses', label: '院区', path: '/md/campuses', cols: ['campusCode', 'campusName', 'isActive'], rows: [] },
  { key: 'depts', label: '科室', path: '/md/depts', cols: ['deptCode', 'deptName', 'deptKind'], rows: [] },
  { key: 'staffs', label: '人员', path: '/md/staffs', cols: ['staffNo', 'staffName', 'staffRole'], rows: [] },
  { key: 'templates', label: '包模板', path: '/md/templates', cols: ['templateCode', 'templateName', 'packCategory', 'needStore'], rows: [] },
  { key: 'devices', label: '设备', path: '/md/devices', cols: ['deviceCode', 'deviceNo', 'deviceKind', 'sterTempClass'], rows: [] },
  { key: 'instruments', label: '器械', path: '/md/instruments', cols: ['instrumentCode', 'instrumentName', 'spec'], rows: [] }
])
async function load(t) {
  t.rows = (await http.get(t.path, { params: { size: 50 } })).data.records
}
onMounted(() => tabs.forEach(load))
</script>
