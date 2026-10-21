<template>
  <div class="page">
    <div class="report-head">配包 / 核包（双人，检查打包者=核包人）</div>
    <el-form :inline="true" style="margin-top:12px">
      <el-form-item label="模板">
        <el-select v-model="templateId" style="width:240px">
          <el-option v-for="t in tpls" :key="t.id" :label="t.templateCode + ' ' + t.templateName" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="回收明细ID"><el-input v-model="recycleItemId" style="width:280px" /></el-form-item>
      <el-button type="primary" @click="doPack">配包提交</el-button>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="barcode" label="条码" width="180" />
      <el-table-column prop="templateName" label="包名" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="packerName" label="配包人" />
      <el-table-column prop="checkerName" label="核包人/检查打包者" />
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="check(row, 1)">核包通过</el-button>
          <el-button size="small" @click="check(row, 0)">不通过</el-button>
          <el-button size="small" type="danger" @click="voidRow(row)">作废</el-button>
          <el-button size="small" @click="print(row, 'process')">过程签</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const tpls = ref([]); const list = ref([])
const templateId = ref('60000000-0000-7000-8000-000000000001')
const recycleItemId = ref('')
onMounted(async () => {
  tpls.value = (await http.get('/md/templates', { params: { size: 50 } })).data.records
  await reload()
})
async function reload() {
  list.value = (await http.get('/instances', { params: { size: 30 } })).data.records
}
async function doPack() {
  const res = await http.post('/pack', { templateId: templateId.value, recycleItemId: recycleItemId.value, qty: 1 })
  ElMessage.success('已配包 ' + res.data.barcode)
  await reload()
}
async function check(row, pass) {
  await http.post('/pack/' + row.id + '/check', { pass, reason: pass ? '' : '清单不符' })
  ElMessage.success(pass ? '已核包' : '保持未核')
  await reload()
}
async function voidRow(row) { await http.post('/pack/' + row.id + '/void'); await reload() }
async function print(row, phase) { await http.post('/print', { barcode: row.barcode, phase, writeRfid: false }); ElMessage.success('已记打印流水') }
</script>
