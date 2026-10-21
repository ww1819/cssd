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
        { path: 'clinical', component: Clinical },
        { path: 'trace', component: Trace },
        { path: 'md', component: Master },
        { path: 'alerts', component: Alerts },
        { path: 'bypass', component: Bypass }
      ]
    }
  ]
})
router.beforeEach((to) => {
  const t = localStorage.getItem('cssd_token')
  if (to.path !== '/login' && !t) return '/login'
})
export default router
