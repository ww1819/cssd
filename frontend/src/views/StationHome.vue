<template>
  <div class="home-cockpit">
    <!-- 顶部 5 概览卡：字体/字色按参考图（标题近黑、标签灰、数字主蓝） -->
    <section class="overview-row">
      <article
        v-for="card in overviewCards"
        :key="card.title"
        class="overview-card"
        :style="{ '--card-accent': card.accent, '--card-head': card.headBg }"
      >
        <div class="overview-card__head">
          <span class="overview-card__icon" :style="{ background: card.accent }">
            <el-icon :size="14" color="#fff"><component :is="card.icon" /></el-icon>
          </span>
          <strong>{{ card.title }}</strong>
        </div>
        <div class="overview-card__body">
          <div v-for="(row, ri) in card.rows" :key="ri" class="overview-card__row">
            <template v-for="(item, ii) in row" :key="item.label">
              <div class="metric-item">
                <i class="metric-dot" :style="{ background: item.dot }" />
                <span class="metric-label">{{ item.label }}</span>
                <b class="metric-value" :style="{ color: item.color }">{{ item.value }}</b>
              </div>
              <span v-if="ii < row.length - 1" class="metric-split" />
            </template>
          </div>
        </div>
      </article>
    </section>

    <!-- 中部：强拦预警 / 岗位待办 / 消息中心 -->
    <section class="mid-row">
      <article class="panel panel--danger">
        <header class="panel__hd panel__hd--danger">
          <span class="panel__hd-title">
            <i class="panel__hd-ico panel__hd-ico--on-color">
              <el-icon :size="13" color="#e11d48"><WarningFilled /></el-icon>
            </i>
            强拦柜预警
          </span>
          <em>最高优先级</em>
        </header>
        <ul class="alert-list">
          <li v-for="(a, i) in blockAlerts" :key="i">
            <div class="alert-list__main">
              <p class="alert-list__title">{{ a.title }}</p>
              <p class="alert-list__desc">{{ a.desc }}</p>
            </div>
            <el-button size="small" type="danger" plain @click="go(a.path)">查看单据</el-button>
          </li>
        </ul>
      </article>

      <article class="panel panel--warn">
        <header class="panel__hd panel__hd--warn">
          <span class="panel__hd-title">
            <i class="panel__hd-ico panel__hd-ico--on-color">
              <el-icon :size="13" color="#f59e0b"><Briefcase /></el-icon>
            </i>
            岗位待办消息
          </span>
        </header>
        <ul class="todo-list">
          <li v-for="(t, i) in todos" :key="i">
            <div class="todo-list__main">
              <span class="todo-list__badge">{{ t.count }}</span>
              <p class="todo-list__text">
                <b>{{ t.title }}</b>
                <span class="todo-list__sep">|</span>
                <span>{{ t.desc }}</span>
              </p>
            </div>
            <el-button size="small" :type="t.action === '处理' ? 'warning' : 'primary'" plain @click="go(t.path)">
              {{ t.action }}
            </el-button>
          </li>
        </ul>
      </article>

      <article class="panel panel--msg">
        <header class="panel__hd panel__hd--plain">
          <span>消息中心</span>
          <div class="msg-stats">
            <span><i class="msg-stats__dot unread" />未读 <b>{{ msgStats.unread }}</b></span>
            <span><i class="msg-stats__dot today" />今日 <b>{{ msgStats.today }}</b></span>
            <span><i class="msg-stats__dot week" />本周 <b>{{ msgStats.week }}</b></span>
          </div>
        </header>
        <ul class="msg-feed">
          <li v-for="(m, i) in msgFeed" :key="i">
            <span class="msg-feed__dot" :class="m.level" />
            <p class="msg-feed__text">{{ m.text }}</p>
            <time class="msg-feed__time">{{ m.time }}</time>
          </li>
        </ul>
      </article>
    </section>

    <!-- 下部：通知跨预警+待办两列，右缘与岗位待办对齐 -->
    <section class="lower-row">
      <article class="panel panel--notice">
        <header class="panel__hd panel__hd--info">
          <span class="panel__hd-title">
            <i class="panel__hd-ico panel__hd-ico--on-color">
              <el-icon :size="13" color="#2f6bff"><BellFilled /></el-icon>
            </i>
            常规通知提醒
          </span>
          <button type="button" class="panel__hd-link" @click="go('/alerts')">
            <el-icon :size="14" color="#faad14"><WarningFilled /></el-icon>
            详情
          </button>
        </header>
        <div class="notice-body">
          <ul class="notice-list">
            <li v-for="(row, i) in noticeRows" :key="i" class="notice-row">
              <div class="notice-cell">
                <p class="notice-list__text">
                  <b>{{ row.left.title }}</b>
                  <span class="notice-list__sep">|</span>
                  <span>{{ row.left.desc }}</span>
                </p>
                <el-button size="small" plain type="primary" @click="go(row.left.path)">详情</el-button>
              </div>
              <div class="notice-mid" :style="{ color: row.pct.color }">
                <b>{{ row.pct.pct }}%</b>
                <em>{{ row.pct.label }}</em>
              </div>
              <div class="notice-cell">
                <p class="notice-list__text">
                  <b>{{ row.right.title }}</b>
                  <span class="notice-list__sep">|</span>
                  <span>{{ row.right.desc }}</span>
                </p>
                <el-button size="small" plain type="primary" @click="go(row.right.path)">详情</el-button>
              </div>
            </li>
          </ul>
        </div>
      </article>

      <div class="lower-charts">
        <article class="panel panel--chart panel--flow">
          <header class="panel__hd panel__hd--plain">
            <span class="panel__hd-title">
              <i class="panel__hd-ico panel__hd-ico--purple">
                <el-icon :size="12" color="#fff"><PieChart /></el-icon>
              </i>
              业务流转分布
            </span>
          </header>
          <div class="chart-box chart-box--flow">
            <div class="flow-chart">
              <div
                v-for="(f, i) in flowDist"
                :key="f.label"
                class="flow-label"
                :style="flowLabelStyle(i)"
              >
                <em :style="{ color: f.color }">{{ f.label }}</em>
                <b>{{ f.pct }}%</b>
              </div>
              <div class="flow-donut" :style="flowDonutStyle">
                <div class="flow-donut__hole">
                  <b>{{ flowTotal }}%</b>
                  <span>在途</span>
                </div>
              </div>
            </div>
          </div>
        </article>

        <article class="panel panel--chart panel--trend">
          <header class="panel__hd panel__hd--plain">
            <span>近7天质控趋势</span>
          </header>
          <div class="chart-box chart-box--trend">
            <svg class="trend-svg" viewBox="0 0 320 160" preserveAspectRatio="none">
              <defs>
                <linearGradient id="trendFill" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="#722ed1" stop-opacity="0.28" />
                  <stop offset="100%" stop-color="#722ed1" stop-opacity="0.02" />
                </linearGradient>
              </defs>
              <g class="trend-grid">
                <line v-for="y in [30, 60, 90, 120]" :key="y" x1="28" :y1="y" x2="310" :y2="y" />
              </g>
              <polygon class="trend-area" :points="trendAreaPoints(trendPass)" />
              <polyline class="trend-line trend-line--a" :points="trendPoints(trendPass)" />
              <g v-for="(d, i) in trendDays" :key="d">
                <text class="trend-x" :x="28 + i * 46" y="152">{{ d }}</text>
              </g>
            </svg>
            <div class="trend-legend">
              <span><i class="c-a" />灭菌合格</span>
            </div>
          </div>
        </article>
      </div>
    </section>

    <!-- 消息明细 -->
    <section class="panel panel--table">
      <header class="panel__hd panel__hd--plain">
        <span>消息明细清单</span>
      </header>
      <el-table :data="msgTable" border size="small" class="msg-table">
        <el-table-column type="index" label="序号" width="64" align="center" />
        <el-table-column label="消息等级" width="100" align="center">
          <template #default="{ row }">
            <span class="level-pill" :class="row.level">{{ row.levelLabel }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="消息内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="post" label="对应岗位" width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center" />
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="go(row.path)">{{ row.action }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  DataAnalysis,
  FirstAidKit,
  Bell,
  BellFilled,
  Van,
  Goods,
  PieChart,
  WarningFilled,
  Briefcase
} from '@element-plus/icons-vue'

const router = useRouter()
function go(path) {
  if (!path) return
  router.push(path)
}

const NUM = '#1677ff'
const overviewCards = [
  {
    title: '在途包总览',
    icon: Van,
    accent: '#1677ff',
    headBg: 'linear-gradient(180deg, #bae0ff 0%, #e6f4ff 55%, #f0f8ff 100%)',
    rows: [
      [
        { label: '待回收', value: 12, color: NUM, dot: '#faad14' },
        { label: '待清洗', value: 8, color: NUM, dot: '#69b1ff' },
        { label: '待包装', value: 16, color: '#fa8c16', dot: '#fa8c16' }
      ],
      [
        { label: '待灭菌', value: 9, color: NUM, dot: '#1677ff' },
        { label: '待发放', value: 21, color: '#722ed1', dot: '#722ed1' }
      ]
    ]
  },
  {
    title: '库存概览',
    icon: Goods,
    accent: '#fa8c16',
    headBg: 'linear-gradient(180deg, #ffd591 0%, #ffe7ba 50%, #fff7e6 100%)',
    rows: [
      [
        { label: '无菌库存', value: 326, color: NUM, dot: '#faad14' },
        { label: '临近效期', value: 14, color: NUM, dot: '#69b1ff' }
      ],
      [
        { label: '已过期', value: 2, color: '#722ed1', dot: '#fa8c16' }
      ]
    ]
  },
  {
    title: '质控概览',
    icon: DataAnalysis,
    accent: '#1677ff',
    headBg: 'linear-gradient(180deg, #91caff 0%, #bae0ff 50%, #e6f4ff 100%)',
    rows: [
      [
        { label: '今日灭菌锅次', value: 18, color: NUM, dot: '#1677ff' },
        { label: '生物监测', value: 16, color: NUM, dot: '#1677ff' }
      ],
      [
        { label: '不合格', value: 1, color: '#f5222d', dot: '#f5222d' },
        { label: '召回中', value: 3, color: NUM, dot: '#1677ff' }
      ]
    ]
  },
  {
    title: '外来器械',
    icon: FirstAidKit,
    accent: '#52c41a',
    headBg: 'linear-gradient(180deg, #b7eb8f 0%, #d9f7be 50%, #f6ffed 100%)',
    rows: [
      [
        { label: '今日待接收', value: 7, color: NUM, dot: '#52c41a' },
        { label: '植入物包', value: 4, color: '#fa8c16', dot: '#fa8c16' }
      ]
    ]
  },
  {
    title: '消息总览',
    icon: Bell,
    accent: '#597ef7',
    headBg: 'linear-gradient(180deg, #adc6ff 0%, #d6e4ff 50%, #f0f5ff 100%)',
    rows: [
      [
        { label: '紧急', value: 3, color: NUM, dot: '#597ef7' },
        { label: '待办', value: 24, color: NUM, dot: '#1677ff' }
      ],
      [
        { label: '提醒', value: 18, color: NUM, dot: '#faad14' }
      ]
    ]
  }
]

const blockAlerts = [
  { title: '生物监测阳性', desc: '3号灭菌器 · 锅次 ST-20261026-03 · 请立即拦截并召回', path: '/alerts' },
  { title: '灭菌锅次不合格', desc: '2号灭菌器 · 锅次 ST-20261026-07 · 物理参数超限', path: '/ster' },
  { title: '无菌包已过期', desc: '骨科基础包 ×3 · 库存区 A-02 · 禁止发放', path: '/dist' },
  { title: '植入物未放行', desc: '外来植入物包 IMP-019 · 待护士长确认', path: '/bypass' }
]

const todos = [
  { count: 12, title: '回收清点待接收', desc: '手术室 / 急诊科交接单待确认', action: '处理', path: '/recycle?tab=regular' },
  { count: 8, title: '配包核对应完成', desc: '骨科、腔镜专科包待配', action: '处理', path: '/pack' },
  { count: 6, title: '灭菌装载待上锅', desc: '高温包已配齐，等待装载', action: '查看单据', path: '/ster' },
  { count: 9, title: '发放待出库', desc: '病区申领单待拣配发放', action: '处理', path: '/dist' },
  { count: 4, title: '清洗复洗复核', desc: '目测不合格器械待复洗', action: '查看单据', path: '/wash?tab=register' }
]

const msgStats = { unread: 8, today: 22, week: 67 }
const msgFeed = [
  { text: '生物监测阳性 | 请立即处理', time: '10分钟前', level: 'urgent' },
  { text: '无菌包近效期提醒 | 11件临近效期', time: '32分钟前', level: 'warn' },
  { text: '外来器械已接收 | IMP-019', time: '1小时前', level: 'info' },
  { text: '设备维保到期 | 1号清洗机', time: '昨天', level: 'info' },
  { text: '灭菌锅次已放行 | ST-20261025-12', time: '昨天', level: 'ok' }
]

const noticeRows = [
  {
    left: { title: '无菌包临近效期', desc: '14包3天后到期', path: '/dist' },
    pct: { label: '资产', pct: 13, color: '#52c41a' },
    right: { title: '耗材近效期', desc: '化学指示卡、生物指示剂各2批', path: '/md?tab=sys-config' }
  },
  {
    left: { title: '设备维保到期', desc: '1号清洗机、封口机维保将到期', path: '/md?tab=devices' },
    pct: { label: '生产', pct: 41, color: '#faad14' },
    right: { title: '监测到期提醒', desc: '本周需完成BD测试3次', path: '/ster' }
  },
  {
    left: { title: '人员资质到期', desc: '2人灭菌岗证照30日内到期', path: '/md?tab=staffs' },
    pct: { label: '质量', pct: 28, color: '#1677ff' },
    right: { title: '外来器械待确认', desc: '今日待接收植入物包4件', path: '/bypass' }
  }
]

const flowDist = [
  { label: '回收', pct: 18, color: '#5b8ff9' },
  { label: '清洗', pct: 22, color: '#61ddaa' },
  { label: '配包', pct: 25, color: '#65789b' },
  { label: '灭菌', pct: 20, color: '#f6bd16' },
  { label: '发放', pct: 15, color: '#7262fd' }
]
const flowTotal = flowDist.reduce((s, x) => s + x.pct, 0)
const flowDonutStyle = computed(() => {
  let acc = 0
  const parts = flowDist.map((f) => {
    const from = acc
    acc += f.pct
    return `${f.color} ${from}% ${acc}%`
  })
  return { background: `conic-gradient(${parts.join(',')})` }
})
/** 环绕甜甜圈的扇区标注位置（对齐效果图） */
function flowLabelStyle(index) {
  let acc = 0
  for (let i = 0; i < index; i += 1) acc += flowDist[i].pct
  const mid = acc + flowDist[index].pct / 2
  const rad = ((mid / 100) * 360 - 90) * (Math.PI / 180)
  const r = 46
  const x = 50 + r * Math.cos(rad)
  const y = 50 + r * Math.sin(rad)
  return { left: `${x}%`, top: `${y}%` }
}

const trendDays = ['10/21', '10/22', '10/23', '10/24', '10/25', '10/26', '10/27']
const trendPass = [62, 70, 68, 78, 74, 82, 88]
function trendPoints(values) {
  const max = 100
  return values
    .map((v, i) => {
      const x = 28 + i * 46
      const y = 130 - (v / max) * 100
      return `${x},${y}`
    })
    .join(' ')
}
function trendAreaPoints(values) {
  const line = trendPoints(values)
  return `28,130 ${line} ${28 + (values.length - 1) * 46},130`
}

const msgTable = [
  {
    level: 'urgent',
    levelLabel: '紧急',
    content: '生物监测阳性，请立即拦截相关锅次并发起召回',
    post: '灭菌岗 / 护士长',
    status: '未处理',
    action: '跳转锅次',
    path: '/ster'
  },
  {
    level: 'todo',
    levelLabel: '待办',
    content: '手术室交接单待确认（回收清点 12 件）',
    post: '回收岗',
    status: '处理中',
    action: '跳转交接单',
    path: '/recycle?tab=regular'
  },
  {
    level: 'remind',
    levelLabel: '提醒',
    content: '无菌库存近效期包 11 件，建议优先发放',
    post: '发放岗',
    status: '待关注',
    action: '跳转库存',
    path: '/dist'
  }
]
</script>

<style scoped>
.home-cockpit {
  height: 100%;
  overflow: auto;
  padding: 10px 14px 16px;
  box-sizing: border-box;
  background: #f3f5f8;
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.12) transparent;
}
.home-cockpit::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}
.home-cockpit::-webkit-scrollbar-track {
  background: transparent;
}
.home-cockpit::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 999px;
}
.home-cockpit:hover::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.14);
}
.home-cockpit::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.22);
}
.home-cockpit::-webkit-scrollbar-corner {
  background: transparent;
}

.overview-row {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 12px;
  font-family: "Microsoft YaHei", "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}
.overview-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  height: 126px;
  display: flex;
  flex-direction: column;
}
/* 表头高度与「强拦柜预警」panel__hd 一致 */
.overview-card__head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  flex-shrink: 0;
  background: var(--card-head);
  box-sizing: border-box;
}
.overview-card__head strong {
  font-size: 14px;
  line-height: 22px;
  color: #262626;
  font-weight: 600;
  letter-spacing: 0;
}
.overview-card__icon {
  width: 22px;
  height: 22px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.overview-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  padding: 6px 6px 10px;
  background: #fff;
}
.overview-card__row {
  display: flex;
  align-items: center;
  min-height: 22px;
}
.metric-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 0 7px;
  min-width: 0;
}
.metric-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}
.metric-label {
  font-size: 13px;
  line-height: 20px;
  color: #595959;
  font-weight: 400;
  white-space: nowrap;
}
.metric-value {
  font-size: 18px;
  line-height: 20px;
  font-weight: 700;
  font-family: "DIN Alternate", "Segoe UI", "Microsoft YaHei", sans-serif;
  letter-spacing: 0;
}
.metric-split {
  width: 1px;
  height: 14px;
  background: #d9d9d9;
  margin: 0 1px;
  flex-shrink: 0;
  align-self: center;
}

/* 中/下行同一列轨：通知跨前两列 → 右缘与岗位待办对齐 */
.mid-row,
.lower-row {
  display: grid;
  grid-template-columns: 1fr 1.22fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
  align-items: stretch;
}
.panel--notice {
  grid-column: 1 / span 2;
}
.lower-charts {
  grid-column: 3;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  min-width: 0;
  min-height: 0;
}
.panel {
  background: #fff;
  border: 1px solid #e6eaf0;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(16, 24, 40, 0.04);
  height: 300px;
  display: flex;
  flex-direction: column;
}
.mid-row .panel {
  height: 258px;
}
.lower-row .panel {
  height: 188px;
}
/* 效果图：流转/趋势卡带浅色底，非纯白 */
.panel--flow {
  background: linear-gradient(180deg, #e8f0ff 0%, #f3f7ff 48%, #f8faff 100%);
  border-color: #d6e4ff;
}
.panel--trend {
  background: linear-gradient(180deg, #f3e8ff 0%, #f8f0ff 48%, #fbf7ff 100%);
  border-color: #efdbff;
}
.panel--flow .panel__hd--plain,
.panel--trend .panel__hd--plain {
  background: transparent;
  border-bottom-color: rgba(0, 0, 0, 0.06);
}
.panel--flow .flow-donut__hole {
  background: #fff;
}
.panel--trend .trend-svg {
  background: rgba(255, 255, 255, 0.55);
}
.panel--table {
  height: auto;
  min-height: auto;
}
.panel__hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 22px;
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
  box-sizing: border-box;
}
.panel__hd em {
  font-style: normal;
  font-size: 12px;
  font-weight: 500;
  opacity: 0.9;
}
.panel__hd-link {
  border: 0;
  background: transparent;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
  line-height: 22px;
  opacity: 0.95;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.panel__hd-link:hover { opacity: 1; text-decoration: underline; }
.panel__hd--danger { background: #e11d48; }
.panel__hd--warn { background: #f59e0b; }
.panel__hd--info { background: #2f6bff; }
.panel__hd--plain {
  background: #fff;
  color: #1f2a37;
  border-bottom: 1px solid #eef1f5;
}
.panel__hd-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.panel__hd-ico {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.panel__hd-ico--purple { background: #722ed1; }
.panel__hd-ico--on-color {
  background: rgba(255, 255, 255, 0.95);
  width: 22px;
  height: 22px;
}

.alert-list,
.todo-list,
.msg-feed {
  list-style: none;
  margin: 0;
  padding: 0 12px;
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  justify-content: flex-start;
}
/* 各行等高（含末行），避免最后一行被压缩或拉高 */
.alert-list li,
.todo-list li,
.msg-feed li {
  flex: 0 0 40px;
  height: 40px;
  max-height: 40px;
  padding: 0;
  border-bottom: 1px solid #f0f0f0;
  background: transparent;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.msg-feed li {
  display: grid;
  grid-template-columns: 10px 1fr auto;
  gap: 8px;
}
.alert-list li {
  flex-basis: 48px;
  height: 48px;
  max-height: 48px;
}
.alert-list__title {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: #e11d48;
  line-height: 18px;
}
.alert-list__desc {
  margin: 2px 0 0;
  font-size: 12px;
  color: #6b7280;
  line-height: 16px;
}
.todo-list__main,
.alert-list__main {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}
.todo-list__badge {
  min-width: 26px;
  height: 26px;
  border-radius: 13px;
  background: #f59e0b;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}
.todo-list__text {
  margin: 0;
  min-width: 0;
  font-size: 13px;
  line-height: 20px;
  color: #595959;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.todo-list__text b {
  color: #262626;
  font-weight: 600;
}
.todo-list__sep {
  margin: 0 6px;
  color: #d9d9d9;
  font-weight: 400;
}

.msg-stats {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0;
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
  border: 0;
}
.msg-stats b { color: #262626; font-size: 13px; margin-left: 2px; font-weight: 600; }
.msg-stats__dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 4px;
  vertical-align: middle;
}
.msg-stats__dot.unread { background: #1677ff; }
.msg-stats__dot.today { background: #fa8c16; }
.msg-stats__dot.week { background: #faad14; }
.msg-feed li {
  display: grid;
  grid-template-columns: 10px 1fr auto;
  gap: 8px;
  align-items: center;
}
.msg-feed__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94a3b8;
}
.msg-feed__dot.urgent { background: #e11d48; }
.msg-feed__dot.warn { background: #f59e0b; }
.msg-feed__dot.info { background: #2f6bff; }
.msg-feed__dot.ok { background: #27ae60; }
.msg-feed__text {
  margin: 0;
  font-size: 13px;
  color: #262626;
  line-height: 20px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.msg-feed__time {
  font-size: 12px;
  color: #8c8c8c;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 常规通知：左通知 | 百分比 | 右通知（三行内嵌） */
.notice-body {
  padding: 0 14px;
  flex: 1;
  min-height: 0;
  overflow: hidden;
  box-sizing: border-box;
  display: flex;
  align-items: stretch;
}
.notice-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  flex: 1;
  justify-content: space-evenly;
  overflow: hidden;
}
.notice-row {
  display: grid;
  grid-template-columns: 1fr 56px 1fr;
  column-gap: 18px;
  align-items: center;
  height: 36px;
  border-bottom: 1px solid #f0f0f0;
  box-sizing: border-box;
}
.notice-row:last-child { border-bottom: 0; }
.notice-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  min-width: 0;
}
.notice-list__text {
  margin: 0;
  min-width: 0;
  flex: 1;
  font-size: 13px;
  line-height: 20px;
  color: #595959;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.notice-list__text b {
  color: #262626;
  font-weight: 600;
}
.notice-list__sep {
  margin: 0 6px;
  color: #d9d9d9;
  font-weight: 400;
}
.notice-mid {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1px;
  line-height: 1;
  flex-shrink: 0;
}
.notice-mid b {
  font-size: 15px;
  font-weight: 700;
  line-height: 1.1;
}
.notice-mid em {
  font-style: normal;
  font-size: 11px;
  color: #8c8c8c;
}
.chart-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px 8px;
  flex: 1;
  min-height: 0;
}
.chart-box--flow {
  justify-content: center;
  padding: 4px 2px 6px;
}
.chart-box--trend {
  flex-direction: column;
  align-items: stretch;
  gap: 2px;
}
.flow-chart {
  position: relative;
  width: 100%;
  max-width: 160px;
  height: 100%;
  max-height: 140px;
  aspect-ratio: 1;
  margin: 0 auto;
}
.flow-donut {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 58%;
  height: 58%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  display: grid;
  place-items: center;
}
.flow-donut__hole {
  width: 58%;
  height: 58%;
  border-radius: 50%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.flow-donut__hole b {
  font-size: 13px;
  color: #1f2a37;
  line-height: 1.1;
}
.flow-donut__hole span {
  font-size: 11px;
  color: #94a3b8;
}
.flow-label {
  position: absolute;
  transform: translate(-50%, -50%);
  text-align: center;
  line-height: 1.15;
  white-space: nowrap;
  pointer-events: none;
  z-index: 1;
}
.flow-label em {
  display: block;
  font-style: normal;
  font-size: 11px;
  font-weight: 600;
}
.flow-label b {
  display: block;
  font-size: 11px;
  color: #595959;
  font-weight: 600;
}

.trend-svg {
  width: 100%;
  flex: 1;
  min-height: 0;
  height: 100%;
  background: #fafbfc;
  border-radius: 4px;
}
.lower-row .trend-legend {
  margin: 0;
  font-size: 11px;
}
.trend-grid line {
  stroke: #eef1f5;
  stroke-width: 1;
}
.trend-line {
  fill: none;
  stroke-width: 2.5;
  stroke-linecap: round;
  stroke-linejoin: round;
}
.trend-area {
  fill: url(#trendFill);
  stroke: none;
}
.trend-line--a { stroke: #722ed1; }
.trend-x {
  fill: #94a3b8;
  font-size: 10px;
}
.trend-legend {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #6b7280;
  justify-content: center;
}
.trend-legend i {
  display: inline-block;
  width: 12px;
  height: 3px;
  margin-right: 6px;
  vertical-align: middle;
  border-radius: 2px;
}
.trend-legend .c-a { background: #722ed1; }

.msg-table {
  margin: 0 12px 12px;
  width: calc(100% - 24px);
}
.msg-table :deep(.el-table__header th.el-table__cell) {
  background: #e8eef8 !important;
  color: #1f2a37;
  font-weight: 600;
}
.msg-table :deep(.el-table__header-wrapper) {
  border-radius: 0;
}
.level-pill {
  display: inline-block;
  min-width: 48px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  color: #fff;
}
.level-pill.urgent { background: #e11d48; }
.level-pill.todo { background: #f59e0b; }
.level-pill.remind { background: #2f6bff; }

@media (max-width: 1400px) {
  .overview-row { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .overview-card { height: auto; min-height: 126px; }
  .mid-row { grid-template-columns: 1fr 1fr; }
  .lower-row { grid-template-columns: 1fr; }
  .panel--notice,
  .lower-charts { grid-column: 1 / -1; }
  .panel { height: auto; min-height: 260px; }
  .mid-row .panel { height: auto; min-height: 240px; }
  .lower-row .panel { height: auto; min-height: 180px; }
  .panel--msg { grid-column: 1 / -1; }
}
@media (max-width: 960px) {
  .overview-row,
  .mid-row,
  .lower-row { grid-template-columns: 1fr; }
  .lower-charts { grid-template-columns: 1fr; }
  .panel { height: auto; min-height: 240px; }
  .notice-row { grid-template-columns: 1fr; height: auto; max-height: none; row-gap: 6px; padding: 8px 0; }
  .notice-mid { flex-direction: row; gap: 4px; justify-content: flex-start; }
}
</style>
