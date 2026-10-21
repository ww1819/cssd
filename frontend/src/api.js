import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({ baseURL: '/api', timeout: 30000 })
http.interceptors.request.use((cfg) => {
  const t = localStorage.getItem('cssd_token')
  if (t) cfg.headers.Authorization = `Bearer ${t}`
  return cfg
})
http.interceptors.response.use((res) => {
  const body = res.data
  if (body && typeof body.code === 'number' && body.code !== 0) {
    ElMessage.error(body.message || '请求失败')
    return Promise.reject(body)
  }
  return body
}, (err) => {
  const msg = err.response?.data?.message || err.message || '网络错误'
  ElMessage.error(msg)
  if (err.response?.status === 401) {
    localStorage.removeItem('cssd_token')
    if (!location.hash.includes('/login')) location.hash = '#/login'
  }
  return Promise.reject(err)
})

export default http
