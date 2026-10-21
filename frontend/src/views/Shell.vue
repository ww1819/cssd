<template>
  <el-container style="height:100%">
    <el-aside width="220px" class="aside">
      <div class="logo">CSSD 追溯</div>
      <el-menu :default-active="$route.path" router background-color="#0b5553" text-color="#d9f3f1" active-text-color="#fff">
        <el-menu-item index="/station">工位大厅</el-menu-item>
        <el-menu-item index="/recycle">回收</el-menu-item>
        <el-menu-item index="/wash">清洗</el-menu-item>
        <el-menu-item index="/pack">配包/核包</el-menu-item>
        <el-menu-item index="/ster">灭菌/监测/放行</el-menu-item>
        <el-menu-item index="/dist">发放/入库</el-menu-item>
        <el-menu-item index="/clinical">科室申领与使用</el-menu-item>
        <el-menu-item index="/bypass">寄消/外来/一次性</el-menu-item>
        <el-menu-item index="/trace">追溯查询</el-menu-item>
        <el-menu-item index="/alerts">告警与召回</el-menu-item>
        <el-menu-item index="/md">主数据</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="hdr">
        <span>{{ staff?.staffName }}（{{ staff?.staffNo }} / {{ roleLabel }}）</span>
        <el-button text @click="logout">退出</el-button>
      </el-header>
      <el-main style="background:#f4f7f6"><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const staff = computed(() => {
  try { return JSON.parse(localStorage.getItem('cssd_staff') || 'null') } catch { return null }
})
const map = { admin: '管理员', nurse_lead: '护士长', operator: '操作员', qc: '质检员', delivery: '下送', clinical: '临床', surgeon: '术者' }
const roleLabel = computed(() => map[staff.value?.role] || staff.value?.role)
function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>
<style scoped>
.aside { background: #0b5553; color: #fff; }
.logo { padding: 18px 16px; font-weight: 700; font-size: 18px; }
.hdr { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e5eeec; }
</style>
