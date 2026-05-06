<template>
  <div class="page-container">
    <el-button @click="$router.back()" :icon="ArrowLeft" style="margin-bottom: 16px">返回</el-button>
    <el-card v-loading="loading">
      <template #header><h3>{{ isEdit ? '编辑文档' : '新建文档' }}</h3></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" style="max-width: 800px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category"><el-option label="账号访问" value="ACCOUNT_ACCESS" /><el-option label="系统Bug" value="SYSTEM_BUG" /><el-option label="网络故障" value="NETWORK_FAILURE" /><el-option label="通用" value="GENERAL" /></el-select>
        </el-form-item>
        <el-form-item label="标签"><el-input v-model="form.tags" placeholder="逗号分隔" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="12" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createKnowledgeDoc, updateKnowledgeDoc, getKnowledgeDoc } from '@/api'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute(); const router = useRouter()
const formRef = ref(); const submitting = ref(false); const loading = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({ title: '', category: 'GENERAL', tags: '', summary: '', content: '' })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }], category: [{ required: true }], content: [{ required: true, message: '请输入内容', trigger: 'blur' }] }

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return; submitting.value = true
  try {
    if (isEdit.value) { await updateKnowledgeDoc(Number(route.params.id), form); ElMessage.success('更新成功') }
    else { await createKnowledgeDoc(form); ElMessage.success('创建成功') }
    router.push('/knowledge')
  } catch (e: any) { ElMessage.error('操作失败') } finally { submitting.value = false }
}

onMounted(async () => {
  if (isEdit.value) { loading.value = true; try { const res = await getKnowledgeDoc(Number(route.params.id)); Object.assign(form, res.data.data) } finally { loading.value = false } }
})
</script>
