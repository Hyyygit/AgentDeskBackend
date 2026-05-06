<template>
  <div class="page-container">
    <div class="page-header">
      <h2>知识库</h2>
      <el-button type="primary" @click="$router.push('/knowledge/create')" :icon="Plus">新建文档</el-button>
    </div>
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item><el-input v-model="query.keyword" placeholder="搜索标题/内容" clearable @clear="fetchData" style="width: 250px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">搜索</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card style="margin-top: 16px" v-loading="loading">
      <el-table :data="docs" stripe>
        <el-table-column prop="docNo" label="编号" width="160" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="70" />
        <el-table-column label="更新时间" width="170"><template #default="{ row }">{{ formatTime(row.updateTime) }}</template></el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/knowledge/${row.id}/edit`)">编辑</el-button>
            <el-button v-if="row.status!=='PUBLISHED'" link type="success" @click="publish(row.id)">发布</el-button>
            <el-button link type="danger" @click="deleteDoc(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" style="margin-top: 16px" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getKnowledgeDocs, publishKnowledgeDoc, deleteKnowledgeDoc } from '@/api'
import type { KnowledgeDoc } from '@/types/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false), docs = ref<KnowledgeDoc[]>([]), total = ref(0)
const query = reactive({ keyword: '', pageNum: 1, pageSize: 10 })
const formatTime = (t: string) => t ? new Date(t).toLocaleString('zh-CN') : '-'

const fetchData = async () => {
  loading.value = true
  try { const res = await getKnowledgeDocs(query); docs.value = res.data.data?.rows || []; total.value = res.data.data?.total || 0 }
  finally { loading.value = false }
}
const publish = async (id: number) => { await publishKnowledgeDoc(id); ElMessage.success('已发布'); fetchData() }
const deleteDoc = async (id: number) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteKnowledgeDoc(id); ElMessage.success('已删除'); fetchData()
}
onMounted(fetchData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
