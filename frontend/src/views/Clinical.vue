<template>
  <div class="page">
    <el-form :inline="true">
      <el-form-item label="科室">
        <el-select v-model="deptId" style="width:220px">
          <el-option v-for="d in depts" :key="d.id" :label="d.deptName" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="模板">
        <el-select v-model="templateId" style="width:220px">
          <el-option v-for="t in tpls" :key="t.id" :label="t.templateName" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="apply">提交申领</el-button>
    </el-form>
    <el-divider>HIS Mock 患者</el-divider>
    <el-table :data="patients" border size="small">
      <el-table-column prop="patientNo" label="住院号" />
      <el-table-column prop="patientName" label="姓名" />
      <el-table-column prop="bedNo" label="床号" />
      <el-table-column prop="surgeryName" label="手术" />
    </el-table>
    <el-divider>术前核准</el-divider>
    <el-space wrap>
      <el-input v-model="pre.barcode" placeholder="已接收条码" />
      <el-input v-model="pre.patientNo" placeholder="住院号" />
      <el-input v-model="pre.patientName" placeholder="姓名" />
      <el-input v-model="pre.bedNo" placeholder="床号" />
      <el-input v-model="pre.surgeryName" placeholder="手术" />
      <el-button type="success" @click="doPre">术前核准</el-button>
      <el-button @click="doPost">术后核准</el-button>
    </el-space>
  </div>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const depts = ref([]); const tpls = ref([]); const patients = ref([])
const deptId = ref('10000000-0000-7000-8000-000000000012')
const templateId = ref('60000000-0000-7000-8000-000000000001')
const pre = reactive({ barcode: '', patientNo: 'ZY10001', patientName: '演示患者甲', bedNo: '12-03', surgeryName: '阑尾切除术' })
onMounted(async () => {
  depts.value = (await http.get('/md/depts', { params: { size: 50 } })).data.records
  tpls.value = (await http.get('/md/templates', { params: { size: 50 } })).data.records
  patients.value = (await http.get('/his/patients')).data
})
async function apply() {
  await http.post('/apply', { deptId: deptId.value, items: [{ templateId: templateId.value, qty: 1 }] })
  ElMessage.success('申领已提交')
}
async function doPre() {
  await http.post('/use/pre', {
    barcodes: [pre.barcode],
    patientNo: pre.patientNo, patientName: pre.patientName, bedNo: pre.bedNo, surgeryName: pre.surgeryName,
    surgeonId: '20000000-0000-7000-8000-000000000008',
    hisPatientId: 'P10001', hisEncounterId: 'E20260922001',
    idempotencyKey: pre.barcode + '|E20260922001|use'
  })
  ElMessage.success('术前已合患者')
}
async function doPost() {
  await http.post('/use/post', { barcodes: [pre.barcode] })
  ElMessage.success('术后已核准')
}
</script>
