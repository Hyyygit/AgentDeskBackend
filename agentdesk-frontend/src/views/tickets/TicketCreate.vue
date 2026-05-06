<template>
  <div class="page-container">
    <el-button @click="$router.back()" :icon="ArrowLeft" style="margin-bottom: 16px">返回</el-button>
    <el-card>
      <template #header><h3>创建工单</h3></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px">
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" placeholder="请简要描述问题" />
        </el-form-item>
        <el-form-item label="分类" prop="ticketCategory">
          <el-select v-model="form.ticketCategory" placeholder="请选择分类">
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
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级">
            <el-option label="P1-紧急" :value="1" />
            <el-option label="P2-高" :value="2" />
            <el-option label="P3-中" :value="3" />
            <el-option label="P4-低" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述问题" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { createTicket } from '@/api'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const form = reactive({ summary: '', ticketCategory: 1, priority: 3, description: '' })
const rules = {
  summary: [{ required: true, message: '请输入摘要', trigger: 'blur' }],
  ticketCategory: [{ required: true, message: '请选择分类', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await createTicket(form)
    ElMessage.success('工单创建成功')
    router.push('/tickets')
  } catch (e: any) {
    ElMessage.error('创建失败')
  } finally { submitting.value = false }
}
</script>
