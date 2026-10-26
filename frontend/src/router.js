import { createRouter, createWebHashHistory } from 'vue-router'
import Login from './views/Login.vue'
import Shell from './views/Shell.vue'
import StationHome from './views/StationHome.vue'
import Recycle from './views/Recycle.vue'
import Wash from './views/Wash.vue'
import Pack from './views/Pack.vue'
import Ster from './views/Ster.vue'
import Dist from './views/Dist.vue'
import Clinical from './views/Clinical.vue'
import Trace from './views/Trace.vue'
import Master from './views/Master.vue'
import Alerts from './views/Alerts.vue'
import Bypass from './views/Bypass.vue'
import Placeholder from './views/Placeholder.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/login', component: Login },
    {
      path: '/',
      component: Shell,
      children: [
        { path: '', redirect: '/station' },
        { path: 'station', component: StationHome },
        { path: 'recycle', component: Recycle },
        { path: 'wash', component: Wash },
        { path: 'pack', component: Pack },
        { path: 'ster', component: Ster },
        { path: 'dist', component: Dist },
        { path: 'adverse', component: Placeholder, meta: { title: '不良事件处理' } },
        { path: 'cost', component: Placeholder, meta: { title: '成本核算管理' } },
        { path: 'report', component: Placeholder, meta: { title: '报表统计管理' } },
        { path: 'clinical', component: Clinical },
        { path: 'dept/item-apply', component: Placeholder, meta: { title: '物品申请' } },
        { path: 'dept/apply-query', component: Placeholder, meta: { title: '查询申请' } },
        { path: 'dept/patient', component: Placeholder, meta: { title: '病人查询' } },
        { path: 'dept/instrument-track', component: Placeholder, meta: { title: '器械追踪' } },
        { path: 'trace', component: Trace },
        { path: 'md', component: Master },
        { path: 'alerts', component: Alerts },
        { path: 'bypass', component: Bypass }
      ]
    }
  ]
})
/** 浏览器刷新 / 冷启动时回到首个可见页；应用内点击菜单仍可正常跳转 */
let coldStart = true
function coldStartHome() {
  try {
    const raw = localStorage.getItem('cssd_menu_auth')
    const list = raw ? JSON.parse(raw) : null
    if (Array.isArray(list) && list.length) {
      if (list.includes('station')) return '/station'
      if (list.includes('md') || list.some((k) => String(k).startsWith('md-'))) return '/md'
      if (list.includes('trace')) return '/trace'
      if (list.includes('alerts')) return '/alerts'
      if (list.includes('clinical') || list.includes('dept-mgmt')) return '/clinical'
      if (list.includes('adverse')) return '/adverse'
      if (list.includes('cost')) return '/cost'
      if (list.includes('report')) return '/report'
      return '/md'
    }
  } catch { /* ignore */ }
  return '/station'
}
router.beforeEach((to) => {
  const t = localStorage.getItem('cssd_token')
  if (to.path !== '/login' && !t) return '/login'
  if (coldStart) {
    coldStart = false
    const home = coldStartHome()
    if (t && to.path !== '/login' && to.path !== home) {
      return home
    }
  }
})
export default router
