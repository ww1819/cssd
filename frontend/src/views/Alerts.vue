<template>
  <div class="page">
    <el-tabs>
      <el-tab-pane label="即将过期">
        <el-table :data="alerts.expiring" border><el-table-column prop="barcode" label="条码" /><el-table-column prop="templateName" label="包" /><el-table-column prop="expireDate" label="失效日" /></el-table>
      </el-tab-pane>
      <el-tab-pane label="已过期">
        <el-table :data="alerts.expired" border><el-table-column prop="barcode" label="条码" /><el-table-column prop="templateName" label="包" /></el-table>
      </el-tab-pane>
      <el-tab-pane label="未回收">
        <el-table :data="alerts.unrecycled" border><el-table-column prop="barcode" label="条码" /><el-table-column prop="status" label="状态" /><el-table-column prop="locDeptName" label="科室" /></el-table>
      </el-tab-pane>
      <el-tab-pane label="召回单">
        <el-table :data="recalls" border>
          <el-table-column prop="recallNo" label="单号" />
          <el-table-column prop="recallReason" label="原因" />
          <el-table-column prop="deviceNo" label="灭菌器" />
          <el-table-column label="明细"><template #default="{ row }"><el-button size="small" @click="open(row)">已使用患者</el-button></template></el-table-column>
        </el-table>
        <el-table :data="ritems" border style="margin-top:8px">
          <el-table-column prop="barcode" /><el-table-column prop="patientName" label="患者" /><el-table-column prop="patientNo" label="住院号" /><el-table-column prop="usedFlag" label="已使用" />
        </el-table>
        <el-input v-model="retBc" placeholder="在科召回扫码退回" style="width:240px;margin-top:8px" />
        <el-button @click="ret">确认收回</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api'
const alerts = ref({}); const recalls = ref([]); const ritems = ref([]); const retBc = ref('')
onMounted(async () => {
  alerts.value = (await http.get('/alerts')).data
  recalls.value = (await http.get('/recall', { params: { size: 20 } })).data.records
})
async function open(row) { ritems.value = (await http.get('/recall/' + row.id + '/items')).data }
async function ret() { await http.post('/recall/return', { barcode: retBc.value }) }
</script>
