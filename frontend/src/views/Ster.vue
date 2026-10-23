<template>
  <div class="page">
    <el-form :inline="true" style="margin-top:12px">
      <el-form-item label="灭菌器">
        <el-select v-model="deviceId" style="width:240px">
          <el-option v-for="d in devices" :key="d.id" :label="d.deviceNo + ' ' + d.deviceName" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="程序">
        <el-select v-model="programId" style="width:200px">
          <el-option v-for="p in programs" :key="p.id" :label="p.programName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="主要装载"><el-input v-model="mainLoad" /></el-form-item>
      <el-button type="primary" @click="create">建锅次</el-button>
      <el-button @click="bd">登记当日BD合格</el-button>
    </el-form>
    <el-input v-model="barcode" placeholder="已核包条码" style="width:260px;margin:8px 8px 8px 0" />
    <el-button @click="add">装锅</el-button>
    <el-table :data="list" border style="margin-top:12px">
      <el-table-column prop="loadNo" label="锅次" />
      <el-table-column prop="loadStatus" label="状态" />
      <el-table-column prop="releaseStatus" label="放行" />
      <el-table-column prop="pcdResult" label="PCD" />
      <el-table-column prop="chemResult" label="化学" />
      <el-table-column prop="bioResult" label="生物" />
      <el-table-column label="操作" width="520">
        <template #default="{ row }">
          <el-button size="small" @click="go('/ster/'+row.id+'/start')">开始</el-button>
          <el-button size="small" @click="go('/ster/'+row.id+'/finish', { paramsJson: { temp: 134 } })">结束</el-button>
          <el-button size="small" @click="mon(row,'pcd')">PCD合格</el-button>
          <el-button size="small" @click="mon(row,'chem')">化学合格</el-button>
          <el-button size="small" @click="mon(row,'bio')">生物合格</el-button>
          <el-button size="small" type="danger" @click="mon(row,'bio','fail')">生物失败召回</el-button>
          <el-button size="small" type="success" @click="go('/ster/'+row.id+'/release')">放行</el-button>
        </template>
      </el-table-column>
    </el-table>
    <p>当前锅 <code>{{ currentId }}</code> · 装锅条码后点「装锅」</p>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const devices = ref([]); const programs = ref([]); const list = ref([])
const deviceId = ref('70000000-0000-7000-8000-000000000002')
const programId = ref('71000000-0000-7000-8000-000000000002')
const mainLoad = ref('口护包')
const barcode = ref(''); const currentId = ref('')
onMounted(async () => {
  devices.value = (await http.get('/md/devices', { params: { size: 50 } })).data.records.filter(d => d.deviceKind === 'sterilizer')
  programs.value = (await http.get('/md/programs', { params: { size: 50, deviceId: deviceId.value } })).data.records
  await reload()
})
async function reload() { list.value = (await http.get('/ster', { params: { size: 20 } })).data.records }
async function create() {
  const r = await http.post('/ster', { deviceId: deviceId.value, programId: programId.value, mainLoadDesc: mainLoad.value })
  currentId.value = r.data.id
  ElMessage.success(r.data.loadNo); await reload()
}
async function add() {
  if (!currentId.value && list.value[0]) currentId.value = list.value[0].id
  await http.post('/ster/' + currentId.value + '/items', { barcode: barcode.value })
  ElMessage.success('已入锅')
}
async function go(url, body) { await http.post(url, body || {}); await reload() }
async function bd() {
  await http.post('/monitor', { deviceId: deviceId.value, monitorKind: 'bd', resultCode: 'pass' })
  ElMessage.success('BD已登记')
}
async function mon(row, kind, result = 'pass') {
  await http.post('/monitor', { deviceId: deviceId.value, loadId: row.id, monitorKind: kind, resultCode: result })
  await reload()
}
</script>
