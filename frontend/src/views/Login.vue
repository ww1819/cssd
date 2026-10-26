<template>
  <div class="login-wrap">
    <el-card class="box">
      <h2>消毒供应追溯系统</h2>
      <p class="sub">工号登录 · 演示密码 123456</p>
      <el-form @submit.prevent="go">
        <el-form-item><el-input v-model="staffNo" placeholder="工号" size="large" /></el-form-item>
        <el-form-item><el-input v-model="password" type="password" placeholder="密码" size="large" /></el-form-item>
        <el-button type="primary" size="large" style="width:100%" native-type="submit" :loading="loading">登录</el-button>
      </el-form>
      <div class="hint">ADMIN / NL001 / OP001 / OP002 / QC001 / DL001 / CL001</div>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api'
import { ElMessage } from 'element-plus'
const router = useRouter()
const staffNo = ref('ADMIN')
const password = ref('123456')
const loading = ref(false)
async function go() {
  loading.value = true
  try {
    const res = await http.post('/auth/login', { staffNo: staffNo.value, password: password.value })
    const staff = { ...(res.data.staff || {}) }
    // 平台 admin 不带机构；机构账号 cssd_01 → 绑定机构编码 01
    if (String(staff.staffNo || '').toLowerCase() === 'admin') {
      delete staff.orgId
      delete staff.orgCode
    } else if (!staff.orgId && !staff.orgCode && staff.staffNo) {
      const m = String(staff.staffNo).match(/^cssd_(\d+)$/i)
      if (m) staff.orgCode = m[1]
    }
    localStorage.setItem('cssd_token', res.data.token)
    localStorage.setItem('cssd_staff', JSON.stringify(staff))
    localStorage.setItem('cssd_menus', JSON.stringify(res.data.menus || []))
    const menuAuth = res.data.menuAuth
    if (Array.isArray(menuAuth)) {
      localStorage.setItem('cssd_menu_auth', JSON.stringify(menuAuth))
    } else {
      localStorage.removeItem('cssd_menu_auth')
    }
    ElMessage.success('登录成功')
    // 有菜单授权时进入首个可见页，否则进工位大厅
    let home = '/station'
    if (Array.isArray(menuAuth) && menuAuth.length) {
      if (menuAuth.includes('station')) home = '/station'
      else if (menuAuth.includes('md') || menuAuth.some((k) => String(k).startsWith('md-'))) home = '/md'
      else if (menuAuth.includes('supply') || menuAuth.some((k) => ['recycle', 'wash', 'pack', 'ster', 'bypass'].some((p) => String(k).startsWith(p)))) home = '/recycle'
      else if (menuAuth.includes('dept-mgmt') || menuAuth.includes('clinical')) home = '/clinical'
      else if (menuAuth.includes('trace')) home = '/trace'
      else if (menuAuth.includes('alerts')) home = '/alerts'
      else if (menuAuth.includes('adverse')) home = '/adverse'
      else if (menuAuth.includes('cost')) home = '/cost'
      else if (menuAuth.includes('report')) home = '/report'
    }
    router.push(home)
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
.login-wrap { height: 100%; display: flex; align-items: center; justify-content: center;
  background:
    radial-gradient(ellipse 80% 60% at 20% 20%, rgba(26, 166, 160, 0.35), transparent 55%),
    radial-gradient(ellipse 70% 50% at 85% 75%, rgba(11, 110, 122, 0.4), transparent 50%),
    linear-gradient(160deg, #0b5f66 0%, #0a3d48 55%, #122a36 100%); }
.box { width: 380px; border-radius: 14px; }
h2 { margin: 0 0 4px; color: #0f7a82; }
.sub, .hint { color: #667; font-size: 13px; }
.hint { margin-top: 12px; }
</style>
