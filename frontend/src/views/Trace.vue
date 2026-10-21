<template>
  <div class="page">
    <div class="report-head">扫码 / 输码历程</div>
    <el-input v-model="code" placeholder="包条码或 RFID" style="width:280px;margin:12px 8px 12px 0" @keyup.enter="search" />
    <el-button type="primary" @click="search">查询</el-button>
    <el-descriptions v-if="data?.instance" :column="3" border style="margin-top:12px">
      <el-descriptions-item label="条码">{{ data.instance.barcode }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ data.instance.status }}</el-descriptions-item>
      <el-descriptions-item label="包名">{{ data.instance.templateName }}</el-descriptions-item>
      <el-descriptions-item label="配包人">{{ data.instance.packerName }}</el-descriptions-item>
      <el-descriptions-item label="检查打包者">{{ data.instance.checkerName }}</el-descriptions-item>
      <el-descriptions-item label="灭菌器">{{ data.instance.deviceNo }}</el-descriptions-item>
      <el-descriptions-item label="灭菌日">{{ data.instance.sterilizeDate }}</el-descriptions-item>
      <el-descriptions-item label="失效日">{{ data.instance.expireDate }}</el-descriptions-item>
      <el-descriptions-item label="患者">{{ data.instance.patientName }} {{ data.instance.patientNo }}</el-descriptions-item>
      <el-descriptions-item label="术者">{{ data.instance.surgeonName }}</el-descriptions-item>
      <el-descriptions-item label="床号">{{ data.instance.bedNo }}</el-descriptions-item>
    </el-descriptions>
    <el-table v-if="data?.items" :data="data.items" border style="margin-top:12px">
      <el-table-column prop="instrumentCode" label="器械编码" />
      <el-table-column prop="instrumentName" label="名称" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column prop="qty" label="数量" />
    </el-table>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import http from '../api'
const code = ref('')
const data = ref(null)
async function search() {
  data.value = (await http.get('/trace', { params: { code: code.value } })).data
}
</script>
