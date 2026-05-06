<template>
  <div class="page-container">
    <div class="page-header">
      <h2>工单管理</h2>
      <el-button type="primary" @click="$router.push('/tickets/create')" :icon="Plus">创建工单</el-button>
    </div>

    <el-card class="search-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="新建" :value="1" />
            <el-option label="已分诊" :value="2" />
            <el-option label="已决策" :value="3" />
            <el-option label="处理中" :value="4" />
            <el-option label="等待人工" :value="5" />
            <el-option label="已解决" :value="6" />
            <el-option label="已关闭" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="query.priority" placeholder="全部" clearable style="width: 140px">
            <el-option label="P1-紧急" :value="1" />
            <el-option label="P2-高" :value="2" />
            <el-option label="P3-中" :value="3" />
            <el-option label="P4-低" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.ticketCategory" placeholder="全部" clearable style="width: 140px">
            <el-option label="账号访问" :value="1" />
            <el-option label="系统Bug" :value="2" />
            <el-option label="网络故障" :value="3" />
            <el-option label="财务流程" :value="4" />
            <el-option label="权限申请" :value="5" />
            <el-option label="投诉" :value="6" />
            <el-option label="咨询" :value="7" />
            <el-option label="通用支持" :value="8" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tickets" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="ticketNo" label="工单编号" width="180" />
        <el-table-column prop="summary" label="摘要" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="100">
          <template #default="{ row }">{{ categoryMap[row.ticketCategory] || '-' }}</template>
        </el-table-column>
        <el-table-column label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)" size="small">{{ 'P' + row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusMap[row.status] || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedGroup" label="处理组" width="120" />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/tickets/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="fetchTickets"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getTickets } from '@/api'
import type { Ticket } from '@/types/api'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tickets = ref<Ticket[]>([])
const total = ref(0)
const query = reactive({ status: undefined as number | undefined, priority: undefined as number | undefined, ticketCategory: undefined as number | undefined, pageNum: 1, pageSize: 10 })

const categoryMap: Record<number, string> = { 1: '账号访问', 2: '系统Bug', 3: '网络故障', 4: '财务流程', 5: '权限申请', 6: '投诉', 7: '咨询', 8: '通用支持' }
const statusMap: Record<number, string> = { 1: '新建', 2: '已分诊', 3: '已决策', 4: '处理中', 5: '等待人工', 6: '已解决', 7: '已关闭' }
const priorityType = (p: number) => p === 1 ? 'danger' : p === 2 ? 'warning' : p === 3 ? '' : 'info'
const statusType = (s: number) => s === 6 ? 'success' : s === 5 ? 'warning' : s === 7 ? 'info' : ''

const formatTime = (t: string) => t ? new Date(t).toLocaleString('zh-CN') : '-'

const fetchTickets = async () => {
  loading.value = true
  try {
    const res = await getTickets(query)
    tickets.value = res.data.data?.rows || []
    total.value = res.data.data?.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { query.pageNum = 1; fetchTickets() }
const resetQuery = () => { query.status = undefined; query.priority = undefined; query.ticketCategory = undefined; handleSearch() }

onMounted(fetchTickets)
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.search-card {
  margin-bottom: 0;
}
</style>
