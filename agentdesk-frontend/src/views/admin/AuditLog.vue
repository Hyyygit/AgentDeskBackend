<template>
  <div class="page-container">
    <h2 style="margin-bottom: 16px">审计日志 - Agent运行记录</h2>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item><el-input v-model="query.agentName" placeholder="Agent名称" clearable style="width: 160px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card style="margin-top: 16px" v-loading="loading">
      <el-table :data="logs" stripe>
        <el-table-column prop="requestId" label="请求ID" width="200" show-overflow-tooltip />
        <el-table-column prop="agentName" label="Agent" width="120" />
        <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="row.runStatus==='SUCCESS'?'success':'danger'" size="small">{{ row.runStatus }}</el-tag></template></el-table-column>
        <el-table-column prop="latencyMs" label="耗时(ms)" width="90" />
        <el-table-column prop="costTokens" label="Token" width="80" />
        <el-table-column label="置信度" width="90"><template #default="{ row }">{{ row.confidence ? (row.confidence * 100).toFixed(0) + '%' : '-' }}</template></el-table-column>
        <el-table-column prop="gateResult" label="门控" width="90" />
        <el-table-column label="时间" width="170"><template #default="{ row }">{{ formatTime(row.createTime) }}</template></el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" style="margin-top: 16px" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAuditLogs } from '@/api'
import type { AgentRunLog } from '@/types/api'

const loading = ref(false), logs = ref<AgentRunLog[]>([]), total = ref(0)
const query = reactive({ agentName: '', pageNum: 1, pageSize: 20 })
const formatTime = (t: string) => t ? new Date(t).toLocaleString('zh-CN') : '-'

const fetchData = async () => { loading.value = true; try { const res = await getAuditLogs(query); logs.value = res.data.data?.rows || []; total.value = res.data.data?.total || 0 } finally { loading.value = false } }
onMounted(fetchData)
</script>
