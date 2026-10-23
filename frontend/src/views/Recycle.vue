<template>
  <div class="page">
    <!-- 常规回收：登记提交 -->
    <template v-if="active === 'regular'">
      <el-form :inline="true">
        <el-form-item label="科室">
          <el-select v-model="deptId" filterable style="width:220px">
            <el-option v-for="d in depts" :key="d.id" :label="d.deptCode + ' ' + d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="篮筐">
          <el-select v-model="basketId" clearable style="width:180px">
            <el-option v-for="b in baskets" :key="b.id" :label="b.basketCode + ' ' + b.basketName" :value="b.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-space>
        <el-input v-model="barcode" placeholder="旧包条码（可空=无码入洗）" style="width:240px" />
        <el-select v-model="templateId" filterable placeholder="模板" style="width:220px">
          <el-option v-for="t in tpls" :key="t.id" :label="t.templateCode + ' ' + t.templateName" :value="t.id" />
        </el-select>
        <el-input-number v-model="qty" :min="1" />
        <el-checkbox v-model="urgent">加急</el-checkbox>
        <el-button type="primary" @click="add">加入明细</el-button>
        <el-button type="success" @click="submit">提交回收</el-button>
      </el-space>
      <el-table :data="items" style="margin-top:12px" border>
        <el-table-column prop="barcode" label="条码" />
        <el-table-column prop="templateId" label="模板ID" />
        <el-table-column prop="qty" label="数量" width="80" />
        <el-table-column prop="urgentFlag" label="加急" width="80" />
      </el-table>
    </template>

    <!-- 回收管理：单据列表 -->
    <template v-else-if="active === 'manage'">
      <div class="toolbar">
        <el-button size="small" @click="loadList">刷新</el-button>
      </div>
      <el-table :data="list" border>
        <el-table-column prop="billNo" label="回收单号" />
        <el-table-column prop="deptName" label="科室" />
        <el-table-column prop="billStatus" label="状态" />
        <el-table-column prop="recycStaffName" label="回收人" />
      </el-table>
    </template>

    <!-- 其余二级菜单：占位，待业务落地 -->
    <template v-else>
      <el-empty :description="placeholderTitle + '（功能待开发）'" />
    </template>
  </div>
</template>
<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import http from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const titles = {
  regular: '常规回收',
  manage: '回收管理',
  'ext-recycle': '外器回收',
  'ext-bill': '外器单据回收',
  'ext-manage': '外器管理',
  'return-reg': '退包登记',
  'return-manage': '退包管理'
}
const active = computed(() => route.query.tab || 'regular')
const placeholderTitle = computed(() => titles[active.value] || '回收')

const depts = ref([])
const baskets = ref([])
const tpls = ref([])
const list = ref([])
const deptId = ref('10000000-0000-7000-8000-000000000012')
const basketId = ref('80000000-0000-7000-8000-000000000001')
const barcode = ref('')
const templateId = ref('60000000-0000-7000-8000-000000000001')
const qty = ref(1)
const urgent = ref(false)
const items = ref([])

async function loadList() {
  list.value = (await http.get('/recycle', { params: { size: 20 } })).data.records
}

onMounted(async () => {
  depts.value = (await http.get('/md/depts', { params: { size: 100 } })).data.records
  baskets.value = (await http.get('/md/baskets', { params: { size: 100 } })).data.records
  tpls.value = (await http.get('/md/templates', { params: { size: 100 } })).data.records
  await loadList()
})

watch(active, (tab) => {
  if (tab === 'manage') loadList()
})

function add() {
  items.value.push({
    barcode: barcode.value || null,
    templateId: templateId.value,
    qty: qty.value,
    basketId: basketId.value,
    urgentFlag: urgent.value ? 1 : 0
  })
}
async function submit() {
  await http.post('/recycle/submit', { deptId: deptId.value, items: items.value })
  ElMessage.success('已提交')
  items.value = []
  await loadList()
}
</script>
<style scoped>
.toolbar { margin-bottom: 12px; }
</style>
