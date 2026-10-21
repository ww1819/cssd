<template>
  <div class="page">
    <div class="report-head">寄消 / 外来器械 / 一次性</div>
    <el-tabs>
      <el-tab-pane label="寄消">
        <el-button type="primary" @click="loan">清洁区建寄消实例</el-button>
      </el-tab-pane>
      <el-tab-pane label="外来">
        <el-button type="primary" @click="ext">登记外来器械</el-button>
        <el-input v-model="extBc" placeholder="术后厂家取走条码" style="width:240px;margin-left:8px" />
        <el-button @click="take">厂家取走</el-button>
      </el-tab-pane>
      <el-tab-pane label="一次性">
        <el-button @click="io('in')">入库 +10</el-button>
        <el-button @click="io('out')">发放 1</el-button>
        <el-button type="danger" @click="block">禁用批次</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const extBc = ref('')
async function loan() {
  const r = await http.post('/loan', { templateId: '60000000-0000-7000-8000-000000000003', loanStaffId: '20000000-0000-7000-8000-000000000007' })
  ElMessage.success('寄消条码 ' + r.data.barcode)
}
async function ext() {
  const r = await http.post('/external', { templateId: '60000000-0000-7000-8000-000000000004', vendorId: '30000000-0000-7000-8000-000000000001', patientNo: 'ZY10001', patientName: '演示患者甲' })
  extBc.value = r.data.barcode
  ElMessage.success(r.data.barcode)
}
async function take() { await http.post('/external/vendor-take', { barcode: extBc.value }); ElMessage.success('已取走') }
async function io(kind) {
  await http.post('/once/io', { lotId: '91000000-0000-7000-8000-000000000001', ioKind: kind, qty: kind === 'in' ? 10 : 1, deptId: '10000000-0000-7000-8000-000000000012' })
  ElMessage.success('已记账')
}
async function block() {
  await http.post('/once/lots/91000000-0000-7000-8000-000000000001/block', { reason: '同厂同批观察' })
  ElMessage.success('已禁用')
}
</script>
