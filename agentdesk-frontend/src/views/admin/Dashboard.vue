<template>
  <div class="page-container">
    <h2 style="margin-bottom: 16px">管理仪表盘</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card><div class="stat-card"><div class="stat-value">{{ stat.value }}</div><div class="stat-label">{{ stat.label }}</div></div></el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top: 16px">
      <template #header><h3>系统健康状态</h3></template>
      <div v-loading="loading">
        <el-tag v-for="svc in services" :key="svc.name" :type="svc.status === 'UP' ? 'success' : 'danger'" style="margin: 4px">{{ svc.name }}: {{ svc.status }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStats, getSystemHealth } from '@/api'

const loading = ref(false)
const stats = ref([{ label: '总工单数', value: 0 }, { label: '待处理', value: 0 }, { label: '处理中', value: 0 }, { label: '今日新增', value: 0 }])
const services = ref<{ name: string; status: string }[]>([])

onMounted(async () => {
  loading.value = true; try {
    const [sRes, hRes] = await Promise.all([getDashboardStats(), getSystemHealth()])
    const d = sRes.data.data; if (d) { stats.value[0].value = d.total || 0; stats.value[1].value = d.pending || 0; stats.value[2].value = d.processing || 0; stats.value[3].value = d.today || 0 }
    const h = hRes.data.data; if (h) services.value = Object.entries(h).map(([k, v]) => ({ name: k, status: String(v) }))
  } finally { loading.value = false }
})
</script>

<style scoped>
.stat-card { text-align: center; padding: 16px; }
.stat-value { font-size: 32px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
</style>
