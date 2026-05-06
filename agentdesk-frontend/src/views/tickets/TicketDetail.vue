<template>
  <div class="page-container">
    <el-button @click="$router.back()" :icon="ArrowLeft" style="margin-bottom: 16px">返回</el-button>
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <h3>{{ ticket?.ticketNo }} - {{ ticket?.summary }}</h3>
          <div>
            <el-tag :type="priorityType(ticket?.priority)">P{{ ticket?.priority }}</el-tag>
            <el-tag :type="statusType(ticket?.status)" style="margin-left: 8px">{{ statusMap[ticket?.status || 0] }}</el-tag>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">{{ ticket?.ticketNo }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ categoryMap[ticket?.ticketCategory || 0] }}</el-descriptions-item>
        <el-descriptions-item label="优先级">P{{ ticket?.priority }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusMap[ticket?.status || 0] }}</el-descriptions-item>
        <el-descriptions-item label="处理组">{{ ticket?.assignedGroup || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ ticket?.assignedUserId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="SLA截止">{{ formatTime(ticket?.deadLine || '') }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(ticket?.createTime || '') }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ ticket?.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 16px" v-if="ticket">
      <template #header><h3>状态操作</h3></template>
      <el-space>
        <el-button v-if="ticket.status === 1" type="primary" @click="updateStatus(2)">分诊</el-button>
        <el-button v-if="ticket.status === 2" type="primary" @click="updateStatus(3)">决策</el-button>
        <el-button v-if="ticket.status === 3" type="primary" @click="updateStatus(4)">开始处理</el-button>
        <el-button v-if="ticket.status === 4" type="warning" @click="updateStatus(5)">转人工</el-button>
        <el-button v-if="ticket.status === 4 || ticket.status === 5" type="success" @click="updateStatus(6)">标记解决</el-button>
        <el-button v-if="ticket.status === 6" type="info" @click="updateStatus(7)">关闭工单</el-button>
      </el-space>
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header><h3>操作时间线</h3></template>
      <el-timeline>
        <el-timeline-item
          v-for="item in timeline"
          :key="item.id"
          :timestamp="formatTime(item.createTime)"
          placement="top"
        >
          <p>{{ item.actionType }} - {{ item.actionDetail }}</p>
        </el-timeline-item>
        <el-empty v-if="!timeline.length" description="暂无操作记录" />
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getTicket, getTicketTimeline, updateTicketStatus } from '@/api'
import type { Ticket } from '@/types/api'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const loading = ref(false)
const ticket = ref<Ticket | null>(null)
const timeline = ref<any[]>([])

const categoryMap: Record<number, string> = { 1: '账号访问', 2: '系统Bug', 3: '网络故障', 4: '财务流程', 5: '权限申请', 6: '投诉', 7: '咨询', 8: '通用支持' }
const statusMap: Record<number, string> = { 1: '新建', 2: '已分诊', 3: '已决策', 4: '处理中', 5: '等待人工', 6: '已解决', 7: '已关闭' }
const priorityType = (p?: number) => p === 1 ? 'danger' : p === 2 ? 'warning' : p === 3 ? '' : 'info'
const statusType = (s?: number) => s === 6 ? 'success' : s === 5 ? 'warning' : s === 7 ? 'info' : ''
const formatTime = (t: string) => t ? new Date(t).toLocaleString('zh-CN') : '-'

const fetchTicket = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const [tRes, tlRes] = await Promise.all([getTicket(id), getTicketTimeline(id)])
    ticket.value = tRes.data.data
    timeline.value = tlRes.data.data || []
  } finally { loading.value = false }
}

const updateStatus = async (newStatus: number) => {
  try {
    await updateTicketStatus(ticket.value!.id, { status: newStatus })
    ElMessage.success('状态更新成功')
    fetchTicket()
  } catch (e: any) {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchTicket)
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.detail-header h3 {
  margin: 0;
}
</style>
