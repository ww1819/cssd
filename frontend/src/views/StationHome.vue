<template>
  <div class="page">
    <div class="station-grid">
      <button class="station-btn" @click="$router.push('/recycle')">回收</button>
      <button class="station-btn" @click="$router.push('/wash')">清洗</button>
      <button class="station-btn" @click="$router.push('/pack')">配包核包</button>
      <button class="station-btn" @click="$router.push('/ster')">灭菌放行</button>
      <button class="station-btn" @click="$router.push('/dist')">入库发放</button>
      <button class="station-btn" @click="$router.push('/clinical')">科室使用</button>
      <button class="station-btn" @click="$router.push('/trace')">扫码追溯</button>
      <button class="station-btn" @click="runDemo">一键演示闭环</button>
    </div>
    <el-alert v-if="demo" style="margin-top:16px" type="success" :closable="false"
      :title="'演示完成，条码 ' + demo.barcode" />
    <pre v-if="demo" class="pre">{{ JSON.stringify({ barcode: demo.barcode, recycleNo: demo.recycleNo, washNo: demo.washNo, loadNo: demo.loadNo, applyNo: demo.applyNo }, null, 2) }}</pre>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import http from '../api'
import { ElMessage } from 'element-plus'
const demo = ref(null)
async function runDemo() {
  const res = await http.post('/demo/run-closed-loop')
  demo.value = res.data
  ElMessage.success('闭环演示已写入，可去追溯查询')
}
</script>
<style scoped>
.pre { background:#fff; padding:12px; overflow:auto; }
</style>
