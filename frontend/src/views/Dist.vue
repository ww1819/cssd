<template>
  <div class="page">
    <div class="report-head">无菌入库 / 按申领发放</div>
    <el-space wrap>
      <el-input v-model="storeBc" placeholder="放行后入库条码" style="width:220px" />
      <el-button type="primary" @click="store">入库</el-button>
      <el-input v-model="legalBc" placeholder="打法规签条码" style="width:220px" />
      <el-button @click="legal">法规签+RFID Mock</el-button>
    </el-space>
    <el-divider />
    <p>从已提交申领发放（可少不可多）</p>
    <el-table :data="applies" border>
      <el-table-column prop="billNo" label="申领单" />
      <el-table-column prop="deptName" label="科室" />
      <el-table-column prop="billStatus" label="状态" />
      <el-table-column label="发">
        <template #default="{ row }">
          <el-input v-model="row._bc" placeholder="条码" size="small" />
          <el-button size="small" type="success" @click="issue(row)">发放并接收</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-divider />
    <el-input v-model="retBc" placeholder="未开包退库条码" style="width:220px" />
    <el-button @click="ret">退库</el-button>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const storeBc = ref(''); const legalBc = ref(''); const retBc = ref(''); const applies = ref([])
onMounted(async () => { applies.value = (await http.get('/apply', { params: { size: 20 } })).data.records })
async function store() { await http.post('/store', { barcode: storeBc.value }); ElMessage.success('已入库') }
async function legal() { await http.post('/print', { barcode: legalBc.value, phase: 'legal', writeRfid: true }); ElMessage.success('法规签已打') }
async function issue(row) {
  await http.post('/dist', { applyBillId: row.id, distMode: 'direct', autoReceive: true, barcodes: [row._bc] })
  ElMessage.success('已发放')
}
async function ret() { await http.post('/dist/return', { barcode: retBc.value }); ElMessage.success('已退库') }
</script>
