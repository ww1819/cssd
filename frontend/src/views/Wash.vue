<template>
  <div class="page">
    <div class="report-head">清洗批次</div>
    <el-form :inline="true" style="margin-top:12px">
      <el-form-item label="清洗机">
        <el-select v-model="deviceId" style="width:220px">
          <el-option v-for="d in devices" :key="d.id" :label="d.deviceNo + ' ' + d.deviceName" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="程序">
        <el-select v-model="programId" style="width:200px">
          <el-option v-for="p in programs" :key="p.id" :label="p.programName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="回收明细ID"><el-input v-model="recycleItemId" style="width:280px" /></el-form-item>
      <el-button type="primary" @click="create">建批</el-button>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="batchNo" label="单号" />
      <el-table-column prop="batchStatus" label="状态" />
      <el-table-column prop="checkResult" label="检测" />
      <el-table-column label="操作" width="360">
        <template #default="{ row }">
          <el-button size="small" @click="act('/wash/'+row.id+'/start')">开始</el-button>
          <el-button size="small" @click="act('/wash/'+row.id+'/finish')">结束</el-button>
          <el-button size="small" type="success" @click="act('/wash/'+row.id+'/check', { checkResult: 'pass' })">合格</el-button>
          <el-button size="small" type="danger" @click="act('/wash/'+row.id+'/check', { checkResult: 'fail' })">不合格</el-button>
          <el-button size="small" @click="act('/wash/'+row.id+'/rewash')">复洗</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const devices = ref([]); const programs = ref([]); const list = ref([])
const deviceId = ref('70000000-0000-7000-8000-000000000001')
const programId = ref('71000000-0000-7000-8000-000000000001')
const recycleItemId = ref('')
onMounted(async () => {
  devices.value = (await http.get('/md/devices', { params: { size: 50 } })).data.records.filter(d => d.deviceKind === 'washer')
  programs.value = (await http.get('/md/programs', { params: { size: 50, deviceId: deviceId.value } })).data.records
  await reload()
})
async function reload() { list.value = (await http.get('/wash', { params: { size: 20 } })).data.records }
async function create() {
  await http.post('/wash', { washMode: 'machine', deviceId: deviceId.value, programId: programId.value, recycleItemIds: [recycleItemId.value] })
  ElMessage.success('已建批'); await reload()
}
async function act(url, body) { await http.post(url, body || {}); ElMessage.success('已执行'); await reload() }
</script>
